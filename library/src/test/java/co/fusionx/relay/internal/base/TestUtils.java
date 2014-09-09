package co.fusionx.relay.internal.base;

import android.util.Log;

import co.fusionx.relay.core.ConnectionConfiguration;
import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.interfaces.RelayConfiguration;
import co.fusionx.relay.misc.NickStorage;

public class TestUtils {

    public static ConnectionConfiguration getFreenodeConfiguration() {
        return getFreenodeBuilder().build();
    }

    public static ConnectionConfiguration.Builder getFreenodeBuilder() {
        final ConnectionConfiguration.Builder builder = new ConnectionConfiguration.Builder();
        builder.setTitle("Freenode");
        builder.setUrl("irc.freenode.net");
        builder.setPort(6667);
        builder.setNickStorage(new NickStorage("holoirctester", "holoirctester", "holoirctester"));
        builder.setServerUserName("holoirctester");
        return builder;
    }

    public static ConnectionConfiguration.Builder getFreenodeBuilderSasl() {
        final ConnectionConfiguration.Builder builder = getFreenodeBuilder();
        builder.setSaslUsername("relay");
        builder.setSaslPassword("relay");
        return builder;
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