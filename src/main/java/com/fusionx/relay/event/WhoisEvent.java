package com.fusionx.relay.event;

public class WhoisEvent extends Event {

    public WhoisEvent(String nick) {
        baseMessage = nick;
    }
}