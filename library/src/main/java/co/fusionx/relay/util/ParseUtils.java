package co.fusionx.relay.util;

import com.google.common.base.CharMatcher;

import org.apache.commons.lang3.StringUtils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {

    private static final Pattern QUOTE_SPLIT_PATTERN = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

    public static String removeInitialColonIfExists(final String line) {
        return line.charAt(0) == ':' ? line.substring(1) : line;
    }

    /**
     * Split the line received from the server into its components
     *
     * @param input          the line received from the server
     * @param colonDelimiter whether a colon means the rest of the line should be added in one
     * @return the parsed list
     */
    public static List<String> splitRawLine(final String input, final boolean colonDelimiter) {
        final List<String> stringParts = new ArrayList<>();
        if (StringUtils.isEmpty(input)) {
            return stringParts;
        }

        String trimmedInput = CharMatcher.WHITESPACE.trimFrom(input);
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

    public static String extractAndRemovePrefix(final List<String> parsedArray) {
        if (parsedArray.size() == 0) {
            return "";
        }
        if (parsedArray.get(0).charAt(0) == ':') {
            return parsedArray.remove(0).substring(1);
        }
        return "";
    }

    public static List<String> splitRawLineWithQuote(final String input) {
        final List<String> list = new ArrayList<>();
        final Matcher m = QUOTE_SPLIT_PATTERN.matcher(input);
        while (m.find()) {
            list.add(m.group(1).replace("\"", ""));
        }
        return list;
    }

    public static String getNickFromPrefix(final String prefix) {
        String nick;
        if (prefix.contains("!")) {
            nick = StringUtils.substringBefore(prefix, "!");
        } else {
            nick = prefix;
        }
        return nick;
    }

    public static boolean isCommandCode(final String command) {
        return StringUtils.isNumeric(command);
    }
}