package co.fusionx.relay.internal.core;

import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.event.server.ServerEvent;

public interface InternalServer extends InternalConversation<ServerEvent>, Server {

    void addCapability(CapCapability capability);
}