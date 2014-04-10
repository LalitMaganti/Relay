package com.fusionx.relay.event.server;

public class WallopsEvent extends ServerEvent {

    public final String message;

    public final String nick;

    public WallopsEvent(final String message, final String nick) {
        this.message = message;
        this.nick = nick;
    }
}
