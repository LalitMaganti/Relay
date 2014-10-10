package co.fusionx.relay.event.channel;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.LibraryUser;

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