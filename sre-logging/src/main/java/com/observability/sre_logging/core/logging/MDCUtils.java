package com.observability.sre_logging.core.logging;

import com.observability.sre_logging.core.context.Context;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.Set;

public class MDCUtils {
    private MDCUtils() {
        throw new UnsupportedOperationException("this is a utility class and cannot be instantiated");
    }

    public static void cleanup(@NonNull Set<String> keys) {
        keys.forEach(MDC::remove);
    }

    public static void initializeMDC(Context context) {
        Optional.ofNullable(context).ifPresent(ctx -> ctx.getMdcKeys().forEach(key -> MDC.put(key, context.getString(key))));
    }

    public static void cleanupMDC(Context context) {
        Optional.ofNullable(context).ifPresent(ctx -> ctx.getMdcKeys().forEach(MDC::remove));
    }

    public static void remove(@NonNull String key) {
        MDC.remove(key);
    }
}
