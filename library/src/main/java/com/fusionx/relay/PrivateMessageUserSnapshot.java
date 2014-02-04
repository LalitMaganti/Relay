package com.fusionx.relay;

import java.util.ArrayList;

public class PrivateMessageUserSnapshot extends PrivateMessageUser {

    public PrivateMessageUserSnapshot(final PrivateMessageUser user) {
        super(user.getNick(), new ArrayList<>(user.getBuffer()));
    }
}