package com.fusionx.relay.misc;

import com.fusionx.relay.bus.ServerCallHandler;

public class CoreListener {

    public static void respondToPing(final ServerCallHandler serverWriter,
            final String serverName) {
        serverWriter.pongServer(serverName);
    }
}