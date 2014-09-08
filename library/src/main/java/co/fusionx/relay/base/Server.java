package co.fusionx.relay.base;

import java.util.Collection;

import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.sender.ServerSender;

public interface Server extends Conversation<ServerEvent>, ServerSender {

    public String getTitle();

    public ConnectionConfiguration getConfiguration();

    public Collection<CapCapability> getCapabilities();
}
