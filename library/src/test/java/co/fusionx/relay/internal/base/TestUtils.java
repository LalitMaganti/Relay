package co.fusionx.relay.internal.base;

import co.fusionx.relay.core.ConnectionConfiguration;
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
}