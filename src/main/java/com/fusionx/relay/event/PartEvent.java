package com.fusionx.relay.event;

public class PartEvent extends Event {

    public final String channelName;

    public final String reason;

    public PartEvent(String reason) {
        this.channelName = reason;
        this.reason = "";
    }

    public PartEvent(String channelName, String reason) {
        this.channelName = channelName;
        this.reason = reason;
    }
}