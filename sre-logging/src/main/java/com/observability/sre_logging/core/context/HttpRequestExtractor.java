package com.observability.sre_logging.core.context;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface HttpRequestExtractor {
    void extract(Context context, HttpServletRequest request);
}
