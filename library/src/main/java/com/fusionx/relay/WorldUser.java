package com.fusionx.relay;

import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.nick.BasicNick;
import com.fusionx.relay.nick.Nick;

import android.widget.Checkable;

import java.util.Collection;
import java.util.Map;

import gnu.trove.map.hash.THashMap;

public class WorldUser implements Checkable {

    protected final UserChannelInterface mUserChannelInterface;

    private final Map<Channel, UserLevel> mUserLevelMap;

    private Nick mNick;

    // Checkable interface
    private boolean mChecked;

    public WorldUser(final String nick, final UserChannelInterface userChannelInterface) {
        mUserLevelMap = new THashMap<>();
        mNick = new BasicNick(nick);
        mUserChannelInterface = userChannelInterface;

        // Checkable interface
        mChecked = false;
    }

    // Called either on part of other or on quit of our user
    public void onRemove(final Channel channel) {
        mUserLevelMap.remove(channel);
    }

    public void onModeChanged(final Channel channel, final UserLevel mode) {
        mUserLevelMap.put(channel, mode);
    }

    public UserLevel getChannelPrivileges(final Channel channel) {
        return mUserLevelMap.get(channel);
    }

    public Collection<Channel> getChannels() {
        return mUserChannelInterface.getAllChannelsInUser(this);
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
                    channel.onDecrementUserType(mUserLevelMap.get(channel));
                    if (addingMode) {
                        channel.onIncrementUserType(levelEnum);
                        onModeChanged(channel, levelEnum);
                        return levelEnum;
                    } else {
                        onModeChanged(channel, UserLevel.NONE);
                        return UserLevel.NONE;
                    }
            }
        }

        return UserLevel.NONE;
    }

    @Override
    public String toString() {
        return mNick.toString();
    }    // Checkable interface

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean b) {
        mChecked = b;
    }

    public void setNick(final String nick) {
        mNick = new BasicNick(nick);
    }

    public Nick getNick() {
        return mNick;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
    }
}