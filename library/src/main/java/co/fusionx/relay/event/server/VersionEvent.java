package co.fusionx.relay.event.server;

public class VersionEvent extends ServerEvent {

    public final String version;

    public final String nick;

    public VersionEvent(String nick, String version) {
        this.nick = nick;
        this.version = version;
    }
}
