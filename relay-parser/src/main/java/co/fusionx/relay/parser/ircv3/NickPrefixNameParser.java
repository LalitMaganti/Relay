package co.fusionx.relay.parser.ircv3;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.constant.ChannelType;
import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ReplyCodeParser;
import co.fusionx.relay.parser.rfc.NameParser;

public class NickPrefixNameParser implements ReplyCodeParser, NameParser.NameObserver {

    private final NickPrefixNameObserver mObserver;

    private final NameParser mNameParser;

    public NickPrefixNameParser(final NickPrefixNameObserver observer) {
        mObserver = observer;

        mNameParser = new NameParser(this);
    }

    public static Pair<String, UserLevel> consumeNickPrefixes(final String rawNick) {
        UserLevel level = UserLevel.NONE;
        for (int i = 0, length = rawNick.length(); i < length; i++) {
            final char c = rawNick.charAt(i);
            final UserLevel charLevel = UserLevel.getLevelFromPrefix(c);
            if (charLevel == UserLevel.NONE) {
                return Pair.of(rawNick.substring(i), level);
            } else if (level == UserLevel.NONE) {
                level = charLevel;
            }
        }
        return null;
    }

    @Override
    public void onNameReply(final ChannelType type, final String channelName,
            final List<String> nickList) {
        final List<Pair<String, UserLevel>> nickLevelList = new ArrayList<>();
        FluentIterable.from(nickList)
                .transform(new Function<String, Pair<String, UserLevel>>() {
                    @Override
                    public Pair<String, UserLevel> apply(final String n) {
                        return consumeNickPrefixes(n);
                    }
                })
                .copyInto(nickLevelList);

        mObserver.onNameReply(type, channelName, nickLevelList);
    }

    @Override
    public void onNameFinished() {
        mObserver.onNameFinished();
    }

    @Override
    public void parseReplyCode(final List<String> parsedArray, final int code) {
        mNameParser.parseReplyCode(parsedArray, code);
    }

    public static interface NickPrefixNameObserver {

        public void onNameReply(final ChannelType type, final String channelName,
                final List<Pair<String, UserLevel>> nickList);

        public void onNameFinished();
    }
}