package com.observability.sre_logging.core.context;

public class CCBContextHolder {
    private final Context context;

    public CCBContextHolder(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cant be null");
        }
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    protected void setContext(Context context) {
        throw new IllegalArgumentException("Context already set");
    }

    public String getApiClientId() {
        return (String) context.get(ContextKeys.API_CLIENT_ID);
    }

    public String getApiSignature() {
        return (String) context.get(ContextKeys.API_SIGNATURE);
    }

    public String getApiSignatureUrl() {
        return (String) context.get(ContextKeys.API_SIGNATURE_URL);
    }

    public String getDpOwner() {
        return (String) context.get(ContextKeys.DP_OWNER);
    }
}
