package com.fusionx.relay.call;

import com.fusionx.relay.writers.WriterCommands;

import android.text.TextUtils;

public class ChannelPartCall extends Call {

    private final String channelName;

    private final String reason;

    public ChannelPartCall(final String channelName, final String reason) {
        this.channelName = channelName;
        this.reason = reason;
    }

    @Override
    public String getLineToSendServer() {
        return TextUtils.isEmpty(channelName) ? String.format(WriterCommands.Part, channelName)
                : String.format(WriterCommands.PartWithReason, channelName, reason).trim();
    }
}