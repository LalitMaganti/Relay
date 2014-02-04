package com.fusionx.relay;

public class ChannelSnapshot extends Channel {

    ChannelSnapshot(final Channel channel) {
        super(channel.getName(), channel.getBuffer());
    }
}