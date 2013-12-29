package com.fusionx.relay;

import com.fusionx.relay.collection.UpdateableTreeSet;
import com.fusionx.relay.constants.UserLevelEnum;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.ColourParserUtils;

import org.apache.commons.lang3.tuple.ImmutablePair;

import android.text.Spanned;
import android.widget.Checkable;

import java.util.HashMap;
import java.util.Set;

public class ChannelUser extends User implements UpdateableTreeSet.Updateable, Checkable {

    final Server mServer;

    private final HashMap<Channel, UserLevelEnum> mUserLevelMap;

    private final HashMap<Channel, Spanned> mChannelSpannedMap;

    // Checkable interface
    private boolean mChecked;

    public ChannelUser(final String nick, final UserChannelInterface userChannelInterface) {
        super(nick, userChannelInterface);

        mUserLevelMap = new HashMap<>();
        mChannelSpannedMap = new HashMap<>();
        mServer = userChannelInterface.getServer();

        // Checkable interface
        mChecked = false;
    }

    public String getPrettyNick(final Channel channel) {
        return String.format(mColourCode, getUserPrefix(channel) + mNick);
    }

    public Spanned getSpannedNick(final Channel channel) {
        final Spanned spannable = mChannelSpannedMap.get(channel);
        if (spannable == null) {
            onUpdateSpannedNick(channel);
        }
        return spannable;
    }

    public String getBracketedNick(final Channel channel) {
        return String.format(mColourCode, "<" + getUserPrefix(channel) + mNick + ">");
    }

    public void onJoin(final Channel channel) {
        onPutMode(channel, UserLevelEnum.NONE);
    }

    // Called either on part of other or on quit of our user
    public void onRemove(final Channel channel) {
        mUserLevelMap.remove(channel);
        mChannelSpannedMap.remove(channel);
    }

    public void onPutMode(final Channel channel, final UserLevelEnum mode) {
        mUserLevelMap.put(channel, mode);
        onUpdateSpannedNick(channel);
    }

    public UserLevelEnum getChannelPrivileges(final Channel channel) {
        return mUserLevelMap.get(channel);
    }

    private void onUpdateSpannedNick(final Channel channel) {
        Spanned spannable = ColourParserUtils.onParseMarkup(getPrettyNick(channel));
        mChannelSpannedMap.put(channel, spannable);
    }

    public char getUserPrefix(final Channel channel) {
        final UserLevelEnum levelEnum = mUserLevelMap.get(channel);
        if (levelEnum != null) {
            return mUserLevelMap.get(channel).getPrefix();
        } else {
            return '\0';
        }
    }

    public Set<Channel> getChannels() {
        return mUserChannelInterface.getAllChannelsInUser(this);
    }

    public void onWhoMode(final String rawMode, final Channel channel) {
        UserLevelEnum mode = UserLevelEnum.NONE;
        for (final UserLevelEnum levelEnum : UserLevelEnum.values()) {
            if (rawMode.contains(String.valueOf(levelEnum.getPrefix()))) {
                mode = levelEnum;
                channel.onIncrementUserType(mode);
            }
        }
        mUserLevelMap.put(channel, mode);
        onUpdateSpannedNick(channel);
    }

    public String onModeChange(final String sendingNick, final Channel channel,
            final String mode) {
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
                    final UserLevelEnum levelEnum = UserLevelEnum.getLevelFromMode(character);
                    channel.onDecrementUserType(mUserLevelMap.get(channel));
                    if (addingMode) {
                        channel.onIncrementUserType(levelEnum);
                        channel.getUsers().update(this, new ImmutablePair<>(channel, levelEnum));
                    } else {
                        channel.getUsers()
                                .update(this, new ImmutablePair<>(channel, UserLevelEnum.NONE));
                    }
                    break;
            }
        }

        onUpdateSpannedNick(channel);

        final String formattedSenderNick;
        final ChannelUser sendingUser = mUserChannelInterface.getUserIfExists(sendingNick);
        if (sendingUser == null) {
            formattedSenderNick = sendingNick;
        } else {
            formattedSenderNick = sendingUser.getPrettyNick(channel);
        }

        return InterfaceHolders.getEventResponses().getModeChangedMessage(mode,
                getColorfulNick(), formattedSenderNick);
    }

    @Override
    public void update(final Object newValue) {
        if (newValue instanceof ImmutablePair) {
            // ArrayList = mode change
            ImmutablePair list = (ImmutablePair) newValue;
            if (list.getLeft() instanceof Channel && list.getRight() instanceof UserLevelEnum) {
                mUserLevelMap.put((Channel) list.getLeft(), (UserLevelEnum) list.getRight());
            }
        } else if (newValue instanceof Channel) {
            onUpdateSpannedNick((Channel) newValue);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof ChannelUser) {
            final ChannelUser us = (ChannelUser) o;
            return us.getNick().equals(mNick) && us.mServer.equals(mServer);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (mNick.hashCode() * 31) + mServer.hashCode();
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