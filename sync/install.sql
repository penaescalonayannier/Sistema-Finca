-- Instalación de sincronización eventual entre nodos de Sistema Finca.
-- Ejecútese una vez en CADA base de datos local como propietario de la BD.
-- No borra ni modifica datos de negocio existentes.

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE SCHEMA IF NOT EXISTS finca_sync;

CREATE TABLE IF NOT EXISTS finca_sync.node (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    installed_at timestamptz NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE IF NOT EXISTS finca_sync.change_log (
    sequence bigserial PRIMARY KEY,
    change_id uuid NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    origin_node_id uuid NOT NULL,
    table_name text NOT NULL,
    record_id uuid NOT NULL,
    operation text NOT NULL CHECK (operation IN ('UPSERT', 'DELETE')),
    row_data jsonb,
    changed_at timestamptz NOT NULL DEFAULT clock_timestamp()
);

CREATE INDEX IF NOT EXISTS idx_finca_sync_change_origin_sequence
    ON finca_sync.change_log (origin_node_id, sequence);

CREATE TABLE IF NOT EXISTS finca_sync.delivery (
    peer_node_id uuid NOT NULL,
    change_id uuid NOT NULL REFERENCES finca_sync.change_log(change_id) ON DELETE CASCADE,
    delivered_at timestamptz NOT NULL DEFAULT clock_timestamp(),
    PRIMARY KEY (peer_node_id, change_id)
);

CREATE TABLE IF NOT EXISTS finca_sync.received_change (
    change_id uuid PRIMARY KEY,
    received_at timestamptz NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE IF NOT EXISTS finca_sync.record_version (
    table_name text NOT NULL,
    record_id uuid NOT NULL,
    changed_at timestamptz NOT NULL,
    origin_node_id uuid NOT NULL,
    deleted boolean NOT NULL DEFAULT false,
    PRIMARY KEY (table_name, record_id)
);

CREATE TABLE IF NOT EXISTS finca_sync.conflict (
    id bigserial PRIMARY KEY,
    change_id uuid NOT NULL,
    table_name text NOT NULL,
    record_id uuid NOT NULL,
    incoming_data jsonb,
    local_changed_at timestamptz,
    local_origin_node_id uuid,
    resolution text NOT NULL,
    detected_at timestamptz NOT NULL DEFAULT clock_timestamp()
);

CREATE OR REPLACE FUNCTION finca_sync.configure_node(node_id uuid, node_name text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE configured_id uuid;
BEGIN
    SELECT id INTO configured_id FROM finca_sync.node LIMIT 1;
    IF configured_id IS NOT NULL AND configured_id <> node_id THEN
        RAISE EXCEPTION 'Este nodo ya está configurado con el identificador %', configured_id;
    END IF;

    INSERT INTO finca_sync.node (id, name)
    VALUES (node_id, node_name)
    ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;
END;
$$;

CREATE OR REPLACE FUNCTION finca_sync.local_node_id()
RETURNS uuid
LANGUAGE plpgsql
STABLE
AS $$
DECLARE result uuid;
BEGIN
    SELECT id INTO result FROM finca_sync.node LIMIT 1;
    IF result IS NULL THEN
        RAISE EXCEPTION 'Sincronización no configurada: falta finca_sync.configure_node(...)';
    END IF;
    RETURN result;
END;
$$;

CREATE OR REPLACE FUNCTION finca_sync.capture_change()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    affected_id uuid;
    payload jsonb;
    action text;
    local_node uuid;
    change_time timestamptz := clock_timestamp();
BEGIN
    IF current_setting('finca_sync.applying', true) = 'on' THEN
        RETURN COALESCE(NEW, OLD);
    END IF;

    IF TG_OP = 'DELETE' THEN
        affected_id := OLD.id;
        payload := NULL;
        action := 'DELETE';
    ELSE
        affected_id := NEW.id;
        payload := to_jsonb(NEW);
        action := 'UPSERT';
    END IF;
    local_node := finca_sync.local_node_id();

    INSERT INTO finca_sync.record_version
        (table_name, record_id, changed_at, origin_node_id, deleted)
    VALUES (TG_TABLE_NAME, affected_id, change_time, local_node, action = 'DELETE')
    ON CONFLICT (table_name, record_id) DO UPDATE
        SET changed_at = EXCLUDED.changed_at,
            origin_node_id = EXCLUDED.origin_node_id,
            deleted = EXCLUDED.deleted;

    INSERT INTO finca_sync.change_log
        (origin_node_id, table_name, record_id, operation, row_data, changed_at)
    VALUES (local_node, TG_TABLE_NAME, affected_id, action, payload, change_time);

    RETURN COALESCE(NEW, OLD);
END;
$$;

CREATE OR REPLACE FUNCTION finca_sync.register_triggers()
RETURNS TABLE(table_name text)
LANGUAGE plpgsql
AS $$
DECLARE item record;
BEGIN
    FOR item IN
        SELECT c.relname
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_attribute a ON a.attrelid = c.oid
        WHERE n.nspname = 'public'
          AND c.relkind = 'r'
          AND a.attname = 'id'
          AND a.atttypid = 'uuid'::regtype
          AND a.attnum > 0
          AND NOT a.attisdropped
        ORDER BY c.relname
    LOOP
        EXECUTE format('DROP TRIGGER IF EXISTS finca_sync_track_change ON public.%I', item.relname);
        EXECUTE format(
            'CREATE TRIGGER finca_sync_track_change '
            || 'AFTER INSERT OR UPDATE OR DELETE ON public.%I '
            || 'FOR EACH ROW EXECUTE FUNCTION finca_sync.capture_change()',
            item.relname
        );
        table_name := item.relname;
        RETURN NEXT;
    END LOOP;
END;
$$;

CREATE OR REPLACE FUNCTION finca_sync.export_changes(peer_id uuid, max_rows integer DEFAULT 1000)
RETURNS TABLE(change_id uuid, payload jsonb)
LANGUAGE sql
STABLE
AS $$
    SELECT c.change_id,
           jsonb_build_object(
               'change_id', c.change_id,
               'origin_node_id', c.origin_node_id,
               'sequence', c.sequence,
               'table_name', c.table_name,
               'record_id', c.record_id,
               'operation', c.operation,
               'row_data', c.row_data,
               'changed_at', c.changed_at
           )
    FROM finca_sync.change_log c
    LEFT JOIN finca_sync.delivery d
      ON d.change_id = c.change_id AND d.peer_node_id = $1
    WHERE d.change_id IS NULL
      AND c.origin_node_id <> $1
    ORDER BY c.sequence
    LIMIT GREATEST(max_rows, 1);
$$;

CREATE OR REPLACE FUNCTION finca_sync.mark_delivered(peer_id uuid, delivered_change_id uuid)
RETURNS void
LANGUAGE sql
AS $$
    INSERT INTO finca_sync.delivery (peer_node_id, change_id)
    VALUES (peer_id, delivered_change_id)
    ON CONFLICT DO NOTHING;
$$;

CREATE OR REPLACE FUNCTION finca_sync.apply_change(payload jsonb)
RETURNS boolean
LANGUAGE plpgsql
AS $$
DECLARE
    incoming_change uuid := (payload->>'change_id')::uuid;
    incoming_node uuid := (payload->>'origin_node_id')::uuid;
    incoming_table text := payload->>'table_name';
    incoming_record uuid := (payload->>'record_id')::uuid;
    incoming_operation text := payload->>'operation';
    incoming_time timestamptz := (payload->>'changed_at')::timestamptz;
    current_version finca_sync.record_version%ROWTYPE;
    update_columns text;
    insert_columns text;
    table_ref regclass;
    incoming_wins boolean;
BEGIN
    IF incoming_change IS NULL OR incoming_node IS NULL OR incoming_record IS NULL
       OR incoming_table IS NULL OR incoming_operation NOT IN ('UPSERT', 'DELETE')
       OR incoming_time IS NULL THEN
        RAISE EXCEPTION 'Cambio de sincronización inválido';
    END IF;

    IF incoming_node = finca_sync.local_node_id() THEN
        RETURN true;
    END IF;

    SELECT to_regclass(format('public.%I', incoming_table)) INTO table_ref;
    IF table_ref IS NULL OR NOT EXISTS (
        SELECT 1 FROM pg_attribute
        WHERE attrelid = table_ref AND attname = 'id' AND atttypid = 'uuid'::regtype
          AND attnum > 0 AND NOT attisdropped
    ) THEN
        RAISE EXCEPTION 'Tabla no sincronizable: %', incoming_table;
    END IF;

    IF EXISTS (SELECT 1 FROM finca_sync.received_change WHERE change_id = incoming_change) THEN
        RETURN true;
    END IF;

    -- Conservar el evento recibido permite retransmitirlo a un tercer nodo
    -- cuando el emisor original no está disponible. change_id evita bucles.
    INSERT INTO finca_sync.change_log
        (change_id, origin_node_id, table_name, record_id, operation, row_data, changed_at)
    VALUES (incoming_change, incoming_node, incoming_table, incoming_record,
            incoming_operation, payload->'row_data', incoming_time)
    ON CONFLICT (change_id) DO NOTHING;

    SELECT * INTO current_version
    FROM finca_sync.record_version
    WHERE table_name = incoming_table AND record_id = incoming_record
    FOR UPDATE;

    incoming_wins := NOT FOUND
        OR incoming_time > current_version.changed_at
        OR (incoming_time = current_version.changed_at
            AND incoming_node::text > current_version.origin_node_id::text);

    IF NOT incoming_wins THEN
        INSERT INTO finca_sync.conflict
            (change_id, table_name, record_id, incoming_data, local_changed_at,
             local_origin_node_id, resolution)
        VALUES (incoming_change, incoming_table, incoming_record, payload->'row_data',
                current_version.changed_at, current_version.origin_node_id, 'local-wins');
        INSERT INTO finca_sync.received_change (change_id) VALUES (incoming_change);
        RETURN true;
    END IF;

    PERFORM set_config('finca_sync.applying', 'on', true);
    IF incoming_operation = 'DELETE' THEN
        EXECUTE format('DELETE FROM public.%I WHERE id = $1', incoming_table)
        USING incoming_record;
    ELSE
        IF jsonb_typeof(payload->'row_data') <> 'object' THEN
            RAISE EXCEPTION 'Un UPSERT requiere row_data para %.%', incoming_table, incoming_record;
        END IF;

        SELECT string_agg(format('%I', a.attname), ', ' ORDER BY a.attnum),
               string_agg(format('%1$I = EXCLUDED.%1$I', a.attname), ', ' ORDER BY a.attnum)
                   FILTER (WHERE a.attname <> 'id')
        INTO insert_columns, update_columns
        FROM pg_attribute a
        WHERE a.attrelid = table_ref
          AND a.attnum > 0
          AND NOT a.attisdropped
          AND a.attgenerated = ''
          AND a.attidentity = '';

        EXECUTE format(
            'INSERT INTO public.%1$I (%2$s) '
            || 'SELECT %2$s FROM jsonb_populate_record(NULL::public.%1$I, $1) '
            || 'ON CONFLICT (id) DO UPDATE SET %3$s',
            incoming_table, insert_columns, update_columns
        ) USING payload->'row_data';
    END IF;

    INSERT INTO finca_sync.record_version
        (table_name, record_id, changed_at, origin_node_id, deleted)
    VALUES (incoming_table, incoming_record, incoming_time, incoming_node,
            incoming_operation = 'DELETE')
    ON CONFLICT (table_name, record_id) DO UPDATE
        SET changed_at = EXCLUDED.changed_at,
            origin_node_id = EXCLUDED.origin_node_id,
            deleted = EXCLUDED.deleted;
    INSERT INTO finca_sync.received_change (change_id) VALUES (incoming_change);
    RETURN true;
END;
$$;

SELECT finca_sync.register_triggers();
