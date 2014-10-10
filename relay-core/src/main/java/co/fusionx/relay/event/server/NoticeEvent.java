package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;

public class NoticeEvent extends ImportantServerEvent {

    public final String sendingNick;

    public NoticeEvent(final Server server, final String sendingNick, final String message) {
        super(server, message);

        this.sendingNick = sendingNick;
    }
}