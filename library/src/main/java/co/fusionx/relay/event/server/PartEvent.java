package co.fusionx.relay.event.server;

import co.fusionx.relay.Channel;

public class PartEvent extends ServerEvent {

    public final String channelName;

    public PartEvent(final Channel channel) {
        this.channelName = channel.getName();
    }
}