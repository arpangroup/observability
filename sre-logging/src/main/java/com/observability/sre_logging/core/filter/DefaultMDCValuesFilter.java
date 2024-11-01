package com.observability.sre_logging.core.filter;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

public class DefaultMDCValuesFilter implements Filter {
    private final Map<String, String> httpDefaultMdcValues;

    @Override
    public void init (final FilterConfig filterConfig) {
        // not required
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        try {
            if (!StringUtils.isEmpty(httpDefaultMdcValues)) {
            }
            httpDefaultMdcValues.forEach(MDC::put);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            httpDefaultMdcValues.keySet().forEach (MDC::remove);
        }
    }

    @Override
    public void destroy() {
        // not required
    }


    public DefaultMDCValuesFilter(final Map<String, String> httpDefaultMdcValues) {
        this.httpDefaultMdcValues = httpDefaultMdcValues;
    }
}