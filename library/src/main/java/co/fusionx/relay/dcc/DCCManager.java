package co.fusionx.relay.dcc;

import java.util.Collection;

import co.fusionx.relay.dcc.chat.DCCChatConversation;
import co.fusionx.relay.dcc.file.DCCFileConversation;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;

public interface DCCManager {

    public Collection<DCCPendingConnection> getPendingConnections();

    public Collection<DCCChatConversation> getChatConversations();

    public Collection<DCCFileConversation> getFileConversations();
}
