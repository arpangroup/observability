package com.observability.sre_logging.core;


import com.observability.sre_logging.core.context.ContextKeys;

public class HeaderKeys {
    public enum HttpType {
        APPLICATION_ID( "Application-Id", ContextKeys.APPLICATION_ID),// Log NFR
        APPLICATION_MODULE ( "Application-Module", ContextKeys.APPLICATION_MODULE),
        CORRELATION_ID ("Correlation-Id", ContextKeys. CORRELATION_ID),
        SESSION_ID ("Session-Id", ContextKeys.SESSION_ID), // Log NFR
        TRANSACTION_ID( "Trace-Id", ContextKeys. TRACE_ID), // Log NFR
        PROTOCOL ("Protocol", ContextKeys. PROTOCOL), // Log NFR
        SPAN_ID("Span-Id", ContextKeys.SPAN_ID),
        PARENT_SPAN_ID("Parent-Span-Id", ContextKeys. PARENT_SPAN_ID),
        AUTHORIZATION( "Authorization", null),
        USER_AGENT("User-Agent", ContextKeys.USER_AGENT),
        AUTHORIZATION_2( "Authorization2", null),
        AUTHORIZATION_2_URL( "Authorization2Url", null),
        CHANNEL_TYPE( "Channel-Type", ContextKeys.CHANNEL_TYPE),
        PATH_PARAMS( "Path-params", ContextKeys.PATH_PARAMS),
        QUERY_PARAMS( "Query-params", ContextKeys.QUERY_PARAMS),
        API_CLIENT_ID( "api-clientId", ContextKeys.API_CLIENT_ID),
        API_SIGNATURE ( "api-signature", ContextKeys.API_SIGNATURE),
        API_SIGNATURE_URL( "api-signatureUrl", ContextKeys.API_SIGNATURE_URL),
        DP_OWNER( "api-owner", ContextKeys.DP_OWNER),
        SESSION_CACHE ( "sessioncache", ContextKeys.SESSION_CACHE),
        LTM_COOKIE_NMC ("cookie_nm", ContextKeys.SESSION_CACHE_COOKIE_NM),
        LTM_COOKIE_VAL("cookie_val", ContextKeys.SESSION_CACHE_COOKIE_VAL);

        private final String key;
        private final String contextKey;

        HttpType(String key, String contextKey) {
            this.key = key;
            this.contextKey = contextKey;
        }

        public String getKey() {
            return key;
        }

        public String getContextKey() {
            return contextKey;
        }
    }
}
