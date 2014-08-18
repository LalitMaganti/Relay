package co.fusionx.relay.parser.main;

import java.util.List;

import co.fusionx.relay.util.IRCUtils;

public class MentionParser {

    public static boolean onMentionableCommand(final String message, final String userNick) {
        final List<String> list = IRCUtils.splitRawLine(message, false);
        for (final String s : list) {
            if (s.startsWith(userNick)) {
                return true;
            }
        }
        return false;
    }
}