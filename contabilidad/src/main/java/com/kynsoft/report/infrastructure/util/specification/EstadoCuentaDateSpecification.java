package com.kynsoft.report.infrastructure.util.specification;

import com.kynsoft.report.infrastructure.entity.EstadoCuenta;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase estática para construir dinámicamente el objeto Specification
 * centrado exclusivamente en el filtrado por rango de fechas (fechaInicio y fechaFin).
 */
public class EstadoCuentaDateSpecification {

    /**
     * Crea una Specification para filtrar por un rango de fechas.
     * @param fechaInicio Fecha de inicio del rango (como LocalDate).
     * @param fechaFin Fecha de fin del rango (como LocalDate).
     * @return Specification que aplica los filtros de fecha.
     */
    public static Specification<EstadoCuenta> filterByDateRange(LocalDate fechaInicio, LocalDate fechaFin) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtro de Fecha de Inicio (fecha >= fechaInicio)
            if (fechaInicio != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fecha"), fechaInicio));
            }

            // 2. Filtro de Fecha de Fin (fecha <= fechaFin)
            if (fechaFin != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fecha"), fechaFin));
            }

            // Si se proporciona al menos una fecha, combina los predicados con AND.
            if (!predicates.isEmpty()) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // Si no se proporciona ninguna fecha, retorna una Specification vacía (no filtra nada)
            return criteriaBuilder.conjunction();
        };
    }
}