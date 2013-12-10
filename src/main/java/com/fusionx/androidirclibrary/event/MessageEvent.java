package com.fusionx.androidirclibrary.event;

public class MessageEvent extends ChannelEvent {

    public MessageEvent(String channelName, String message) {
        super(channelName, message, false);
    }
}
