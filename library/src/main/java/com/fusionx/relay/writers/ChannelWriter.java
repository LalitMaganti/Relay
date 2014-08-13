package com.fusionx.relay.writers;

import com.fusionx.relay.call.channel.ChannelActionCall;
import com.fusionx.relay.call.channel.ChannelKickCall;
import com.fusionx.relay.call.channel.ChannelMessageCall;
import com.fusionx.relay.call.channel.ChannelPartCall;
import com.fusionx.relay.call.channel.ChannelTopicCall;
import com.squareup.otto.Subscribe;

import java.io.Writer;

public class ChannelWriter extends RawWriter {

    public ChannelWriter(Writer out) {
        super(out);
    }

    @Subscribe
    public void sendMessage(final ChannelMessageCall call) {
        writeLineToServer(call.getLineToSendServer());
    }

    @Subscribe
    public void sendAction(final ChannelActionCall call) {
        writeLineToServer(call.getLineToSendServer());
    }

    @Subscribe
    public void partChannel(final ChannelPartCall call) {
        writeLineToServer(call.getLineToSendServer());
    }

    @Subscribe
    public void onKick(final ChannelKickCall call) {
        writeLineToServer(call.getLineToSendServer());
    }

    @Subscribe
    public void onTopic(final ChannelTopicCall call) {
        writeLineToServer(call.getLineToSendServer());
    }
}