package co.fusionx.relay.dcc.core;

import java.util.Collection;

import co.fusionx.relay.internal.dcc.base.RelayDCCChatConversation;
import co.fusionx.relay.internal.dcc.base.RelayDCCFileConversation;
import co.fusionx.relay.internal.dcc.base.RelayDCCPendingConnection;

public interface DCCManager {

    public Collection<RelayDCCPendingConnection> getPendingConnections();

    public Collection<RelayDCCChatConversation> getChatConversations();

    public Collection<RelayDCCFileConversation> getFileConversations();
}
