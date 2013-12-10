package com.fusionx.androidirclibrary.event;

public class ChannelEvent extends Event {

    public final String channelName;

    public final String message;

    public final boolean userListChanged;

    public ChannelEvent(final String channelName, final String message, boolean userListChanged) {
        this.channelName = channelName;
        this.message = message;
        this.userListChanged = userListChanged;
    }
}