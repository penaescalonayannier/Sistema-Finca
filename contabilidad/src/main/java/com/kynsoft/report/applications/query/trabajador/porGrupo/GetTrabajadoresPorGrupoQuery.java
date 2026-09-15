package com.kynsoft.report.applications.query.trabajador.porGrupo;

import com.kynsoft.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetTrabajadoresPorGrupoQuery implements IQuery {
    private UUID grupoId;
    private int page;
    private int pageSize;
}
