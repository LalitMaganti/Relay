package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;

public class ChannelNoticeEvent extends ChannelEvent {

    public final String originNick;

    public final String notice;

    public ChannelNoticeEvent(final Channel channel, final String originNick, final String notice) {
        super(channel);
        this.originNick = originNick;
        this.notice = notice;
    }
}