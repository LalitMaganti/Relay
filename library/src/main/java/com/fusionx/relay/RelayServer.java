package com.fusionx.relay;

import com.fusionx.relay.bus.ServerCallHandler;
import com.fusionx.relay.bus.ServerEventBus;
import com.fusionx.relay.event.server.ServerEvent;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RelayServer implements Server {

    private final ServerConfiguration mConfiguration;

    private final ServerConnection mServerConnection;

    private final List<ServerEvent> mBuffer;

    private final ServerEventBus mServerEventBus;

    private final ServerCallHandler mServerCallHandler;

    private final Set<RelayChannelUser> mUsers;

    private final RelayUserChannelInterface mUserChannelInterface;

    private final boolean mValid;

    private final RelayMainUser mUser;

    public RelayServer(final ServerConfiguration configuration, final ServerConnection connection,
            final Collection<String> ignoreList) {
        mConfiguration = configuration;
        mServerConnection = connection;

        mUserChannelInterface = new RelayUserChannelInterface(this);
        mUserChannelInterface.updateIgnoreList(ignoreList);

        mValid = true;

        mBuffer = new ArrayList<>();
        mServerEventBus = new ServerEventBus(this);
        mServerCallHandler = new ServerCallHandler(this, connection.getServerCallHandler());

        // Set the nick name to the first choice nick
        mUser = new RelayMainUser(configuration.getNickStorage().getFirstChoiceNick());

        mUsers = new HashSet<>();
        mUsers.add(mUser);
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
        mServerCallHandler.onConnectionTerminated();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Server)) {
            return false;
        }
        final Server server = (Server) o;
        return server.getTitle().equals(getTitle());
    }

    /**
     * Sets up the writers based on the output stream passed into the method
     *
     * @param writer the which the writers will use
     */
    public void onOutputStreamCreated(final BufferedWriter writer) {
        mServerCallHandler.onOutputStreamCreated(writer);
    }

    @Override
    public void updateIgnoreList(final Collection<String> list) {
        mUserChannelInterface.updateIgnoreList(list);
    }

    public ServerConnection getServerConnection() {
        return mServerConnection;
    }

    void addUser(final RelayChannelUser user) {
        mUsers.add(user);
    }

    void removeUser(final RelayChannelUser user) {
        mUsers.remove(user);
    }

    @Override
    public Collection<RelayChannelUser> getUsers() {
        return mUsers;
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

    @Override
    public boolean isValid() {
        return mValid;
    }

    // Getters and Setters
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
        return mServerConnection.getStatus();
    }

    @Override
    public ServerCallHandler getServerCallHandler() {
        return mServerCallHandler;
    }

    @Override
    public ServerEventBus getServerEventBus() {
        return mServerEventBus;
    }

    @Override
    public ServerConfiguration getConfiguration() {
        return mConfiguration;
    }
}