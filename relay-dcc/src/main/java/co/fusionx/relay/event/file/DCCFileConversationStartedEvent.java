package co.fusionx.relay.event.file;

import co.fusionx.relay.base.RelayDCCFileConversation;

public class DCCFileConversationStartedEvent extends DCCFileEvent {

    public DCCFileConversationStartedEvent(final RelayDCCFileConversation relayDccFileConversation) {
        super(relayDccFileConversation);
    }
}