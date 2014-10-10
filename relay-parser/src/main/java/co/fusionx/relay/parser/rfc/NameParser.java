package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.constant.ChannelType;
import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.parser.ReplyCodeParser;
import co.fusionx.relay.util.ParseUtils;

public class NameParser implements ReplyCodeParser {

    private final NameObserver mObserver;

    public NameParser(final NameObserver observer) {
        mObserver = observer;
    }

    @Override
    public void parseReplyCode(final List<String> parsedArray, final int code) {
        if (code == ReplyCodes.RPL_NAMREPLY) {
            parseNameReply(parsedArray);
        } else {
            parseNameFinished();
        }
    }

    private void parseNameReply(final List<String> parsedArray) {
        final String typeString = parsedArray.get(0);
        final String channelName = parsedArray.get(1);
        final String rawNickList = parsedArray.get(2);

        final ChannelType type = ChannelType.fromString(typeString);
        final List<String> nickList = ParseUtils.splitRawLine(rawNickList, false);

        mObserver.onNameReply(type, channelName, nickList);
    }

    private void parseNameFinished() {
        mObserver.onNameFinished();
    }

    public static interface NameObserver {

        public void onNameReply(final ChannelType type, final String channelName,
                final List<String> nickList);

        public void onNameFinished();
    }
}