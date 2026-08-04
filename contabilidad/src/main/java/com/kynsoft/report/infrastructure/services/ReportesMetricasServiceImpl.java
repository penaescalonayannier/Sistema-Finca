package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.applications.query.responseObject.*;
import com.kynsoft.report.domain.services.IReportesMetricasService;
import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import com.kynsoft.report.infrastructure.entity.Trabajador;
import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import com.kynsoft.report.infrastructure.repository.query.DiaTrabajoReadDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.TrabajadorReadDataJPARepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportesMetricasServiceImpl implements IReportesMetricasService {

    private final DiaTrabajoReadDataJPARepository diaTrabajoRepository;
    private final TrabajadorReadDataJPARepository trabajadorRepository;

    public ReportesMetricasServiceImpl(
            DiaTrabajoReadDataJPARepository diaTrabajoRepository,
            TrabajadorReadDataJPARepository trabajadorRepository) {
        this.diaTrabajoRepository = diaTrabajoRepository;
        this.trabajadorRepository = trabajadorRepository;
    }

    @Override
    public List<AbsentismoResponse> calcularAbsentismo(String year, String mes, UUID trabajadorId) {
        // Obtener datos de días trabajados para el mes
        List<DiaTrabajo> diasTrabajados = diaTrabajoRepository.findByYearAndMesWithTrabajadores(year, mes);

        // Obtener lista de todos los trabajadores activos
        List<Trabajador> trabajadores = trabajadorRepository.findAll().stream()
                .filter(t -> t.getActivo() != null && t.getActivo())
                .collect(Collectors.toList());

        // Si se especifica un trabajador, filtrar
        if (trabajadorId != null) {
            trabajadores = trabajadores.stream()
                    .filter(t -> t.getId().equals(trabajadorId))
                    .collect(Collectors.toList());
        }

        // Obtener la fecha límite (hoy si es mes actual, o último día del mes)
        LocalDate fechaLimite = calcularFechaLimite(year, mes);

        // Calcular días laborables del mes (o hasta hoy si es mes actual)
        int yearInt = Integer.parseInt(year);
        int mesInt = obtenerMesInt(mes);
        YearMonth ym = YearMonth.of(yearInt, mesInt);
        LocalDate primerDia = ym.atDay(1);
        int diasLaborables = calcularDiasLaborablesHasta(primerDia, fechaLimite);

        // Agrupar trabajadores por día (filtrando por fecha límite)
        Map<UUID, Set<LocalDate>> trabajadorDias = new HashMap<>();
        for (DiaTrabajo dt : diasTrabajados) {
            // Filtrar solo días hasta la fecha límite
            if (dt.getFecha().isAfter(fechaLimite)) {
                continue;
            }
            for (TrabajadorDia td : dt.getTrabajadores()) {
                trabajadorDias.computeIfAbsent(td.getTrabajador().getId(), k -> new HashSet<>())
                        .add(dt.getFecha());
            }
        }

        // Calcular métricas
        List<AbsentismoResponse> resultado = new ArrayList<>();
        for (Trabajador trabajador : trabajadores) {
            int diasTrabajados_count = trabajadorDias.getOrDefault(trabajador.getId(), new HashSet<>()).size();
            int diasFaltados = diasLaborables - diasTrabajados_count;
            double porcentajeAsistencia = (diasTrabajados_count / (double) diasLaborables) * 100;

            AbsentismoResponse response = new AbsentismoResponse();
            response.setTrabajadorId(trabajador.getId().toString());
            response.setNombre(trabajador.getNombre());
            response.setRuc(trabajador.getRuc());
            response.setCargo(trabajador.getCargo() != null ? trabajador.getCargo().getName() : null);
            response.setCuenta(trabajador.getCuenta());
            response.setDiasLaborables(diasLaborables);
            response.setDiasTrabajados(diasTrabajados_count);
            response.setDiasFaltados(diasFaltados);
            response.setPorcentajeAsistencia(Math.round(porcentajeAsistencia * 100.0) / 100.0);
            response.setPatron(detectarPatronFaltas(trabajadorDias.get(trabajador.getId()), diasLaborables));
            response.setTendencia("ESTABLE"); // Simplificado: se podría calcular con histórico

            resultado.add(response);
        }

        // Ordenar por porcentaje de asistencia (ascendente)
        resultado.sort(Comparator.comparingDouble(AbsentismoResponse::getPorcentajeAsistencia));
        return resultado;
    }

    @Override
    public List<ProductividadResponse> calcularProductividad(String year, String mes, UUID trabajadorId) {
        // Obtener datos de días trabajados
        List<DiaTrabajo> diasTrabajados = diaTrabajoRepository.findByYearAndMesWithTrabajadores(year, mes);

        // Obtener la fecha límite (hoy si es mes actual, o último día del mes)
        LocalDate fechaLimite = calcularFechaLimite(year, mes);

        // Obtener lista de todos los trabajadores activos
        List<Trabajador> trabajadores = trabajadorRepository.findAll().stream()
                .filter(t -> t.getActivo() != null && t.getActivo())
                .collect(Collectors.toList());

        // Si se especifica un trabajador, filtrar
        if (trabajadorId != null) {
            trabajadores = trabajadores.stream()
                    .filter(t -> t.getId().equals(trabajadorId))
                    .collect(Collectors.toList());
        }

        Map<UUID, List<Double>> horasPorTrabajador = new HashMap<>();
        Map<UUID, Integer> diasPorTrabajador = new HashMap<>();

        // Agrupar horas por trabajador (filtrando por fecha límite)
        for (DiaTrabajo dt : diasTrabajados) {
            // Filtrar solo días hasta la fecha límite
            if (dt.getFecha().isAfter(fechaLimite)) {
                continue;
            }
            for (TrabajadorDia td : dt.getTrabajadores()) {
                UUID idTrabajador = td.getTrabajador().getId();
                double horas = parsearHoras(td.getHoras());
                horasPorTrabajador.computeIfAbsent(idTrabajador, k -> new ArrayList<>()).add(horas);
                diasPorTrabajador.put(idTrabajador, diasPorTrabajador.getOrDefault(idTrabajador, 0) + 1);
            }
        }

        // Calcular métricas
        List<ProductividadResponse> resultado = new ArrayList<>();
        for (Trabajador trabajador : trabajadores) {
            List<Double> horas = horasPorTrabajador.getOrDefault(trabajador.getId(), new ArrayList<>());
            int diasTrabajados_count = diasPorTrabajador.getOrDefault(trabajador.getId(), 0);

            double totalHoras = horas.stream().mapToDouble(Double::doubleValue).sum();
            double horasPromedioDia = diasTrabajados_count > 0 ? totalHoras / diasTrabajados_count : 0.0;
            double normaEsperada = diasTrabajados_count * 8.0; // Norma por defecto: 8 horas/día
            double porcentajeCumplimiento = normaEsperada > 0 ? (totalHoras / normaEsperada) * 100 : 0.0;
            double variabilidad = calcularDesviacionEstandar(horas);
            String consistencia = clasificarConsistencia(variabilidad);

            ProductividadResponse response = new ProductividadResponse();
            response.setTrabajadorId(trabajador.getId().toString());
            response.setNombre(trabajador.getNombre());
            response.setRuc(trabajador.getRuc());
            response.setCargo(trabajador.getCargo() != null ? trabajador.getCargo().getName() : null);
            response.setCuenta(trabajador.getCuenta());
            response.setTotalHoras(Math.round(totalHoras * 100.0) / 100.0);
            response.setNormaEsperada(Math.round(normaEsperada * 100.0) / 100.0);
            response.setPorcentajeCumplimiento(Math.round(porcentajeCumplimiento * 100.0) / 100.0);
            response.setHorasPromedioDia(Math.round(horasPromedioDia * 100.0) / 100.0);
            response.setVariabilidad(Math.round(variabilidad * 100.0) / 100.0);
            response.setConsistencia(consistencia);
            response.setDiasTrabajados(diasTrabajados_count);

            resultado.add(response);
        }

        // Ordenar por porcentaje de cumplimiento (descendente)
        resultado.sort((a, b) -> Double.compare(b.getPorcentajeCumplimiento(), a.getPorcentajeCumplimiento()));
        return resultado;
    }

    @Override
    public List<RankingResponse> calcularRankings(String year, String mes, String cargo) {
        // Primero calcular productividad
        List<ProductividadResponse> productividades = calcularProductividad(year, mes, null);

        // Obtener trabajadores por cargo
        Set<String> cargos = productividades.stream()
                .map(ProductividadResponse::getCargo)
                .collect(Collectors.toSet());

        // Filtrar si se especifica cargo
        if (cargo != null && !cargo.isEmpty()) {
            cargos = cargos.stream()
                    .filter(c -> c.equalsIgnoreCase(cargo))
                    .collect(Collectors.toSet());
        }

        List<RankingResponse> resultado = new ArrayList<>();

        for (String cargoActual : cargos) {
            // Filtrar productividades por cargo
            List<ProductividadResponse> porCargo = productividades.stream()
                    .filter(p -> p.getCargo().equalsIgnoreCase(cargoActual))
                    .collect(Collectors.toList());

            // Calcular promedio del cargo
            double promedioCargo = porCargo.stream()
                    .mapToDouble(ProductividadResponse::getPorcentajeCumplimiento)
                    .average()
                    .orElse(0.0);

            // Obtener top 5
            List<ProductividadResponse> top5List = porCargo.stream()
                    .sorted((a, b) -> Double.compare(b.getPorcentajeCumplimiento(), a.getPorcentajeCumplimiento()))
                    .limit(5)
                    .collect(Collectors.toList());

            List<TrabajadorRankingResponse> top5 = new ArrayList<>();
            for (int i = 0; i < top5List.size(); i++) {
                top5.add(crearTrabajadorRanking(i + 1, top5List.get(i)));
            }

            // Obtener bottom 5
            List<ProductividadResponse> bottom5List = porCargo.stream()
                    .sorted(Comparator.comparingDouble(ProductividadResponse::getPorcentajeCumplimiento))
                    .limit(5)
                    .collect(Collectors.toList());

            List<TrabajadorRankingResponse> bottom5 = new ArrayList<>();
            for (int i = 0; i < bottom5List.size(); i++) {
                bottom5.add(crearTrabajadorRanking(porCargo.size() - i, bottom5List.get(i)));
            }

            RankingResponse rankingResponse = new RankingResponse();
            rankingResponse.setCargo(cargoActual);
            rankingResponse.setTopPerformers(top5);
            rankingResponse.setBottomPerformers(bottom5);
            rankingResponse.setPromedioCargo(Math.round(promedioCargo * 100.0) / 100.0);
            rankingResponse.setTotalTrabajadores(porCargo.size());

            resultado.add(rankingResponse);
        }

        return resultado;
    }

    @Override
    public List<HorasExcedidasSummaryResponse> calcularHorasExcedidasSummary(String year, String mes) {
        // Obtener datos de días trabajados
        List<DiaTrabajo> diasTrabajados = diaTrabajoRepository.findByYearAndMesWithTrabajadores(year, mes);

        // Obtener la fecha límite (hoy si es mes actual, o último día del mes)
        LocalDate fechaLimite = calcularFechaLimite(year, mes);

        // Obtener lista de trabajadores activos para filtro
        Set<UUID> trabajadoresActivos = trabajadorRepository.findAll().stream()
                .filter(t -> t.getActivo() != null && t.getActivo())
                .map(Trabajador::getId)
                .collect(Collectors.toSet());

        Map<UUID, HorasExcedidasSummaryResponse> resumenPorTrabajador = new HashMap<>();

        for (DiaTrabajo dt : diasTrabajados) {
            // Filtrar solo días hasta la fecha límite
            if (dt.getFecha().isAfter(fechaLimite)) {
                continue;
            }

            for (TrabajadorDia td : dt.getTrabajadores()) {
                // Filtrar solo trabajadores activos
                if (!trabajadoresActivos.contains(td.getTrabajador().getId())) {
                    continue;
                }

                double horas = parsearHoras(td.getHoras());

                if (horas > 8.0) {
                    UUID idTrabajador = td.getTrabajador().getId();
                    HorasExcedidasSummaryResponse response = resumenPorTrabajador.computeIfAbsent(
                            idTrabajador,
                            k -> {
                                HorasExcedidasSummaryResponse r = new HorasExcedidasSummaryResponse();
                                r.setTrabajadorId(idTrabajador.toString());
                                r.setNombre(td.getTrabajador().getNombre());
                                r.setRuc(td.getTrabajador().getRuc());
                                r.setCargo(td.getTrabajador().getCargo() != null ? td.getTrabajador().getCargo().getName() : null);
                                r.setDiasExcedidos(0);
                                r.setTotalHorasExcedidas(0.0);
                                r.setDiasConExceso(new ArrayList<>());
                                return r;
                            }
                    );

                    double horasExcedidas = horas - 8.0;
                    response.setDiasExcedidos(response.getDiasExcedidos() + 1);
                    response.setTotalHorasExcedidas(response.getTotalHorasExcedidas() + horasExcedidas);
                    response.getDiasConExceso().add(dt.getFecha().toString());
                }
            }
        }

        // Ordenar por total de horas excedidas (descendente)
        return resumenPorTrabajador.values().stream()
                .sorted((a, b) -> Double.compare(b.getTotalHorasExcedidas(), a.getTotalHorasExcedidas()))
                .collect(Collectors.toList());
    }

    // Métodos auxiliares

    private double parsearHoras(String horasString) {
        if (horasString == null || horasString.isEmpty()) {
            return 0.0;
        }

        try {
            if (horasString.contains(":")) {
                String[] partes = horasString.split(":");
                int horas = Integer.parseInt(partes[0]);
                int minutos = Integer.parseInt(partes[1]);
                return horas + (minutos / 60.0);
            } else {
                return Double.parseDouble(horasString);
            }
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int obtenerMesInt(String mes) {
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

    private String detectarPatronFaltas(Set<LocalDate> diasTrabajados, int diasLaborables) {
        if (diasTrabajados == null || diasTrabajados.isEmpty()) {
            return "CONSECUTIVO";
        }
        // Simplificado: se podría implementar lógica más sofisticada
        return "OCASIONAL";
    }

    private double calcularDesviacionEstandar(List<Double> valores) {
        if (valores.isEmpty()) {
            return 0.0;
        }

        double promedio = valores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double varianza = valores.stream()
                .mapToDouble(v -> Math.pow(v - promedio, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(varianza);
    }

    private String clasificarConsistencia(double variabilidad) {
        if (variabilidad < 0.5) {
            return "ALTA";
        } else if (variabilidad < 1.5) {
            return "MEDIA";
        } else {
            return "BAJA";
        }
    }

    private TrabajadorRankingResponse crearTrabajadorRanking(int ranking, ProductividadResponse prod) {
        return new TrabajadorRankingResponse(
                ranking,
                prod.getTrabajadorId(),
                prod.getNombre(),
                prod.getRuc(),
                prod.getCargo(),
                Math.round(prod.getPorcentajeCumplimiento() * 100.0) / 100.0,
                prod.getTotalHoras(),
                prod.getPorcentajeCumplimiento()
        );
    }

    /**
     * Calcula la fecha límite para procesar datos
     * Si el mes/año seleccionado es el mes/año actual, retorna el día de hoy
     * Si es un mes anterior, retorna el último día del mes
     */
    private LocalDate calcularFechaLimite(String year, String mes) {
        int yearInt = Integer.parseInt(year);
        int mesInt = obtenerMesInt(mes);
        YearMonth ym = YearMonth.of(yearInt, mesInt);

        LocalDate hoy = LocalDate.now();
        YearMonth mesActual = YearMonth.from(hoy);

        // Si es el mes/año actual, retornar hoy
        if (ym.equals(mesActual)) {
            return hoy;
        }
        // Si es un mes anterior, retornar el último día del mes
        return ym.atEndOfMonth();
    }

    /**
     * Calcula la cantidad de días laborables (lunes a viernes) entre dos fechas (inclusive)
     */
    private int calcularDiasLaborablesHasta(LocalDate desde, LocalDate hasta) {
        int diasLaborables = 0;
        LocalDate fecha = desde;
        while (!fecha.isAfter(hasta)) {
            int dayOfWeek = fecha.getDayOfWeek().getValue();
            // 1 = Lunes, 5 = Viernes (1-5 son laborables)
            if (dayOfWeek >= 1 && dayOfWeek <= 5) {
                diasLaborables++;
            }
            fecha = fecha.plusDays(1);
        }
        return diasLaborables;
    }
}
