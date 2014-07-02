package com.fusionx.relay;

public class AppUser extends WorldUser {

    public AppUser(final String nick, final UserChannelInterface userChannelInterface) {
        super(nick, userChannelInterface);

        userChannelInterface.putAppUser(this);
    }
}