package com.mj.loyalty.common.util;

import org.slf4j.MDC;

public class CorrelationIdUtil {
    public static final String CORRELATION_ID_KEY = "correlationId";

    public static String getCurrentCorrelationId() {
        return MDC.get(CORRELATION_ID_KEY);
    }

    public static void setCurrentCorrelationId(String correlationId) {
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }

    public static void clear() {
        MDC.clear();
    }
}