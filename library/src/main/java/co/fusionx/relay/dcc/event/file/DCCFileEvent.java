package co.fusionx.relay.dcc.event.file;

import co.fusionx.relay.dcc.event.DCCEvent;
import co.fusionx.relay.dcc.file.DCCFileConversation;

public class DCCFileEvent extends DCCEvent<DCCFileConversation, DCCFileEvent> {

    public DCCFileEvent(final DCCFileConversation fileConversation) {
        super(fileConversation);
    }
}