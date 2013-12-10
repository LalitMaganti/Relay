package com.fusionx.androidirclibrary.event;

public class PrivateMessageEvent extends PrivateEvent {

    public PrivateMessageEvent(String nick, final String message, final boolean newPrivateMessage) {
        super(nick, message, newPrivateMessage);
    }
}
