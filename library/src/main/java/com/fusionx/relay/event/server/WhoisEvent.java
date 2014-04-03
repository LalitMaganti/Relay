package com.fusionx.relay.event.server;

public class WhoisEvent extends ServerEvent {

    public final String whoisMessage;

    public WhoisEvent(String whoisMessage) {
        this.whoisMessage = whoisMessage;
    }
}