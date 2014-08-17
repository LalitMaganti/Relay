package co.fusionx.relay.dcc.file;

public class DCCGetStartedEvent {

    private final DCCGetConnection getConnection;

    public DCCGetStartedEvent(final DCCGetConnection connection) {
        this.getConnection = connection;
    }
}