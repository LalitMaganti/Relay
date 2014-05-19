package com.fusionx.relay.event.server;

public class NewPrivateMessageEvent extends ServerEvent {

    public final String nick;

    public NewPrivateMessageEvent(final String nick) {
        this.nick = nick;
    }
}