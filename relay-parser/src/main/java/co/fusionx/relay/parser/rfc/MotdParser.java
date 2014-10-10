package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.parser.ReplyCodeParser;

public class MotdParser implements ReplyCodeParser {

    private final MotdObserver mMotdObserver;

    public MotdParser(final MotdObserver motdObserver) {
        mMotdObserver = motdObserver;
    }

    @Override
    public void parseReplyCode(final List<String> parsedArray, final int code) {
        final String message = parsedArray.get(0);

        mMotdObserver.onMotd(code, message);
    }

    public static interface MotdObserver {

        public void onMotd(final int code, final String motdMessage);
    }
}