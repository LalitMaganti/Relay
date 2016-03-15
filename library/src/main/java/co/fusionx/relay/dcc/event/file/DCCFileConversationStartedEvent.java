package co.fusionx.relay.dcc.event.file;

import co.fusionx.relay.dcc.file.DCCFileConversation;

public class DCCFileConversationStartedEvent extends DCCFileEvent {

    public DCCFileConversationStartedEvent(final DCCFileConversation dccFileConversation) {
        super(dccFileConversation);
    }
}