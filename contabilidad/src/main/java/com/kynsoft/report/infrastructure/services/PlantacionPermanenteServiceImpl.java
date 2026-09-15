package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.PlantacionPermanenteDto;
import com.kynsoft.report.domain.dto.enums.TipoPlantacion;
import com.kynsoft.report.domain.services.IPlantacionPermanenteService;
import com.kynsoft.report.infrastructure.entity.PlantacionPermanente;
import com.kynsoft.report.infrastructure.repository.command.PlantacionPermanenteWriteRepository;
import com.kynsoft.report.infrastructure.repository.query.PlantacionPermanenteReadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Plantaciones Permanentes.
 * Tabla independiente según NCC No. 7 (Resolución 1038/2017 MFP) - Grupos 12 y 13
 */
@Service
public class PlantacionPermanenteServiceImpl implements IPlantacionPermanenteService {

    private final PlantacionPermanenteReadRepository readRepository;
    private final PlantacionPermanenteWriteRepository writeRepository;

    public PlantacionPermanenteServiceImpl(PlantacionPermanenteReadRepository readRepository,
                                            PlantacionPermanenteWriteRepository writeRepository) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;
    }

    @Override
    @Transactional
    public PlantacionPermanenteDto create(PlantacionPermanenteDto dto) {
        PlantacionPermanente plantacion = new PlantacionPermanente(dto);
        if (plantacion.getId() == null) {
            plantacion.setId(UUID.randomUUID());
        }
        if (plantacion.getNumeroInventario() == null) {
            plantacion.setNumeroInventario(generarNumeroInventario(dto));
        }
        plantacion = writeRepository.save(plantacion);
        return plantacion.toAggregate();
    }

    @Override
    @Transactional
    public PlantacionPermanenteDto update(PlantacionPermanenteDto dto) {
        PlantacionPermanente plantacion = readRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Plantación no encontrada: " + dto.getId()));

        plantacion.setNumeroInventario(dto.getNumeroInventario());
        plantacion.setTipoPlantacion(dto.getTipoPlantacion());
        plantacion.setBloque(dto.getBloque());
        plantacion.setCampo(dto.getCampo());
        plantacion.setAreaHectareas(dto.getAreaHectareas());
        plantacion.setTipoCepa(dto.getTipoCepa());
        plantacion.setCodigoVariedad(dto.getCodigoVariedad());
        plantacion.setAniosCepa(dto.getAniosCepa());
        plantacion.setFincaId(dto.getFincaId());
        plantacion.setValorAdquisicion(dto.getValorAdquisicion());
        plantacion.setDepreciacionAcumulada(dto.getDepreciacionAcumulada());
        plantacion.setValorResidual(dto.getValorResidual());
        plantacion.setValorTasacion(dto.getValorTasacion());
        plantacion.setDestino(dto.getDestino());
        plantacion.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        plantacion = writeRepository.save(plantacion);
        return plantacion.toAggregate();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        writeRepository.deleteById(id);
    }

    @Override
    public Optional<PlantacionPermanenteDto> findById(UUID id) {
        return readRepository.findById(id)
                .map(PlantacionPermanente::toAggregate);
    }

    @Override
    public Page<PlantacionPermanenteDto> search(String query, TipoPlantacion tipoPlantacion,
                                                 Integer bloque, UUID fincaId, Pageable pageable) {
        return readRepository.search(query, tipoPlantacion, bloque, fincaId, pageable)
                .map(PlantacionPermanente::toAggregate);
    }

    @Override
    public List<PlantacionPermanenteDto> findByBloque(Integer bloque) {
        return readRepository.findByBloque(bloque).stream()
                .map(PlantacionPermanente::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlantacionPermanenteDto> findByTipoPlantacion(TipoPlantacion tipoPlantacion) {
        return readRepository.findByTipoPlantacion(tipoPlantacion).stream()
                .map(PlantacionPermanente::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlantacionPermanenteDto> findByFinca(UUID fincaId) {
        return readRepository.findByFincaId(fincaId).stream()
                .map(PlantacionPermanente::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResumenPlantacionDto> getResumenPorTipo(UUID fincaId) {
        List<Object[]> results = readRepository.getResumenPorTipo(fincaId);
        List<ResumenPlantacionDto> resumen = new ArrayList<>();

        for (Object[] row : results) {
            TipoPlantacion tipo = (TipoPlantacion) row[0];
            BigDecimal areaTotal = (BigDecimal) row[1];
            BigDecimal valorTotal = (BigDecimal) row[2];
            Long cantidadCampos = (Long) row[3];
            resumen.add(new ResumenPlantacionDto(tipo, areaTotal, valorTotal, cantidadCampos));
        }

        return resumen;
    }

    private String generarNumeroInventario(PlantacionPermanenteDto dto) {
        String prefix = dto.getTipoPlantacion() == TipoPlantacion.CANA ? "12" : "13";
        String bloque = dto.getBloque() != null ? String.format("%02d", dto.getBloque()) : "00";
        String campo = dto.getCampo() != null ? String.format("%02d", dto.getCampo()) : "00";
        return prefix + "-" + bloque + campo;
    }
}
