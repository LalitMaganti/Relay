package com.fusionx.androidirclibrary.event;

public class InviteEvent extends Event {

    public final String invitedChannel;

    public InviteEvent(final String invitedChannel) {
        this.invitedChannel = invitedChannel;
    }
}