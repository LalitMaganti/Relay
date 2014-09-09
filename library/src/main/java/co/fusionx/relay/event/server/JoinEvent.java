package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.conversation.Server;

public final class JoinEvent extends ServerEvent {

    public final Channel channel;

    public JoinEvent(final Server server, final Channel channel) {
        super(server);

        this.channel = channel;
    }
}