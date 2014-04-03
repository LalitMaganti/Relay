package com.fusionx.relay.event.server;

public class GenericServerEvent extends ServerEvent {

    public final String message;

    public GenericServerEvent(final String message) {
        this.message = message;
    }
}