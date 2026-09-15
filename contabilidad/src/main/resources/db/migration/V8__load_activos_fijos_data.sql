-- V8__load_activos_fijos_data.sql
-- Carga de datos de Activos Fijos Tangibles desde Excel UBPC Lorenzo Dalis
-- Generado automáticamente

-- Variables para IDs de grupos y finca
DO $$
DECLARE
    v_finca_id UUID;
    v_grupo_01 UUID;
    v_grupo_02 UUID;
    v_grupo_04 UUID;
    v_grupo_05 UUID;
    v_grupo_07 UUID;
    v_grupo_08 UUID;
    v_grupo_12 UUID;
    v_grupo_13 UUID;
BEGIN
    -- Get first finca
    SELECT id INTO v_finca_id FROM finca LIMIT 1;
    
    -- Get group IDs
    SELECT id INTO v_grupo_01 FROM grupo_activo_fijo WHERE codigo = '01';
    SELECT id INTO v_grupo_02 FROM grupo_activo_fijo WHERE codigo = '02';
    SELECT id INTO v_grupo_04 FROM grupo_activo_fijo WHERE codigo = '04';
    SELECT id INTO v_grupo_05 FROM grupo_activo_fijo WHERE codigo = '05';
    SELECT id INTO v_grupo_07 FROM grupo_activo_fijo WHERE codigo = '07';
    SELECT id INTO v_grupo_08 FROM grupo_activo_fijo WHERE codigo = '08';
    SELECT id INTO v_grupo_12 FROM grupo_activo_fijo WHERE codigo = '12';
    SELECT id INTO v_grupo_13 FROM grupo_activo_fijo WHERE codigo = '13';

    -- ===== ACTIVOS FIJOS TANGIBLES (Grupos 01-07) =====
    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f39a8ace-d3eb-473d-8957-b7bff7c04d10', '01-0008', 'Edificio Comedor', v_grupo_01, v_finca_id, 6061.14, 6061.14, 0.0, 18.0, 208772.06, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('9d3d0238-0f12-4677-9e81-89c00bacf85e', '01-002', 'Edificio Oficina', v_grupo_01, v_finca_id, 10000.0, 10000.0, 0.0, 18.0, 240411.1, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('3409f212-cd68-436b-a4cd-652f1c3865ef', '01-005', 'Baño Oficina', v_grupo_01, v_finca_id, 549.99, 150.0, 0.0, 18.0, 35821.13, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('da66f622-b476-4f2e-8a48-71b179aa77fe', '01-007', 'Almacen # 2', v_grupo_01, v_finca_id, 2000.0, 2000.0, 0.0, 18.0, 60323.18, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('2a62b9e2-940a-4155-9ac2-8cf1515d3b97', '01-0014', 'Cuarto Herbicida', v_grupo_01, v_finca_id, 1116.41, 1116.41, 0.0, 18.0, 38624.1, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f3d3bb61-40c4-4a8a-974b-6a01f719112e', '01-0011', 'Almacen # 1', v_grupo_01, v_finca_id, 2000.0, 2000.0, 0.0, 18.0, 60323.18, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('5275f73c-e82e-46fb-af03-770001c9bfa5', '01-0006', 'Cocina', v_grupo_01, v_finca_id, 900.0, 900.0, 0.0, 18.0, 28515.94, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f3ebe166-57bc-4246-9ff4-4c719cf7bfa7', '01-0019', 'Pista Combustible', v_grupo_01, v_finca_id, 4571.14, 4571.14, 0.0, 18.0, 179880.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('4cb06843-1232-40b5-8d69-36c140f0e486', '01-0017', 'Nave Taller', v_grupo_01, v_finca_id, 12000.0, 12000.0, 0.0, 18.0, 81312.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('35c3e055-70c3-424d-8ce3-e976a2c907ae', '01-0018', 'Casita CVP', v_grupo_01, v_finca_id, 500.0, 500.0, 0.0, 18.0, 9115.23, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('79fd0262-851f-45bd-88a6-89c57644be56', '01-0020', 'Modulo pecuario', v_grupo_01, v_finca_id, 181901.69, 118420.41, 63481.28, NULL, 101623.15, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('cf2a5fe0-7d78-4430-8040-0e6efe8f4231', '02-OO22', 'Punto de venta', v_grupo_02, v_finca_id, 290000.0, 1563.25, 288436.75, NULL, 78310.93, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('75310e65-25e8-4664-a68b-3f04318d960e', '01-0021', 'Minindustria', v_grupo_01, v_finca_id, 87044.29, 3037.0, 84007.29, NULL, 245040.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('49af3972-713a-49a9-ab98-77296527ae53', '02-2026', 'Tanque Diesel Pista', v_grupo_02, v_finca_id, 10762.05, 10762.05, 0.0, 20.0, 6888.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('b3c13243-e23f-4aca-ba12-d8166140a892', '02-0008', 'POZO   VAQUERIA', v_grupo_02, v_finca_id, 200.0, 160.0, 40.0, 10.0, 40.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('14eb09be-47a4-4bf9-9266-5519399bb42a', '02-0007', 'POZO PLAN VIANDA', v_grupo_02, v_finca_id, 200.0, 160.0, 40.0, 10.0, 40.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('efab48fd-f8c9-47e1-9452-ffd4aef1d4a7', '02-0002', 'POZO HUERTO', v_grupo_02, v_finca_id, 2818.56, 2256.0, 562.56, 10.0, 562.56, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('9620a6d7-5999-4e1d-ab98-f6e043b85ba1', '04-0007', 'Arado ADI-3', v_grupo_04, v_finca_id, 1616.13, 120.45, 1495.68, 10.0, 1495.68, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('416a9b15-c484-4bfd-a9bf-a60ebe1c3953', '04-02', 'Surcador 4', v_grupo_04, v_finca_id, 210.0, 25.0, 185.0, 10.0, 185.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('3caeac3b-c53f-44ea-8b45-60c8a483df55', '04-1117', 'Surcador Romy', v_grupo_04, v_finca_id, 210.0, 25.0, 185.0, 10.0, 185.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f8754415-ecff-4b25-a6b4-570a13effffe', '04-3379', 'Surcador Sencillo', v_grupo_04, v_finca_id, 227.8, 27.8, 200.0, 10.0, 200.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('b3c7b299-d26d-4026-96e3-28f3ec1c3743', '04-3388', 'Surcador tradicional', v_grupo_04, v_finca_id, 227.8, 27.8, 200.0, 10.0, 200.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('58b39715-0a17-4bb2-ada9-d38e66f87ee7', '04-1936', 'FC-10', v_grupo_04, v_finca_id, 1348.51, 48.51, 1300.0, 10.0, 1300.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('44b3a63c-2bd2-4598-af05-fe7a0c2f7e6d', '04-1938', 'F-350 Fertilizador', v_grupo_04, v_finca_id, 1728.84, 50.2, 1678.6399999999999, 10.0, 1678.67, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('964a7998-dd6c-4d0b-9c0a-79fb1c826a42', '04-2621', 'Subsulador SD-240', v_grupo_04, v_finca_id, 1246.2, 125.0, 1121.2, 10.0, 1121.2, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('3b0346d7-bf1e-4683-8586-8587aa592c11', '04-2622', 'Subsulador SD-240', v_grupo_04, v_finca_id, 1246.2, 125.0, 1121.2, 10.0, 1121.2, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('ae7bdca2-8523-4155-a636-e18820d1451c', '04-0403', 'Chapiadora', v_grupo_04, v_finca_id, 868.03, 75.2, 792.8299999999999, 10.0, 792.83, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('d9556156-01e3-4ee7-b3e5-24b678fc2d8c', '04-2131', 'Asperjadora', v_grupo_04, v_finca_id, 4186.19, 80.0, 4106.19, 10.0, 4106.19, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('72911b7c-6cad-4668-9df4-0372f5c9104a', '04-1056', 'Carreta Pipa', v_grupo_04, v_finca_id, 4575.9, 4575.9, 0.0, 10.0, 457.59, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('fa4f4166-0ade-41c6-ac1b-00f6530dff64', '04-1602', 'Carreta Pipa', v_grupo_04, v_finca_id, 3043.35, 1043.35, 2000.0, 10.0, 2000.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('abbc525c-bb11-4934-aa47-6d0f0ea1dd35', '04-2132', 'Cultivadora de Buey', v_grupo_04, v_finca_id, 650.0, 100.0, 550.0, 10.0, 550.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('2d016a69-fa66-42e2-bc00-3e7be6bd24d3', '04-2133', 'Cultivadora de Buey', v_grupo_04, v_finca_id, 650.0, 100.0, 550.0, 10.0, 550.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('9aa96969-1f4f-4022-a27a-b27038a0fc0b', '04-213', 'Cultivadora de Buey', v_grupo_04, v_finca_id, 650.0, 100.0, 550.0, 10.0, 550.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('52a9a397-14f8-40b9-bcda-c895265f43f9', '04-2139', 'Cultivadora de Buey', v_grupo_04, v_finca_id, 500.0, 100.0, 400.0, 10.0, 400.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('b88ed713-4fc7-4bcc-aa8f-8ffdf3261513', '04-2138', 'Arado de Buey', v_grupo_04, v_finca_id, 500.0, 100.0, 400.0, 10.0, 400.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('1187ad68-0a7e-4f71-be1a-eb17c5c4f0d1', '04-2135', 'Carretón de Buey', v_grupo_04, v_finca_id, 1000.0, 100.0, 900.0, 10.0, 900.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('0a62c5f2-f2ba-4793-8b6b-8198a287b3e6', '04-2136', 'Carretón de Buey', v_grupo_04, v_finca_id, 1000.0, 100.0, 900.0, 10.0, 900.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('682ce9fc-91d9-4ab3-a8c3-de2890e8b099', '04-2137', 'Carretón de Buey', v_grupo_04, v_finca_id, 2500.0, 100.0, 2400.0, 10.0, 2400.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('d15f55a5-9ad5-44a6-9f84-2a06854ee50f', '04-1503', 'Tractor Yun-6', v_grupo_04, v_finca_id, 23264.44, 21264.44, 2000.0, 10.0, 2000.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('2c3efef9-88ee-4f6b-a943-605302271359', '04-1602', 'Tractor Yun-6', v_grupo_04, v_finca_id, 18063.74, 16063.74, 2000.0000000000018, 10.0, 2000.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('40959105-bd4f-462b-90cc-d20cb2536dbd', '04-0008', 'Tractor Yun-6', v_grupo_04, v_finca_id, 23264.44, 20264.44, 3000.0, 10.0, 3000.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('4441c7b1-fde0-4504-9d6d-ccd642367f05', '04-0009', 'Tractor MTZ-80', v_grupo_04, v_finca_id, 14576.73, 14576.73, 0.0, 10.0, 1457.67, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('439af6ff-ad52-46d8-bd1c-26b973f5c81c', '04-1010', 'Tractor MTZ-80', v_grupo_04, v_finca_id, 4576.73, 2652.35, 1924.3799999999997, 10.0, 1924.38, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('9eca9b84-7f10-4cd9-a7c1-9bb7195b093d', '04-1039', 'Carreta', v_grupo_04, v_finca_id, 13942.24, 13942.24, 0.0, 10.0, 1394.22, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f7cbd7c7-9996-438e-bc63-e65f65767ae1', '04-1037', 'Carreta', v_grupo_04, v_finca_id, 12984.64, 7542.0, 5442.639999999999, 10.0, 5442.64, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('8e93d6f0-96bb-4abb-862d-2ebf842af99a', '04-1049', 'Carreta', v_grupo_04, v_finca_id, 13362.65, 11245.2, 2117.449999999999, 10.0, 2117.45, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('b6cd91fd-f2d8-45b8-b316-2061d8b495ad', '04-2427', 'Carreta Cosina', v_grupo_04, v_finca_id, 16984.64, 10984.64, 6000.0, 10.0, 6000.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('39157aba-6a5c-438e-b30f-6656d20fe793', '04-99246', 'KTP-2 Super', v_grupo_04, v_finca_id, 45275.5, 33528.03, 11747.470000000001, 10.0, 11747.47, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('c5266331-372f-4349-ab2c-955d630092c4', '04-2028', 'Carretón T. Leche Buey', v_grupo_04, v_finca_id, 1000.0, 85.2, 914.8, 10.0, 914.8, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('9e44a9b2-6c7a-4f22-b221-b47deddb2350', '04-2029', 'Carretón Tiro personal', v_grupo_04, v_finca_id, 100.0, 100.0, 0.0, 10.0, 10.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('6ff5043e-8d76-452e-a4ef-c8f1731a6c35', '04-2123', 'Soldador Electrico', v_grupo_04, v_finca_id, 190.0, 190.0, 0.0, 10.0, 19.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('1ccaa41d-8fe4-468e-b6be-2c1e9229b559', '04-2129', 'Moto Soldador 650', v_grupo_04, v_finca_id, 1573.3, 245.25, 1328.05, 10.0, 1328.05, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('821b23ee-1e34-4c29-9300-f350d4a647e2', '04-0004', 'Grada Multiple', v_grupo_04, v_finca_id, 25641.9, 245.2, 25396.7, 10.0, 25396.7, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('8df0af12-640f-4e46-b3a8-b32aa67ea2cd', '04-1925', 'Novia 1.5', v_grupo_04, v_finca_id, 4088.87, 240.25, 3848.62, 10.0, 3848.62, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('2a3ccd1d-1452-4d98-9a20-2d62b4155b00', '04-2142', 'GRADA 2500 LB', v_grupo_04, v_finca_id, 32000.0, 19000.0, 13000.0, 10.0, 13000.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('51b96417-731a-4d5e-ac3e-89e674473025', '04-2143', 'Arado de Buey', v_grupo_04, v_finca_id, 1000.0, 300.0, 700.0, 10.0, 700.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f90968d5-9749-4288-88f6-0579324d07d1', '04--4102', 'guarapera', v_grupo_04, v_finca_id, 12000.0, 65.0, 11935.0, 10.0, 11935.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('40fb6a55-cf4e-4332-8f73-5f63d5020da6', '04-2141', 'Grada Multiple', v_grupo_04, v_finca_id, 28641.9, 20641.9, 8000.0, 10.0, 8000.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('3b67546d-7992-499b-9fa4-a508f4e97ccc', '05--0001', 'computadora', v_grupo_05, v_finca_id, 566.16, 42.16, 524.0, 20.0, 524.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('8e94185f-1ee8-4397-b32f-8846e349d675', '05--4100', 'computadora', v_grupo_05, v_finca_id, 580.87, 200.0, 380.87, 20.0, 380.87, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('15da063f-8385-41a3-a373-fbb5c2b8b0c3', '05--4101', 'apc', v_grupo_05, v_finca_id, 357.56, 225.56, 132.0, 20.0, 132.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('e90c07b3-9fc7-4e5e-8417-807ac49686ae', '05-0009', 'bacup', v_grupo_05, v_finca_id, 46000.0, 2.0, 45998.0, 20.0, 45998.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('6a94edae-de0e-4943-8579-7d4662cda2f6', '05-9971', 'bascula', v_grupo_05, v_finca_id, 500.0, 300.0, 200.0, 20.0, 200.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('75df4413-154c-498d-bd31-080fad6811f1', '05-0010', 'teclado-mouse', v_grupo_05, v_finca_id, 8256.0, 13.0, 8243.0, 20.0, 8423.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('cf35ba4e-ad1a-439b-ba5f-e915435d4168', '05-0011', 'teclado-mouse', v_grupo_05, v_finca_id, 8256.0, 13.0, 8243.0, 20.0, 8243.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('3d20e454-18b9-4fa1-bf36-c2fa4c8ad310', '05--0004', 'laptop', v_grupo_05, v_finca_id, 13000.0, 12.3, 12987.7, 20.0, 12987.7, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('ee344fa2-a998-4f65-943e-f8396c00a722', '05--0005', 'laptop', v_grupo_05, v_finca_id, 13000.0, 12.3, 12987.7, 20.0, 12987.7, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('3ae1c2a5-696d-4ef5-ad5a-becd22b47ffa', '07--3356', 'Buro de madera', v_grupo_07, v_finca_id, 497.0, 250.0, 247.0, 10.0, 247.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('88126b6a-22cc-4e98-8fee-19a7eff3e8ce', '07--3064', 'Estante de Madera', v_grupo_07, v_finca_id, 120.0, 120.0, 0.0, 10.0, 12.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('4f9df0e3-485b-4c39-a141-91330a1330ef', '07--2007', 'Estante de Chapa', v_grupo_07, v_finca_id, 690.0, 600.0, 90.0, 10.0, 90.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('821f6800-3d84-434e-8e25-5fafd7e9a40d', '07--3360', 'Estante de Barra Currugada', v_grupo_07, v_finca_id, 690.0, 600.0, 90.0, 10.0, 90.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('594fd2b9-01ee-48e5-a45a-848039a491f8', '07--2006', 'Estante de Chapa', v_grupo_07, v_finca_id, 690.0, 600.0, 90.0, 10.0, 90.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('02b8818d-d8cf-410d-a9ea-8b486631071c', '07--2013', 'Estante de Chapa', v_grupo_07, v_finca_id, 490.0, 300.0, 190.0, 10.0, 190.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('114bfb4e-b5e7-47ab-8b2a-c1618d4202f9', '07--3366', 'Mesa de Chapa', v_grupo_07, v_finca_id, 727.0, 400.0, 327.0, 10.0, 327.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('12384eb5-b1fe-4e5d-850c-037c0046de34', '07--3367', 'Mesa de Chapa', v_grupo_07, v_finca_id, 727.0, 400.0, 327.0, 10.0, 327.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('a71fa57d-c485-4904-be9b-2002b72173ce', '07--3368', 'Mesa de Chapa', v_grupo_07, v_finca_id, 727.0, 400.0, 327.0, 10.0, 327.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('fe8bc566-fcbb-4389-83cc-e0a98ba7435a', '07--2011', 'Estante de Angular', v_grupo_07, v_finca_id, 690.0, 450.0, 240.0, 10.0, 240.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f4100db5-702a-446c-84c7-776652c70df6', '07--3363', 'Estante de Angular', v_grupo_07, v_finca_id, 690.0, 450.0, 240.0, 10.0, 240.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('0dd7d714-1128-49f2-a625-fb87194c696a', '07--2008', 'Estante de Angular', v_grupo_07, v_finca_id, 690.0, 450.0, 240.0, 10.0, 240.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('451205ce-8be3-4d71-8718-1e686a7a9f04', '07--2010', 'Estante de Angular', v_grupo_07, v_finca_id, 690.0, 450.0, 240.0, 10.0, 240.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('3f44ce56-9019-4fa5-904a-6a1f7b986faa', '07--3365', 'Taquilla de chapa Soldadura', v_grupo_07, v_finca_id, 131.0, 131.0, 0.0, 10.0, 13.1, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('c2700ff8-6290-427a-9f84-76bec34b83e9', '07--8064', 'Mesa angular', v_grupo_07, v_finca_id, 40.0, 40.0, 0.0, 10.0, 4.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('ad06d8a1-8556-45e5-9223-88c3b385bc1f', '07--8061', 'Mesa de pesa de inyectores', v_grupo_07, v_finca_id, 40.0, 40.0, 0.0, 10.0, 4.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('8219d4fb-567e-4b09-9063-07df8264d9b9', '07--2012', 'Estante de Angular', v_grupo_07, v_finca_id, 90.0, 90.0, 0.0, 10.0, 9.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('69d4ea22-f7c5-44f9-a8fe-a5e49f20f03b', '07--8062', 'Mesa de chapa y tubo', v_grupo_07, v_finca_id, 40.0, 40.0, 0.0, 10.0, 4.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('d4a56a48-fa27-41d2-94c8-393e510ef831', '07--2009', 'Estante de chapa', v_grupo_07, v_finca_id, 120.0, 120.0, 0.0, 10.0, 12.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('b70ea6c1-0d76-4db7-a826-732e22d97786', '07--3352', 'estante de madera', v_grupo_07, v_finca_id, 180.0, 180.0, 0.0, 10.0, 18.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('c35d4edc-be0f-4a19-ad08-6c88f53b8f22', '07--2036', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 200.0, 0.0, 10.0, 20.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('cd05f637-3df2-433a-aef3-0c0b50d82c32', '07--2059', 'Silla de soisa', v_grupo_07, v_finca_id, 100.0, 100.0, 0.0, 10.0, 10.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('4aa93e74-8599-4f79-8f64-6e850691e785', '07--0097', 'Buro de madera', v_grupo_07, v_finca_id, 497.0, 197.0, 300.0, 10.0, 300.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('d587c839-59e8-4142-b2fe-c256ce8416f5', '07--2020', 'Silla de soisa', v_grupo_07, v_finca_id, 100.0, 90.0, 10.0, 10.0, 10.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('aa52b920-9506-4e99-bcb8-7f3a875e93bd', '07--001', 'Banqueta', v_grupo_07, v_finca_id, 493.0, 150.0, 343.0, 10.0, 343.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('ff9f1c9b-f900-4020-afd5-7a51598198d1', '07--002', 'Banqueta', v_grupo_07, v_finca_id, 493.0, 150.0, 343.0, 10.0, 343.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('a2f191f9-2f83-4e00-b9a8-b7d7afa12869', '07--003', 'Banqueta', v_grupo_07, v_finca_id, 493.0, 150.0, 343.0, 10.0, 343.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('790e7239-74f8-4dc7-9c8a-1f90e9085268', '07--004', 'Banqueta', v_grupo_07, v_finca_id, 493.0, 150.0, 343.0, 10.0, 343.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('96ba3659-ef11-41ae-ac9d-12f775d3d4de', '07--005', 'Banqueta', v_grupo_07, v_finca_id, 493.0, 150.0, 343.0, 10.0, 343.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('a9880ae8-808c-4f27-a6a8-480c246eb235', '07--006', 'Banqueta', v_grupo_07, v_finca_id, 493.0, 150.0, 343.0, 10.0, 343.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('dfb9f533-d67f-4e34-a9ad-a6902b5584bb', '07--007', 'Banqueta', v_grupo_07, v_finca_id, 493.0, 150.0, 343.0, 10.0, 343.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('e1d50e25-2b3f-45d1-9a39-e2ac3d6a604e', '07--008', 'Banqueta', v_grupo_07, v_finca_id, 493.0, 150.0, 343.0, 10.0, 343.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('1220ca3a-f833-411d-b01a-eef3f873a09b', '07--009', 'Banqueta', v_grupo_07, v_finca_id, 493.0, 150.0, 343.0, 10.0, 343.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f41bf549-223c-4a67-8d61-37b064885951', '07--8111', 'Banquetilla de madera', v_grupo_07, v_finca_id, 300.0, 250.0, 50.0, 10.0, 50.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('cd707f5b-c7bf-4e57-b10e-6906ed3d2022', '07--8075', 'Banquetilla de madera', v_grupo_07, v_finca_id, 300.0, 250.0, 50.0, 10.0, 50.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f29cb184-b7c3-4fcd-be6a-8b037c5b0539', '07--8079', 'Aparatador de madera', v_grupo_07, v_finca_id, 104.56, 104.56, 0.0, 10.456, 10.46, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('0d1434af-d6df-493d-8809-dca1cb9a8c98', '07--8074', 'Banquetilla de madera', v_grupo_07, v_finca_id, 300.0, 50.0, 250.0, 10.0, 250.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('3a8a9897-1aec-4441-86cb-9ffac40610a8', '07--8072', 'Banquetilla de madera', v_grupo_07, v_finca_id, 300.0, 50.0, 250.0, 10.0, 250.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('892ce492-7fbc-4eb1-aa25-1c6eff6e6e50', '07--8073', 'Banquetilla de madera', v_grupo_07, v_finca_id, 300.0, 50.0, 250.0, 10.0, 250.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f14171d2-d007-4f66-88a5-0983f1a3bb14', '07--8076', 'Banquetilla de madera', v_grupo_07, v_finca_id, 300.0, 50.0, 250.0, 10.0, 250.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('c5a826b2-87dd-41c3-864f-c4a6567a187e', '07--8078', 'Mostrador e madera', v_grupo_07, v_finca_id, 520.0, 400.0, 120.0, 10.0, 120.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('b2275af0-6116-4246-84e5-82eb09bc3022', '07--8102', 'Mesa de madera', v_grupo_07, v_finca_id, 920.0, 470.0, 450.0, 10.0, 450.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('83f309f4-b3df-4b6d-b89c-4a24bf76559a', '07--8103', 'Mesa de madera', v_grupo_07, v_finca_id, 920.0, 470.0, 450.0, 10.0, 450.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('43c3936f-4a6a-45c8-93f5-d5c6e32685c3', '07--8104', 'Mesa de madera', v_grupo_07, v_finca_id, 920.0, 470.0, 450.0, 10.0, 450.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('c5cdc802-463f-482e-ba76-99d92d78fc7f', '07--8105', 'Mesa de madera', v_grupo_07, v_finca_id, 920.0, 470.0, 450.0, 10.0, 450.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('80e0260c-0417-4db2-8cbd-1a09922b314f', '07--8090', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f95cb6e4-4f32-4232-a90c-b59b6c0e5be7', '07--8082', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('be55456e-9ae7-4053-9475-c1b61be5cb72', '07--8084', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('2009acac-4229-4b9b-a03f-ed90b6f0bc71', '07--8083', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('5fbc2da5-bc05-4a25-a5cd-fad508d1c555', '07--8086', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('cd58f9a0-6833-4108-8317-3ece5728f7cd', '7--8098', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('1d97bbc9-cdcb-428d-a76f-7e761d24c495', '7--8089', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('ae0236b1-d0f7-41df-b4b7-2686b88cafb3', '7--8094', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('ece7bae0-8f73-4631-aadf-135aea02abe6', '7--8092', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('7ce29095-651c-4cb0-90f8-6d95b5756ff7', '7--8099', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('a52af2da-1422-43d3-8692-238d176fda68', '7--8096', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('a98beaab-f4b9-490c-92da-b81e4fdeac88', '7--8101', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('79ff6f6e-d79e-4009-87d2-483b46055248', '7--8087', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('9305d193-b31b-46d1-bfaa-464a74ae97f6', '7--8088', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('4062dc8f-e3ae-4fc7-961e-f62f271e8344', '7--8091', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('1b0c8ff0-f9da-4485-92c1-2e61029aeed4', '7--8100', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 60.0, 140.0, 10.0, 140.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('1afedaf4-f951-4efe-80f0-5fb1297e9fb8', '7--2022', 'Banquetilla de madera', v_grupo_07, v_finca_id, 300.0, 150.0, 150.0, 10.0, 150.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('5c2743c9-2d02-46a8-a7c3-e9dc00f6ff6f', '7--9953', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 100.0, 100.0, 10.0, 100.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('d74910cb-42e3-4d20-a474-d04fb314eb55', '7--2022', 'Buro de madera', v_grupo_07, v_finca_id, 497.0, 300.0, 197.0, 10.0, 197.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('98ccdd0f-46f9-48bd-91ad-cb90529c63f0', '7--9973', 'Bancada', v_grupo_07, v_finca_id, 97.0, 45.0, 52.0, 10.0, 52.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('dc53392e-4a96-478f-9748-1f7fd5b62e4f', '7--9972', 'bancada', v_grupo_07, v_finca_id, 97.0, 45.0, 52.0, 10.0, 52.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('7926c775-0ffe-4671-ae45-0ecbdaaf8b01', '7--2082', 'Silla soisa', v_grupo_07, v_finca_id, 10.0, 10.0, 0.0, 10.0, 1.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('85067191-1d69-4e4a-b860-588531a293dd', '7--22084', 'Silla soisa', v_grupo_07, v_finca_id, 10.0, 10.0, 0.0, 10.0, 1.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('4da947b2-6526-4754-9f34-20fb9b69395e', '7--5366', 'Archivo de metal', v_grupo_07, v_finca_id, 144.28, 140.28, 4.0, 10.0, 4.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('846d1feb-6c08-460e-abd2-1ee519b993c2', '7--9913', 'Taburete de madera', v_grupo_07, v_finca_id, 50.0, 50.0, 0.0, 10.0, 5.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('5adf9206-5f54-4f4e-8b56-6ac15ee882e8', '7--9928', 'Taburete de madera', v_grupo_07, v_finca_id, 50.0, 50.0, 0.0, 10.0, 5.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('de3389f7-0de5-49e8-b979-8aade8e1363b', '7--0697', 'Buro de madera', v_grupo_07, v_finca_id, 497.0, 250.0, 247.0, 10.0, 247.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('8eee940d-38b5-4552-95e8-26bf5754deaf', '7--8106', 'Mesa madera ovalada', v_grupo_07, v_finca_id, 920.0, 300.0, 620.0, 10.0, 620.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('7a5cb106-9b73-41c6-b3df-27a44c1204ee', '07-4099', 'nevera grande', v_grupo_07, v_finca_id, 5000.0, 4806.28, 193.72000000000025, 10.0, 193.72, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('0bdfbdaf-1555-4e2d-b357-d2cfaf480d3c', '7--9970', 'Buro de madera', v_grupo_07, v_finca_id, 497.0, 300.0, 197.0, 10.0, 197.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('7ce5fd1e-fed3-4311-877a-4f82e2c67e09', '7--2026', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 25.0, 175.0, 10.0, 175.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('176ded3e-8f4e-4d74-9f06-aa84671a693d', '7--2021', 'Silla de madera', v_grupo_07, v_finca_id, 200.0, 25.0, 175.0, 10.0, 175.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('2456f420-a504-471c-b675-13dd0852d57d', '7--2023', 'Silla de Hierro', v_grupo_07, v_finca_id, 200.0, 25.0, 175.0, 10.0, 175.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('506bb389-f8a3-48b5-944e-8796af166af3', '7--9974', 'Buro de madera', v_grupo_07, v_finca_id, 497.0, 300.0, 197.0, 10.0, 197.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('5b5f8645-6594-4671-984a-135fc478b8c3', '7--3031', 'estante de madera', v_grupo_07, v_finca_id, 35.0, 35.0, 0.0, 10.0, 3.5, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('d6e667e9-c443-4c16-8413-4430ee4ce793', '7--3354', 'estante de hierro', v_grupo_07, v_finca_id, 250.0, 50.0, 200.0, 10.0, 200.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('5dc4441e-3472-4fdb-9f8f-ae88b5339824', '7--3355', 'estante de hierro', v_grupo_07, v_finca_id, 250.0, 50.0, 200.0, 10.0, 200.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('ac310191-9b2e-4cf5-887c-cfc2b6b3cbc6', '7--3356', 'estante de hierro', v_grupo_07, v_finca_id, 250.0, 50.0, 200.0, 10.0, 200.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('0a67bd98-fb59-43ae-bb8e-0f1c5587817c', '7--2057', 'estante de hierro', v_grupo_07, v_finca_id, 250.0, 50.0, 200.0, 10.0, 200.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('d7dcdb9f-d773-4e08-b1a4-b8c20999d1bd', '7--3357', 'estante de hierro', v_grupo_07, v_finca_id, 250.0, 50.0, 200.0, 10.0, 200.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('1d739807-0634-4d9d-a22f-92b805ddea12', '7--9955', 'Buro', v_grupo_07, v_finca_id, 131.77, 100.0, 31.77000000000001, 10.0, 31.77, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('28b90d31-27cf-47ed-a0d6-17bd99742255', '7--2053', 'Silla soisa', v_grupo_07, v_finca_id, 200.0, 50.0, 150.0, 10.0, 150.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('d32fb606-bacf-4dba-8a6b-8bb7b094fb8b', '7--2055', 'Silla soisa', v_grupo_07, v_finca_id, 200.0, 50.0, 150.0, 10.0, 150.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('bc82be74-5fcf-4f27-95bf-801497be2122', '7--3359', 'Buro de madera', v_grupo_07, v_finca_id, 365.0, 65.0, 300.0, 10.0, 300.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('75f3d945-b9f4-44ac-93b5-2efadb3ea0c3', '7--2084', 'silla', v_grupo_07, v_finca_id, 200.0, 50.0, 150.0, 10.0, 150.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f5d373d4-3932-4a1f-b88e-a48be557927b', '7--2050', 'Estante de chapa', v_grupo_07, v_finca_id, 300.0, 50.0, 250.0, 10.0, 250.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('86c8c6f5-2962-4928-bb37-621c79a5d936', '7--2054', 'estante de angular', v_grupo_07, v_finca_id, 300.0, 50.0, 250.0, 10.0, 250.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('4edc8e87-5c2e-4b67-95ff-ebff5627bfd4', '7--2056', 'Estante de Angular', v_grupo_07, v_finca_id, 300.0, 50.0, 250.0, 10.0, 250.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('f331fe39-0ce0-40ce-8fbc-fac8746adbce', '07-0003', 'Aire Acondicionado', v_grupo_07, v_finca_id, 18000.0, 62.0, 17938.0, 10.0, 17938.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('d0825b38-11dd-41e3-9f0f-a361b79f66b8', '07-0006', 'ventilador', v_grupo_07, v_finca_id, 10350.53, 10.53, 10340.0, 10.0, 10340.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('6fb304cb-e25c-4558-bd67-200826824796', '07--0013', 'ventilador', v_grupo_07, v_finca_id, 13573.2, 30.2, 13543.0, 10.0, 13543.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('9724e29d-fc5e-41f1-9723-31ab778c37e0', '07--0016', 'ventilador', v_grupo_07, v_finca_id, 13573.2, 10.53, 13562.67, 10.0, 13562.67, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('fc823f2b-bfcc-4e52-9db1-3c43315e7d64', '07--014', 'ventilador', v_grupo_07, v_finca_id, 13573.2, 10.53, 13562.67, 10.0, 13562.67, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('81bbd0ad-14b8-497d-88dd-ff1c3a0c9bea', '07--8109', 'ventilador', v_grupo_07, v_finca_id, 147.56, 17.56, 130.0, 10.0, 130.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('83fcd1bc-b2e8-4e43-aa90-12b15e9bc6d5', '07--0014', 'ventilador', v_grupo_07, v_finca_id, 13573.2, 10.53, 13562.67, 10.0, 13562.67, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('2ba49759-6399-4247-96f8-abc45cb6d958', '07-0012', 'ventilador', v_grupo_07, v_finca_id, 13573.2, 10.53, 13562.67, 10.0, 13562.67, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('c4c76416-0bc0-4464-8799-88e55e3cd3dd', '07--8107', 'ventilador', v_grupo_07, v_finca_id, 100.0, 70.0, 30.0, 10.0, 30.0, 'ECTE', true, NOW());

    INSERT INTO activo_fijo_tangible (id, numero_inventario, descripcion, grupo_id, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, estado_tecnico_porcentaje, valor_tasacion, destino, activo, created_at)
    VALUES ('87f23202-dfd8-4b20-b9f2-452bf4ec1cb9', '07--8110', 'ventilador', v_grupo_07, v_finca_id, 147.56, 135.56, 12.0, 10.0, 12.0, 'ECTE', true, NOW());

    -- ===== ACTIVOS ANIMALES (Grupo 08) =====
    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('aba1d087-278e-4862-9522-e4789d2017a6', 'T122-20/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('36189590-83af-49ce-ad96-506375cbf28e', 'T122-15/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('035bee50-51cd-45a7-8dac-c789f83b4595', 'T122-21/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('ff810522-9e47-4adc-a3c9-abe866c37846', 'T122-23/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('b8c50db9-6cb0-4039-b5c1-2bfa012dca4d', 'T122-18/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('5ee25030-e5be-45c7-916c-10c47adb6bf2', 'T122-4/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('bb942c05-4954-4986-9f10-a97911e38f34', 'T122-3/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('555f0c6b-9149-408c-a0cc-434017990c88', 'T122-22/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('fda3fcd8-0e83-4360-8fef-fc469f2a43ec', 'T122-10/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('faa95c93-c9e8-42f3-9682-d42e531a6f2c', 'T122-1/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('13cdf296-53e9-4604-93bc-33ce9477b1c3', 'T122-5/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('0a8a518a-ddc1-4075-83d7-5b8b27ad296e', 'T122-13/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('25dcea45-d7f3-4685-b281-cbd275bda5e4', 'T122-12/24', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('d97a53bd-5f8d-4eab-b5d3-691c461c10a1', 'T122-24/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('02e24382-458e-40ae-99e7-1066d1396bcc', 'T122-25/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('77fa1770-09bf-49ca-b71d-ba8d204f9bda', 'T122-2625', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('d7a262d9-36db-4f6b-87ad-d0e6fcfce610', 'T122-27/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('515a7116-c6e5-4ffd-a6be-958a3b13d991', 'T122-28/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('6f4df05c-47a2-4b74-8f5b-894065dbe366', 'T122-29/25', 'TERNERO', 'VACUNO', v_finca_id, 210.0, 0, 0, NULL, 210.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('ee977cdd-3e5b-4274-bc95-75d40a64b1e6', 'T122-10/23', 'ANOJO', 'VACUNO', v_finca_id, 1800.0, 0, 0, NULL, 1800.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('1e71feec-def5-4ca8-8aac-ea1139f26910', 'T122-20/23', 'ANOJO', 'VACUNO', v_finca_id, 1800.0, 0, 0, NULL, 1800.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('71da38bd-2115-4a08-88e5-d570291d8f70', 'T122-7/24', 'ANOJO', 'VACUNO', v_finca_id, 1800.0, 0, 0, NULL, 1800.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('98d3c007-2031-47f5-94e2-bbf1f6fae9c2', 'T122-11/24', 'ANOJO', 'VACUNO', v_finca_id, 1800.0, 0, 0, NULL, 1800.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('3182421b-eeb3-4e06-a960-cf9fc0c704fb', 'T122-6/24', 'ANOJO', 'VACUNO', v_finca_id, 1800.0, 0, 0, NULL, 1800.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('56fbd980-0d62-49fb-827f-088a39943f58', 'T122-8/24', 'ANOJO', 'VACUNO', v_finca_id, 1800.0, 0, 0, NULL, 1800.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('637d1946-6d95-4eb2-bde2-9165a70c144f', 'T122-10/24', 'ANOJO', 'VACUNO', v_finca_id, 1800.0, 0, 0, NULL, 1800.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('3f7585bd-ef67-490d-8a83-1ee25b2d0d68', 'T122-5/24', 'ANOJO', 'VACUNO', v_finca_id, 1800.0, 0, 0, NULL, 1800.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('28c3f006-4216-40fd-b266-a98ef974a8fb', 'T122-9/24', 'ANOJO', 'VACUNO', v_finca_id, 1800.0, 0, 0, NULL, 1800.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('49b9c5ec-b61e-47e1-b8ba-d23d25987119', 'T122-14/24', 'ANOJO', 'VACUNO', v_finca_id, 1800.0, 0, 0, NULL, 1800.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('71095c87-09e3-4026-924d-3fed0c875124', 'T122-5/25', 'ANOJO', 'VACUNO', v_finca_id, 1800.0, 0, 0, NULL, 1800.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('d18f369e-bab4-4460-af94-4eee47c105de', 'T122-2/22', 'TORETE', 'VACUNO', v_finca_id, 14820.0, 0, 0, NULL, 14820.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('9e8a9ab7-5ffb-4e78-8d67-86323f175513', 'T122-4/22', 'TORETE', 'VACUNO', v_finca_id, 14820.0, 0, 0, NULL, 14820.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('edf2bf58-70ce-463f-885f-0febd42169ad', 'T122-12/20', 'TORETE', 'VACUNO', v_finca_id, 14820.0, 0, 0, NULL, 14820.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('27a12edb-5643-4f15-8d00-34bff362e2c8', 'T122-8/21', 'TORETE', 'VACUNO', v_finca_id, 14820.0, 0, 0, NULL, 14820.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('37eba93c-3d11-498a-bf3c-eb282a13b4ef', 'T122-1/22', 'TORETE', 'VACUNO', v_finca_id, 14820.0, 0, 0, NULL, 14820.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('cea56b3b-d724-482d-9fc0-7c07f369140c', 'T122-S/N', 'TORETE', 'VACUNO', v_finca_id, 14820.0, 0, 0, NULL, 14820.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('5d974411-dff0-4877-9448-5dcbca66e203', 'T122-5/23', 'TORETE', 'VACUNO', v_finca_id, 14820.0, 0, 0, NULL, 14820.0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('d4eb0f58-d642-47ca-b537-c67fb3fa6f77', 'T122-738', 'BUEY', 'VACUNO', v_finca_id, 4000.0, 900.0, 3100.0, 4, 3100.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('55a9b676-50d1-4b62-9531-93b07cfc1efb', 'T122-791', 'BUEY', 'VACUNO', v_finca_id, 4000.0, 900.0, 3100.0, 4, 3100.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('083e7529-1f87-40d8-8c7c-0e1cf0453a05', 'T122-41', 'BUEY', 'VACUNO', v_finca_id, 4000.0, 900.0, 3100.0, 4, 3100.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('0ed177d4-ce00-4d4c-8777-70da5ab8179c', 'T122-751', 'BUEY', 'VACUNO', v_finca_id, 4000.0, 900.0, 3100.0, 4, 3100.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('7c983bad-7c99-4187-96bd-a2277d0149d7', 'T122-37', 'BUEY', 'VACUNO', v_finca_id, 4000.0, 900.0, 3100.0, 4, 3100.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('3efb8023-72dd-4522-8b6e-d8895b94a9e1', 'T122-807', 'BUEY', 'VACUNO', v_finca_id, 4000.0, 900.0, 3100.0, 4, 3100.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('f6ad2a38-2f80-474e-84c1-5cc89e43703d', 'T122-51', 'BUEY', 'VACUNO', v_finca_id, 4000.0, 900.0, 3100.0, 4, 3100.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('694c9a89-758b-4ab4-b2b5-ea08653e2091', 'T122-714', 'BUEY', 'VACUNO', v_finca_id, 4000.0, 900.0, 3100.0, 4, 3100.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('ff30956a-6566-4116-916f-f314ba23dbac', 'T122-73', 'BUEY', 'VACUNO', v_finca_id, 4000.0, 900.0, 3100.0, 4, 3100.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('19a74352-1cfa-49ae-b0eb-b4ffc796798d', 'T122-6-21', 'BUEY', 'VACUNO', v_finca_id, 4000.0, 900.0, 3100.0, 4, 3100.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('eb3b46b6-0370-4971-9d43-24805f99eae5', 'T122-52-23', 'BUEY', 'VACUNO', v_finca_id, 4000.0, 900.0, 3100.0, 4, 3100.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('8a75e018-2ade-4a4c-8c45-6593bc2bf801', 'T24 82738-0', 'SEMENTAL', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('b3abca13-350e-4924-bb6d-f8e3f8262dba', 'T122-9/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('3b0077eb-4447-4c2f-a464-b4b076bbfe6e', 'T122-12/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('6200cabe-80bf-4995-9eb0-26eb8dee96ad', 'T122-15/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('24149006-8790-47cf-ac40-0b10dae109ca', 'T122-2/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('88f2b2fd-2cca-4f48-b57e-0266c2c35517', 'T122-14/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('8cfb499b-7831-4cab-88bc-50186c0acece', 'T122-16/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('bbd85237-c56b-4fec-97ef-7999c7a4ae08', 'T122-8/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('f3f64054-afbd-45e2-9db9-3a990bad7daf', 'T122-19/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('ae1d79f5-aba4-4544-b4bc-03d714921556', 'T122-6/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('4acda0e0-a17d-46b5-8847-1b5750c4fd46', 'T122-7/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('c7fa9afe-d07b-4806-9629-a609e4cc3060', 'T122-11/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('28a8f2e8-d424-47b4-8e18-b79b0fc5c6bf', 'T122-17/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('0e8e1c05-4bdb-4630-96f2-d2c9eb921062', 'T122-17/24', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('35277096-0f9a-40da-8091-24c36b303bc6', 'T122-30/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('647c17f8-211f-4012-8970-d2c876e5f63c', 'T122-31/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('7431c1f7-a37e-4161-873b-bcc042b98e46', 'T122-32/25', 'TERNERO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('ee5a5109-45db-4e6d-ab9c-3a1fb82e24b3', 'T122-13/23', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('0255c168-3b5f-40a4-ab6d-dec06ffbbec5', 'T122-7/23', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('37827461-8039-4307-8a84-deaa68a456d7', 'T122-19/23', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('3a86a8a9-30a1-42ff-a09d-b8ee8461e667', 'T122-18/23', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('fc8bd482-28e9-4a5d-a383-15c5dad8d10c', 'T122-1/23', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('737b997b-1d9e-4cab-a45f-1b78ebb6ff1d', 'T122-16/24', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('70e6dbf7-d257-4561-8357-9cbe03248ea2', 'T122-1/23', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('fe09007e-2e35-47ab-afe5-a9741e67d088', 'T122-6/23', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('59aec498-1224-4e91-8f54-981d77786dd6', 'T122-11/23', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('42cd99b5-f52c-4f20-aba3-91626fa5ad0e', 'T122-3/23', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('b36b6614-56c7-440d-b0ff-9527b84d7af4', 'T122-17/23', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('d1139579-fa1d-4748-877e-1384a18ad74b', 'T122-2/24', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('40807c54-abd7-48be-ac15-530e1b1895e7', 'T122-3/24', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('fa8ea571-9e7c-4339-8df1-e2ad15fc34a5', 'T122-4/24', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('0506ef75-e965-445f-96a4-e5b0db7ac0c7', 'T122-2/23', 'ANOJO', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('2d92a075-6aff-47b5-9065-9dd30f020557', 'T122-3/21', 'NOVILLA', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('b3ef38c4-3314-4c26-9499-737e87dea20a', 'T122-32/22', 'NOVILLA', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('9d42a601-4e53-4426-8176-2bb5d874d3c0', 'T122-4/21', 'NOVILLA', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('f636d9cd-cbc0-40c5-a4c5-463a7f61c094', 'T122-2/21', 'NOVILLA', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('d748d616-2855-4da8-b8de-215d5c80648b', 'T122-9/22', 'NOVILLA', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('7ab61e41-b825-4402-ac19-3dec303cb8b8', 'T122-5/21', 'NOVILLA', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('a5711017-7bb2-4c25-9f33-d4c54265f0e9', 'T122-5/23', 'NOVILLA', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('391c85d7-8f3d-460e-8711-fe8d381cf6f0', 'T122-7/23', 'NOVILLA', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('4c8fc8e3-6f91-4f75-a6bf-d384dab72a3f', 'T122-6/22', 'NOVILLA', 'VACUNO', v_finca_id, 0.0, 0, 0.0, NULL, 0.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('f86eca51-469b-4b31-a001-8effaaaa8e58', 'T2A57-13', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 12, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('bb963e16-6777-466f-a695-6a217ce20774', 'T122-734', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 12, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('4ce02a0d-0781-400a-b884-ab7111c6614d', 'T2A57-12', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 13, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('db11736f-169b-4afd-8f4a-951b72e8a9f3', 'T122-4/20', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 5, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('6e97fa0b-eeec-42e8-9ad3-ac66f4002c76', 'T122-11/20', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.19, 2843.81, 5, 2843.81, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('c1062836-a63c-4306-b0e0-221e0a3d0d98', 'T122      15', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('e0e99324-b432-4c5e-8d47-ffdf7e0bc38e', 'T2A57-4-14', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 9, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('c72900ea-dd57-418a-a349-96a92e6a6e52', 'T2A57-2-14', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 9, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('e3bf5a13-4deb-40bd-8490-63c8734235fc', 'T2A57  16', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 9, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('18de56e5-9bab-442e-aa7a-d1f36048384f', 'T122 30-20', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 5, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('3602851c-b728-4248-8adf-5bc9121ecf50', 'T2A57 9-15', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('3103a493-2a27-4581-abc6-a94390c8dd03', 'T2A57  21', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 4, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('dd00e4fb-f63a-4f89-b064-2a771af33764', 'T122  121', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 4, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('f6eeee72-1d4c-4dbe-b69d-057ca783f15d', 'T122  801', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('1d7052b0-40e7-4390-b133-7a233ed8cf49', 'T122  730', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('3c305ee6-04e7-4dae-96b2-11a39fb8b71d', 'T122  623', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 3, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('dbf90c65-da7f-40ba-9fce-a1645d81aa13', 'T122   11', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 14, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('b829a4ff-ae17-4866-b5d6-aba52c262b66', 'T122  1-21', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 4, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('e25d485a-efea-413d-a198-dcfa833ce4b6', 'T122   607', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 18, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('f617215c-e6fc-43ed-93c3-a824b3f3f532', 'T122  722', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 3, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('83cf859f-fce4-459b-9e18-6fa6ae7b10f3', 'T2A57 24-15', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('dd77a8c3-06d8-42fb-a9f6-af25b3d00f39', 'T122 50/20', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 5, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('d0fc70e9-2e40-4ffb-a285-8bb3f5f3a396', 'T122  99', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('8a97772a-3531-4987-bd78-df724a45c95d', 'T2150  24', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 2, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('61dfdd51-b95a-4e9d-8187-4bae0782b000', 'T122  758', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('dd616578-9882-48ee-a760-f7adbdd347e7', 'T122  25', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 5, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('20123195-ac0d-4ca7-9039-5ec85d6eaaf1', 'T122   111', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('d2423f92-b30d-4374-9647-e3e3eabca36e', 'T122 727', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('55616b1f-43a8-4abf-8d78-3cde50552455', 'T122 713', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 12, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('17be3545-afe8-4f58-9800-1284edbc4716', 'T122-788', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 12, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('8911fbfd-83ab-4582-ba68-4e56124e3e23', 'T2A57 39', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 5, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('dff8d721-c22a-40ff-9964-1fd93bf8dbda', 'T122 43/20', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 5, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('66f49803-28b6-4ebf-8b75-e486e4509189', 'T2A57  41', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('39d55b2d-fff1-4bea-91db-b96ae7f20f98', 'T122 19-20', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 5, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('c970b2d5-57dd-4022-a217-e9a2775b78df', 'T122  828', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 12, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('9c931c7f-b29a-4fae-8adc-0e82475329db', 'T122  803', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 12, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('2aa812bb-f603-4985-876d-3963a3f54166', 'T122  825', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('612e91fd-7bb6-45ab-ace8-73afda98ca08', 'T122 55-20', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 5, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('5592c8dc-6966-46d2-a8b0-19ad9829aaf1', 'T122 26-20', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 5, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('d55eafab-6fb0-453f-b0c4-b7a8324da5de', 'T122 15-20', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 5, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('65d7664e-b176-49c3-82fc-065f28ccba23', 'T122 22-20', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 5, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('acd34808-c22b-4686-9e39-63975bf2800a', 'T122 805', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 10, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('dd24ae93-a810-4a1e-a656-8ad28876e337', 'T2A57 20-11', 'VACA', 'VACUNO', v_finca_id, 4100.0, 1256.18, 2843.8199999999997, 15, 2843.8199999999997, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('f31a8fe8-f2ab-435f-950e-738606274bf6', 'T122-11', 'YEGUA', 'EQUINO', v_finca_id, 2499.57, 800.0, 1699.5700000000002, 14, 1685.5700000000002, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('d5e9b2b9-b8af-40e5-b029-f2abba6bfc30', 'T122-12', 'YEGUA', 'EQUINO', v_finca_id, 2499.57, 800.0, 1699.5700000000002, 13, 1686.5700000000002, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('243fc16f-9058-42e7-9eab-9d7855119915', '40/5-19', 'YEGUA', 'EQUINO', v_finca_id, 2499.57, 800.0, 1699.5700000000002, 6, 1693.5700000000002, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('b87a7ea2-374f-4e99-8076-05a6ae47def3', 'T28-30/8', 'YEGUA', 'EQUINO', v_finca_id, 2499.57, 800.0, 1699.5700000000002, 17, 1682.5700000000002, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('f943f791-8ed6-4447-a2a3-4c39a5f2f00f', 'T122-2/22', 'YEGUA', 'EQUINO', v_finca_id, 2499.57, 800.0, 1699.5700000000002, 3, 1696.5700000000002, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('1ace0c39-51b0-4bdb-978e-b8718c95ed16', 'T122-10', 'YEGUA', 'EQUINO', v_finca_id, 2499.57, 800.0, 1699.5700000000002, 15, 1684.5700000000002, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('28cf4d18-4560-408c-8b9c-60aa945e6837', 'T122-7', 'CABALLO', 'EQUINO', v_finca_id, 2600.0, 800.0, 1800.0, 18, 1782.0, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('cd3cb221-9265-4c58-9861-89261c29a691', 'T122-14', 'CABALLO', 'EQUINO', v_finca_id, 2600.0, 744.17, 1855.83, 11, 1844.83, 'ECTE', true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('d803d938-34f5-4be0-8452-69dce50c6f2d', 'S/N', 'POTRO', 'EQUINO', v_finca_id, 0.0, 0, 0, NULL, 0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('4c550cd8-3d2d-4b0a-93d8-d0da782899e6', 'S/N', 'POTRO', 'EQUINO', v_finca_id, 0.0, 0, 0, NULL, 0, NULL, true, NOW());

    INSERT INTO activo_animal (id, codigo_arete, categoria, tipo_ganado, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_vida, valor_tasacion, destino, activo, created_at)
    VALUES ('22285ce1-b021-4d7c-aa4d-d1314c8cbe9e', 'S/N', 'POTRANCA', 'EQUINO', v_finca_id, 0.0, 0, 0, NULL, 0, NULL, true, NOW());

    -- ===== PLANTACIONES CAÑA (Grupo 12) =====
    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('d5613a41-40ba-49c9-a1a7-3c16226e951a', '12-B01-C01', 'CANA', 1, 1, 20.8, 'SIEMBRA_QUEDADA', '97445', v_finca_id, 46500.31, 46500.31, 0, 3, 46500.31, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('e995792e-72fa-414c-b62a-fb68fb56e6a7', '12-B01-C02', 'CANA', 1, 2, 13.5, 'SIEMBRA_QUEDADA', '97445', v_finca_id, 26697.55, 26697.55, 0, 3, 26697.55, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('ef1fd373-d7fb-46a1-9e12-7c8c89e73749', '12-B01-C03', 'CANA', 1, 3, 15.1, 'RETONO_QUEDADO', '86503', v_finca_id, 58916.45, 58916.45, 0, 3, 58916.45, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('8e5b0a78-4b2f-49d1-b084-93a91697e8bb', '12-B02-C01', 'CANA', 2, 1, 12.9, 'RETONO_1', '97445', v_finca_id, 34866.72, 34866.72, 0, 3, 34866.72, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('c2d4e110-d202-45ea-bcd4-45919dfda94b', '12-B02-C02', 'CANA', 2, 2, 8.4, 'RETONO_1', '97445', v_finca_id, 20790.74, 20790.74, 0, 3, 20790.74, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('ff4f8c3e-3117-42e4-9d84-dc1fd859a956', '12-B02-C03', 'CANA', 2, 3, 8.5, 'RETONO_1', '97445', v_finca_id, 29901.37, 29901.37, 0, 3, 29901.37, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('d3e1d51e-80fd-4816-a876-44ea16f077af', '12-B03-C01', 'CANA', 3, 1, 8.4, 'RETONO_RETONO_QUEDADO', '97414', v_finca_id, 29591.85, 29591.85, 0, 2, 29591.85, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('1a4c8b54-c490-45d7-adb7-f74fe53bab8c', '12-B03-C02', 'CANA', 3, 2, 15.1, 'RETONO_RETONO_QUEDADO', '97414', v_finca_id, 48216.45, 48216.45, 0, 2, 48216.45, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('0f086567-0fc9-43d6-8f7b-1d7277b5e185', '12-B03-C03', 'CANA', 3, 3, 9.2, 'RETONO_RETONO_QUEDADO', '97414', v_finca_id, 21003.52, 21003.52, 0, 2, 21003.52, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('69b20056-f3b8-4928-aa90-6a6f985650cd', '12-B03-C04', 'CANA', 3, 4, 6.0, 'RETONO_1', '97414', v_finca_id, 25810.25, 25810.25, 0, 2, 25810.25, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('824b1480-8902-40b5-a083-07a6d181133b', '12-B03-C05', 'CANA', 3, 5, 5.7, 'RETONO_1', '97414', v_finca_id, 24711.77, 24711.77, 0, 2, 24711.77, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('957e900d-4917-4e3d-b9a8-7e92c3e5a5b0', '12-B03-C06', 'CANA', 3, 6, 7.3, 'RETONO_QUEDADO', '97414', v_finca_id, 20635.29, 20635.29, 0, 2, 20635.29, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('e6c4dc71-77e4-427c-af90-3df4dcc16f61', '12-B03-C07', 'CANA', 3, 7, 4.0, 'RETONO_QUEDADO', '97414', v_finca_id, 23317.65, 23317.65, 0, 2, 23317.65, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('1a54e6aa-47bb-41b2-adec-fac92f1c55ee', '12-B04-C01', 'CANA', 4, 1, 8.9, 'SIEMBRA_QUEDADA', '97414', v_finca_id, 20964.71, 20964.71, 0, 1, 20964.71, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('3236f84a-822a-4de3-8d23-1fc3b5df56a4', '12-B04-C02', 'CANA', 4, 2, 7.8, 'SIEMBRA_QUEDADA', '97414', v_finca_id, 29901.37, 29901.37, 0, 1, 29901.37, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('faab41fc-badf-4048-9f33-e81ac4c5d37a', '12-B04-C03', 'CANA', 4, 3, 8.2, 'SIEMBRA_QUEDADA', '97414', v_finca_id, 20790.74, 20790.74, 0, 1, 20790.74, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('b0b3b07a-7eab-4ef3-9d67-af59011c4064', '12-B04-C04', 'CANA', 4, 4, 7.9, 'SIEMBRA_QUEDADA', '97414', v_finca_id, 20595.43, 20595.43, 0, 1, 20595.43, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('eb55b69b-9a33-49bf-8d58-5bb6ad6fcab3', '12-B04-C05', 'CANA', 4, 5, 8.2, 'RETONO_QUEDADO', '97414', v_finca_id, 21964.71, 21964.71, 0, 6, 21964.71, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('26fc4165-ce44-4c6f-bb53-c8f571c73379', '12-B04-C06', 'CANA', 4, 6, 8.2, 'RETONO_QUEDADO', '97414', v_finca_id, 20657.5, 20657.5, 0, 6, 20657.5, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('bcd6f26d-366c-4890-84c3-016517b98b20', '12-B04-C07', 'CANA', 4, 7, 3.7, 'RETONO_QUEDADO', '97414', v_finca_id, 25317.65, 25317.65, 0, 6, 25317.65, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('83641199-a19d-4d46-a657-5640466f3291', '12-B05-C01', 'CANA', 5, 1, 12.7, NULL, NULL, v_finca_id, 24266.72, 24266.72, 0, NULL, 24266.72, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('b50f425f-d05b-448d-b082-c332736f0d98', '12-B05-C02', 'CANA', 5, 2, 12.3, 'RETONO_QUEDADO', '95414', v_finca_id, 33491.6, 33491.6, 0, 4, 33491.6, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('600a0637-9080-4dc9-b78f-dbdb48f463f2', '12-B05-C03', 'CANA', 5, 3, 9.3, 'RETONO_QUEDADO', '95414', v_finca_id, 21790.74, 21790.74, 0, 4, 21790.74, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('f5326617-57c9-48e4-801f-6d9f57d94ae0', '12-B05-C04', 'CANA', 5, 4, 3.4, 'RETONO_QUEDADO', '95414', v_finca_id, 22954.36, 22954.36, 0, 4, 22954.36, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('0b5fcaa0-202d-475c-804f-05e8abf1a979', '12-B06-C01', 'CANA', 6, 1, 5.9, 'RETONO_1', '95414', v_finca_id, 26810.25, 26810.25, 0, 6, 26810.25, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('0ed0c37c-11c2-48c2-93b2-b00a60982a41', '12-B06-C02', 'CANA', 6, 2, 7.6, 'RETONO_QUEDADO', '95414', v_finca_id, 20395.43, 20395.43, 0, 6, 20395.43, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('afc0d19f-d7ca-4a02-b73b-1a947a30560d', '12-B06-C03', 'CANA', 6, 3, 7.9, 'RETONO_QUEDADO', '95414', v_finca_id, 20595.43, 20595.43, 0, 6, 20595.43, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('a1a1cc95-eb60-4361-82f0-e9afc988cf25', '12-B06-C04', 'CANA', 6, 4, 8.1, 'RETONO_QUEDADO', '95414', v_finca_id, 31964.71, 31964.71, 0, 6, 31964.71, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('9ea3ecf0-2bfe-4ee6-ab21-39637d3ff629', '12-B06-C05', 'CANA', 6, 5, 8.2, 'RETONO_QUEDADO', '95414', v_finca_id, 30657.5, 30657.5, 0, 6, 30657.5, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('491b2d15-5304-4051-9d01-ec44636d7564', '12-B06-C06', 'CANA', 6, 6, 4.8, 'RETONO_QUEDADO', '95414', v_finca_id, 25937.14, 25937.14, 0, 6, 25937.14, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('414368d3-a414-48c1-9c15-395e6f383ce7', '12-B06-C08', 'CANA', 6, 8, 7.2, 'RETONO_1', '95414', v_finca_id, 20237.29, 20237.29, 0, 6, 20237.29, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('dda92d26-3670-4a7f-b64c-95b574fafeab', '12-B08-C04', 'CANA', 8, 4, 12.8, 'SIEMBRA_QUEDADA', '95414', v_finca_id, 48907.19, 48907.19, 0, 1, 48907.19, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('b3a10834-600b-4253-bcb4-4526f8a8b600', '12-B09-C02', 'CANA', 9, 2, 12.5, 'RETONO_1', '633781', v_finca_id, 48371.83, 48371.83, 0, 5, 48371.83, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('719a0b1e-ae9f-40f2-98ac-a8200884b0b0', '12-B09-C03', 'CANA', 9, 3, 7.3, 'RETONO_1', '633781', v_finca_id, 35581.22, 35581.22, 0, 5, 35581.22, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('45a45d1d-d67d-44f5-bcf3-5a9b726e0342', '12-B09-C04', 'CANA', 9, 4, 3.3, 'RETONO_1', '633781', v_finca_id, 22032.58, 22032.58, 0, 5, 22032.58, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('85ae013d-3469-428e-8f3f-bfe55605d680', '12-B09-C05', 'CANA', 9, 5, 7.7, 'RETONO_1', '633781', v_finca_id, 20635.29, 20635.29, 0, 5, 20635.29, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('26412b12-80ff-4686-8a27-25bbdad10c1f', '12-B09-C06', 'CANA', 9, 6, 7.4, 'RETONO_1', '633781', v_finca_id, 35905.78, 35905.78, 0, 5, 35905.78, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('235da693-5b9e-49a2-8c31-68ab56b8eb4d', '12-B09-C07', 'CANA', 9, 7, 10.5, 'RETONO_1', '633781', v_finca_id, 44220.19, 44220.19, 0, 5, 44220.19, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('e90742e1-b1d5-4195-a57c-77b857afd7db', '12-B10-C01', 'CANA', 10, 1, 3.7, 'RETONO_1', '95414', v_finca_id, 25317.65, 25317.65, 0, 4, 25317.65, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('ad145661-2d92-4ef4-bd96-5b20af60a1bb', '12-B10-C02', 'CANA', 10, 2, 13.6, 'RETONO_RETONO_QUEDADO', '95414', v_finca_id, 40000.58, 40000.58, 0, 3, 40000.58, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('4e346b56-7ad5-4759-acab-8ada2d539e34', '12-B10-C03', 'CANA', 10, 3, 6.5, 'RETONO_1', '95414', v_finca_id, 23890.0, 23890.0, 0, 4, 23890.0, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('ac3c0ed2-eb35-428f-8fa1-cdd690878371', '12-B10-C04', 'CANA', 10, 4, 7.4, 'RETONO_1', '95414', v_finca_id, 35810.25, 35810.25, 0, 4, 35810.25, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('96446e03-7aee-4594-9d4f-c83713e36cbe', '12-B10-C05', 'CANA', 10, 5, 4.2, NULL, '95414', v_finca_id, 25032.58, 25032.58, 0, 4, 25032.58, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('88e83abd-c534-485b-9fc9-74d5183b3850', '12-B10-C06', 'CANA', 10, 6, 6.4, NULL, '95414', v_finca_id, 23890.94, 23890.94, 0, NULL, 23890.94, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('32cb3a96-fd2c-48a5-bded-04669fb9b70e', '12-B10-C07', 'CANA', 10, 7, 14.9, NULL, '95414', v_finca_id, 51697.55, 51697.55, 0, NULL, 51697.55, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('54b326b0-2fee-4226-9517-932a108de6dd', '12-B11-C01', 'CANA', 11, 1, 1.1, 'RETONO_QUEDADO', '95416', v_finca_id, 22948.9, 22948.9, 0, 4, 22948.9, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('fb4a9b00-1fed-472a-80cb-eec9f0684b50', '12-B11-C02', 'CANA', 11, 2, 5.2, 'RETONO_QUEDADO', '95416', v_finca_id, 23643.34, 23643.34, 0, 4, 23643.34, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('5bda49b8-898d-441d-b82f-2eeb0ade6a84', '12-B11-C03', 'CANA', 11, 3, 10.0, 'RETONO_QUEDADO', '95416', v_finca_id, 26220.19, 26220.19, 0, 4, 26220.19, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('be7493ad-f5e2-4378-be10-118540170264', '12-B11-C04', 'CANA', 11, 4, 7.0, 'RETONO_QUEDADO', '95416', v_finca_id, 25581.22, 25581.22, 0, 4, 25581.22, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('b2d89c2f-fe53-4e92-aed1-6fd027cc2cf6', '12-B11-C05', 'CANA', 11, 5, 8.4, 'RETONO_QUEDADO', '95416', v_finca_id, 30657.5, 30657.5, 0, 4, 30657.5, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('1a29b9c3-84dd-4c71-9d4f-27e5355f8102', '12-B11-C06', 'CANA', 11, 6, 9.3, 'RETONO_QUEDADO', '95416', v_finca_id, 31989.71, 31989.71, 0, 4, 31989.71, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('61536f63-6f61-47b6-ad8a-84ffac20a412', '12-B11-C07', 'CANA', 11, 7, 3.4, 'RETONO_QUEDADO', '95416', v_finca_id, 28032.58, 28032.58, 0, 4, 28032.58, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('172ef3ca-c011-454b-bd45-262d7873266b', '12-B11-C08', 'CANA', 11, 8, 8.4, 'RETONO_QUEDADO', '95416', v_finca_id, 20657.5, 20657.5, 0, 4, 20657.5, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('690c8035-0db2-4f56-9083-ae2719d7cc44', '12-B11-C09', 'CANA', 11, 9, 10.0, 'RETONO_QUEDADO', '95416', v_finca_id, 35274.76, 35274.76, 0, 4, 35274.76, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('ac38df4a-9afa-4c5a-8b5c-734a49eaa6a0', '12-B11-C11', 'CANA', 11, 11, 10.2, NULL, '95416', v_finca_id, 36220.19, 36220.19, 0, NULL, 36220.19, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('89aaab22-0c4b-4392-b514-d6a2c77256df', '12-B13-C02', 'CANA', 13, 2, 10.2, NULL, '95467', v_finca_id, 36220.19, 36220.19, 0, NULL, 36220.19, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('01e5fc3a-8f2a-494c-8811-7122f2f286b8', '12-B13-C05', 'CANA', 13, 5, 14.1, 'RETONO_1', '90416', v_finca_id, 38125.73, 38125.73, 0, 3, 38125.73, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('39e1b50d-6e1e-4b65-90e0-16a5a3671a0f', '12-B14-C01', 'CANA', 14, 1, 6.0, 'RETONO_QUEDADO', '97415', v_finca_id, 22890.94, 22890.94, 0, 2, 22890.94, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('db0d112e-e040-42cf-89a0-cedc965c48e2', '12-B15-C01', 'CANA', 15, 1, 9.4, 'RETONO_1', '95416', v_finca_id, 31989.71, 31989.71, 0, 5, 31989.71, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('a387d2fd-7f5b-4ca9-9377-5ff5ff8173b6', '12-B15-C02', 'CANA', 15, 2, 9.1, 'RETONO_1', '95416', v_finca_id, 31989.71, 31989.71, 0, 5, 31989.71, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('c21c3d6f-b5f4-413c-af67-f7c84b34737f', '12-B15-C03', 'CANA', 15, 3, 6.7, 'RETONO_1', '95416', v_finca_id, 27909.52, 27909.52, 0, 5, 27909.52, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('b89c26ba-1ba8-455b-a7c3-153a68750da0', '12-B16-C01', 'CANA', 16, 1, 8.0, 'RETONO_RETONO_QUEDADO', '90469', v_finca_id, 21816.98, 21816.98, 0, 4, 21816.98, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('d21c6bc2-6384-41e5-b57a-c6d9c1c9eca3', '12-B16-C02', 'CANA', 16, 2, 8.4, 'RETONO_QUEDADO', '90469', v_finca_id, 27002.1, 27002.1, 0, 4, 27002.1, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('1f2ecd81-41ad-4622-b168-739e622e2b18', '12-B16-C03', 'CANA', 16, 3, 9.4, 'RETONO_QUEDADO', '90469', v_finca_id, 21989.71, 21989.71, 0, 4, 21989.71, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('d5223d9b-3b26-4e6e-bb6f-0bf402eca35c', '12-B16-C04', 'CANA', 16, 4, 10.0, 'RETONO_QUEDADO', '90469', v_finca_id, 35274.76, 35274.76, 0, 4, 35274.76, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('03e95fa3-d299-46d3-821d-994b9e73348b', '12-B16-C05', 'CANA', 16, 5, 12.3, 'RETONO_QUEDADO', '90469', v_finca_id, 46288.47, 46288.47, 0, 4, 46288.47, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('7e0651dd-7308-4039-b7ff-bda0cd359d54', '12-B16-C06', 'CANA', 16, 6, 15.7, 'RETONO_QUEDADO', '90469', v_finca_id, 51894.35, 51894.35, 0, 4, 51894.35, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('83c33fc3-f547-46e9-8702-fefdde400777', '12-B16-C07', 'CANA', 16, 7, 7.8, 'RETONO_QUEDADO', '90469', v_finca_id, 26789.9, 26789.9, 0, 4, 26789.9, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('8bcb75ba-5464-46d9-af42-b589e82532b4', '12-B17-C01', 'CANA', 17, 1, 2.8, NULL, '5514', v_finca_id, 25345.9, 25345.9, 0, NULL, 25345.9, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('f9b4d1dc-6a23-4800-9eec-290520f8b321', '12-B17-C02', 'CANA', 17, 2, 7.0, NULL, '5514', v_finca_id, 25457.91, 25457.91, 0, NULL, 25457.91, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('a895de32-3fd0-48da-9b2e-2961ecd0d4a5', '12-B17-C03', 'CANA', 17, 3, 7.7, 'SIEMBRA_QUEDADA', '97445', v_finca_id, 26789.9, 26789.9, 0, 1, 26789.9, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('a952852f-b656-4683-b102-e0aa94418645', '12-B17-C04', 'CANA', 17, 4, 7.8, 'RETONO_QUEDADO', '97445', v_finca_id, 27679.45, 27679.45, 0, 2, 27679.45, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('c9968f2d-f114-4a2f-9995-e08545ab6d92', '12-B17-C05', 'CANA', 17, 5, 8.8, 'RETONO_QUEDADO', '97445', v_finca_id, 31816.98, 31816.98, 0, 2, 31816.98, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('6d310aa1-f8c5-4eed-9980-37e210ac1532', '12-B17-C06', 'CANA', 17, 6, 7.5, NULL, '5514', v_finca_id, 25439.3, 25439.3, 0, NULL, 25439.3, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('0045bce1-905f-416f-8781-006dbaef80eb', '12-B17-C07', 'CANA', 17, 7, 5.3, NULL, '5514', v_finca_id, 23238.55, 23238.55, 0, NULL, 23238.55, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('1728ede6-caba-43a4-b2e4-bff012ed34d4', '12-B17-C08', 'CANA', 17, 8, 4.4, NULL, '5514', v_finca_id, 27732.58, 27732.58, 0, NULL, 27732.58, 'ECTE', true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, bloque, campo, area_hectareas, tipo_cepa, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, destino, activo, created_at)
    VALUES ('00d0996c-d703-4f64-9b5c-667add14644b', '12-B11-C11', 'CANA', 11, 11, 10.2, NULL, '86503', v_finca_id, 38542.23, 38542.23, 0, 1, 38542.23, 'ECTE', true, NOW());

    -- ===== PLANTACIONES FRUTALES (Grupo 13) =====
    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, area_hectareas, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, activo, created_at)
    VALUES ('a06859d2-8527-4f6b-bb36-fbae3c6a0f3d', '013-001', 'PLATANO', 17.0, 'P/Burro', v_finca_id, 45600.35, 45600.35, 0, NULL, 50120.45, true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, area_hectareas, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, activo, created_at)
    VALUES ('a704b869-c726-49a3-9044-e8256e13840e', '013--002', 'MANGO', 2.4, 'Mango', v_finca_id, 10560.3, 10560.3, 0, NULL, 12452.32, true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, area_hectareas, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, activo, created_at)
    VALUES ('2acde2f6-ce29-4326-b022-eb7675b9b017', '013--003', 'GUAYABA', 1.6, 'Guayaba', v_finca_id, 3400.35, 3400.35, 0, NULL, 5420.23, true, NOW());

    INSERT INTO plantacion_permanente (id, numero_inventario, tipo_plantacion, area_hectareas, codigo_variedad, finca_id, valor_adquisicion, depreciacion_acumulada, valor_residual, anios_cepa, valor_tasacion, activo, created_at)
    VALUES ('467600e2-21a0-4500-9117-9a0af1fce4d0', '013--004', 'OTROS_FRUTALES', 1.5, 'Otros Frutas', v_finca_id, 2510.95, 2510.95, 0, NULL, 3645.23, true, NOW());

END $$;