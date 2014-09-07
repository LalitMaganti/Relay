package co.fusionx.relay.internal.parser.main;

import java.util.List;

import co.fusionx.relay.util.ParseUtils;

public class MentionParser {

    public static boolean onMentionableCommand(final String message, final String userNick) {
        final List<String> list = ParseUtils.splitRawLine(message, false);
        for (final String s : list) {
            if (s.startsWith(userNick)) {
                return true;
            }
        }
        return false;
    }
}