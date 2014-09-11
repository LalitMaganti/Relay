package co.fusionx.relay.internal.dcc;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.core.SessionConfiguration;
import co.fusionx.relay.dcc.DCCManager;
import co.fusionx.relay.dcc.chat.DCCChatConversation;
import co.fusionx.relay.dcc.event.file.DCCFileConversationStartedEvent;
import co.fusionx.relay.dcc.file.DCCFileConversation;
import co.fusionx.relay.dcc.pending.DCCPendingChatConnection;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.dcc.pending.DCCPendingSendConnection;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.bus.PostableBus;
import co.fusionx.relay.internal.sender.PacketSender;

@Singleton
public class RelayDCCManager implements DCCManager {

    private final Map<String, DCCChatConversation> mChatConversations;

    private final Map<String, DCCFileConversation> mFileConversations;

    private final Set<DCCPendingConnection> mPendingConnections;

    private final PostableBus<Event> mBus;

    private final SessionConfiguration mSessionConfiguration;

    private final PacketSender mPacketSender;

    @Inject
    public RelayDCCManager(final PostableBus<Event> bus,
            final SessionConfiguration sessionConfiguration,
            final PacketSender packetSender) {
        mBus = bus;
        mSessionConfiguration = sessionConfiguration;
        mPacketSender = packetSender;

        mChatConversations = new HashMap<>();
        mFileConversations = new HashMap<>();
        mPendingConnections = new HashSet<>();
    }

    public void addPendingConnection(final DCCPendingConnection pendingConnection) {
        mPendingConnections.add(pendingConnection);
    }

    @Override
    public Collection<DCCChatConversation> getChatConversations() {
        return ImmutableSet.copyOf(mChatConversations.values());
    }

    @Override
    public Collection<DCCFileConversation> getFileConversations() {
        return ImmutableSet.copyOf(mFileConversations.values());
    }

    @Override
    public Collection<DCCPendingConnection> getPendingConnections() {
        return ImmutableSet.copyOf(mPendingConnections);
    }

    public void acceptDCCConnection(final DCCPendingSendConnection connection, final File file) {
        if (!mPendingConnections.contains(connection)) {
            // TODO - Maybe send an event instead?
            mSessionConfiguration.getSettingsProvider()
                    .logNonFatalError("DCC Connection not managed by this server");
            return;
        }
        // This chat is no longer pending - remove it
        mPendingConnections.remove(connection);

        // Check if we have an existing conversation
        final Optional<DCCFileConversation> optConversation = FluentIterable
                .from(mFileConversations.values())
                .filter(f -> f.getId().equals(connection.getDccRequestNick()))
                .first();
        // Get the conversation or a new one if it does not exist
        final DCCFileConversation conversation = optConversation
                .or(() -> new DCCFileConversation(mBus,
                        mSessionConfiguration.getConnectionConfiguration(), mPacketSender,
                        connection.getDccRequestNick()));

        // If the conversation was not present add it
        if (!optConversation.isPresent()) {
            mFileConversations.put(connection.getDccRequestNick(), conversation);
        }
        // A pending send becomes a get here
        conversation.getFile(connection, file);

        // The conversation has been started
        mBus.postEvent(new DCCFileConversationStartedEvent(conversation));
    }

    public void acceptDCCConnection(final DCCPendingChatConnection connection) {
        if (!mPendingConnections.contains(connection)) {
            // TODO - Maybe send an event instead?
            mSessionConfiguration.getSettingsProvider()
                    .logNonFatalError("DCC Connection not managed by this server");
            return;
        }
        // This chat is no longer pending - remove it
        mPendingConnections.remove(connection);

        final DCCChatConversation conversation = new DCCChatConversation(mBus,
                mSessionConfiguration, connection);
        mChatConversations.put(connection.getDccRequestNick(), conversation);
        conversation.startChat();
    }

    public void declineDCCConnection(final DCCPendingConnection connection) {
        mPendingConnections.remove(connection);
    }

    public DCCFileConversation getFileConversation(final String nick) {
        return mFileConversations.get(nick);
    }
}