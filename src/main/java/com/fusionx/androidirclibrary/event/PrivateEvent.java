package com.fusionx.androidirclibrary.event;

public class PrivateEvent extends Event {

    public final String userNick;

    public final String message;

    public final boolean newPrivateMessage;

    PrivateEvent(String userNick, String message, boolean newPrivateMessage) {
        this.userNick = userNick;
        this.message = message;
        this.newPrivateMessage = newPrivateMessage;
    }
}
