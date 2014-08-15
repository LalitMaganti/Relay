package co.fusionx.relay.misc;

import co.fusionx.relay.bus.ServerCallHandler;

public class CoreListener {

    public static void respondToPing(final ServerCallHandler serverWriter,
            final String serverName) {
        serverWriter.pongServer(serverName);
    }
}