package com.fusionx.relay;

import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.ColourParserUtils;

import android.text.Spanned;
import android.widget.Checkable;

import java.util.Collection;
import java.util.Map;

import gnu.trove.map.hash.THashMap;

public class WorldUser implements Checkable {

    protected final UserChannelInterface mUserChannelInterface;

    private final Map<Channel, UserLevel> mUserLevelMap;

    private final Map<Channel, Spanned> mChannelSpannedMap;

    private Nick mNick;

    // Checkable interface
    private boolean mChecked;

    public WorldUser(final String nick, final UserChannelInterface userChannelInterface) {
        mNick = new Nick(nick);

        mUserChannelInterface = userChannelInterface;

        mUserLevelMap = new THashMap<>();
        mChannelSpannedMap = new THashMap<>();

        // Checkable interface
        mChecked = false;
    }

    public String getPrettyNick(final Channel channel) {
        if (InterfaceHolders.getPreferences().shouldNickBeColourful()) {
            return String.format(mNick.getColourCode(), getPrefixedNick(channel));
        } else {
            return getPrefixedNick(channel);
        }
    }

    public Spanned getSpannedNick(final Channel channel) {
        final Spanned spannable = mChannelSpannedMap.get(channel);
        if (spannable == null) {
            onChannelNickChanged(channel);
        }
        return spannable;
    }

    // Called either on part of other or on quit of our user
    public void onRemove(final Channel channel) {
        mUserLevelMap.remove(channel);
        mChannelSpannedMap.remove(channel);
    }

    public void onModeChanged(final Channel channel, final UserLevel mode) {
        mUserLevelMap.put(channel, mode);
        onChannelNickChanged(channel);
    }

    public UserLevel getChannelPrivileges(final Channel channel) {
        return mUserLevelMap.get(channel);
    }

    public void onChannelNickChanged(final Channel channel) {
        final Spanned spannable = ColourParserUtils.onParseMarkup(getPrettyNick(channel));
        mChannelSpannedMap.put(channel, spannable);
    }

    public char getUserPrefix(final Channel channel) {
        final UserLevel levelEnum = mUserLevelMap.get(channel);
        if (levelEnum != null) {
            return mUserLevelMap.get(channel).getPrefix();
        } else {
            return '\0';
        }
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
        onChannelNickChanged(channel);

        return UserLevel.NONE;
    }

    public boolean isUserNickEqual(final WorldUser user) {
        return mNick.getNick().equals(user.getNick());
    }

    public String getColorfulNick() {
        return mNick.getColorfulNick();
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

    public String getNick() {
        return mNick.getNick();
    }

    public void setNick(String nick) {
        mNick = new Nick(nick);
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
    }

    String getPrefixedNick(final Channel channel) {
        return getUserPrefix(channel) + getNick();
    }


}