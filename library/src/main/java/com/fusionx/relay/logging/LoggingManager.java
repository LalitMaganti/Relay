package com.fusionx.relay.logging;

import com.fusionx.relay.Server;
import com.fusionx.relay.event.Event;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gnu.trove.map.hash.THashMap;

/**
 * This class is NOT thread safe
 */
public abstract class LoggingManager {

    private static final SimpleDateFormat sStaticFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final LoggingPreferences mLoggingPreferences;

    private final Map<Server, LogHandler> mLoggingServers;

    private ExecutorService sLoggingService;

    private boolean mStarted;

    protected LoggingManager(final LoggingPreferences preferences) {
        mLoggingPreferences = preferences;
        mLoggingServers = new THashMap<>();
        mStarted = false;
    }

    public void addServerToManager(final Server server) {
        if (mLoggingServers.containsKey(server)) {
            throw new IllegalArgumentException("This server is already present in this manager");
        }
        final LogHandler logHandler = new LogHandler(server);
        mLoggingServers.put(server, logHandler);

        if (mStarted) {
            logHandler.startLogging();
        }
    }

    public void removeServerFromManager(final Server server) {
        final LogHandler handler = mLoggingServers.get(server);
        if (handler == null) {
            throw new IllegalArgumentException("This server is not present in this manager");
        }
        if (mStarted) {
            handler.stopLogging();
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

        for (final Map.Entry<Server, LogHandler> entry : mLoggingServers.entrySet()) {
            entry.getValue().stopLogging();
        }

        // Kill the logging service
        sLoggingService.shutdownNow();
        sLoggingService = null;
    }

    public abstract CharSequence getMessageFromEvent(final Server server, final Event event);

    public boolean isStarted() {
        return mStarted;
    }

    private String getServerPath(final Server server) {
        return String.format("%s/%s", mLoggingPreferences.getLoggingPath(), server.getTitle());
    }

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
            final CharSequence sequence = getMessageFromEvent(mServer, event);
            // If logging path is null then that's an issue
            if (sequence != null && mLoggingPreferences.getLoggingPath() != null) {
                sLoggingService.submit(new LoggingRunnable(mServer, event, sequence.toString()));
            } else {
                // TODO - throw an exception
            }
        }
    }

    private final class LoggingRunnable implements Runnable {

        public final String mLogString;

        private final Server mServer;

        private final Event mEvent;

        private LoggingRunnable(final Server server, final Event event, final String logString) {
            mServer = server;
            mEvent = event;
            mLogString = logString;
        }

        @Override
        public void run() {
            final String path = getServerPath(mServer);
            final String line = mLoggingPreferences.shouldLogTimestamps()
                    ? String.format("%s: %s", mEvent.timestamp.toString(), mLogString)
                    : mLogString;
            final File file = new File(path, String.format("%s.txt",
                    sStaticFormat.format(new Date())));
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new IllegalArgumentException();
            }

            try {
                final FileWriter writer = new FileWriter(file, true);
                writer.append(line).append("\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}