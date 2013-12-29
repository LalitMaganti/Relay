package com.fusionx.relay.event;

public class PrivateActionEvent extends PrivateEvent {

    public PrivateActionEvent(String recipientNick, String action, final boolean isNew) {
        super(action, recipientNick, isNew);
    }
}
