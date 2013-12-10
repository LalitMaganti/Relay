package com.fusionx.relay.misc;

import com.fusionx.relay.writers.ServerWriter;

public class CoreListener {

    public static void respondToPing(final ServerWriter serverWriter, final String serverName) {
        serverWriter.pongServer(serverName);
    }
}