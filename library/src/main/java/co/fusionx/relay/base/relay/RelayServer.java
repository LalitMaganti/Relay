package co.fusionx.relay.base.relay;

import com.google.common.base.Optional;

import android.os.Handler;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.fusionx.relay.base.ConnectionStatus;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.sender.relay.RelayPacketSender;
import co.fusionx.relay.bus.ServerEventBus;
import co.fusionx.relay.dcc.RelayDCCManager;
import co.fusionx.relay.event.server.NewPrivateMessageEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.sender.relay.RelayServerSender;
import co.fusionx.relay.sender.ServerSender;

public class RelayServer implements Server {

    private final ServerConfiguration mConfiguration;

    private final RelayIRCConnection mRelayIRCConnection;

    private final List<ServerEvent> mBuffer;

    private final ServerEventBus mServerEventBus;

    private final RelayPacketSender mRelayPacketSender;

    private final Set<RelayChannelUser> mUsers;

    private final RelayUserChannelInterface mUserChannelInterface;

    private final ServerSender mServerSender;

    private final RelayMainUser mUser;

    private final RelayDCCManager mRelayDCCManager;

    private boolean mValid;

    public RelayServer(final ServerConfiguration configuration, final RelayIRCConnection connection,
            final Handler callHandler, final Collection<String> ignoreList) {
        mConfiguration = configuration;
        mRelayIRCConnection = connection;

        mUserChannelInterface = new RelayUserChannelInterface(this);
        mUserChannelInterface.updateIgnoreList(ignoreList);

        // Create the DCCManager
        mRelayDCCManager = new RelayDCCManager(this);

        // The server is valid :)
        mValid = true;

        mBuffer = new ArrayList<>();
        mServerEventBus = new ServerEventBus();
        mRelayPacketSender = new RelayPacketSender(callHandler);

        // Create the server sender
        mServerSender = new RelayServerSender(this, mRelayPacketSender);

        // Set the nick name to the first choice nick
        mUser = new RelayMainUser(configuration.getNickStorage().getFirst());

        mUsers = new HashSet<>();
        mUsers.add(mUser);
    }

    public void postAndStoreEvent(final ServerEvent event) {
        mBuffer.add(event);
        mServerEventBus.post(event);
    }

    public void onServerEvent(final ServerEvent event) {
        mBuffer.add(event);
    }

    public void onConnectionTerminated() {
        // Clear the global list of users - it's now invalid
        mUsers.clear();

        // Keep our own user inside though
        mUsers.add(mUser);

        // Need to remove anything using the old socket OutputStream in-case a reconnection occurs
        mRelayPacketSender.onConnectionTerminated();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelayServer)) {
            return false;
        }
        final RelayServer server = (RelayServer) o;
        return getTitle().equals(server.getTitle());
    }

    /**
     * Sets up the writers based on the output stream passed into the method
     *
     * @param writer the which the writers will use
     */
    public void onOutputStreamCreated(final BufferedWriter writer) {
        mRelayPacketSender.onOutputStreamCreated(writer);
    }

    public RelayIRCConnection getRelayIRCConnection() {
        return mRelayIRCConnection;
    }

    void addUser(final RelayChannelUser user) {
        mUsers.add(user);
    }

    void removeUser(final RelayChannelUser user) {
        mUsers.remove(user);
    }

    public void markInvalid() {
        mValid = false;
    }

    public RelayPacketSender getRelayPacketSender() {
        return mRelayPacketSender;
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
    public boolean isValid() {
        return mValid;
    }

    @Override
    public List<ServerEvent> getBuffer() {
        return mBuffer;
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
    public ServerEventBus getServerEventBus() {
        return mServerEventBus;
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
}