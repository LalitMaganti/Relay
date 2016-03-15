package co.fusionx.relay.internal.parser.main;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.fusionx.relay.util.ParseUtils;

public class MentionParser {
    private static final String MENTION_PATTERN_FORMAT = "(^|[^\\d\\w])%s([^\\d\\w]|$)";
    public static boolean onMentionableCommand(final String message, final String userNick) {
        final List<String> list = ParseUtils.splitRawLine(message, false);
        final Pattern pattern =
                Pattern.compile(String.format(Locale.US, MENTION_PATTERN_FORMAT, userNick));
        for (final String s : list) {
            if (pattern.matcher(s).matches()) {
                return true;
            }
        }
        return false;
    }
}