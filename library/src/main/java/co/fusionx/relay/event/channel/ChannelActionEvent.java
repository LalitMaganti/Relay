package co.fusionx.relay.event.channel;

import java.util.List;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.internal.base.RelayMainUser;

public class ChannelActionEvent extends ChannelEvent {

    public final String action;
    public final List<FormatSpanInfo> formats;

    public final RelayMainUser user;

    public ChannelActionEvent(final Channel channel, final String action,
            final List<FormatSpanInfo> formats, final RelayMainUser user) {
        super(channel);

        this.action = action;
        this.formats = formats;
        this.user = user;
    }
}