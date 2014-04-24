package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.nick.BasicNick;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.nick.Nick;

public class WorldNickChangeEvent extends WorldUserEvent {

    public final Nick oldNick;

    public WorldNickChangeEvent(final Channel channel, final Nick oldNick, final WorldUser user) {
        super(channel, user);

        this.oldNick = oldNick;
    }
}