package com.fusionx.relay.call;

public class ModeCall extends Call {
    public final String channelName;

    public final String mode;

    public final String nick;

    public ModeCall(String channelName, String mode, String nick) {
        this.channelName = channelName;
        this.mode = mode;
        this.nick = nick;
    }
}