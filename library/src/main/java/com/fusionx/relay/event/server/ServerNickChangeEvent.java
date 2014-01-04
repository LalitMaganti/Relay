package com.fusionx.relay.event.server;

import com.fusionx.relay.WorldUser;

public class ServerNickChangeEvent extends ServerEvent {

    public final String oldNick;

    public final String newNick;

    public ServerNickChangeEvent(final String oldNick, final WorldUser user) {
        this.oldNick = oldNick;
        this.newNick = user.getColorfulNick();
    }
}