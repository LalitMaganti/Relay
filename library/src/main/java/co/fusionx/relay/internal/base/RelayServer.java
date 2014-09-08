package co.fusionx.relay.internal.base;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.fusionx.relay.base.ConnectionStatus;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.internal.dcc.RelayDCCManager;
import co.fusionx.relay.misc.EventBus;
import co.fusionx.relay.sender.ServerSender;

@Singleton
public class RelayServer extends RelayAbstractConversation<ServerEvent> implements Server {

    private final ServerConfiguration mConfiguration;

    private final Set<CapCapability> mCapabilities;

    private final RelayDCCManager mRelayDCCManager;

    private final ServerSender mServerSender;

    @Inject
    RelayServer(final ServerConfiguration configuration,
            final ServerSender serverSender,
            final RelayDCCManager dccManager,
            final EventBus<Event> superBus) {
        super(superBus);

        mConfiguration = configuration;
        mServerSender = serverSender;
        mRelayDCCManager = dccManager;

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
        /*final Optional<RelayQueryUser> optional = mUserChannelInterface.getQueryUser(nick);
        final RelayQueryUser user = optional.or(mUserChannelInterface.addQueryUser(nick));
        if (!optional.isPresent()) {
            postAndStoreEvent(new NewPrivateMessageEvent(user));
        }
        user.sendMessage(message);*/
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
    public void addCapability(final CapCapability capability) {
        mCapabilities.add(capability);
    }

    public ImmutableSet<CapCapability> getCapabilities() {
        return ImmutableSet.copyOf(mCapabilities);
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
    public ServerConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public RelayDCCManager getDCCManager() {
        return mRelayDCCManager;
    }
}