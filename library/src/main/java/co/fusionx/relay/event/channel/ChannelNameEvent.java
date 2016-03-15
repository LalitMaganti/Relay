package co.fusionx.relay.event.channel;

import java.util.Collection;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;

public class ChannelNameEvent extends ChannelEvent {

    public final Collection<? extends ChannelUser> users;

    public ChannelNameEvent(final Channel channel, final Collection<? extends ChannelUser> users) {
        super(channel);

        this.users = users;
    }
}
