package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.LibraryUser;
import co.fusionx.relay.internal.base.RelayLibraryUser;

public class ChannelMessageEvent extends ChannelEvent {

    public final String message;

    public final LibraryUser user;

    public ChannelMessageEvent(final Channel channel, final String message,
            final LibraryUser user) {
        super(channel);

        this.message = message;
        this.user = user;
    }
}