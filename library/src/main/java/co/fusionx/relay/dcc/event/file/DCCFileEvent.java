package co.fusionx.relay.dcc.event.file;

import co.fusionx.relay.internal.dcc.base.RelayDCCFileConversation;
import co.fusionx.relay.dcc.event.DCCEvent;

public class DCCFileEvent extends DCCEvent<RelayDCCFileConversation, DCCFileEvent> {

    public DCCFileEvent(final RelayDCCFileConversation fileConversation) {
        super(fileConversation);
    }
}