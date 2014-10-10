package co.fusionx.relay.core;

import java.io.File;

import co.fusionx.relay.base.RelayDCCFileConversation;
import co.fusionx.relay.base.RelayDCCPendingConnection;
import co.fusionx.relay.base.RelayDCCPendingSendConnection;
import co.fusionx.relay.base.RelayRelayDCCPendingChatConnection;

public interface InternalDCCManager extends DCCManager {

    public void addPendingConnection(RelayDCCPendingConnection pendingConnection);

    public void acceptDCCConnection(RelayDCCPendingSendConnection connection, File file);

    public void acceptDCCConnection(RelayRelayDCCPendingChatConnection connection);

    public void declineDCCConnection(RelayDCCPendingConnection connection);

    public RelayDCCFileConversation getFileConversation(String nick);
}