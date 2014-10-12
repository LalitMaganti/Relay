package co.fusionx.relay.parser.rfc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import co.fusionx.relay.constant.ChannelPrefix;
import co.fusionx.relay.parser.CommandParser;

public class ModeParser implements CommandParser {

    private final ModeObserver mModeObserver;

    public ModeParser(final ModeObserver modeObserver) {
        mModeObserver = modeObserver;
    }

    private static List<ModeChange> parseModeString(final List<String> parsedArray) {
        final List<ModeChange> modeChanges = new ArrayList<>();

        // Turn the list into a queue
        final Queue<String> queue = new ArrayDeque<>(parsedArray);
        while (!queue.isEmpty()) {
            // Get the mode at the start of the list
            final String mode = queue.poll();
            // This could be a mode, or a mode param or null
            String peeked = queue.peek();

            // Create the params list
            final List<String> params = new ArrayList<>();

            // If there is a +/- then it's a mode
            while (peeked != null && !peeked.startsWith("+") && !peeked.startsWith("-")) {
                // Only continue if it's not a mode - it has to be a mode param
                final String param = queue.remove();
                params.add(param);

                peeked = queue.peek();
            }
            modeChanges.add(new ModeChange(mode, params));
        }
        return modeChanges;
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
        final List<ModeChange> modeChanges = ModeParser.parseModeString(parsedArray);

        mModeObserver.onChannelMode(prefix, channelName, modeChanges);
    }

    private void parseUserModeCommand(final List<String> parsedArray, final String prefix,
            final String recipient) {
        final List<ModeChange> modeChanges = ModeParser.parseModeString(parsedArray);

        mModeObserver.onUserMode(prefix, recipient, modeChanges);
    }

    public interface ModeObserver {

        public void onChannelMode(final String prefix, final String channelName,
                final List<ModeChange> modeChanges);

        public void onUserMode(final String prefix, final String recipient,
                final List<ModeChange> modeChanges);
    }

    public static class ModeChange {

        private final String mMode;

        private final List<String> mModeParams;

        public ModeChange(final String mode, final List<String> modeParams) {
            mMode = mode;
            mModeParams = modeParams;
        }

        public String getMode() {
            return mMode;
        }

        public List<String> getModeParams() {
            return mModeParams;
        }
    }
}