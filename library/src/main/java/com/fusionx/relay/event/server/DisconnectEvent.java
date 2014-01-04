package com.fusionx.relay.event.server;

public class DisconnectEvent extends ServerEvent {

    public final String serverMessage;

    public final boolean userSent;

    public final boolean retryPending;

    public DisconnectEvent(final String serverMessage, boolean userSent, boolean retryPending) {
        this.serverMessage = serverMessage;
        this.userSent = userSent;
        this.retryPending = retryPending;
    }
}