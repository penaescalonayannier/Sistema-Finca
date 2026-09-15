package com.kynsoft.report.domain.services;

import com.kynsoft.share.core.domain.request.FilterCriteria;
import com.kynsoft.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.DeleteTrabajadorResponse;
import com.kynsoft.report.domain.dto.TrabajadorDto;
import com.kynsoft.report.domain.dto.TransferirTrabajadorResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITrabajadorService {

    void create(TrabajadorDto object);

    void update(TrabajadorDto object);

    DeleteTrabajadorResponse delete(UUID id);

    DeleteTrabajadorResponse previewDesactivacion(UUID id);

    void reactivar(UUID id);

    TransferirTrabajadorResponse transferir(UUID trabajadorId, UUID nuevaFincaId);

    TrabajadorDto findById(UUID id);

    TrabajadorDto findByRuc(String ruc);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    PaginatedResponse findByFincaId(UUID fincaId, Pageable pageable);

    PaginatedResponse findByGrupoId(UUID grupoId, Pageable pageable);

    List<TrabajadorDto> findAll(List<UUID> ids);

    List<TrabajadorDto> findAllActivos();

    int desasignarTrabajadoresDeGrupo(UUID grupoId);
}
