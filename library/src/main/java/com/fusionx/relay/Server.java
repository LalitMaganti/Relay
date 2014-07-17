package com.fusionx.relay;

import com.fusionx.relay.bus.ServerCallBus;
import com.fusionx.relay.bus.ServerEventBus;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.interfaces.Conversation;
import com.fusionx.relay.writers.ChannelWriter;
import com.fusionx.relay.writers.ServerWriter;
import com.fusionx.relay.writers.UserWriter;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server implements Conversation {

    private final ServerConfiguration mConfiguration;

    private final ServerConnection mServerConnection;

    private final List<ServerEvent> mBuffer;

    private final ServerEventBus mServerEventBus;

    private final ServerCallBus mServerCallBus;

    private final Set<ChannelUser> mUsers;

    private final UserChannelInterface mUserChannelInterface;

    private AppUser mUser;

    public Server(final ServerConfiguration configuration, final ServerConnection connection,
            final Collection<String> ignoreList) {
        mConfiguration = configuration;
        mServerConnection = connection;

        mBuffer = new ArrayList<>();
        mServerEventBus = new ServerEventBus(this);
        mServerCallBus = new ServerCallBus(this, connection.getServerCallHandler());

        // Set the nick name to the first choice nick
        mUser = new AppUser(configuration.getNickStorage().getFirstChoiceNick());

        mUsers = new HashSet<>();
        mUsers.add(mUser);

        mUserChannelInterface = new UserChannelInterface(this);
        mUserChannelInterface.updateIgnoreList(ignoreList);
    }

    public void onServerEvent(final ServerEvent event) {
        mBuffer.add(event);
    }

    public void onConnectionTerminated() {
        mUserChannelInterface.onConnectionTerminated();

        // Need to remove old writers as they would be using the old socket OutputStream if a
        // reconnection occurs
        mServerCallBus.onConnectionTerminated();
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
     * @return the server writer created from the OutputStreamWriter
     */
    public ServerWriter onOutputStreamCreated(final Writer writer) {
        final ServerWriter serverWriter = new ServerWriter(writer);
        mServerCallBus.register(serverWriter);
        mServerCallBus.register(new ChannelWriter(writer));
        mServerCallBus.register(new UserWriter(writer));
        return serverWriter;
    }

    public void updateIgnoreList(final Collection<String> list) {
        mUserChannelInterface.updateIgnoreList(list);
    }

    void addUser(final ChannelUser user) {
        mUsers.add(user);
    }

    void removeUser(final ChannelUser user) {
        mUsers.remove(user);
    }

    public Collection<ChannelUser> getUsers() {
        return mUsers;
    }

    // Conversation Interface
    @Override
    public String getId() {
        return getTitle();
    }

    @Override
    public Server getServer() {
        return this;
    }

    // Getters and Setters
    public List<ServerEvent> getBuffer() {
        return mBuffer;
    }

    public UserChannelInterface getUserChannelInterface() {
        return mUserChannelInterface;
    }

    public AppUser getUser() {
        return mUser;
    }

    public String getTitle() {
        return mConfiguration.getTitle();
    }

    public ConnectionStatus getStatus() {
        return mServerConnection.getStatus();
    }

    public ServerCallBus getServerCallBus() {
        return mServerCallBus;
    }

    public ServerEventBus getServerEventBus() {
        return mServerEventBus;
    }

    public ServerConfiguration getConfiguration() {
        return mConfiguration;
    }
}