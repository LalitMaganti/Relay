package co.fusionx.relay.configuration;

import com.fusionx.relay.configuration.ParcelableConnectionConfiguration;
import com.fusionx.relay.core.ParcelableNickProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

import co.fusionx.relay.internal.base.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class DefaultConnectionConfigurationTest {

    public static ParcelableConnectionConfiguration getFreenodeConfiguration() {
        return getFreenodeBuilder().build();
    }

    public static ParcelableConnectionConfiguration.Builder getFreenodeBuilder() {
        final ParcelableConnectionConfiguration.Builder builder
                = new ParcelableConnectionConfiguration
                .Builder();
        builder.setTitle("Freenode");
        builder.setUrl("irc.freenode.net");
        builder.setPort(6667);
        builder.setNickStorage(
                new ParcelableNickProvider("holoirctester", "holoirctester", "holoirctester"));
        builder.setServerUserName("holoirctester");
        return builder;
    }

    public static ConnectionConfiguration.Builder getFreenodeBuilderSasl() {
        final ParcelableConnectionConfiguration.Builder builder = getFreenodeBuilder();
        builder.setSaslUsername("relay");
        builder.setSaslPassword("relay");
        return builder;
    }

    // Builder tests
    @Test
    public void testBuilderParcelling() {
        final Parcel parcel = Parcel.obtain();
        final ParcelableConnectionConfiguration.Builder expected = getFreenodeBuilder();
        expected.writeToParcel(parcel, 0);

        // done writing, now reset parcel for reading
        parcel.setDataPosition(0);

        final ParcelableConnectionConfiguration.Builder actual =
                ParcelableConnectionConfiguration.Builder.CREATOR.createFromParcel(parcel);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    // Configuration tests
    @Test
    public void testBuild() {
        final ConnectionConfiguration.Builder builder = TestUtils.getFreenodeBuilder();
        final ConnectionConfiguration configuration = TestUtils.getFreenodeConfiguration();
        assertEquals(builder.getTitle(), configuration.getTitle());
    }

    @Test
    public void testParcelling() {
        final Parcel parcel = Parcel.obtain();
        final ParcelableConnectionConfiguration expected = getFreenodeConfiguration();
        expected.writeToParcel(parcel, 0);

        // done writing, now reset parcel for reading
        parcel.setDataPosition(0);

        final ParcelableConnectionConfiguration actual
                = ParcelableConnectionConfiguration.CREATOR.createFromParcel(parcel);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }
}