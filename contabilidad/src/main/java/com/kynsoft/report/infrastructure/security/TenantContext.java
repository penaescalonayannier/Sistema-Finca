package com.kynsoft.report.infrastructure.security;

import com.kynsoft.report.domain.dto.Rol;

import java.util.UUID;

public class TenantContext {

    private static final ThreadLocal<TenantInfo> context = new ThreadLocal<>();

    private TenantContext() {
        // Utility class
    }

    public static void set(TenantInfo info) {
        context.set(info);
    }

    public static TenantInfo get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }

    public static UUID getUsuarioId() {
        TenantInfo info = get();
        return info != null ? info.getUsuarioId() : null;
    }

    public static UUID getFincaId() {
        TenantInfo info = get();
        return info != null ? info.getFincaId() : null;
    }

    public static UUID getTrabajadorId() {
        TenantInfo info = get();
        return info != null ? info.getTrabajadorId() : null;
    }

    public static Rol getRol() {
        TenantInfo info = get();
        return info != null ? info.getRol() : null;
    }

    public static boolean isAdmin() {
        return getRol() == Rol.ADMIN;
    }

    public static UUID getFincaSeleccionada() {
        TenantInfo info = get();
        return info != null ? info.getFincaSeleccionada() : null;
    }

    public static void setFincaSeleccionada(UUID fincaId) {
        TenantInfo info = get();
        if (info != null) {
            info.setFincaSeleccionada(fincaId);
        }
    }

    /**
     * Returns the effective finca ID for filtering.
     * For ADMIN with fincaSeleccionada: returns fincaSeleccionada
     * For ADMIN without selection: returns null (no filter)
     * For USER/RESPONSABLE: returns their fincaId
     */
    public static UUID getEffectiveFincaId() {
        if (isAdmin()) {
            return getFincaSeleccionada(); // null means no filter for admin
        }
        return getFincaId();
    }

    /**
     * Returns true if tenant filtering should be applied.
     */
    public static boolean shouldFilter() {
        if (isAdmin()) {
            return getFincaSeleccionada() != null;
        }
        return true; // USER/RESPONSABLE always filter
    }
}
