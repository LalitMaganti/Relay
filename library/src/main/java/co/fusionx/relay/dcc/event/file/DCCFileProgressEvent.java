package co.fusionx.relay.dcc.event.file;

import co.fusionx.relay.internal.dcc.base.RelayDCCFileConversation;
import co.fusionx.relay.internal.dcc.core.DCCConnection;

public class DCCFileProgressEvent extends DCCFileEvent {

    public final DCCConnection connection;

    public final long progress;

    public DCCFileProgressEvent(final RelayDCCFileConversation fileConversation,
            final DCCConnection connection, final long progress) {
        super(fileConversation);

        this.connection = connection;
        this.progress = progress;
    }
}
