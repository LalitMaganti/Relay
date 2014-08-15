package co.fusionx.relay.event.server;

public class ImportantServerEvent extends ServerEvent {

    public final String message;

    ImportantServerEvent(String message) {
        this.message = message;
    }
}