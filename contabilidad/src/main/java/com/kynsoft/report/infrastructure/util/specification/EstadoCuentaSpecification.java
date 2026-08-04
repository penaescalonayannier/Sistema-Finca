package com.kynsoft.report.infrastructure.util.specification;

import com.kynsoft.report.infrastructure.entity.EstadoCuenta;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase estática para construir dinámicamente el objeto Specification.
 */
public class EstadoCuentaSpecification {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Specification<EstadoCuenta> getFilter(String query, String fechaInicio, String fechaFin, String filterTipo) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtro de Búsqueda General (query en refOrigen, refCorriente, observaciones)
            if (query != null && !query.trim().isEmpty()) {
                String likeQuery = "%" + query.toLowerCase() + "%";
                Predicate generalSearch = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("refOrigen")), likeQuery),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("refCorriente")), likeQuery),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("observaciones")), likeQuery)
                );
                predicates.add(generalSearch);
            }

            // 2. Filtro de Tipo (Cr o Db)
            if (filterTipo != null && !filterTipo.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("tipo"), filterTipo));
            }

            // 3. Filtro de Fecha de Inicio (fecha >= fechaInicio)
            if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
                LocalDate fechaInicioDate = LocalDate.parse(fechaInicio, DATE_FORMATTER);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fecha"), fechaInicioDate));
            }

            // 4. Filtro de Fecha de Fin (fecha <= fechaFin)
            if (fechaFin != null && !fechaFin.trim().isEmpty()) {
                LocalDate fechaFinDate = LocalDate.parse(fechaFin, DATE_FORMATTER);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fecha"), fechaFinDate));
            }

            // Combinar todos los predicados con AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}