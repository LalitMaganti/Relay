package co.fusionx.relay.internal.core;

import co.fusionx.relay.conversation.QueryUser;
import co.fusionx.relay.event.query.QueryEvent;

public interface InternalQueryUser extends InternalConversation<QueryEvent>, QueryUser {
}