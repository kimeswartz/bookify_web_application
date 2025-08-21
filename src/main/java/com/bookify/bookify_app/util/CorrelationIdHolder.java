package com.bookify.bookify_app.util;

// ********************************************************************************************
// * CorrelationIdHolder stores the correlation ID in a ThreadLocal for the current request.  *
// * Provides methods to set, get, and clear the ID, ensuring request-specific traceability.  *
// ********************************************************************************************

public final class CorrelationIdHolder {
    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    private CorrelationIdHolder() {}

    public static void setId(String id) {
        CORRELATION_ID.set(id);
    }

    public static String getId() {
        return CORRELATION_ID.get();
    }

    public static void clear() {
        CORRELATION_ID.remove();
    }

}
