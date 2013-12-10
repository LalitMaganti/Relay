package com.fusionx.relay.event;

public class NickChangeEvent extends Event {

    public final String oldNick;

    public final String newNick;

    public NickChangeEvent(String oldNick, String newNick) {
        this.oldNick = oldNick;
        this.newNick = newNick;
    }
}