package co.fusionx.relay.event.channel;

import java.util.List;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.FormatSpanInfo;

/**
 * Both user and nick can be null
 */
public class ChannelWorldMessageEvent extends ChannelWorldUserEvent {

    public final String message;
    public final List<FormatSpanInfo> formats;

    public ChannelWorldMessageEvent(final Channel channel, final String message,
            final ChannelUser sendingUser, final boolean mention, List<FormatSpanInfo> formats) {
        super(channel, sendingUser, mention);

        this.message = message;
        this.formats = formats;
    }

    public ChannelWorldMessageEvent(Channel channel, String message, String sendingNick,
            boolean mention, List<FormatSpanInfo> formats) {
        super(channel, sendingNick, mention);

        this.message = message;
        this.formats = formats;
    }
}
