package co.fusionx.relay.event.server;

public class MotdEvent extends ServerEvent {

    public final String motdLine;

    public MotdEvent(String motdLine) {
        this.motdLine = motdLine;
    }
}