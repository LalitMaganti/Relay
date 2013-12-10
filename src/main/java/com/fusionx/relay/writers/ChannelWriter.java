package com.fusionx.relay.writers;

import com.fusionx.relay.event.ActionEvent;
import com.fusionx.relay.event.MessageEvent;
import com.fusionx.relay.event.PartEvent;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.StringUtils;

import java.io.OutputStreamWriter;

public class ChannelWriter extends RawWriter {

    public ChannelWriter(OutputStreamWriter out) {
        super(out);
    }

    @Subscribe
    public void sendMessage(final MessageEvent event) {
        writeLineToServer(String.format(WriterCommands.PRIVMSG, event.channelName, event.message));
    }

    @Subscribe
    public void sendAction(final ActionEvent event) {
        writeLineToServer(String.format(WriterCommands.Action, event.channelName, event.message));
    }

    @Subscribe
    public void partChannel(final PartEvent event) {
        writeLineToServer(StringUtils.isEmpty(event.channelName) ?
                String.format(WriterCommands.Part, event.channelName) :
                String.format(WriterCommands.PartWithReason, event.channelName, event.reason)
                        .trim());
    }
}