package com.observability.sre_logging.core.context.filter;

import com.observability.sre_logging.core.context.Context;
import com.observability.sre_logging.core.context.ContextKeys;
import com.observability.sre_logging.core.context.HttpRequestExtractor;
import com.observability.sre_logging.core.logging.MDCUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;

import java.io.IOException;

public class ContextFilter implements Filter {
    org.slf4j.Logger log =  org.slf4j.LoggerFactory.getLogger(ContextFilter.class);
    private ObjectProvider<HttpRequestExtractor> httpRequestExtractorSet;
    private boolean enabled;
    private String applicationName;
    private String applicationId;

    public ContextFilter(ObjectProvider<HttpRequestExtractor> httpRequestExtractorSet, boolean enabled, Environment environment) {
        this.httpRequestExtractorSet = httpRequestExtractorSet;
        this.enabled = enabled;
        this.applicationId = environment.getProperty("spring.application.id", "unset");
        this.applicationName = environment.getProperty("spring.application.name", "unset");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // not required
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Context context = Context.getScopedContext();
        try {
            // Add request level data to context
            if (enabled && servletRequest instanceof HttpServletRequest) {

            }
            // Add global application metadata to context
            context.put(ContextKeys.APPLICATION_ID, this.applicationId);
            context.put(ContextKeys.APPLICATION_MODULE, this.applicationName);
            MDCUtils.initializeMDC(context);
            filterChain.doFilter(servletRequest, servletResponse);

        } finally {
            MDCUtils.cleanupMDC(context);
            context.clear();
        }
    }

    @Override
    public void destroy() {
        // not required
    }
}
