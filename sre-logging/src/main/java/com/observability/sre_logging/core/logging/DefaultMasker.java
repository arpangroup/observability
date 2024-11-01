package com.observability.sre_logging.core.logging;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class DefaultMasker implements MaskerFunction{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DefaultMasker.class);
    private char maskCharacter = Masked. MASK_DEFAULT;
    private int maskLength = Masked.MASK_LENGTH_DEFAULT;
    private int exposedSuffixLength = Masked. EXPOSED_SUFFIX_LENGTH_DEFAULT;
    private boolean maskEntireShortValue = Masked.MASK_ENTIRE_SHORT_VALUE_DEFAULT;

    public void configure (Map<String, Object> config) {
        if (config.containsKey(Masked. MASK_CONFIG)) {
            maskCharacter = String.valueOf(config.get(Masked.MASK_CONFIG)).charAt(0);
        }
        if (config.containsKey(Masked.MASK_LENGTH_CONFIG)) {
            maskLength = (Integer) config.get(Masked.MASK_LENGTH_CONFIG);
        }
        if (config.containsKey(Masked. EXPOSED_SUFFIX_LENGTH_CONFIG)) {
            exposedSuffixLength = (Integer) config.get(Masked. EXPOSED_SUFFIX_LENGTH_CONFIG);
        }
        if (config.containsKey(Masked.MASK_ENTIRE_SHORT_VALUE_CONFIG)) {
            maskEntireShortValue = (Boolean) config.get(Masked.MASK_ENTIRE_SHORT_VALUE_CONFIG);
        }
    }

    @Override
    public String apply(String value) {
        StringBuilder sb = new StringBuilder();
        int exposedLength = getExposedSuffixLength();
        int valueLength = value.length();
        int unExposedLength;
        if (valueLength > exposedLength) {
            unExposedLength = valueLength - exposedLength;
            while (unExposedLength < exposedLength) {
                unExposedLength++;
                exposedLength--;
            }
        } else if (isMaskEntireShortValue()) {
            unExposedLength = valueLength;
        } else {
            if (valueLength > 0) {
                unExposedLength = valueLength - 1;
            } else {
                unExposedLength = 0;
            }
        }
        if (getMaskLength() > 0) {
            sb.append(StringUtils.repeat(getMaskCharacter(), valueLength > 0 ? getMaskLength() : 0));
        } else {
            sb.append(StringUtils.repeat(getMaskCharacter(), unExposedLength));
        }
        sb.append(value.substring(unExposedLength));
        return sb.toString();
    }

    public char getMaskCharacter() {
        return maskCharacter;
    }

    public int getMaskLength() {
        return maskLength;
    }

    public int getExposedSuffixLength() {
        return exposedSuffixLength;
    }

    public boolean isMaskEntireShortValue() {
        return maskEntireShortValue;
    }
}
