package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.constant.ChannelPrefix;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.util.StringUtils;

public class ModeParser implements CommandParser {

    private final ModeObserver mModeObserver;

    public ModeParser(final ModeObserver modeObserver) {
        mModeObserver = modeObserver;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String recipient = parsedArray.remove(0);

        final char firstChar = recipient.charAt(0);

        if (ChannelPrefix.isPrefix(firstChar)) {
            parseChannelModeCommand(parsedArray, prefix, recipient);
        } else {
            parseUserModeCommand(parsedArray, prefix, recipient);
        }
    }

    private void parseChannelModeCommand(final List<String> parsedArray, final String prefix,
            final String channelName) {
        /*final List<ModeChange> params = new ArrayList<>();

        // Turn the list into a queue
        final Queue<String> queue = new ArrayDeque<>(parsedArray);
        while (!queue.isEmpty()) {
            // Get the mode at the start of the list
            final String mode = queue.poll();
            // This could be a mode, or a mode param or null
            String peeked = queue.peek();

            // If there is a +/- then it's a mode
            while (peeked != null && !peeked.startsWith("+") && !peeked.startsWith("-")) {
                // Only continue if it's not a mode - it has to be a mode param
                final String param = queue.remove();
                parseChannelMode(param);

                peeked = queue.peek();
            }
        }*/

        final String modeString = StringUtils.concatenateStringList(parsedArray);
        mModeObserver.onChannelMode(prefix, channelName, modeString);
    }

    private void parseUserModeCommand(final List<String> parsedArray, final String prefix,
            final String recipient) {
        final String modeString = StringUtils.concatenateStringList(parsedArray);
        mModeObserver.onUserMode(prefix, recipient, modeString);
    }

    /*private void parseChannelMode(String param) {
        final char first = param.charAt(0);
        param = param.substring(1);

        for (final char modeChar : param.toCharArray()) {

        }
    }*/

    public interface ModeObserver {

        public void onChannelMode(final String prefix, final String channelName,
                final String modeString);

        public void onUserMode(String prefix, String recipient, String modeString);
    }

    public class ModeChange {

    }
}