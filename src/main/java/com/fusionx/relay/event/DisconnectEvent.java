package com.fusionx.relay.event;

public class DisconnectEvent extends ServerEvent {

    public final boolean retryPending;

    public final boolean userTriggered;

    public DisconnectEvent(String message, final boolean retryPending, final boolean userTriggered) {
        super(message);

        this.retryPending = retryPending;
        this.userTriggered = userTriggered;
    }
}
