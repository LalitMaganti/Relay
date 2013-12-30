package com.fusionx.relay.event;

public class PrivateNickChangeEvent extends PrivateEvent {

    public final String newNick;

    public PrivateNickChangeEvent(final String oldNick, final String message,
            final String newNick) {
        super(oldNick, message, false);

        this.newNick = newNick;
    }
}