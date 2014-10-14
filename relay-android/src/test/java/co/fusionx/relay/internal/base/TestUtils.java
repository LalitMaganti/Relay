package co.fusionx.relay.internal.base;

import co.fusionx.relay.configuration.DefaultConnectionConfiguration;
import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.provider.DefaultNickProvider;

public class TestUtils {

    public static ConnectionConfiguration getFreenodeConfiguration() {
        return getFreenodeBuilder().build();
    }

    public static ConnectionConfiguration.Builder getFreenodeBuilder() {
        final ConnectionConfiguration.Builder builder = new DefaultConnectionConfiguration
                .Builder();
        builder.setTitle("Freenode");
        builder.setUrl("irc.freenode.net");
        builder.setPort(6667);
        builder.setNickProvider(
                new DefaultNickProvider("holoirctester", "holoirctester", "holoirctester"));
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