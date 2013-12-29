package com.fusionx.relay.event;

public class QuitEvent extends Event {

    public final String reason;

    public QuitEvent(String reason) {
        this.reason = reason;
    }
}