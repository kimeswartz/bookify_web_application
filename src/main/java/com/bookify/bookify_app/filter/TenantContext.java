package com.bookify.bookify_app.filter;

// ********************************************************************************************
// * TenantContext holds the current clinic (tenant) identifier in a ThreadLocal variable.    *
// * Provides methods to set, get, and clear the clinic ID for the current request thread.    *
// * WHY: Enables multi-tenant request handling by isolating clinic context per request.      *
// ********************************************************************************************

public final class TenantContext {

    private static final ThreadLocal<String> CLINIC = new ThreadLocal<>();

    private TenantContext() {}

    public static void setClinicId(String id) {
        CLINIC.set(id);
    }

    public static String getClinicId() {
        return CLINIC.get();
    }

    public static void clear() {
        CLINIC.remove();
    }
}
