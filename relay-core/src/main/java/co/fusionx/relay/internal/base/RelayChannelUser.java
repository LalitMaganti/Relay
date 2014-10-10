package co.fusionx.relay.internal.base;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.Nick;
import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;

public class RelayChannelUser implements InternalChannelUser {

    private final Map<InternalChannel, UserLevel> mUserLevelMap;

    private RelayNick mNick;

    public RelayChannelUser(final String nick) {
        mNick = new RelayNick(nick);

        // Linked hash map used to preserve insertion order - so that the channels are always
        // displayed to the user in the order they were joined
        mUserLevelMap = new LinkedHashMap<>();
    }

    @Override
    public Set<InternalChannel> getChannels() {
        return mUserLevelMap.keySet();
    }

    @Override
    public void addChannel(final InternalChannel channel, final UserLevel level) {
        mUserLevelMap.put(channel, level);
    }

    @Override
    public void removeChannel(final InternalChannel channel) {
        mUserLevelMap.remove(channel);
    }

    @Override
    public void onModeChanged(final InternalChannel channel, final UserLevel mode) {
        mUserLevelMap.put(channel, mode);
    }

    @Override
    public UserLevel getChannelPrivileges(final Channel channel) {
        if (channel instanceof InternalChannel) {
            final InternalChannel internalChannel = (InternalChannel) channel;
            final UserLevel level = mUserLevelMap.get(internalChannel);
            if (level == null) {
                // getPreferences().logMissingData(relayChannel.getServer());
            }
            return level == null ? UserLevel.NONE : level;
        }
        return UserLevel.NONE;
    }

    @Override
    public String toString() {
        return mNick.toString();
    }

    @Override
    public Nick getNick() {
        return mNick;
    }

    @Override
    public void setNick(final String nick) {
        mNick = new RelayNick(nick);
    }

    @Override
    public boolean isNickEqual(final String nick) {
        return mNick.getNickAsString().equals(nick);
    }

    @Override
    public boolean isNickEqual(final InternalChannelUser user) {
        return mNick.equals(user.getNick());
    }
}