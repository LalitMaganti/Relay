package co.fusionx.relay.dcc;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.dcc.chat.DCCChatConversation;
import co.fusionx.relay.dcc.event.file.DCCFileConversationStartedEvent;
import co.fusionx.relay.dcc.file.DCCFileConversation;
import co.fusionx.relay.dcc.pending.DCCPendingChatConnection;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.dcc.pending.DCCPendingSendConnection;
import gnu.trove.set.hash.THashSet;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelayDCCManager implements DCCManager {

    private final Set<DCCChatConversation> mChatConversations;

    private final Set<DCCFileConversation> mFileConversations;

    private final Set<DCCPendingConnection> mPendingConnections;

    private final RelayServer mServer;

    public RelayDCCManager(final RelayServer relayServer) {
        mServer = relayServer;
        mChatConversations = new THashSet<>();
        mFileConversations = new THashSet<>();
        mPendingConnections = new THashSet<>();
    }

    public void addPendingConnection(final DCCPendingConnection pendingConnection) {
        mPendingConnections.add(pendingConnection);
    }

    @Override
    public Collection<DCCChatConversation> getChatConversations() {
        return ImmutableSet.copyOf(mChatConversations);
    }

    @Override
    public Collection<DCCFileConversation> getFileConversations() {
        return ImmutableSet.copyOf(mFileConversations);
    }

    @Override
    public Collection<DCCPendingConnection> getPendingConnections() {
        return ImmutableSet.copyOf(mPendingConnections);
    }

    public void acceptDCCConnection(final DCCPendingSendConnection connection, final File file) {
        if (!mPendingConnections.contains(connection)) {
            // TODO - Maybe send an event instead?
            getPreferences().logServerLine("DCC Connection not managed by this server");
            return;
        }
        // This chat is no longer pending - remove it
        mPendingConnections.remove(connection);

        // Check if we have an existing conversation
        final Optional<DCCFileConversation> optConversation = FluentIterable
                .from(mFileConversations)
                .filter(f -> f.getId().equals(connection.getDccRequestNick()))
                .first();
        // Get the conversation or a new one if it does not exist
        final DCCFileConversation conversation = optConversation
                .or(new DCCFileConversation(mServer, connection.getDccRequestNick()));
        // If the conversation was not present add it
        if (!optConversation.isPresent()) {
            mFileConversations.add(conversation);
        }
        // A pending send becomes a get here
        conversation.getFile(connection, file);

        // The conversation has been started
        mServer.getServerEventBus().post(new DCCFileConversationStartedEvent(conversation));
    }

    public void acceptDCCConnection(final DCCPendingChatConnection connection) {
        if (!mPendingConnections.contains(connection)) {
            // TODO - Maybe send an event instead?
            getPreferences().logServerLine("DCC Connection not managed by this server");
            return;
        }
        // This chat is no longer pending - remove it
        mPendingConnections.remove(connection);

        final DCCChatConversation conversation = new DCCChatConversation(mServer, connection);
        mChatConversations.add(conversation);
        conversation.startChat();
    }

    public void declineDCCConnection(final DCCPendingConnection connection) {
        mPendingConnections.remove(connection);
    }
}