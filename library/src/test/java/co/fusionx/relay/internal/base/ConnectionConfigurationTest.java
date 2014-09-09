package co.fusionx.relay.internal.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

import co.fusionx.relay.core.ConnectionConfiguration;

import static co.fusionx.relay.core.ConnectionConfiguration.Builder.CREATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ConnectionConfigurationTest {

    // Builder tests
    @Test
    public void testBuilderParcelling() {
        final Parcel parcel = Parcel.obtain();
        final ConnectionConfiguration.Builder expected = TestUtils.getFreenodeBuilder();
        expected.writeToParcel(parcel, 0);

        // done writing, now reset parcel for reading
        parcel.setDataPosition(0);

        final ConnectionConfiguration.Builder actual = CREATOR.createFromParcel(parcel);

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
        final ConnectionConfiguration expected = TestUtils.getFreenodeConfiguration();
        expected.writeToParcel(parcel, 0);

        // done writing, now reset parcel for reading
        parcel.setDataPosition(0);

        final ConnectionConfiguration actual = ConnectionConfiguration.CREATOR.createFromParcel(parcel);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }
}