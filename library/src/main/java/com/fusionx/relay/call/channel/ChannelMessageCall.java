package com.fusionx.relay.call.channel;

import com.fusionx.relay.call.Call;
import com.fusionx.relay.writers.WriterCommands;

public class ChannelMessageCall extends Call {

    private final String channelName;

    private final String message;

    public ChannelMessageCall(String channelName, String message) {
        this.channelName = channelName;
        this.message = message;
    }

    @Override
    public String getLineToSendServer() {
        return String.format(WriterCommands.PRIVMSG, channelName, message);
    }
}