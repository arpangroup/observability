package com.observability.sre_logging.core.logging;

import java.util.Map;
import java.util.function.Function;

public interface MaskerFunction extends Function<String, String> {
    void configure (Map<String, Object> config);

}
