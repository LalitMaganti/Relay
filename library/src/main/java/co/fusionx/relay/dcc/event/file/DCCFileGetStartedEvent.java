package co.fusionx.relay.dcc.event.file;

import co.fusionx.relay.dcc.file.DCCFileConversation;
import co.fusionx.relay.dcc.file.DCCGetConnection;

public class DCCFileGetStartedEvent extends DCCFileEvent {

    public final DCCGetConnection getConnection;

    public DCCFileGetStartedEvent(final DCCFileConversation fileConversation,
            final DCCGetConnection connection) {
        super(fileConversation);
        this.getConnection = connection;
    }
}