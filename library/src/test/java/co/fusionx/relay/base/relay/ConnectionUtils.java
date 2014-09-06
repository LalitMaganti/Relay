package co.fusionx.relay.base.relay;

import co.fusionx.relay.base.ServerConfiguration;

public class ConnectionUtils {

    public static RelayIRCConnection getConnection(final ServerConfiguration configuration) {
        return new RelayIRCConnection(configuration);
    }

    public static RelayServer getServerFromConnection(final RelayIRCConnection connection) {
        return connection.getServer();
    }
}