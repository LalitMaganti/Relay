package co.fusionx.relay.base;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.configuration.SessionConfiguration;
import co.fusionx.relay.event.file.DCCFileConversationStartedEvent;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.core.Postable;
import co.fusionx.relay.core.InternalDCCManager;
import co.fusionx.relay.internal.sender.PacketSender;

public class RelayDCCManager implements InternalDCCManager {

    private final Map<String, RelayDCCChatConversation> mChatConversations;

    private final Map<String, RelayDCCFileConversation> mFileConversations;

    private final Set<RelayDCCPendingConnection> mPendingConnections;

    private final Postable<Event> mPostable;

    private final SessionConfiguration mSessionConfiguration;

    private final LibraryUser mLibraryUser;

    private final PacketSender mPacketSender;

    @Inject
    public RelayDCCManager(final Postable<Event> bus,
            final SessionConfiguration sessionConfiguration,
            final InternalUserChannelGroup userGroup,
            final PacketSender packetSender) {
        mPostable = bus;
        mSessionConfiguration = sessionConfiguration;
        mLibraryUser = userGroup.getUser();
        mPacketSender = packetSender;

        mChatConversations = new HashMap<>();
        mFileConversations = new HashMap<>();
        mPendingConnections = new HashSet<>();
    }

    @Override
    public void addPendingConnection(final RelayDCCPendingConnection pendingConnection) {
        mPendingConnections.add(pendingConnection);
    }

    @Override
    public Collection<RelayDCCChatConversation> getChatConversations() {
        return ImmutableSet.copyOf(mChatConversations.values());
    }

    @Override
    public Collection<RelayDCCFileConversation> getFileConversations() {
        return ImmutableSet.copyOf(mFileConversations.values());
    }

    @Override
    public Collection<RelayDCCPendingConnection> getPendingConnections() {
        return ImmutableSet.copyOf(mPendingConnections);
    }

    @Override
    public void acceptDCCConnection(final RelayDCCPendingSendConnection connection,
            final File file) {
        if (!mPendingConnections.contains(connection)) {
            // TODO - Maybe send an event instead?
            mSessionConfiguration.getSettingsProvider()
                    .logNonFatalError("DCC Connection not managed by this server");
            return;
        }
        // This chat is no longer pending - remove it
        mPendingConnections.remove(connection);

        // Check if we have an existing conversation
        final Optional<RelayDCCFileConversation> optConversation = FluentIterable
                .from(mFileConversations.values())
                .filter(new Predicate<RelayDCCFileConversation>() {
                    @Override
                    public boolean apply(final RelayDCCFileConversation f) {
                        return f.getId().equals(connection.getDccRequestNick());
                    }
                })
                .first();
        // Get the conversation or a new one if it does not exist
        final RelayDCCFileConversation conversation = optConversation
                .or(new Supplier<RelayDCCFileConversation>() {
                    @Override
                    public RelayDCCFileConversation get() {
                        return new RelayDCCFileConversation(mPostable,
                                mSessionConfiguration.getConnectionConfiguration(), mPacketSender,
                                connection.getDccRequestNick());
                    }
                });

        // If the conversation was not present add it
        if (!optConversation.isPresent()) {
            mFileConversations.put(connection.getDccRequestNick(), conversation);
        }
        // A pending send becomes a get here
        conversation.getFile(connection, file);

        // The conversation has been started
        mPostable.postEvent(new DCCFileConversationStartedEvent(conversation));
    }

    @Override
    public void acceptDCCConnection(final RelayRelayDCCPendingChatConnection connection) {
        if (!mPendingConnections.contains(connection)) {
            // TODO - Maybe send an event instead?
            mSessionConfiguration.getSettingsProvider()
                    .logNonFatalError("DCC Connection not managed by this server");
            return;
        }
        // This chat is no longer pending - remove it
        mPendingConnections.remove(connection);

        final RelayDCCChatConversation conversation = new RelayDCCChatConversation(mPostable,
                mSessionConfiguration, connection, mLibraryUser);
        mChatConversations.put(connection.getDccRequestNick(), conversation);
        conversation.startChat();
    }

    @Override
    public void declineDCCConnection(final RelayDCCPendingConnection connection) {
        mPendingConnections.remove(connection);
    }

    @Override
    public RelayDCCFileConversation getFileConversation(final String nick) {
        return mFileConversations.get(nick);
    }
}