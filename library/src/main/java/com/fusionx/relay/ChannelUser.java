package com.fusionx.relay;

import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.nick.BasicNick;
import com.fusionx.relay.nick.Nick;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ChannelUser {

    private final Map<Channel, UserLevel> mUserLevelMap;

    private Nick mNick;

    public ChannelUser(final String nick) {
        mNick = new BasicNick(nick);

        // Linked hash map used to preserve insertion order - so that the channels are always
        // displayed to the user in the order they were joined
        mUserLevelMap = new LinkedHashMap<>();
    }

    // Channel
    public Set<Channel> getChannels() {
        return mUserLevelMap.keySet();
    }

    public void addChannel(final Channel channel, final UserLevel level) {
        mUserLevelMap.put(channel, level);
    }

    public void removeChannel(final Channel channel) {
        mUserLevelMap.remove(channel);
    }

    // TODO this is the same as addChannel
    public void onModeChanged(final Channel channel, final UserLevel mode) {
        mUserLevelMap.put(channel, mode);
    }

    public UserLevel getChannelPrivileges(final Channel channel) {
        return mUserLevelMap.get(channel);
    }

    public UserLevel onModeChange(final Channel channel, final String mode) {
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

    public Nick getNick() {
        return mNick;
    }

    public void setNick(final String nick) {
        mNick = new BasicNick(nick);
    }

    public boolean isNickEqual(String oldRawNick) {
        return mNick.getNickAsString().equals(oldRawNick);
    }
}