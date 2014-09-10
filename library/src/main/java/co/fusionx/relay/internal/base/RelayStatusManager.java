package co.fusionx.relay.internal.base;

import com.google.common.collect.FluentIterable;

import java.util.Collection;

import javax.inject.Inject;

import co.fusionx.relay.core.SessionConfiguration;
import co.fusionx.relay.core.SessionStatus;
import co.fusionx.relay.event.channel.ChannelConnectEvent;
import co.fusionx.relay.event.channel.ChannelDisconnectEvent;
import co.fusionx.relay.event.channel.ChannelStopEvent;
import co.fusionx.relay.event.query.QueryConnectEvent;
import co.fusionx.relay.event.query.QueryDisconnectEvent;
import co.fusionx.relay.event.query.QueryStopEvent;
import co.fusionx.relay.event.server.ConnectEvent;
import co.fusionx.relay.event.server.ConnectingEvent;
import co.fusionx.relay.event.server.DisconnectEvent;
import co.fusionx.relay.event.server.ReconnectEvent;
import co.fusionx.relay.event.server.StopEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalStatusManager;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.function.FluentIterables;

public class RelayStatusManager implements InternalStatusManager {

    private final SessionConfiguration mConfiguration;

    private final InternalServer mServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final InternalQueryUserGroup mQueryUserGroup;

    private int mReconnectAttempts;

    private SessionStatus mStatus = SessionStatus.DISCONNECTED;

    @Inject
    public RelayStatusManager(final SessionConfiguration configuration,
            final InternalServer server, final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryUserGroup) {
        mConfiguration = configuration;
        mServer = server;
        mUserChannelGroup = userChannelGroup;
        mQueryUserGroup = queryUserGroup;

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
    public void resetAttemptCount() {
        mReconnectAttempts = 0;
    }

    @Override
    public boolean isReconnectNeeded() {
        return mReconnectAttempts < mConfiguration.getSettingsProvider().getReconnectAttempts();
    }

    @Override
    public void onConnecting() {
        mStatus = SessionStatus.CONNECTING;

        mServer.getBus().post(new ConnectingEvent(mServer));
    }

    @Override
    public void onReconnecting() {
        mStatus = SessionStatus.RECONNECTING;

        mServer.getBus().post(new ReconnectEvent(mServer));
    }

    @Override
    public void onConnected() {
        // Since we are now connected, reset the reconnect attempts
        mReconnectAttempts = 0;

        mStatus = SessionStatus.CONNECTED;

        for (final InternalChannel channel : mUserChannelGroup.getUser().getChannels()) {
            channel.getBus().post(new ChannelConnectEvent(channel));
        }
        for (final InternalQueryUser user : mQueryUserGroup.getQueryUsers()) {
            user.getBus().post(new QueryConnectEvent(user));
        }
        mServer.getBus().post(new ConnectEvent(mServer,
                mConfiguration.getConnectionConfiguration().getUrl()));

        // Since we are now connected we can try to rejoin the channels we had joined previously
        // or if we weren't joined to any channels previously then send a join for each of the
        // auto join channels
        final Collection<InternalChannel> channels = mUserChannelGroup.getUser().getChannels();
        final FluentIterable<String> channelNames = channels.isEmpty()
                ? FluentIterable.from(mConfiguration.getConnectionConfiguration()
                .getAutoJoinChannels())
                : FluentIterable.from(channels).transform(InternalChannel::getName);
        FluentIterables.forEach(channelNames, mServer::sendJoin);
    }

    @Override
    public void onDisconnected(final String serverMessage, final boolean retryPending) {
        mStatus = SessionStatus.DISCONNECTED;

        for (final InternalChannel channel : mUserChannelGroup.getUser().getChannels()) {
            channel.getBus().post(new ChannelDisconnectEvent(channel, serverMessage));
        }
        for (final InternalQueryUser user : mQueryUserGroup.getQueryUsers()) {
            user.getBus().post(new QueryDisconnectEvent(user, serverMessage));
        }
        mServer.getBus().post(new DisconnectEvent(mServer, serverMessage, retryPending));
    }

    @Override
    public void onStopped() {
        mStatus = SessionStatus.STOPPED;

        for (final InternalChannel channel : mUserChannelGroup.getUser().getChannels()) {
            channel.getBus().post(new ChannelStopEvent(channel));
            channel.markInvalid();
        }

        for (final InternalQueryUser user : mQueryUserGroup.getQueryUsers()) {
            user.getBus().post(new QueryStopEvent(user));
            user.markInvalid();
        }

        mServer.getBus().post(new StopEvent(mServer));
        mServer.markInvalid();
    }
}