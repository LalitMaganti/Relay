package co.fusionx.relay.core;

import java.util.Collection;

import co.fusionx.relay.base.RelayDCCChatConversation;
import co.fusionx.relay.base.RelayDCCFileConversation;
import co.fusionx.relay.base.RelayDCCPendingConnection;

public interface DCCManager {

    public Collection<RelayDCCPendingConnection> getPendingConnections();

    public Collection<RelayDCCChatConversation> getChatConversations();

    public Collection<RelayDCCFileConversation> getFileConversations();
}
