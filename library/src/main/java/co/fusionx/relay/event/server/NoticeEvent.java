package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;

public class NoticeEvent extends ImportantServerEvent {

    public final String sendingNick;

    public NoticeEvent(final Server server, final String message, String sendingNick) {
        super(server, message);

        this.sendingNick = sendingNick;
    }
}