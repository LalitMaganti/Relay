package co.fusionx.relay.internal.base;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.base.SessionStatus;
import co.fusionx.relay.event.channel.ChannelConnectEvent;
import co.fusionx.relay.event.channel.ChannelDisconnectEvent;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelStopEvent;
import co.fusionx.relay.event.query.QueryConnectEvent;
import co.fusionx.relay.event.query.QueryDisconnectEvent;
import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.event.query.QueryStopEvent;
import co.fusionx.relay.event.server.ConnectEvent;
import co.fusionx.relay.event.server.ConnectingEvent;
import co.fusionx.relay.event.server.DisconnectEvent;
import co.fusionx.relay.event.server.ReconnectEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.event.server.StopEvent;
import co.fusionx.relay.internal.function.FluentIterables;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

@Singleton
public class RelayStatusManager implements StatusManager {

    private final ServerConfiguration mConfiguration;

    private final RelayServer mServer;

    private final RelayUserChannelDao mDao;

    private int mReconnectAttempts;

    private SessionStatus mStatus = SessionStatus.DISCONNECTED;

    @Inject
    public RelayStatusManager(final ServerConfiguration configuration, final RelayServer server,
            final RelayUserChannelDao dao) {
        mConfiguration = configuration;
        mServer = server;
        mDao = dao;

        mReconnectAttempts = 0;
    }

    @Override
    public SessionStatus getStatus() {
        return mStatus;
    }

    @Override
    public void incrementAttemptCount() {
        mReconnectAttempts += 1;
    }

    @Override
    public boolean isReconnectNeeded() {
        return mReconnectAttempts < getPreferences().getReconnectAttemptsCount();
    }

    @Override
    public void onConnecting() {
        mStatus = SessionStatus.CONNECTING;

        mServer.postAndStoreEvent(new ConnectingEvent(mServer));
    }

    @Override
    public void onReconnecting() {
        mStatus = SessionStatus.RECONNECTING;

        mServer.postAndStoreEvent(new ReconnectEvent(mServer));
    }

    @Override
    public void onConnected() {
        // Since we are now connected, reset the reconnect attempts
        mReconnectAttempts = 0;

        onStatusChanged(SessionStatus.CONNECTED,
                ChannelConnectEvent::new,
                QueryConnectEvent::new,
                () -> new ConnectEvent(mServer, mConfiguration.getUrl()));

        // Since we are now connected we can try to rejoin the channels we had joined previously
        // or if we weren't joined to any channels previously then send a join for each of the
        // auto join channels
        final Collection<RelayChannel> channels = mDao.getUser().getChannels();
        final FluentIterable<String> channelNames = channels.isEmpty()
                ? FluentIterable.from(mConfiguration.getAutoJoinChannels())
                : FluentIterable.from(channels).transform(RelayChannel::getName);
        FluentIterables.forEach(channelNames, mServer::sendJoin);
    }

    @Override
    public void onDisconnected(final String serverMessage, final boolean retryPending) {
        onStatusChanged(SessionStatus.DISCONNECTED,
                channel -> new ChannelDisconnectEvent(channel, serverMessage),
                user -> new QueryDisconnectEvent(user, serverMessage),
                () -> new DisconnectEvent(mServer, serverMessage, retryPending));
    }

    @Override
    public void onStopped() {
        mStatus = SessionStatus.STOPPED;

        for (final RelayChannel channel : mDao.getUser().getChannels()) {
            channel.postAndStoreEvent(new ChannelStopEvent(channel));
            channel.markInvalid();
        }

        for (final RelayQueryUser user : mDao.getUser().getQueryUsers()) {
            user.postAndStoreEvent(new QueryStopEvent(user));
            user.markInvalid();
        }

        mServer.postAndStoreEvent(new StopEvent(mServer));
        mServer.markInvalid();
    }

    private void onStatusChanged(final SessionStatus status,
            final Function<RelayChannel, ChannelEvent> channelFunction,
            final Function<RelayQueryUser, QueryEvent> queryFunction,
            final Supplier<ServerEvent> serverFunction) {
        mStatus = status;

        FluentIterables.forEach(FluentIterable.from(mDao.getUser().getChannels()),
                c -> c.postAndStoreEvent(channelFunction.apply(c)));

        FluentIterables.forEach(FluentIterable.from(mDao.getUser().getQueryUsers()),
                u -> u.postAndStoreEvent(queryFunction.apply(u)));

        mServer.postAndStoreEvent(serverFunction.get());
    }
}