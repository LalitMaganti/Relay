package co.fusionx.relay.internal.base;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.Nick;
import co.fusionx.relay.constants.UserLevel;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelayChannelUser implements ChannelUser {

    private final Map<RelayChannel, UserLevel> mUserLevelMap;

    private RelayNick mNick;

    public RelayChannelUser(final String nick) {
        mNick = new RelayNick(nick);

        // Linked hash map used to preserve insertion order - so that the channels are always
        // displayed to the user in the order they were joined
        mUserLevelMap = new LinkedHashMap<>();
    }

    @Override
    public Set<RelayChannel> getChannels() {
        return mUserLevelMap.keySet();
    }

    public void addChannel(final RelayChannel channel, final UserLevel level) {
        mUserLevelMap.put(channel, level);
    }

    public void removeChannel(final RelayChannel channel) {
        mUserLevelMap.remove(channel);
    }

    public void onModeChanged(final RelayChannel channel, final UserLevel mode) {
        mUserLevelMap.put(channel, mode);
    }

    @Override
    public UserLevel getChannelPrivileges(final Channel channel) {
        if (channel instanceof RelayChannel) {
            final RelayChannel relayChannel = (RelayChannel) channel;
            final UserLevel level = mUserLevelMap.get(relayChannel);
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

    public void setNick(final String nick) {
        mNick = new RelayNick(nick);
    }

    public boolean isNickEqual(final String nick) {
        return mNick.getNickAsString().equals(nick);
    }

    public boolean isNickEqual(final RelayChannelUser user) {
        return mNick.equals(user.getNick());
    }
}