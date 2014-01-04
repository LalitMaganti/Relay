package com.fusionx.relay;

import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.ColourParserUtils;

import android.text.Spanned;
import android.widget.Checkable;

import java.util.Collection;
import java.util.Map;

import gnu.trove.map.hash.THashMap;

public class WorldUser extends User implements Checkable {

    final Server mServer;

    private final Map<Channel, UserLevel> mUserLevelMap;

    private final Map<Channel, Spanned> mChannelSpannedMap;

    // Checkable interface
    private boolean mChecked;

    public WorldUser(final String nick, final UserChannelInterface userChannelInterface) {
        super(nick, userChannelInterface);

        mUserLevelMap = new THashMap<>();
        mChannelSpannedMap = new THashMap<>();
        mServer = userChannelInterface.getServer();

        // Checkable interface
        mChecked = false;
    }

    public String getPrefixedNick(final Channel channel) {
        return getUserPrefix(channel) + mNick;
    }

    public String getPrettyNick(final Channel channel) {
        if (InterfaceHolders.getPreferences().shouldNickBeColourful()) {
            return String.format(mColourCode, getPrefixedNick(channel));
        } else {
            return getPrefixedNick(channel);
        }
    }

    public Spanned getSpannedNick(final Channel channel) {
        final Spanned spannable = mChannelSpannedMap.get(channel);
        if (spannable == null) {
            onUpdateSpannedNick(channel);
        }
        return spannable;
    }

    public String getBracketedNick(final Channel channel) {
        return String.format(mColourCode, "<" + getPrefixedNick(channel) + ">");
    }

    public void onJoin(final Channel channel) {
        onPutMode(channel, UserLevel.NONE);
    }

    // Called either on part of other or on quit of our user
    public void onRemove(final Channel channel) {
        mUserLevelMap.remove(channel);
        mChannelSpannedMap.remove(channel);
    }

    public void onPutMode(final Channel channel, final UserLevel mode) {
        mUserLevelMap.put(channel, mode);
        onUpdateSpannedNick(channel);
    }

    public UserLevel getChannelPrivileges(final Channel channel) {
        return mUserLevelMap.get(channel);
    }

    private void onUpdateSpannedNick(final Channel channel) {
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

    public void onWhoMode(final String rawMode, final Channel channel) {
        UserLevel mode = UserLevel.NONE;
        for (final UserLevel levelEnum : UserLevel.values()) {
            if (rawMode.contains(String.valueOf(levelEnum.getPrefix()))) {
                mode = levelEnum;
                channel.onIncrementUserType(mode);
            }
        }
        mUserLevelMap.put(channel, mode);
        onUpdateSpannedNick(channel);
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
                    // TODO - don't return straight away
                    final UserLevel levelEnum = UserLevel.getLevelFromMode(character);
                    channel.onDecrementUserType(mUserLevelMap.get(channel));
                    if (addingMode) {
                        channel.onIncrementUserType(levelEnum);
                        onPutMode(channel, levelEnum);

                        return levelEnum;
                    } else {
                        onPutMode(channel, UserLevel.NONE);
                        return UserLevel.NONE;
                    }
            }
        }
        onUpdateSpannedNick(channel);

        return UserLevel.NONE;
    }

    public void onUpdateNick(final Channel channel) {
        onUpdateSpannedNick(channel);
    }

    public boolean isUserNickEqual(final User user) {
        return mNick.equals(user.getNick());
    }

    // Checkable interface
    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean b) {
        mChecked = b;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
    }
}