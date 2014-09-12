package co.fusionx.relay.dcc.event.file;

import co.fusionx.relay.internal.dcc.base.RelayDCCFileConversation;

public class DCCFileConversationStartedEvent extends DCCFileEvent {

    public DCCFileConversationStartedEvent(final RelayDCCFileConversation relayDccFileConversation) {
        super(relayDccFileConversation);
    }
}