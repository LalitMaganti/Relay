package com.fusionx.relay.event.server;

public class VersionEvent extends ServerEvent {

    public final String version;

    private final String nick;

    public VersionEvent(String nick, String version) {
        this.nick = nick;
        this.version = version;
    }
}
