package co.fusionx.relay.internal.base;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.fusionx.relay.base.ConnectionConfiguration;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.internal.sender.base.RelayServerSender;
import co.fusionx.relay.internal.sender.packet.PacketSender;

@Singleton
public class RelayServer extends RelayAbstractConversation<ServerEvent> implements Server {

    private final ConnectionConfiguration mConfiguration;

    private final Set<CapCapability> mCapabilities;

    private final RelayServerSender mServerSender;

    @Inject
    RelayServer(final GenericBus<Event> sessionBus, final ConnectionConfiguration configuration,
            final PacketSender packetSender, final RelayQueryUserGroup group) {
        super(sessionBus);

        mConfiguration = configuration;
        mServerSender = new RelayServerSender(packetSender, this, group);

        mCapabilities = new HashSet<>();
    }

    // Internal methods
    public void addCapability(final CapCapability capability) {
        mCapabilities.add(capability);
    }

    // Conversation Interface
    @Override
    public String getId() {
        return getTitle();
    }

    // Server Interface - getters
    @Override
    public String getTitle() {
        return mConfiguration.getTitle();
    }

    @Override
    public ConnectionConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public ImmutableSet<CapCapability> getCapabilities() {
        return ImmutableSet.copyOf(mCapabilities);
    }

    // Equals and hashcode
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelayServer)) {
            return false;
        }

        final RelayServer server = (RelayServer) o;
        return getTitle().equals(server.getTitle());
    }

    @Override
    public int hashCode() {
        return getTitle().hashCode();
    }

    // ServerSender interface
    @Override
    public void sendQuery(final String nick, final String message) {
        mServerSender.sendQuery(nick, message);
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