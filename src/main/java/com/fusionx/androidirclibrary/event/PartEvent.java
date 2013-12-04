package com.fusionx.androidirclibrary.event;

public class PartEvent extends Event {

    public final String channelName;

    public PartEvent(String channelName) {
        this.channelName = channelName;
    }
}