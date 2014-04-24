package com.fusionx.relay.event.server;

import com.fusionx.relay.WorldUser;
import com.fusionx.relay.nick.Nick;

public class ServerNickChangeEvent extends ServerEvent {

    public final Nick oldNick;

    public final Nick newNick;

    public ServerNickChangeEvent(final Nick oldNick, final WorldUser user) {
        this.oldNick = oldNick;
        this.newNick = user.getNick();
    }
}