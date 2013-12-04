package com.fusionx.androidirclibrary.event;

public class JoinEvent extends Event {

    public final String channelToJoin;

    public JoinEvent(final String channelToJoin) {
        this.channelToJoin = channelToJoin;
    }
}