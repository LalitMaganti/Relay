package com.fusionx.relay.logging;

import com.fusionx.relay.Server;
import com.fusionx.relay.event.Event;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gnu.trove.map.hash.THashMap;

/**
 * This class is NOT thread safe
 */
public abstract class LoggingManager {

    private final Map<Server, LogHandler> mLoggingServers;

    private ExecutorService sLoggingService;

    private boolean mStarted;

    protected LoggingManager() {
        mLoggingServers = new THashMap<>();
        mStarted = false;
    }

    public void addServerToManager(final Server server) {
        if (mLoggingServers.containsKey(server)) {
            throw new IllegalArgumentException("This server is already present in this manager");
        }
        mLoggingServers.put(server, new LogHandler(server));
    }

    public void removeServerFromManager(final Server server) {
        if (!mLoggingServers.containsKey(server)) {
            throw new IllegalArgumentException("This server is not present in this manager");
        }
        mLoggingServers.remove(server);
    }

    public void startLogging() {
        if (mStarted) {
            throw new IllegalArgumentException("Already started.");
        }
        mStarted = true;

        // Start the logging service
        sLoggingService = Executors.newSingleThreadExecutor();

        for (final Map.Entry<Server, LogHandler> entry : mLoggingServers.entrySet()) {
            entry.getValue().startLogging();
        }
    }

    public void stopLogging() {
        if (!mStarted) {
            throw new IllegalArgumentException("Already stopped.");
        }
        mStarted = false;

        // Kill the logging service
        sLoggingService.shutdownNow();
        sLoggingService = null;

        for (final Map.Entry<Server, LogHandler> entry : mLoggingServers.entrySet()) {
            entry.getValue().stopLogging();
        }
    }

    public abstract CharSequence getMessageFromEvent(final Event event);

    private final class LogHandler {

        private final Server mServer;

        public LogHandler(final Server server) {
            mServer = server;
        }

        public void startLogging() {
            mServer.getServerEventBus().register(this);
        }

        public void stopLogging() {
            mServer.getServerEventBus().unregister(this);
        }

        public void onEvent(final Event event) {
            final CharSequence sequence = getMessageFromEvent(event);
            if (sequence != null) {
                sLoggingService.submit(new LoggingRunnable(sequence.toString()));
            } else {
                // TODO - throw an exception
            }
        }
    }

    private final class LoggingRunnable implements Runnable {

        public final String mLogString;

        private LoggingRunnable(final String logString) {
            mLogString = logString;
        }

        @Override
        public void run() {

        }
    }
}