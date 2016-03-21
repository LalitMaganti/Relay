package co.fusionx.relay.event.channel;

import java.util.List;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.FormatSpanInfo;

public class ChannelWorldActionEvent extends ChannelWorldUserEvent {

    public final String action;
    public final List<FormatSpanInfo> formats;

    public ChannelWorldActionEvent(final Channel channel, final String action,
            final ChannelUser sendingUser, final boolean mention,
            final List<FormatSpanInfo> formats) {
        super(channel, sendingUser, mention);

        this.action = action;
        this.formats = formats;
    }

    public ChannelWorldActionEvent(final Channel channel, final String action,
            final String sendingUser, final boolean mention, final List<FormatSpanInfo> formats) {
        super(channel, sendingUser, mention);

        this.action = action;
        this.formats = formats;
    }
}