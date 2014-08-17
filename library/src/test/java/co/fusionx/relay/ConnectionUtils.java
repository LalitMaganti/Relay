package co.fusionx.relay;

import android.os.Handler;
import android.os.Looper;

public class ConnectionUtils {

    public static IRCConnection getConnection(final ServerConfiguration configuration) {
        final Handler handler = new Handler(Looper.getMainLooper());
        return new IRCConnection(configuration, handler, null);
    }

    public static RelayServer getServerFromConnection(final IRCConnection connection) {
        return connection.getServer();
    }
}