package co.fusionx.relay.internal.base;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import co.fusionx.relay.base.ConnectionConfiguration;
import co.fusionx.relay.base.Session;
import co.fusionx.relay.base.QueryUserGroup;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.base.SessionStatus;
import co.fusionx.relay.base.UserChannelGroup;
import co.fusionx.relay.dcc.DCCManager;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.dcc.RelayDCCManager;
import co.fusionx.relay.bus.GenericBus;
import dagger.ObjectGraph;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelaySession implements Session {

    private final ObjectGraph mObjectGraph;

    private final ScheduledExecutorService mScheduledExecutorService;

    @Inject
    StatusManager mStatusManager;

    @Inject
    GenericBus<Event> mSessionBus;

    @Inject
    RelayServer mServer;

    @Inject
    RelayUserChannelGroup mDao;

    @Inject
    RelayQueryUserGroup mQueryManager;

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
            startSessionQuietly();
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

    private void startSessionQuietly() {
        // Start the session with no delay
        startConnect(0);

        for (; mStatusManager.isReconnectNeeded(); mStatusManager.incrementAttemptCount()) {
            startConnect(5000);
        }
    }

    private void startConnect(final int delay) {
        mConnection = mObjectGraph.get(RelayIRCConnection.class);
        mScheduledExecutorService.schedule(mConnection::connect, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public SessionStatus getStatus() {
        return mStatusManager.getStatus();
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