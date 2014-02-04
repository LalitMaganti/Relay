package com.fusionx.relay.parser;

import com.fusionx.relay.Channel;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.MentionEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

public class MentionParser {

    public static void onMentionableCommand(final String message, final String userNick,
                                            final ServerEventBus bus, final Channel channel) {
        final List<String> list = IRCUtils.splitRawLine(message, false);
        for (final String s : list) {
            if (s.startsWith(userNick)) {
                final ChannelEvent event = new MentionEvent(channel);
                bus.post(event);
                return;
            }
        }
    }
}