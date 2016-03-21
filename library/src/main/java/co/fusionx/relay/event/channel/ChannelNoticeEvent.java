package co.fusionx.relay.event.channel;

import java.util.List;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.FormatSpanInfo;

public class ChannelNoticeEvent extends ChannelEvent {

    public final String originNick;

    public final String notice;
    public final List<FormatSpanInfo> formats;

    public ChannelNoticeEvent(final Channel channel, final String originNick,
            final String notice, final List<FormatSpanInfo> formats) {
        super(channel);
        this.originNick = originNick;
        this.notice = notice;
        this.formats = formats;
    }
}