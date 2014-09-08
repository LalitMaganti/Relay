package co.fusionx.relay.base;

import co.fusionx.relay.dcc.DCCManager;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.misc.EventBus;
import co.fusionx.relay.sender.ServerSender;

public interface Server extends Conversation<ServerEvent>, ServerSender {

    public String getTitle();

    public DCCManager getDCCManager();

    public ServerConfiguration getConfiguration();
}
