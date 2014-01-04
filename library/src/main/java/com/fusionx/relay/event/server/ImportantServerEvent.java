package com.fusionx.relay.event.server;

public class ImportantServerEvent extends ServerEvent {
    public final String message;

    public ImportantServerEvent(String message) {
        this.message = message;
    }
}