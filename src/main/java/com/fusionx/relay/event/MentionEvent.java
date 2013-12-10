package com.fusionx.relay.event;

public class MentionEvent extends Event {

    public final String destination;

    public MentionEvent(String destination) {
        this.destination = destination;
    }
}
