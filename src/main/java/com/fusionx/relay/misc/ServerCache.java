package com.fusionx.relay.misc;

public class ServerCache {

    private boolean mCached;

    private String mIrcTitle;

    public String getIrcTitle() {
        return mIrcTitle;
    }

    public void setIrcTitle(String ircTitle) {
        mIrcTitle = ircTitle;
    }

    public boolean isCached() {
        return mCached;
    }

    public void setCached(final boolean cached) {
        mCached = cached;
    }
}
