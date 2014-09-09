package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;

public class DisconnectEvent extends StatusChangeEvent {

    public final String serverMessage;

    public final boolean retryPending;

    public DisconnectEvent(final Server server, final String serverMessage, boolean retryPending) {
        super(server);

        this.serverMessage = serverMessage;
        this.retryPending = retryPending;
    }
}