package co.fusionx.relay.internal.dcc.core;

import java.io.File;

import co.fusionx.relay.dcc.core.DCCManager;
import co.fusionx.relay.internal.dcc.base.RelayDCCFileConversation;
import co.fusionx.relay.internal.dcc.base.RelayDCCPendingConnection;
import co.fusionx.relay.internal.dcc.base.RelayDCCPendingSendConnection;
import co.fusionx.relay.internal.dcc.base.RelayRelayDCCPendingChatConnection;

public interface InternalDCCManager extends DCCManager {

    public void addPendingConnection(RelayDCCPendingConnection pendingConnection);

    public void acceptDCCConnection(RelayDCCPendingSendConnection connection, File file);

    public void acceptDCCConnection(RelayRelayDCCPendingChatConnection connection);

    public void declineDCCConnection(RelayDCCPendingConnection connection);

    public RelayDCCFileConversation getFileConversation(String nick);
}