package com.fusionx.androidirclibrary.event;

public class WhoisEvent extends Event {
    public WhoisEvent(String nick) {
        baseMessage = nick;
    }
}