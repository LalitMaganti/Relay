package co.fusionx.relay.internal.base;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import co.fusionx.relay.base.IRCSession;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.base.SessionStatus;
import co.fusionx.relay.base.UserChannelDao;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.misc.GenericBus;
import dagger.ObjectGraph;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelaySession implements IRCSession {

    private final ObjectGraph mObjectGraph;

    @Inject
    StatusManager mStatusManager;

    @Inject
    RelayServer mServer;

    @Inject
    RelayUserChannelDao mDao;

    @Inject
    GenericBus<Event> mSessionBus;

    @Inject
    ScheduledExecutorService mScheduledExecutorService;

    private RelayIRCConnection mConnection;

    public RelaySession(final ServerConfiguration configuration) {
        mObjectGraph = ObjectGraph.create(new RelayBaseModule(configuration));
        mObjectGraph.inject(this);
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
    public UserChannelDao getUserChannelDao() {
        return mDao;
    }
}