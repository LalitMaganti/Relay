package co.fusionx.relay.event.server;

import co.fusionx.relay.Channel;

public final class JoinEvent extends ServerEvent {

    public final Channel channel;

    public JoinEvent(final Channel channel) {
        this.channel = channel;
    }
}