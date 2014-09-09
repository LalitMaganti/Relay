package co.fusionx.relay.internal.base;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.core.ConnectionConfiguration;
import co.fusionx.relay.core.QueryUserGroup;
import co.fusionx.relay.core.Session;
import co.fusionx.relay.core.SessionStatus;
import co.fusionx.relay.core.UserChannelGroup;
import co.fusionx.relay.dcc.DCCManager;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalStatusManager;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.dcc.RelayDCCManager;
import dagger.ObjectGraph;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelaySession implements Session {

    private final ObjectGraph mObjectGraph;

    private final ScheduledExecutorService mScheduledExecutorService;

    @Inject
    InternalStatusManager mInternalStatusManager;

    @Inject
    GenericBus<Event> mSessionBus;

    @Inject
    InternalServer mServer;

    @Inject
    InternalUserChannelGroup mDao;

    @Inject
    InternalQueryUserGroup mQueryManager;

    @Inject
    RelayDCCManager mDCCManager;

    private RelayIRCConnection mConnection;

    public RelaySession(final ConnectionConfiguration configuration) {
        mObjectGraph = ObjectGraph.create(new RelayBaseModule(configuration));
        mObjectGraph.inject(this);

        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startSession() {
        try {
            mInternalStatusManager.resetAttemptCount();

            startConnect(this::connect, 0);
        } catch (final RuntimeException ex) {
            getPreferences().handleException(ex);
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
        mConnection = mObjectGraph.get(RelayIRCConnection.class);
        mConnection.connect();

        if (!mConnection.isStopped() && mInternalStatusManager.isReconnectNeeded()) {
            mInternalStatusManager.onReconnecting();
            startConnect(this::reconnect, 5000);
        } else if (!mConnection.isStopped()) {
            mInternalStatusManager.onDisconnected("No reconnects pending", false);
        }
    }

    private void reconnect() {
        mInternalStatusManager.incrementAttemptCount();
        connect();
    }

    @Override
    public SessionStatus getStatus() {
        return mInternalStatusManager.getStatus();
    }

    @Override
    public GenericBus<Event> getSessionBus() {
        return mSessionBus;
    }

    @Override
    public Server getServer() {
        return mServer;
    }

    @Override
    public UserChannelGroup getUserChannelManager() {
        return mDao;
    }

    @Override
    public QueryUserGroup getQueryManager() {
        return mQueryManager;
    }

    @Override
    public DCCManager getDCCManager() {
        return mDCCManager;
    }
}