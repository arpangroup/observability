package com.observability.sre_logging.core.logging;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Masked {
    char MASK_DEFAULT = '*';
    int MASK_LENGTH_DEFAULT = 4;
    int EXPOSED_SUFFIX_LENGTH_DEFAULT = 4;
    boolean MASK_ENTIRE_SHORT_VALUE_DEFAULT = false;

    String MASK_CONFIG = "maskingCharacter";
    String MASK_LENGTH_CONFIG = "maskLength";
    String EXPOSED_SUFFIX_LENGTH_CONFIG = "exposedSuffixLength";
    String MASK_ENTIRE_SHORT_VALUE_CONFIG = "maskEntireShortValue";

    int maskLength() default MASK_LENGTH_DEFAULT;

    int exposedSuffixLength() default EXPOSED_SUFFIX_LENGTH_DEFAULT;

    boolean maskEntireShortValue() default MASK_ENTIRE_SHORT_VALUE_DEFAULT;

    char maskingCharacter() default MASK_DEFAULT;

    Class<? extends MaskerFunction> transformer() default DefaultMasker.class;
}