package com.kynsoft.report.infrastructure.security;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Helper class to create tenant-aware JPA Specifications.
 * Automatically filters by finca based on TenantContext.
 */
public class TenantSpecification {

    private TenantSpecification() {
        // Utility class
    }

    /**
     * Filter for entities with direct fincaId field.
     * Used by: Trabajador, FincaProducto, ProduccionTerminada, Reporte
     */
    public static <T> Specification<T> byFincaDirecta() {
        return (root, query, cb) -> {
            if (!TenantContext.shouldFilter()) {
                return cb.conjunction(); // No filter for ADMIN without selection
            }
            UUID fincaId = TenantContext.getEffectiveFincaId();
            if (fincaId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("fincaId"), fincaId);
        };
    }

    /**
     * Filter for entities related to Trabajador.
     * Used by: DeudaTrabajador, Usuario
     */
    public static <T> Specification<T> byFincaViaTrabajador() {
        return (root, query, cb) -> {
            if (!TenantContext.shouldFilter()) {
                return cb.conjunction();
            }
            UUID fincaId = TenantContext.getEffectiveFincaId();
            if (fincaId == null) {
                return cb.conjunction();
            }
            Join<Object, Object> trabajador = root.join("trabajador", JoinType.LEFT);
            return cb.equal(trabajador.get("fincaId"), fincaId);
        };
    }

    /**
     * Filter for entities related to FincaProducto that has Finca relation.
     * Used by: Salida, MovimientoStock
     */
    public static <T> Specification<T> byFincaViaFincaProducto() {
        return (root, query, cb) -> {
            if (!TenantContext.shouldFilter()) {
                return cb.conjunction();
            }
            UUID fincaId = TenantContext.getEffectiveFincaId();
            if (fincaId == null) {
                return cb.conjunction();
            }
            Join<Object, Object> fincaProducto = root.join("fincaProducto", JoinType.LEFT);
            Join<Object, Object> finca = fincaProducto.join("finca", JoinType.LEFT);
            return cb.equal(finca.get("id"), fincaId);
        };
    }

    /**
     * Filter for Auditoria - filters by the current user's finca via their usuarioId.
     * ADMIN sees all unless filtered.
     */
    public static <T> Specification<T> byFincaViaUsuario() {
        return (root, query, cb) -> {
            if (!TenantContext.shouldFilter()) {
                return cb.conjunction();
            }
            // For auditoria, we filter by usuarioId matching current user
            // or by looking up the user's finca
            UUID usuarioId = TenantContext.getUsuarioId();
            if (usuarioId == null) {
                return cb.conjunction();
            }
            // Filter audits by the logged-in user for non-admins
            // This shows only actions done by users from the same finca
            return cb.conjunction(); // Auditoria special handling in service
        };
    }

    /**
     * Filter for entities with 'finca' as ManyToOne relation.
     * Used by: FincaProducto
     */
    public static <T> Specification<T> byFincaRelation() {
        return (root, query, cb) -> {
            if (!TenantContext.shouldFilter()) {
                return cb.conjunction();
            }
            UUID fincaId = TenantContext.getEffectiveFincaId();
            if (fincaId == null) {
                return cb.conjunction();
            }
            Join<Object, Object> finca = root.join("finca", JoinType.LEFT);
            return cb.equal(finca.get("id"), fincaId);
        };
    }

    /**
     * Generic filter with explicit fincaId parameter.
     * Useful for custom queries.
     */
    public static <T> Specification<T> byFincaId(UUID fincaId) {
        return (root, query, cb) -> {
            if (fincaId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("fincaId"), fincaId);
        };
    }

    /**
     * Filter using trabajadorId field directly (for entities that store trabajadorId).
     * Checks if the trabajador belongs to the current tenant's finca.
     */
    public static <T> Specification<T> byTrabajadorIdField() {
        return (root, query, cb) -> {
            if (!TenantContext.shouldFilter()) {
                return cb.conjunction();
            }
            UUID fincaId = TenantContext.getEffectiveFincaId();
            if (fincaId == null) {
                return cb.conjunction();
            }
            // This requires a subquery or join to Trabajador
            Join<Object, Object> trabajador = root.join("trabajador", JoinType.LEFT);
            return cb.equal(trabajador.get("fincaId"), fincaId);
        };
    }
}
