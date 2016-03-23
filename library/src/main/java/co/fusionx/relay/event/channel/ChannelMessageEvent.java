package co.fusionx.relay.event.channel;

import java.util.List;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.internal.base.RelayMainUser;

public class ChannelMessageEvent extends ChannelEvent {

    public final String message;
    public final List<FormatSpanInfo> formats;

    public final RelayMainUser user;

    public ChannelMessageEvent(final Channel channel, final String message,
            final List<FormatSpanInfo> formats, final RelayMainUser user) {
        super(channel);

        this.message = message;
        this.formats = formats;
        this.user = user;
    }
}