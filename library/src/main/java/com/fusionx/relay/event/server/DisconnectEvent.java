package com.fusionx.relay.event.server;

public class DisconnectEvent extends StatusChangeEvent {

    public final String serverMessage;

    public final boolean retryPending;

    public DisconnectEvent(final String serverMessage, boolean retryPending) {
        this.serverMessage = serverMessage;
        this.retryPending = retryPending;
    }
}