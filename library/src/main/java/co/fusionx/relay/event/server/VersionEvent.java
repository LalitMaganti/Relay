package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;

public class VersionEvent extends ServerEvent {

    public final String version;

    public final String nick;

    public VersionEvent(final Server server, String nick, String version) {
        super(server);

        this.nick = nick;
        this.version = version;
    }
}
