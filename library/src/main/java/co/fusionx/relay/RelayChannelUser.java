package co.fusionx.relay;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import co.fusionx.relay.constants.UserLevel;

public class RelayChannelUser implements ChannelUser {

    private final Map<RelayChannel, UserLevel> mUserLevelMap;

    private RelayNick mNick;

    public RelayChannelUser(final String nick) {
        mNick = new RelayNick(nick);

        // Linked hash map used to preserve insertion order - so that the channels are always
        // displayed to the user in the order they were joined
        mUserLevelMap = new LinkedHashMap<>();
    }

    // Channel
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

    // TODO this is the same as addChannel
    public void onModeChanged(final RelayChannel channel, final UserLevel mode) {
        mUserLevelMap.put(channel, mode);
    }

    @Override
    public UserLevel getChannelPrivileges(final Channel rawChannel) {
        if (rawChannel instanceof RelayChannel) {
            final RelayChannel channel = (RelayChannel) rawChannel;
            return mUserLevelMap.get(channel);
        }
        return null;
    }

    public UserLevel onModeChange(final RelayChannel channel, final String mode) {
        boolean addingMode = false;
        for (char character : mode.toCharArray()) {
            switch (character) {
                case '+':
                    addingMode = true;
                    break;
                case '-':
                    addingMode = false;
                    break;
                case 'o':
                case 'v':
                case 'h':
                case 'a':
                case 'q':
                    // TODO - don't return straight away - more checking may need to be done
                    final UserLevel levelEnum = UserLevel.getLevelFromMode(character);
                    channel.decrementUserType(mUserLevelMap.get(channel));
                    if (addingMode) {
                        channel.incrementUserType(levelEnum);
                        onModeChanged(channel, levelEnum);
                        return levelEnum;
                    }
                    onModeChanged(channel, UserLevel.NONE);
                    return UserLevel.NONE;
            }
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

    public boolean isNickEqual(final String oldRawNick) {
        return mNick.getNickAsString().equals(oldRawNick);
    }

    public boolean isNickEqual(final RelayChannelUser user) {
        return mNick.getNickAsString().equals(user.getNick().getNickAsString());
    }
}