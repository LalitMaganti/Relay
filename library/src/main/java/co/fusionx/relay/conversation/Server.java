package co.fusionx.relay.conversation;

import java.util.Collection;

import co.fusionx.relay.core.ConnectionConfiguration;
import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.core.SessionConfiguration;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.sender.ServerSender;

public interface Server extends Conversation<ServerEvent>, ServerSender {

    public String getTitle();

    public SessionConfiguration getConfiguration();

    public Collection<CapCapability> getCapabilities();
}
