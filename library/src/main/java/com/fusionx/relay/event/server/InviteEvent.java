package com.fusionx.relay.event.server;

public class InviteEvent extends ServerEvent {

    public final String channelName;

    public final String invitingUser;

    public InviteEvent(String channelName, String invitingUser) {
        this.channelName = channelName;
        this.invitingUser = invitingUser;
    }
}