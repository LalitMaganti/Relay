package co.fusionx.relay.event.server;

import co.fusionx.relay.ChannelUser;
import co.fusionx.relay.Nick;

public class ServerNickChangeEvent extends ServerEvent {

    public final Nick oldNick;

    public final Nick newNick;

    public ServerNickChangeEvent(final Nick oldNick, final ChannelUser user) {
        this.oldNick = oldNick;
        this.newNick = user.getNick();
    }
}