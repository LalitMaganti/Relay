package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import java.io.BufferedWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import co.fusionx.relay.base.ConnectionStatus;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.internal.dcc.RelayDCCManager;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.event.server.NewPrivateMessageEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.misc.EventBus;
import co.fusionx.relay.sender.ServerSender;
import co.fusionx.relay.internal.sender.RelayPacketSender;
import co.fusionx.relay.internal.sender.RelayServerSender;

public class RelayServer extends RelayAbstractConversation<ServerEvent> implements Server {

    private final ServerConfiguration mConfiguration;

    private final RelayIRCConnection mRelayIRCConnection;

    private final Set<RelayChannelUser> mUsers;

    private final RelayPacketSender mRelayPacketSender;

    private final ServerSender mServerSender;

    private final EventBus<Event> mServerWideEventBus;

    private final Set<CapCapability> mCapabilities;

    private final RelayUserChannelInterface mUserChannelInterface;

    private final RelayMainUser mUser;

    private final RelayDCCManager mRelayDCCManager;

    public RelayServer(final ServerConfiguration configuration,
            final RelayIRCConnection connection) {
        super(null);

        mConfiguration = configuration;
        mRelayIRCConnection = connection;

        // Set the nick name to the first choice nick
        mUser = new RelayMainUser(configuration.getNickStorage().getFirst());

        mUsers = new HashSet<>();
        mUsers.add(mUser);

        mUserChannelInterface = new RelayUserChannelInterface(this);

        // Create the DCCManager
        mRelayDCCManager = new RelayDCCManager(this);

        mServerWideEventBus = new EventBus<>();
        mRelayPacketSender = new RelayPacketSender();

        mCapabilities = new HashSet<>();

        // Create the server sender
        mServerSender = new RelayServerSender(this, mRelayPacketSender);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelayServer)) {
            return false;
        }
        final RelayServer server = (RelayServer) o;
        return getTitle().equals(server.getTitle());
    }

    // Server Interface
    @Override
    public Collection<RelayChannelUser> getUsers() {
        return mUsers;
    }

    @Override
    public String getId() {
        return getTitle();
    }

    @Override
    public RelayServer getServer() {
        return this;
    }

    @Override
    public RelayUserChannelInterface getUserChannelInterface() {
        return mUserChannelInterface;
    }

    @Override
    public RelayMainUser getUser() {
        return mUser;
    }

    @Override
    public String getTitle() {
        return mConfiguration.getTitle();
    }

    @Override
    public ConnectionStatus getStatus() {
        return mRelayIRCConnection.getStatus();
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
        // Clear the global list of users - it's now invalid
        mUsers.clear();

        // Keep our own user inside though
        mUsers.add(mUser);

        // Need to remove anything using the old socket OutputStream in-case a reconnection occurs
        mRelayPacketSender.onConnectionTerminated();
    }

    public void onOutputStreamCreated(final BufferedWriter writer) {
        mRelayPacketSender.onOutputStreamCreated(writer);
    }

    public void addCapability(final CapCapability capability) {
        mCapabilities.add(capability);
    }

    public ImmutableSet<CapCapability> getCapabilities() {
        return ImmutableSet.copyOf(mCapabilities);
    }

    void addUser(final RelayChannelUser user) {
        mUsers.add(user);
    }

    void removeUser(final RelayChannelUser user) {
        mUsers.remove(user);
    }

    public RelayPacketSender getRelayPacketSender() {
        return mRelayPacketSender;
    }
}