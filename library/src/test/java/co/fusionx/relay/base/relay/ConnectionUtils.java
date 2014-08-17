package co.fusionx.relay.base.relay;

import android.os.Handler;
import android.os.Looper;

import co.fusionx.relay.base.relay.RelayIRCConnection;
import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.base.ServerConfiguration;

public class ConnectionUtils {

    public static RelayIRCConnection getConnection(final ServerConfiguration configuration) {
        final Handler handler = new Handler(Looper.getMainLooper());
        return new RelayIRCConnection(configuration, handler, null);
    }

    public static RelayServer getServerFromConnection(final RelayIRCConnection connection) {
        return connection.getServer();
    }
}