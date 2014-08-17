package co.fusionx.relay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.misc.NickStorage;

import static co.fusionx.relay.base.ServerConfiguration.Builder.CREATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ServerConfigurationTest {

    // Configuration statics
    public static ServerConfiguration getFreenodeConfiguration() {
        return getFreenodeConfigurationBuilder().build();
    }

    // Builder statics
    private static ServerConfiguration.Builder getFreenodeConfigurationBuilder() {
        final ServerConfiguration.Builder builder = new ServerConfiguration.Builder();
        builder.setTitle("Freenode");
        builder.setUrl("irc.freenode.net");
        builder.setPort(6667);
        builder.setNickStorage(new NickStorage("holoirctester", "holoirctester", "holoirctester"));
        builder.setServerUserName("holoirctester");
        return builder;
    }

    // Builder tests
    @Test
    public void testBuilderParcelling() {
        final Parcel parcel = Parcel.obtain();
        final ServerConfiguration.Builder expected = getFreenodeConfigurationBuilder();
        expected.writeToParcel(parcel, 0);

        // done writing, now reset parcel for reading
        parcel.setDataPosition(0);

        final ServerConfiguration.Builder actual = CREATOR.createFromParcel(parcel);

        assertThat(actual)
                .isNotNull()
                .isEqualToComparingFieldByField(expected);
    }

    // Configuration tests
    @Test
    public void testBuild() {
        final ServerConfiguration.Builder builder = getFreenodeConfigurationBuilder();
        final ServerConfiguration configuration = getFreenodeConfiguration();
        assertEquals(builder.getTitle(), configuration.getTitle());
    }

    @Test
    public void testParcelling() {
        final Parcel parcel = Parcel.obtain();
        final ServerConfiguration expected = getFreenodeConfiguration();
        expected.writeToParcel(parcel, 0);

        // done writing, now reset parcel for reading
        parcel.setDataPosition(0);

        final ServerConfiguration actual = ServerConfiguration.CREATOR.createFromParcel(parcel);

        assertThat(actual)
                .isNotNull()
                .isEqualToComparingFieldByField(expected);
    }
}