package com.fusionx.relay.logging;

public class LoggingPreferences {

    private String mLoggingPath;

    private boolean mTimeStamp;

    public boolean isTimeStamp() {
        return mTimeStamp;
    }

    public LoggingPreferences setTimeStamp(boolean timeStamp) {
        mTimeStamp = timeStamp;
        return this;
    }

    private String getLoggingPath() {
        return mLoggingPath;
    }

    private LoggingPreferences setLoggingPath(final String path) {
        mLoggingPath = path;
        return this;
    }
}
