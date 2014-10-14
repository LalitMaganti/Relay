package co.fusionx.relay.internal.base;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.core.QueryUserGroup;
import co.fusionx.relay.core.Session;
import co.fusionx.relay.configuration.SessionConfiguration;
import co.fusionx.relay.core.SessionStatus;
import co.fusionx.relay.core.UserChannelGroup;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.bus.EventBus;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalStatusManager;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.provider.RelayProvider;
import co.fusionx.relay.parser.UserInputParser;
import dagger.ObjectGraph;

public class RelaySession implements Session {

    private final ObjectGraph mObjectGraph;

    private final ScheduledExecutorService mScheduledExecutorService;

    private final SessionConfiguration mConfiguration;

    @Inject
    InternalStatusManager mStatusManager;

    @Inject
    EventBus<Event> mSessionBus;

    @Inject
    InternalServer mServer;

    @Inject
    InternalUserChannelGroup mUserChannelGroup;

    @Inject
    InternalQueryUserGroup mQueryManager;

    @Inject
    UserInputParser mUserInputParser;

    private IRCConnection mConnection;

    public RelaySession(final SessionConfiguration configuration) {
        mConfiguration = configuration;

        mObjectGraph = ObjectGraph.create(new RelayProvider(configuration));
        mObjectGraph.inject(this);

        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startSession() {
        try {
            mStatusManager.resetAttemptCount();

            startConnect(this::connect, 0);
        } catch (final RuntimeException ex) {
            mConfiguration.getDebuggingProvider().handleFatalError(ex);
        }
    }

    public void stopSession() {
        if (getStatus() == SessionStatus.CONNECTED) {
            mConnection.disconnect();
        } else if (!mScheduledExecutorService.isShutdown()) {
            mScheduledExecutorService.shutdownNow();
        }
    }

    private void startConnect(final Runnable runnable, final int delay) {
        mScheduledExecutorService.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    private void connect() {
        // Create the connection from the object graph
        mConnection = mObjectGraph.get(IRCConnection.class);

        // We are now in the phase where we can say we are connecting to the server
        mStatusManager.onConnecting();

        // Attempt to connect to the server - returns a possible message related to disconnection
        final String disconnectMessage = mConnection.connect();

        if (mConnection.isStopped()) {
            mStatusManager.onStopped();
            mConnection.close();
            return;
        } else {
            mStatusManager.onDisconnected(disconnectMessage,
                    mStatusManager.isReconnectNeeded());
            mConnection.close();
            mUserChannelGroup.onConnectionTerminated();
        }

        if (mStatusManager.isReconnectNeeded()) {
            mStatusManager.onReconnecting();
            startConnect(this::reconnect, 5000);
        } else {
            mStatusManager.onDisconnected("No reconnects pending", false);
        }
    }

    private void reconnect() {
        mStatusManager.incrementAttemptCount();
        connect();
    }

    @Override
    public SessionStatus getStatus() {
        return mStatusManager.getStatus();
    }

    @Override
    public void registerForEvents(final Object object) {
        mSessionBus.registerForEvents(object);
    }

    @Override
    public void registerForEvents(final Object object, final int priority) {
        mSessionBus.registerForEvents(object, priority);
    }

    @Override
    public void unregisterFromEvents(final Object object) {
        mSessionBus.unregisterFromEvents(object);
    }

    @Override
    public Server getServer() {
        return mServer;
    }

    @Override
    public UserInputParser getUserInputParser() {
        return mUserInputParser;
    }

    @Override
    public UserChannelGroup getUserChannelManager() {
        return mUserChannelGroup;
    }

    @Override
    public QueryUserGroup getQueryManager() {
        return mQueryManager;
    }
}