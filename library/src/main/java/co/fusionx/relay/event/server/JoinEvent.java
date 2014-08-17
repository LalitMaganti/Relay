package co.fusionx.relay.event.server;

import co.fusionx.relay.base.Channel;

public final class JoinEvent extends ServerEvent {

    public final Channel channel;

    public JoinEvent(final Channel channel) {
        super(channel.getServer());

        this.channel = channel;
    }
}