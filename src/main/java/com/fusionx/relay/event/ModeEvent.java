package com.fusionx.relay.event;

public class ModeEvent extends Event {

    public final String mode;

    public final String channel;

    public final String nick;

    public ModeEvent(String mode, String channel, String nick) {
        this.mode = mode;
        this.channel = channel;
        this.nick = nick;
    }
}