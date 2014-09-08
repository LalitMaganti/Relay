package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.internal.base.RelayLibraryUser;

public class ChannelActionEvent extends ChannelEvent {

    public final String action;

    public final RelayLibraryUser user;

    public ChannelActionEvent(final Channel channel, final String action,
            final RelayLibraryUser user) {
        super(channel);

        this.action = action;
        this.user = user;
    }
}