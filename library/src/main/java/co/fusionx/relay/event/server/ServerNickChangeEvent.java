package co.fusionx.relay.event.server;

import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.Nick;
import co.fusionx.relay.base.Server;

public class ServerNickChangeEvent extends ServerEvent {

    public final Nick oldNick;

    public final Nick newNick;

    public ServerNickChangeEvent(final Server server, final Nick oldNick, final ChannelUser user) {
        super(server);

        this.oldNick = oldNick;
        this.newNick = user.getNick();
    }
}