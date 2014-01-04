package com.fusionx.relay.event.server;

public class ConnectEvent extends ServerEvent {
    public final String serverUrl;

    public ConnectEvent(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}