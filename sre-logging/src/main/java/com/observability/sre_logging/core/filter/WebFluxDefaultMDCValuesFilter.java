package com.observability.sre_logging.core.filter;


import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

public class WebFluxDefaultMDCValuesFilter implements WebFilter {
    private final Map<String, String> webfluxDefaultMdcValues;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        Mono<Void> result;
        try {
            if (!StringUtils.isEmpty(webfluxDefaultMdcValues)) {
                webfluxDefaultMdcValues.forEach(MDC::put);
            }
            result = webFilterChain.filter(serverWebExchange);
        } finally {
            webfluxDefaultMdcValues.keySet().forEach(MDC::remove);
        }
        return result;
    }

    public WebFluxDefaultMDCValuesFilter(final Map<String, String> webfluxDefaultMdcValues) {
        this.webfluxDefaultMdcValues = webfluxDefaultMdcValues;
    }
}
