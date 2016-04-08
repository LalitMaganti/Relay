package co.fusionx.relay.internal.base;

import android.util.Log;

import co.fusionx.relay.base.Server;
import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.interfaces.RelayConfiguration;
import co.fusionx.relay.internal.sender.RelayBaseSender;
import co.fusionx.relay.internal.sender.RelayServerSender;
import co.fusionx.relay.misc.NickStorage;

public class TestUtils {

    public static ServerConfiguration getFreenodeConfiguration() {
        return getFreenodeBuilder().build();
    }

    public static ServerConfiguration.Builder getFreenodeBuilder() {
        final ServerConfiguration.Builder builder = new ServerConfiguration.Builder();
        builder.setTitle("Freenode");
        builder.setUrl("irc.freenode.net");
        builder.setPort(6667);
        builder.setNickStorage(new NickStorage("holoirctester", "holoirctester", "holoirctester"));
        builder.setServerUserName("holoirctester");
        return builder;
    }

    public static ServerConfiguration.Builder getFreenodeBuilderSasl() {
        final ServerConfiguration.Builder builder = getFreenodeBuilder();
        builder.setSaslUsername("relay");
        builder.setSaslPassword("relay");
        return builder;
    }

    public static RelayIRCConnection getConnection(final ServerConfiguration configuration) {
        return new RelayIRCConnection(configuration, getFreenodeServer(), new RelayBaseSender());
    }

    public static RelayServer getServerFromConnection(final RelayIRCConnection connection) {
        return connection.getServer();
    }

    public static RelayServer getFreenodeServer() {
        RelayBaseSender baseSender = new RelayBaseSender();
        return new RelayServer(getFreenodeConfiguration(), baseSender, new RelayServerSender(baseSender));
    }

    public static RelayServer getServerFromConfiguration(final ServerConfiguration configuration) {
        final RelayIRCConnection connection = getConnection(configuration);
        return getServerFromConnection(connection);
    }

    public static class DefaultRelayConfiguration implements RelayConfiguration {

        @Override
        public int getReconnectAttemptsCount() {
            return 0;
        }

        @Override
        public String getPartReason() {
            return null;
        }

        @Override
        public String getQuitReason() {
            return null;
        }

        @Override
        public boolean isSelfEventHidden() {
            return true;
        }

        @Override
        public boolean isMOTDShown() {
            return false;
        }

        @Override
        public void logMissingData(final Server server) {
            Log.e("Relay", server.getTitle());
        }

        @Override
        public void logServerLine(final String line) {
            Log.e("Relay", line);
        }

        @Override
        public void handleException(final Exception ex) {
            ex.printStackTrace();
        }
    }
}