package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;

public class WallopsEvent extends ServerEvent {

    public final String message;

    public final String nick;

    public WallopsEvent(final Server server, final String message, final String nick) {
        super(server);

        this.message = message;
        this.nick = nick;
    }
}
