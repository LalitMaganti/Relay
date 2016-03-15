package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.internal.base.RelayMainUser;

public class ChannelActionEvent extends ChannelEvent {

    public final String action;

    public final RelayMainUser user;

    public ChannelActionEvent(final Channel channel, final String action,
            final RelayMainUser user) {
        super(channel);

        this.action = action;
        this.user = user;
    }
}