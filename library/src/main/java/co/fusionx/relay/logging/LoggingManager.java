package co.fusionx.relay.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.fusionx.relay.core.Session;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.event.server.ServerEvent;

public abstract class LoggingManager {

    private static final SimpleDateFormat sStaticFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final LoggingSettingsProvider mLoggingSettingsProvider;

    private final Map<Session, LogHandler> mLoggingConnections;

    private ExecutorService mLoggingService;

    private boolean mStarted;

    protected LoggingManager(final LoggingSettingsProvider preferences) {
        mLoggingSettingsProvider = preferences;
        mLoggingConnections = new HashMap<>();
        mStarted = false;
    }

    public void addConnectionToManager(final Session session) {
        if (mLoggingConnections.containsKey(session)) {
            throw new IllegalArgumentException("This session is already present in this manager");
        }
        final LogHandler logHandler = new LogHandler(session);
        mLoggingConnections.put(session, logHandler);

        if (mStarted) {
            logHandler.startLogging();
        }
    }

    public void removeConnectionFromManager(final Session session) {
        final LogHandler handler = mLoggingConnections.get(session);
        if (handler == null) {
            throw new IllegalArgumentException("This session is not present in this manager");
        }
        if (mStarted) {
            handler.stopLogging();
        }
        mLoggingConnections.remove(session);
    }

    public void startLogging() {
        if (mStarted) {
            throw new IllegalArgumentException("Already started.");
        }
        mStarted = true;

        // Start the logging service
        mLoggingService = Executors.newSingleThreadExecutor();

        for (final LogHandler handler : mLoggingConnections.values()) {
            handler.startLogging();
        }
    }

    public void stopLogging() {
        if (!mStarted) {
            throw new IllegalArgumentException("Already stopped.");
        }
        mStarted = false;

        for (final LogHandler handler : mLoggingConnections.values()) {
            handler.stopLogging();
        }

        // Kill the logging service
        mLoggingService.shutdownNow();
        mLoggingService = null;
    }

    public abstract CharSequence getMessageFromEvent(final Session connection,
            final Event event);

    public boolean isStarted() {
        return mStarted;
    }

    private String getServerPath(final Session connection) {
        return String.format("%s/%s", mLoggingSettingsProvider.getLoggingPath(),
                connection.getServer().getTitle());
    }

    protected abstract boolean shouldLogEvent(final Event event);

    private final class LogHandler {

        private static final int LOG_PRIORITY = 500;

        private final Session mSession;

        public LogHandler(final Session session) {
            mSession = session;
        }

        public void startLogging() {
            mSession.registerForEvents(this, LOG_PRIORITY);
        }

        public void stopLogging() {
            mSession.unregisterFromEvents(this);
        }

        public void onEvent(final ServerEvent event) {
            if (shouldLogEvent(event)) {
                final CharSequence sequence = getMessageFromEvent(mSession, event);
                // If logging path is null then that's an issue
                if (sequence != null && mLoggingSettingsProvider.getLoggingPath() != null) {
                    mLoggingService.submit(new LoggingRunnable(mSession, event,
                            sequence.toString(), ""));
                } else {
                    // TODO - throw an exception
                }
            }
        }

        public void onEvent(final ChannelEvent event) {
            if (shouldLogEvent(event)) {
                final CharSequence sequence = getMessageFromEvent(mSession, event);
                // If logging path is null then that's an issue
                if (sequence != null && mLoggingSettingsProvider.getLoggingPath() != null) {
                    mLoggingService.submit(new LoggingRunnable(mSession, event,
                            sequence.toString(), event.conversation.getName()));
                } else {
                    // TODO - throw an exception
                }
            }
        }

        public void onEvent(final QueryEvent event) {
            if (shouldLogEvent(event)) {
                final CharSequence sequence = getMessageFromEvent(mSession, event);
                // If logging path is null then that's an issue
                if (sequence != null && mLoggingSettingsProvider.getLoggingPath() != null) {
                    mLoggingService.submit(new LoggingRunnable(mSession, event,
                            sequence.toString(), event.conversation.getNick().getNickAsString()));
                } else {
                    // TODO - throw an exception
                }
            }
        }
    }

    private final class LoggingRunnable implements Runnable {

        public final String mLogString;

        private final Session mSession;

        private final Event mEvent;

        private final String mDirectory;

        private LoggingRunnable(final Session session, final Event event,
                final String logString, final String directory) {
            mSession = session;
            mEvent = event;
            mLogString = logString;
            mDirectory = directory;
        }

        @Override
        public void run() {
            final String path = getServerPath(mSession);
            final String line = mLoggingSettingsProvider.shouldLogTimestamps()
                    ? String.format("%s: %s", mEvent.timestamp.format("%H:%M:%S"), mLogString)
                    : mLogString;
            final File file = new File(String.format("%s/%s", path, mDirectory),
                    String.format("%s.txt", sStaticFormat.format(new Date())));
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