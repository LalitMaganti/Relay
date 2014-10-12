package co.fusionx.relay.event.file;

import co.fusionx.relay.internal.base.RelayDCCFileConversation;
import co.fusionx.relay.internal.base.RelayDCCGetConnection;

public class DCCFileGetStartedEvent extends DCCFileEvent {

    public final RelayDCCGetConnection getConnection;

    public DCCFileGetStartedEvent(final RelayDCCFileConversation fileConversation,
            final RelayDCCGetConnection connection) {
        super(fileConversation);
        this.getConnection = connection;
    }
}