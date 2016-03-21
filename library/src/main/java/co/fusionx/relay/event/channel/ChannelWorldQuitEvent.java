package co.fusionx.relay.event.channel;

import java.util.List;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.constants.UserLevel;

public class ChannelWorldQuitEvent extends ChannelWorldUserEvent {

    public final String reason;
    public final List<FormatSpanInfo> formats;

    public final UserLevel level;

    public ChannelWorldQuitEvent(final Channel channel, final ChannelUser user,
            final UserLevel level, final String reason, final List<FormatSpanInfo> formats) {
        super(channel, user);

        this.level = level;
        this.reason = reason;
        this.formats = formats;
    }
}