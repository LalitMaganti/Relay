package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.core.Nick;

public class ServerNickChangeEvent extends ServerEvent {

    public final Nick oldNick;

    public final Nick newNick;

    public ServerNickChangeEvent(final Server server, final Nick oldNick, final ChannelUser user) {
        super(server);

        this.oldNick = oldNick;
        this.newNick = user.getNick();
    }
}