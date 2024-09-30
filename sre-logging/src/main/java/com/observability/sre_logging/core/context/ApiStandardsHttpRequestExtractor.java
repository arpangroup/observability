package com.observability.sre_logging.core.context;


import com.observability.sre_logging.core.HeaderKeys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApiStandardsHttpRequestExtractor implements HttpRequestExtractor {
    org.slf4j.Logger log =  org.slf4j.LoggerFactory.getLogger(ApiStandardsHttpRequestExtractor.class);

    /**
     * Add CCB API standard request header information to Context/MDC
     * <p>
     * relies on the assumption that the context will filter out null header value
     *
     * @param context
     * @param servletRequest
     */
    @Override
    public void extract(Context context, HttpServletRequest servletRequest) {
        // to both Context and MDC
        context.putMDC (ContextKeys. TRACE_ID, servletRequest.getHeader (HeaderKeys. HttpType. TRANSACTION_ID.getKey())); context.putMDC (ContextKeys.SPAN_ID, servletRequest.getHeader (HeaderKeys. HttpType.SPAN_ID.getKey()));
        context.putMDC (ContextKeys.PARENT_SPAN_ID, servletRequest.getHeader (HeaderKeys.HttpType.PARENT_SPAN_ID.getKey()));
        context.putMDC (ContextKeys.PROTOCOL, servletRequest.getHeader (HeaderKeys.HttpType.PROTOCOL.getKey()) != null ? servletRequest.getHeader (HeaderKeys.HttpType.PROTOCOL.getKey()) : servletRequest.getProtocol());
        if (servletRequest.getHeader (HeaderKeys.HttpType.SESSION_ID.getKey()) != null) {
            context.putMDC(ContextKeys.SESSION_ID, servletRequest.getHeader(HeaderKeys.HttpType.SESSION_ID.getKey()));
        }
        context.putMDC (ContextKeys. SESSION_ID, servletRequest.getHeader (HeaderKeys. HttpType.SESSION_ID.getKey()));
        context.putMDC (ContextKeys.REMOTE_APPLICATION_ID, servletRequest.getHeader (HeaderKeys.HttpType.APPLICATION_ID.getKey()));
        context.putMDC (ContextKeys.REMOTE_APPLICATION_MODULE, servletRequest.getHeader (HeaderKeys.HttpType.APPLICATION_MODULE.getKey()));
        context.putMDC (ContextKeys.REMOTE_HOST, servletRequest.getRemoteHost());
        context.putMDC (ContextKeys. CORRELATION_ID, servletRequest.getHeader (HeaderKeys. HttpType.CORRELATION_ID.getKey()));
        context.putMDC (ContextKeys.USER_AGENT, servletRequest.getHeader (HeaderKeys. HttpType.USER_AGENT.getKey()));
        context.putMDC (ContextKeys.USER, servletRequest.getRemoteUser());
        context.putMDC (ContextKeys.CHANNEL_TYPE, servletRequest.getHeader (HeaderKeys. HttpType.CHANNEL_TYPE.getKey()));
        // to Context only
        context.put(ContextKeys.API_CLIENT_ID, servletRequest.getHeader (HeaderKeys. HttpType.API_CLIENT_ID.getKey()), false);
        context.put(ContextKeys.API_SIGNATURE, servletRequest.getHeader (HeaderKeys. HttpType.API_SIGNATURE.getKey()), false);
        context.put(ContextKeys.API_SIGNATURE_URL, servletRequest.getHeader (HeaderKeys. HttpType.API_SIGNATURE_URL.getKey()), false);
        context.put(ContextKeys.DP_OWNER, servletRequest.getHeader (HeaderKeys.HttpType.DP_OWNER.getKey()), false);
        context.put(ContextKeys.PATH_PARAMS, servletRequest.getHeader (HeaderKeys.HttpType.PATH_PARAMS.getKey()), false);
        context.put(ContextKeys.QUERY_PARAMS, servletRequest.getHeader (HeaderKeys. HttpType.QUERY_PARAMS.getKey()), false);
        putSessionCacheIntoContext(context, servletRequest.getHeader (HeaderKeys.HttpType.SESSION_CACHE.getKey()));
    }

    private void putSessionCacheIntoContext(Context context, String sessionCache) {
        if (StringUtils.hasText(sessionCache)) {
            Map<String, String> sessionCacheCookieElementsMap = Stream.of(sessionCache.split(";")).map(sessionCacheElement -> Arrays.stream(sessionCacheElement.trim().split("=")).filter(elementKeyValueArray -> elementKeyValueArray.length() == 2).collect(Collectors.toMap(elementKeyValueArray -> elementKeyValueArray[0], elementKeyValueArray -> elementKeyValueArray[1])));
            boolean isValidSessionCache = true;
            if (null != sessionCacheCookieElementsMap && !sessionCacheCookieElementsMap.isEmpty()) {
                List<String> sessionCacheElementsList = List.of (ContextKeys.SESSION_CACHE_KEY, ContextKeys.SESSION_CACHE_COOKIE_NM, ContextKeys.SESSION_CACHE_COOKIE_VAL, ContextKeys.SESSION_CACHE_ADDR);
                for (String sessionCacheElement: sessionCacheElementsList) {
                    if (!StringUtils.hasText(sessionCacheCookieElementsMap.get(sessionCacheElement))) {
                        isValidSessionCache = false;
                        break;
                    }
                }
            } else {
                isValidSessionCache = false;
            }
            if (isValidSessionCache) {
                context.put(ContextKeys.SESSION_CACHE, sessionCache, false);
                sessionCacheCookieElementsMap.forEach((key, value)-> context.put(key, value, false));
            } else {
                log.error("Invalid sessioncache");
            }
        } else {
            log.debug("sessioncache not found in request");
        }
    }
}
