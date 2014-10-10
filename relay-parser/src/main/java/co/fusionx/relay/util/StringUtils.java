package co.fusionx.relay.util;

import java.util.Collection;

public class StringUtils {

    public static String concatenateStringList(final Collection<String> list) {
        final StringBuilder builder = new StringBuilder();
        for (final String item : list) {
            builder.append(item).append(" ");
        }
        return builder.toString().trim();
    }

    public static boolean isEmpty(final String string) {
        return org.apache.commons.lang3.StringUtils.isEmpty(string);
    }
}