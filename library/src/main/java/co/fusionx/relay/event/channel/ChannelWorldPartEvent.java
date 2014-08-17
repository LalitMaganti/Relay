package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;

public class ChannelWorldPartEvent extends ChannelWorldUserEvent {

    public final String reason;

    public ChannelWorldPartEvent(final Channel channel, final ChannelUser user,
            final String reason) {
        super(channel, user.getNick());

        this.reason = reason;
    }
}