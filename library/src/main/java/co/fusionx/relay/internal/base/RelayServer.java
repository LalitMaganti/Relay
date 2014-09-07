package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import java.io.BufferedWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import co.fusionx.relay.base.ConnectionStatus;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.event.server.NewPrivateMessageEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.internal.dcc.RelayDCCManager;
import co.fusionx.relay.internal.sender.BaseSender;
import co.fusionx.relay.misc.EventBus;
import co.fusionx.relay.sender.ServerSender;

public class RelayServer extends RelayAbstractConversation<ServerEvent> implements Server {

    private final ServerConfiguration mConfiguration;

    private final EventBus<Event> mServerWideEventBus;

    private final Set<CapCapability> mCapabilities;

    private final RelayUserChannelInterface mUserChannelInterface;

    private final RelayDCCManager mRelayDCCManager;

    private final BaseSender mBaseSender;

    private final ServerSender mServerSender;

    private ConnectionStatus mStatus = ConnectionStatus.DISCONNECTED;

    @Inject
    RelayServer(final ServerConfiguration configuration,
            final BaseSender baseSender, final ServerSender serverSender) {
        super(null);

        mConfiguration = configuration;
        mBaseSender = baseSender;
        mServerSender = serverSender;

        mUserChannelInterface = new RelayUserChannelInterface(this, baseSender);
        mRelayDCCManager = new RelayDCCManager(this, baseSender);

        mServerWideEventBus = new EventBus<>();

        mCapabilities = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelayServer)) {
            return false;
        }
        final RelayServer server = (RelayServer) o;
        return getTitle().equals(server.getTitle());
    }

    // ServerSender interface
    @Override
    public void sendQuery(final String nick, final String message) {
        final Optional<RelayQueryUser> optional = mUserChannelInterface.getQueryUser(nick);
        final RelayQueryUser user = optional.or(mUserChannelInterface.addQueryUser(nick));
        if (!optional.isPresent()) {
            postAndStoreEvent(new NewPrivateMessageEvent(user));
        }
        user.sendMessage(message);
    }

    @Override
    public void sendJoin(final String channelName) {
        mServerSender.sendJoin(channelName);
    }

    @Override
    public void sendNick(final String newNick) {
        mServerSender.sendNick(newNick);
    }

    @Override
    public void sendWhois(final String nick) {
        mServerSender.sendWhois(nick);
    }

    @Override
    public void sendRawLine(final String rawLine) {
        mServerSender.sendRawLine(rawLine);
    }

    // Internal methods
    public void postAndStoreEvent(final ServerEvent event) {
        mBuffer.add(event);

        mEventBus.post(event);
        mServerWideEventBus.post(event);
    }

    public void onConnectionTerminated() {
        mUserChannelInterface.onConnectionTerminated();

        // Need to remove anything using the old socket OutputStream in-case a reconnection occurs
        mBaseSender.onConnectionTerminated();
    }

    public void onOutputStreamCreated(final BufferedWriter writer) {
        mBaseSender.onOutputStreamCreated(writer);
    }

    public void addCapability(final CapCapability capability) {
        mCapabilities.add(capability);
    }

    public ImmutableSet<CapCapability> getCapabilities() {
        return ImmutableSet.copyOf(mCapabilities);
    }

    public void updateStatus(final ConnectionStatus status) {
        mStatus = status;
    }

    // Conversation Interface
    @Override
    public String getId() {
        return getTitle();
    }

    @Override
    public RelayServer getServer() {
        return this;
    }

    // Server Interface - getters
    @Override
    public Collection<RelayChannelUser> getUsers() {
        return mUserChannelInterface.getUsers();
    }

    @Override
    public RelayUserChannelInterface getUserChannelInterface() {
        return mUserChannelInterface;
    }

    @Override
    public RelayMainUser getUser() {
        return mUserChannelInterface.getMainUser();
    }

    @Override
    public String getTitle() {
        return mConfiguration.getTitle();
    }

    @Override
    public ConnectionStatus getStatus() {
        return mStatus;
    }

    @Override
    public EventBus<Event> getServerWideBus() {
        return mServerWideEventBus;
    }

    @Override
    public ServerConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public RelayDCCManager getDCCManager() {
        return mRelayDCCManager;
    }
}