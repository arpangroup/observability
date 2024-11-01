package com.observability.sre_logging.core.util;

public class PlatformDetectionUtil {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger (PlatformDetectionUtil.class);
    public static final String AWS_REGION_ENV_VAR = "AWS_REGION";
    public static final String VCAP_APPLICATION_ENV_VAR = "VCAP_APPLICATION";
    public static final String KUBERNETES_PORT_ENV_VAR = "KUBERNETES_PORT";
    public static final String KUBERNETES_SERVICE_HOST_ENV_VAR = "KUBERNETES_SERVICE_HOST";
    public static final String AWS_ECS_FARGATE_V4_ENV_VAR = "ECS_CONTAINER_METADATA_URI_V4";

    private PlatformDetectionUtil() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isCloudFoundryDetected() {
        return isEnvVarNonEmpty (VCAP_APPLICATION_ENV_VAR);
    }

    public static boolean isGkpDetected() {
        return isEnvVarNonEmpty (AWS_REGION_ENV_VAR) && isKubernetesDetected();
    }

    public static boolean isAwsEksDetected() {
        return isEnvVarNonEmpty (AWS_REGION_ENV_VAR) && isKubernetesDetected();
    }

    public static boolean isAwsEcsFargateDetected() {
        return isEnvVarNonEmpty (AWS_REGION_ENV_VAR) && isEnvVarNonEmpty(AWS_ECS_FARGATE_V4_ENV_VAR);
    }

    public static boolean isKubernetesDetected () {
        return isEnvVarNonEmpty (KUBERNETES_PORT_ENV_VAR) || isEnvVarNonEmpty (KUBERNETES_SERVICE_HOST_ENV_VAR);
    }

    private static boolean isEnvVarNonEmpty(String envVar) {
        try {
            String value = System.getenv(envVar);
            return ! (value == null || value.isEmpty());
        } catch (Exception e) {
            log.error("Unable to process environment variable: " + envVar, e);
            throw e;
        }
    }
}
