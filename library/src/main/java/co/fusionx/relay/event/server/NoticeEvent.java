package co.fusionx.relay.event.server;

public class NoticeEvent extends ImportantServerEvent {

    public final String sendingNick;

    public NoticeEvent(final String message, String sendingNick) {
        super(message);

        this.sendingNick = sendingNick;
    }
}