package com.kynsoft.report.infrastructure.security;

import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

/**
 * Validates tenant access for write operations.
 */
public class TenantValidator {

    private TenantValidator() {
        // Utility class
    }

    /**
     * Validates that the current user can write to the specified finca.
     * ADMIN can write to any finca.
     * USER/RESPONSABLE can only write to their own finca.
     *
     * @param targetFincaId the finca to write to
     * @throws AccessDeniedException if access is denied
     */
    public static void validateWriteAccess(UUID targetFincaId) {
        if (TenantContext.isAdmin()) {
            return; // ADMIN can write anywhere
        }

        UUID userFincaId = TenantContext.getFincaId();
        if (userFincaId == null) {
            throw new AccessDeniedException("Usuario no tiene finca asignada");
        }

        if (!userFincaId.equals(targetFincaId)) {
            throw new AccessDeniedException("No tiene permiso para operar en esta finca");
        }
    }

    /**
     * Gets the finca ID to use for write operations.
     * For USER/RESPONSABLE: always their own finca
     * For ADMIN: the selected finca or throws if none selected
     *
     * @return the finca ID to use
     * @throws IllegalStateException if ADMIN has no finca selected
     */
    public static UUID getWriteFincaId() {
        if (!TenantContext.isAdmin()) {
            UUID fincaId = TenantContext.getFincaId();
            if (fincaId == null) {
                throw new IllegalStateException("Usuario no tiene finca asignada");
            }
            return fincaId;
        }

        // ADMIN must have a finca selected for write operations
        UUID selected = TenantContext.getFincaSeleccionada();
        if (selected != null) {
            return selected;
        }

        // If no finca selected, admin must specify it explicitly
        throw new IllegalStateException("Administrador debe seleccionar una finca para esta operación");
    }

    /**
     * Gets the finca ID for write, with a fallback for ADMIN.
     * If ADMIN has no selection but provides an explicit fincaId, use that.
     *
     * @param explicitFincaId finca ID provided in the request
     * @return the finca ID to use
     */
    public static UUID getWriteFincaId(UUID explicitFincaId) {
        if (!TenantContext.isAdmin()) {
            // Non-admin always uses their finca, ignore explicit
            return TenantContext.getFincaId();
        }

        // ADMIN: prefer explicit, then selected
        if (explicitFincaId != null) {
            return explicitFincaId;
        }
        if (TenantContext.getFincaSeleccionada() != null) {
            return TenantContext.getFincaSeleccionada();
        }

        throw new IllegalStateException("Debe especificar la finca para esta operación");
    }

    /**
     * Validates read access to a specific entity's finca.
     * Used to verify a user can access a specific record.
     *
     * @param entityFincaId the finca of the entity being accessed
     * @throws AccessDeniedException if access is denied
     */
    public static void validateReadAccess(UUID entityFincaId) {
        if (TenantContext.isAdmin()) {
            return; // ADMIN can read anything
        }

        UUID userFincaId = TenantContext.getFincaId();
        if (userFincaId == null || !userFincaId.equals(entityFincaId)) {
            throw new AccessDeniedException("No tiene permiso para acceder a este registro");
        }
    }
}
