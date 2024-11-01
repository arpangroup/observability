package com.observability.sre_logging.core.logging;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class AlphaNumericMasker extends DefaultMasker {
    private static final Pattern ALPHANUMERIC_GROUPS = Pattern.compile("[0-9A-Za-z]+");
    private static final Pattern ALPHANUMERICS = Pattern.compile("[0-9A-Za-z]");

    @Override
    public String apply(String propertyValue) {
        if (getMaskLength() > 1) {
            String replacement = StringUtils.repeat(getMaskCharacter(), getMaskLength()); return ALPHANUMERIC_GROUPS.matcher (propertyValue).replaceAll(replacement);
        } else {
            String replacement = String.valueOf(getMaskCharacter());
            return ALPHANUMERICS.matcher (propertyValue).replaceAll(replacement);
        }
    }
}
