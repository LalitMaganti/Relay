package com.fusionx.relay.writers;

import com.fusionx.relay.call.ChannelActionCall;
import com.fusionx.relay.call.ChannelKickCall;
import com.fusionx.relay.call.ChannelMessageCall;
import com.fusionx.relay.call.ChannelPartCall;
import com.squareup.otto.Subscribe;

import android.text.TextUtils;

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
    public void partChannel(final ChannelPartCall event) {
        writeLineToServer(event.getLineToSendServer());
    }

    @Subscribe
    public void onKick(final ChannelKickCall event) {
        writeLineToServer(event.getLineToSendServer());
    }
}