package co.fusionx.relay.dcc.event.file;

import co.fusionx.relay.dcc.DCCConnection;
import co.fusionx.relay.dcc.file.DCCFileConversation;

public class DCCFileProgressEvent extends DCCFileEvent {

    public final DCCConnection connection;

    public final long progress;

    public DCCFileProgressEvent(final DCCFileConversation fileConversation,
            final DCCConnection connection, final long progress) {
        super(fileConversation);

        this.connection = connection;
        this.progress = progress;
    }
}
