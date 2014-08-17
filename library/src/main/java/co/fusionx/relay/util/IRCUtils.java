package co.fusionx.relay.util;

import com.google.common.base.CharMatcher;

import org.apache.commons.lang3.StringUtils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRCUtils {

    private static final Pattern QUOTE_SPLIT_PATTERN = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

    public static String getNickFromRaw(final String rawSource) {
        String nick;
        if (rawSource.contains("!") && rawSource.contains("@")) {
            nick = StringUtils.substringBefore(rawSource, "!");
        } else {
            nick = rawSource;
        }
        return nick;
    }

    /**
     * Split the line received from the server into it's components
     *
     * @param input          the line received from the server
     * @param colonDelimiter whether a colon means the rest of the line should be added in one go
     * @return the parsed list
     */
    public static List<String> splitRawLine(final String input, final boolean colonDelimiter) {
        final List<String> stringParts = new ArrayList<>();
        if (TextUtils.isEmpty(input)) {
            return stringParts;
        }

        final String colonLessLine = input.charAt(0) == ':' ? input.substring(1) : input;
        // Heavily optimized version string split by space with all characters after :
        // added as a single entry. Under benchmarks, its faster than StringTokenizer,
        // String.split, toCharArray, and charAt
        String trimmedInput = CharMatcher.WHITESPACE.trimFrom(colonLessLine);
        int pos = 0, end;
        while ((end = trimmedInput.indexOf(' ', pos)) >= 0) {
            stringParts.add(trimmedInput.substring(pos, end));
            pos = end + 1;
            if (trimmedInput.charAt(pos) == ':' && colonDelimiter) {
                stringParts.add(trimmedInput.substring(pos + 1));
                return stringParts;
            }
        }
        // No more spaces, add last part of line
        stringParts.add(trimmedInput.substring(pos));
        return stringParts;
    }

    public static void removeFirstElementFromList(final List<String> list, final int noOfTimes) {
        for (int i = 1; i <= noOfTimes; i++) {
            list.remove(0);
        }
    }

    public static String concatenateStringList(final Collection<String> list) {
        final StringBuilder builder = new StringBuilder();
        for (final String item : list) {
            builder.append(item).append(" ");
        }
        return builder.toString().trim();
    }


    /**
     * Returns the 32bit dotted format of the provided long ip.
     *
     * @param ip the long ip
     * @return the 32bit dotted format of <code>ip</code>
     * @throws IllegalArgumentException if <code>ip</code> is invalid
     */
    public static String ipDecimalToString(final long ip) {
        // if ip is bigger than 255.255.255.255 or smaller than 0.0.0.0
        if (ip > 4294967295l || ip < 0) {
            throw new IllegalArgumentException("invalid ip");
        }
        final StringBuilder ipAddress = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            int shift = i * 8;
            ipAddress.append((ip & (0xff << shift)) >> shift);
            if (i > 0) {
                ipAddress.append(".");
            }
        }
        return ipAddress.toString();
    }

    public static List<String> splitRawLineWithQuote(final String input) {
        final List<String> list = new ArrayList<>();
        final Matcher m = QUOTE_SPLIT_PATTERN.matcher(input);
        while (m.find()) {
            list.add(m.group(1).replace("\"", ""));
        }
        return list;
    }
}
