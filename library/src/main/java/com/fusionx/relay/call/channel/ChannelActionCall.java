package com.fusionx.relay.call.channel;

import com.fusionx.relay.call.Call;
import com.fusionx.relay.writers.WriterCommands;

public class ChannelActionCall extends Call {

    private final String action;

    private final String channelName;

    public ChannelActionCall(final String channelName, final String action) {
        this.action = action;
        this.channelName = channelName;
    }

    @Override
    public String getLineToSendServer() {
        return String.format(WriterCommands.ACTION, channelName, action);
    }
}