package com.fusionx.relay.event.server;

public class PrivateNoticeEvent extends ImportantServerEvent {

    public final String sendingNick;

    public PrivateNoticeEvent(final String message, String sendingNick) {
        super(message);

        this.sendingNick = sendingNick;
    }
}