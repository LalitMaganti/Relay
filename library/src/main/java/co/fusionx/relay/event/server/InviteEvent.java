package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;

public class InviteEvent extends ServerEvent {

    public final String channelName;

    public final String invitingUser;

    public InviteEvent(final Server server, final String channelName, final String invitingUser) {
        super(server);

        this.channelName = channelName;
        this.invitingUser = invitingUser;
    }
}