package com.fusionx.relay.call;

public class VersionCall extends Call {

    public final String askingUser;

    public final String version;

    public VersionCall(String askingUser, String version) {
        this.askingUser = askingUser;
        this.version = version;
    }
}