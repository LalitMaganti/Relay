package co.fusionx.relay.event.server;

public class ErrorEvent extends ServerEvent {

    public final String line;

    public ErrorEvent(final String rawLine) {
        line = rawLine;
    }
}