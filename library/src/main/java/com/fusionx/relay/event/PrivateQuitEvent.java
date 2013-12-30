package com.fusionx.relay.event;

public class PrivateQuitEvent extends PrivateEvent {

    public PrivateQuitEvent(String userNick, String message) {
        super(userNick, message, false);
    }
}