package com.fusionx.relay.event.server;

public class ConnectEvent extends StatusChangeEvent {

    public final String serverUrl;

    public ConnectEvent(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}