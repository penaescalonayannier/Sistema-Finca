package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.responseObject.ReporteResponse;
import com.kynsoft.report.domain.dto.ReporteConsolidadoDto;
import com.kynsoft.report.domain.dto.ReporteConsolidadoPorResponsablePdfDto;
import com.kynsoft.report.domain.dto.ReporteDto;
import com.kynsoft.report.domain.dto.ResponsableConsolidadoDto;
import com.kynsoft.report.domain.dto.TrabajadorConsolidadoDto;
import com.kynsoft.report.domain.services.IReporteService;
import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import com.kynsoft.report.infrastructure.entity.Producto;
import com.kynsoft.report.infrastructure.entity.Reporte;
import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import com.kynsoft.report.infrastructure.repository.command.ReporteWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DiaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.ReporteReadDataJPARepository;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements IReporteService {

    private final ReporteWriteDataJPARepository repositoryCommand;
    private final ReporteReadDataJPARepository repositoryQuery;
    private final DiaTrabajoReadDataJPARepository diaTrabajoRepository;

    public ReporteServiceImpl(ReporteWriteDataJPARepository repositoryCommand,
            ReporteReadDataJPARepository repositoryQuery,
            DiaTrabajoReadDataJPARepository diaTrabajoRepository) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.diaTrabajoRepository = diaTrabajoRepository;
    }

    @Override
    public void create(ReporteDto object) {
        // Validar que el código no exista
        repositoryCommand.save(new Reporte(object));
    }

    @Override
    public void update(ReporteDto object) {
        // Verificar que el reporte exista
        Reporte reporteExistente = repositoryQuery.findById(object.getId())
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "Reporte not found."))));

        // Actualizar solo los campos básicos
        reporteExistente.setBloque(object.getBloque());
        reporteExistente.setCampo(object.getCampo());
        reporteExistente.setArea(object.getArea());
        reporteExistente.setNorma(object.getNorma());
        reporteExistente.setCodigo(object.getCodigo());
        reporteExistente.setYear(object.getYear());
        reporteExistente.setMes(object.getMes());
        reporteExistente.setFecha(object.getFecha());
        reporteExistente.setTrabajadorResponsableId(object.getTrabajadorResponsableId());

        // ⚠️ IMPORTANTE: No tocamos la lista de días, se mantiene intacta
        repositoryCommand.save(reporteExistente);
    }

    @Override
    public void delete(UUID id) {
        try {
            // Verificar que el producto exista antes de eliminar
            repositoryQuery.findById(id)
                    .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Reporte not found."))));
            repositoryCommand.deleteById(id);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(
                    DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "Element cannot be deleted as it has a related element.")));
        }
    }

    @Override
    public ReporteDto findById(UUID id) {
        return repositoryQuery.findById(id)
                .map(Reporte::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "Reporte not found."))));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Producto> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Reporte> data = repositoryQuery.findAll(specifications, pageable);
        return createPaginatedResponse(data);
    }

    private PaginatedResponse createPaginatedResponse(Page<Reporte> data) {
        List<ReporteResponse> responses = data.getContent().stream()
                .map(Reporte::toAggregate)
                .map(ReporteResponse::new)
                .collect(Collectors.toList());

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public ReporteConsolidadoDto getConsolidado(String year, String mes) {
        // Obtener todos los días del mes con sus trabajadores
        List<DiaTrabajo> dias = diaTrabajoRepository.findByYearAndMesWithTrabajadores(year, mes);

        if (dias.isEmpty()) {
            return ReporteConsolidadoDto.builder()
                    .year(year)
                    .mes(mes)
                    .trabajadores(Collections.emptyList())
                    .build();
        }

        // Determinar los días del mes
        int daysInMonth = YearMonth.of(Integer.parseInt(year), getMonthNumber(mes)).lengthOfMonth();

        // Mapa para agrupar por trabajador
        Map<String, TrabajadorData> trabajadorDataMap = new LinkedHashMap<>();

        // Procesar cada día
        for (DiaTrabajo dia : dias) {
            int diaDelMes = dia.getFecha().getDayOfMonth();

            for (TrabajadorDia td : dia.getTrabajadores()) {
                String trabajadorId = td.getTrabajador().getId().toString();
                String nombre = td.getTrabajador().getNombre();
                String ruc = td.getTrabajador().getRuc();
                String cargo = td.getTrabajador().getCargo() != null ? td.getTrabajador().getCargo().getName() : null;
                String cuenta = td.getTrabajador().getCuenta();
                String horas = td.getHoras() != null ? td.getHoras() : "";

                TrabajadorData data = trabajadorDataMap.computeIfAbsent(
                        trabajadorId,
                        k -> new TrabajadorData(trabajadorId, nombre, ruc, cargo, cuenta, daysInMonth)
                );

                if (horas != null && !horas.isEmpty()) {
                    int horasNum = convertirHorasANumero(horas);

                    // Si ya hay horas para este día, sumarlas
                    String horasActuales = data.horasPorDia.get(diaDelMes);
                    if (horasActuales != null && !horasActuales.isEmpty()) {
                        int horasAnteriores = convertirHorasANumero(horasActuales);
                        int horasTotales = horasAnteriores + horasNum;
                        data.horasPorDia.put(diaDelMes, String.valueOf(horasTotales));
                        data.totalHoras += horasNum;
                    } else {
                        data.horasPorDia.put(diaDelMes, horas);
                        data.totalHoras += horasNum;
                    }
                }
            }
        }

        // Convertir a DTO
        List<TrabajadorConsolidadoDto> trabajadores = trabajadorDataMap.values().stream()
                .map(data -> TrabajadorConsolidadoDto.builder()
                .trabajadorId(data.trabajadorId)
                .nombre(data.nombre)
                .ruc(data.ruc)
                .cargo(data.cargo)
                .cuenta(data.cuenta)
                .horasPorDia(data.horasPorDia)
                .totalHoras((double) data.totalHoras)
                .build()
                )
                .sorted(Comparator.comparing(TrabajadorConsolidadoDto::getNombre))
                .collect(Collectors.toList());

        return ReporteConsolidadoDto.builder()
                .year(year)
                .mes(mes)
                .trabajadores(trabajadores)
                .build();
    }

    @Override
    public ReporteConsolidadoPorResponsablePdfDto getConsolidadoPorResponsable(String year, String mes) {
        // Obtener todos los días del mes con sus trabajadores
        List<DiaTrabajo> dias = diaTrabajoRepository.findByYearAndMesWithTrabajadores(year, mes);

        if (dias.isEmpty()) {
            int daysInMonth = YearMonth.of(Integer.parseInt(year), getMonthNumber(mes)).lengthOfMonth();
            return ReporteConsolidadoPorResponsablePdfDto.builder()
                    .year(year)
                    .mes(mes)
                    .responsables(Collections.emptyList())
                    .diasDelMes(daysInMonth)
                    .build();
        }

        // Determinar los días del mes
        int daysInMonth = YearMonth.of(Integer.parseInt(year), getMonthNumber(mes)).lengthOfMonth();

        // Mapa para agrupar por responsable: responsableId -> { trabajadorId -> Datos del trabajador }
        Map<String, Map<String, TrabajadorDataPorResponsable>> responsablesTrabajadoresMap = new HashMap<>();

        // Procesar cada día
        for (DiaTrabajo dia : dias) {
            int diaDelMes = dia.getFecha().getDayOfMonth();

            // Procesar cada trabajador del día
            for (TrabajadorDia td : dia.getTrabajadores()) {
                String responsableId = dia.getReporte().getTrabajadorResponsableId() != null
                        ? dia.getReporte().getTrabajadorResponsableId().toString()
                        : "SIN_RESPONSABLE";
                String responsableNombre = dia.getReporte().getTrabajadorResponsable() != null
                        ? dia.getReporte().getTrabajadorResponsable().getNombre()
                        : "Sin asignar";

                String trabajadorId = td.getTrabajador().getId().toString();
                String nombre = td.getTrabajador().getNombre();
                String ruc = td.getTrabajador().getRuc();
                String cargo = td.getTrabajador().getCargo() != null ? td.getTrabajador().getCargo().getName() : null;
                String cuenta = td.getTrabajador().getCuenta();
                String horas = td.getHoras() != null ? td.getHoras() : "";

                // Obtener o crear el mapa de trabajadores para este responsable
                Map<String, TrabajadorDataPorResponsable> trabajadoresMap = responsablesTrabajadoresMap
                        .computeIfAbsent(responsableId, k -> new HashMap<>());

                // Obtener o crear el registro del trabajador
                TrabajadorDataPorResponsable data = trabajadoresMap.computeIfAbsent(
                        trabajadorId,
                        k -> new TrabajadorDataPorResponsable(trabajadorId, nombre, ruc, cargo, cuenta, responsableId, responsableNombre, daysInMonth)
                );

                // Consolidar horas al día - SUMA si hay múltiples registros
                if (horas != null && !horas.isEmpty()) {
                    int horasNum = convertirHorasANumero(horas);

                    // Si ya hay horas para este día, sumarlas
                    String horasActuales = data.horasPorDia.get(diaDelMes);
                    if (horasActuales != null && !horasActuales.isEmpty()) {
                        int horasAnteriores = convertirHorasANumero(horasActuales);
                        int horasTotales = horasAnteriores + horasNum;
                        data.horasPorDia.put(diaDelMes, String.valueOf(horasTotales));
                    } else {
                        data.horasPorDia.put(diaDelMes, horas);
                    }
                }
            }
        }

        // Convertir a DTOs
        java.util.List<ResponsableConsolidadoDto> responsablesList = new java.util.ArrayList<>();

        for (Map.Entry<String, Map<String, TrabajadorDataPorResponsable>> responsableEntry : responsablesTrabajadoresMap.entrySet()) {
            String responsableId = responsableEntry.getKey();
            Map<String, TrabajadorDataPorResponsable> trabajadoresMap = responsableEntry.getValue();

            List<TrabajadorConsolidadoDto> trabajadoresList = trabajadoresMap.values().stream()
                    .map(data -> TrabajadorConsolidadoDto.builder()
                            .trabajadorId(data.trabajadorId)
                            .nombre(data.nombre)
                            .ruc(data.ruc)
                            .cargo(data.cargo)
                            .cuenta(data.cuenta)
                            .horasPorDia(data.horasPorDia)
                            .totalHoras(calcularTotalHoras(data.horasPorDia))
                            .build())
                    .sorted((a, b) -> a.getNombre().compareTo(b.getNombre()))
                    .collect(Collectors.toList());

            if (!trabajadoresList.isEmpty()) {
                TrabajadorDataPorResponsable firstWorker = trabajadoresMap.values().stream().findFirst().orElse(null);
                if (firstWorker != null) {
                    ResponsableConsolidadoDto responsable = ResponsableConsolidadoDto.builder()
                            .trabajadorResponsableId(java.util.UUID.fromString(responsableId.equals("SIN_RESPONSABLE") ? "00000000-0000-0000-0000-000000000000" : responsableId))
                            .trabajadorResponsableNombre(firstWorker.responsableNombre)
                            .trabajadores(trabajadoresList)
                            .build();
                    responsablesList.add(responsable);
                }
            }
        }

        // Ordenar responsables por nombre
        responsablesList.sort((a, b) -> {
            String nameA = a.getTrabajadorResponsableNombre() != null ? a.getTrabajadorResponsableNombre() : "";
            String nameB = b.getTrabajadorResponsableNombre() != null ? b.getTrabajadorResponsableNombre() : "";
            return nameA.compareTo(nameB);
        });

        return ReporteConsolidadoPorResponsablePdfDto.builder()
                .year(year)
                .mes(mes)
                .responsables(responsablesList)
                .diasDelMes(daysInMonth)
                .build();
    }

    private double calcularTotalHoras(Map<Integer, String> horasPorDia) {
        return horasPorDia.values().stream()
                .mapToDouble(h -> {
                    try {
                        return Double.parseDouble(h);
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                })
                .sum();
    }

    private int getMonthNumber(String mes) {
        Map<String, Integer> meses = new HashMap<>();
        meses.put("Enero", 1);
        meses.put("Febrero", 2);
        meses.put("Marzo", 3);
        meses.put("Abril", 4);
        meses.put("Mayo", 5);
        meses.put("Junio", 6);
        meses.put("Julio", 7);
        meses.put("Agosto", 8);
        meses.put("Septiembre", 9);
        meses.put("Octubre", 10);
        meses.put("Noviembre", 11);
        meses.put("Diciembre", 12);
        return meses.getOrDefault(mes, 1);
    }

    private int convertirHorasANumero(String horas) {
        try {
            if (horas.contains(":")) {
                String[] partes = horas.split(":");
                int horasInt = Integer.parseInt(partes[0]);
                return horasInt + (partes.length > 1 && Integer.parseInt(partes[1]) > 0 ? 1 : 0);
            } else {
                return Integer.parseInt(horas);
            }
        } catch (Exception e) {
            return 0;
        }
    }

    // Clase auxiliar para consolidado simple
    private static class TrabajadorData {

        String trabajadorId;
        String nombre;
        String ruc;
        String cargo;
        String cuenta;
        Map<Integer, String> horasPorDia;
        int totalHoras = 0;

        TrabajadorData(String id, String nombre, String ruc, String cargo, String cuenta, int daysInMonth) {
            this.trabajadorId = id;
            this.nombre = nombre;
            this.ruc = ruc;
            this.cargo = cargo;
            this.cuenta = cuenta;
            this.horasPorDia = new LinkedHashMap<>();
            for (int i = 1; i <= daysInMonth; i++) {
                horasPorDia.put(i, "");
            }
        }
    }

    // Clase auxiliar para consolidado por responsable
    private static class TrabajadorDataPorResponsable {
        String trabajadorId;
        String nombre;
        String ruc;
        String cargo;
        String cuenta;
        String responsableId;
        String responsableNombre;
        Map<Integer, String> horasPorDia;

        TrabajadorDataPorResponsable(String trabajadorId, String nombre, String ruc, String cargo, String cuenta, String responsableId, String responsableNombre, int daysInMonth) {
            this.trabajadorId = trabajadorId;
            this.nombre = nombre;
            this.ruc = ruc;
            this.cargo = cargo;
            this.cuenta = cuenta;
            this.responsableId = responsableId;
            this.responsableNombre = responsableNombre;
            this.horasPorDia = new HashMap<>();
            for (int i = 1; i <= daysInMonth; i++) {
                this.horasPorDia.put(i, "");
            }
        }
    }
}
