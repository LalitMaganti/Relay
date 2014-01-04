package com.fusionx.relay.event.user;

import com.fusionx.relay.PrivateMessageUser;

public class WorldPrivateActionEvent extends WorldPrivateEvent {

    public final String action;

    public WorldPrivateActionEvent(PrivateMessageUser userNick, String action) {
        super(userNick);

        this.action = action;
    }
}