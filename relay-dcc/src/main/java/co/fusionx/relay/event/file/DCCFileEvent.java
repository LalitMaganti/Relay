package co.fusionx.relay.event.file;

import co.fusionx.relay.internal.base.RelayDCCFileConversation;
import co.fusionx.relay.event.DCCEvent;

public class DCCFileEvent extends DCCEvent<RelayDCCFileConversation, DCCFileEvent> {

    public DCCFileEvent(final RelayDCCFileConversation fileConversation) {
        super(fileConversation);
    }
}