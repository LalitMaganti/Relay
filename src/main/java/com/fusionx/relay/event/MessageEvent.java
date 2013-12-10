package com.fusionx.relay.event;

public class MessageEvent extends ChannelEvent {

    public MessageEvent(String channelName, String message) {
        super(channelName, message, false);
    }
}
