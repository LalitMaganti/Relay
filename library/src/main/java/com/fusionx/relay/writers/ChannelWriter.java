package com.fusionx.relay.writers;

import com.fusionx.relay.call.ChannelActionCall;
import com.fusionx.relay.call.ChannelKickCall;
import com.fusionx.relay.call.ChannelMessageCall;
import com.fusionx.relay.call.ChannelPartCall;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.StringUtils;

import java.io.OutputStreamWriter;

public class ChannelWriter extends RawWriter {

    public ChannelWriter(OutputStreamWriter out) {
        super(out);
    }

    @Subscribe
    public void sendMessage(final ChannelMessageCall event) {
        writeLineToServer(String.format(WriterCommands.PRIVMSG, event.channelName, event.message));
    }

    @Subscribe
    public void sendAction(final ChannelActionCall event) {
        final String line = String.format(WriterCommands.ACTION, event.channelName, event.action);
        writeLineToServer(line);
    }

    @Subscribe
    public void partChannel(final ChannelPartCall event) {
        writeLineToServer(StringUtils.isEmpty(event.channelName) ?
                String.format(WriterCommands.Part, event.channelName) :
                String.format(WriterCommands.PartWithReason, event.channelName, event.reason)
                        .trim());
    }

    @Subscribe
    public void onKick(final ChannelKickCall event) {
        writeLineToServer(StringUtils.isEmpty(event.channelName) ?
                String.format(WriterCommands.Kick, event.channelName, event.userNick) :
                String.format(WriterCommands.KickWithReason, event.channelName, event.userNick,
                        event.reason).trim());
    }
}