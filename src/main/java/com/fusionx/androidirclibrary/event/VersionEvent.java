package com.fusionx.androidirclibrary.event;

public class VersionEvent extends Event {

    public final String askingUser;

    public final String version;

    public VersionEvent(final String askingUser, final String version) {
        this.askingUser = askingUser;
        this.version = version;
    }
}