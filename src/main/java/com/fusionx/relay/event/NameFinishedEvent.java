package com.fusionx.relay.event;

import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.constants.UserListChangeType;

import java.util.Collection;

public class NameFinishedEvent extends ChannelEvent {
    public final Collection<ChannelUser> users;

    public NameFinishedEvent(String channelName, Collection<ChannelUser> users) {
        super(channelName, "", UserListChangeType.ADD, null);

        this.users = users;
    }
}