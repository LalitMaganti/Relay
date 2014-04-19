package com.fusionx.relay;

import com.fusionx.relay.communication.ServerCallBus;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.connection.ServerConnection;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.interfaces.Conversation;
import com.fusionx.relay.writers.ChannelWriter;
import com.fusionx.relay.writers.ServerWriter;
import com.fusionx.relay.writers.UserWriter;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Server implements Conversation {

    private final ServerConnection mServerConnection;

    private final UserChannelInterface mUserChannelInterface;

    private final List<ServerEvent> mBuffer;

    private final ServerConfiguration mConfiguration;

    private final ServerEventBus mServerEventBus;

    private final ServerCallBus mServerCallBus;

    private AppUser mUser;

    public Server(final ServerConfiguration configuration, final ServerConnection connection,
            List<String> ignoreList) {
        mServerConnection = connection;
        mConfiguration = configuration;
        mBuffer = new ArrayList<>();
        mServerEventBus = new ServerEventBus(this);
        mServerCallBus = new ServerCallBus(this, connection.getServerCallHandler());
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
        if (o == null || !(o instanceof Server)) {
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

    public void updateIgnoreList(final List<String> list) {
        mUserChannelInterface.updateIgnoreList(list);
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

    public void setUser(final AppUser user) {
        mUser = user;
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
}