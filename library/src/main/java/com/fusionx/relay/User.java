package com.fusionx.relay;

import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.IRCUtils;

public abstract class User {

    String mNick;

    final String mColourCode;

    final UserChannelInterface mUserChannelInterface;

    User(final String nick, final UserChannelInterface userChannelInterface) {
        mNick = nick;
        mUserChannelInterface = userChannelInterface;

        mColourCode = "<color=" + IRCUtils.generateRandomColor(InterfaceHolders.getPreferences()
                .getTheme()) + ">%1$s</color>";
    }

    public String getColorfulNick() {
        if (InterfaceHolders.getPreferences().shouldNickBeColourful()) {
            return String.format(mColourCode, mNick);
        } else {
            return mNick;
        }
    }

    @Override
    public String toString() {
        return mNick;
    }

    // Getters and setters
    public String getNick() {
        return mNick;
    }

    public void setNick(String nick) {
        mNick = nick;
    }
}