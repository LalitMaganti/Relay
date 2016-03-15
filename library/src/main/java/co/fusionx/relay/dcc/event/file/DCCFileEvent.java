package co.fusionx.relay.dcc.event.file;

import co.fusionx.relay.dcc.event.DCCEvent;
import co.fusionx.relay.dcc.file.DCCFileConversation;

public class DCCFileEvent extends DCCEvent {

    public final DCCFileConversation fileConversation;

    public DCCFileEvent(final DCCFileConversation fileConversation) {
        this.fileConversation = fileConversation;
    }
}