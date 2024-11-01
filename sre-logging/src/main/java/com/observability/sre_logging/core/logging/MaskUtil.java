package com.observability.sre_logging.core.logging;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaskUtil {

    private MaskUtil() {
        throw new java.lang. UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Runs the object through the default
     *
     * @param object object to mask
     * @param <T>    The type of the object, maybe Object?
     * @return       The masked string equivalent
     */
    public static <T> Supplier<String> mask (@NonNull T object) {
        if (object == null) {
            throw new java.lang.NullPointerException("object is marked non-null but is null");
        }
        return () -> new DefaultMasker().apply(object.toString());
    }


    /**
     * Mask the given object's alpha and numeric characters.
     *
     * @param object object to mask
     * @param <T>    The type of the object, maybe Object?
     * @return       String serialization of object with alphanumeric masking
     */
    public static <T> Supplier<String> alpha (@NonNull T object) {
        if (object == null) {
            throw new java.lang.NullPointerException("object is marked non-null but is null");
        }
        return () -> new AlphaNumericMasker().apply(object.toString());
    }

    public static <T> String alphaNow (@NonNull T object) {
        if (object == null) {
            throw new java.lang.NullPointerException("object is marked non-null but is null");
        }
        return new AlphaNumericMasker().apply(object.toString());
    }

    public static <T> String maskNow (@NonNull T object) {
        if (object == null) {
            throw new java.lang.NullPointerException("object is marked non-null but is null");
        }
        return new DefaultMasker().apply(object.toString());
    }

    private static final String DEFAULT_MASK_CHAR ="*";


    /**
     *
     * The method performs masking on the specified string message based on appropriate patterns passed.
     * <p>
     * Example 1: Masking single field
     * Portion of the string to mask: email=john.doe@gmail.com,
     * Patterns passed as a list with single entry: email=([\d]\D]{0,8})
     * Resulting masked string: email=********@gmail.com
     * <p>
     * Example 2: Masking multiple fields
     * Portions of the string to mask: email-john.doe@gmail.com, phone=1234567890
     * Patterns passed as a list with 2 entries: email=([\d|\D]{0,8}) phone=([\d|\D]{0,6})
     * Resulting masked string: email=********@gmail.com, phone=******7890
     * <p>
     * Example 3: Masking single field in a JSON string
     * Portion of the string to mask: "email":"john.doe@gmail.com"
     * Patterns passed as a list with single entry: "email": ([\d\D]{0,8})
     * Resulting masked string: "email":********e@gmail.com"
     * <p>
     * Note: You can also pass in regular expression which can pass either of the patters
     * i.e. phone=([\d|\D]{0,6}) | "phone": ([\d\D]{0,6}) will match text pattern as well as json format
     *
     * @param message       The string that needs to be masked
     * @param maskPatterns  Regular expressions that need to be matched. The expression should contain the number of characters to match // @param mode Appropriate mode which needs to be enabled during the match
     * @param maskChar      The character that needs to be used to replace the matched pattern
     * @return Masked string
     */
    public static String mask (String message, List<String> maskPatterns, int mode, String maskChar) {
        if (StringUtils.isBlank (message)) {
            return null;
        }
        if (maskPatterns == null || maskPatterns.isEmpty()) {
            return message;
        }
        StringBuilder sb = new StringBuilder(message);
        maskPatterns.forEach (pattern -> {
            Matcher m = Pattern.compile(pattern, mode). matcher (sb.toString());
            while (m.find()) {
            }
            replaceGroup (m, sb, (StringUtils.isBlank (maskChar)? DEFAULT_MASK_CHAR: maskChar));
        });
        return sb.toString();
    }

    // This method replaces the matched group with the masking character.
    private static void replaceGroup (Matcher m, StringBuilder sb, String maskChar) {
        for (int i = 1; i <= m.groupCount(); i++) {
            if (m.group(i) != null) {
                sb.replace(m.start(i), m.end(i), StringUtils.repeat(maskChar, (m.end(i) - m.start(i))));
            }
        }
    }
}
