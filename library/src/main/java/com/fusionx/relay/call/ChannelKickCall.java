package com.fusionx.relay.call;

import com.fusionx.relay.writers.WriterCommands;

import android.text.TextUtils;

public class ChannelKickCall extends Call {

    public final String channelName;

    public final String userNick;

    public final String reason;

    public ChannelKickCall(final String channelName, final String userNick, final String reason) {
        this.channelName = channelName;
        this.userNick = userNick;
        this.reason = reason;
    }

    @Override
    public String getLineToSendServer() {
        return TextUtils.isEmpty(channelName)
                ? String.format(WriterCommands.Kick, channelName, userNick)
                : String.format(WriterCommands.KickWithReason, channelName, userNick,
                        reason).trim();
    }
}