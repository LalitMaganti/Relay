package co.fusionx.relay.parser.ircv3;

import com.google.common.collect.FluentIterable;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.fusionx.relay.constant.ChannelType;
import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.parser.ObserverHelper;
import co.fusionx.relay.parser.ReplyCodeParser;
import co.fusionx.relay.parser.rfc.NameParser;

public class NickPrefixNameParser implements ReplyCodeParser, NameParser.NameObserver {

    public final ObserverHelper<NickPrefixNameObserver> mObserverHelper = new ObserverHelper<>();

    private final NameParser mNameParser;

    public NickPrefixNameParser() {
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

    public NickPrefixNameParser addObserver(final NickPrefixNameObserver observer) {
        mObserverHelper.addObserver(observer);
        return this;
    }

    public NickPrefixNameParser addObservers(
            final Collection<? extends NickPrefixNameObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void onNameReply(final ChannelType type, final String channelName,
            final List<String> nickList) {
        final List<Pair<String, UserLevel>> nickLevelList = new ArrayList<>();
        FluentIterable.from(nickList)
                .transform(NickPrefixNameParser::consumeNickPrefixes)
                .copyInto(nickLevelList);

        mObserverHelper.notifyObservers(
                observer -> observer.onNameReply(type, channelName, nickLevelList));
    }

    @Override
    public void onNameFinished() {
        mObserverHelper.notifyObservers(NickPrefixNameObserver::onNameFinished);
    }

    @Override
    public void parseReplyCode(final String target, final List<String> parsedArray, final int code) {
        mNameParser.parseReplyCode(target, parsedArray, code);
    }

    @Override
    public List<Integer> parsableCodes() {
        return mNameParser.parsableCodes();
    }

    public static interface NickPrefixNameObserver {

        public void onNameReply(final ChannelType type, final String channelName,
                final List<Pair<String, UserLevel>> nickList);

        public void onNameFinished();
    }
}