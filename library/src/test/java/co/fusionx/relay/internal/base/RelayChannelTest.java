package co.fusionx.relay.internal.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RelayChannelTest {

    @Test
    public void testIsChannelPrefix() {
        assertThat(RelayChannel.isChannelPrefix('+')).isTrue();
        assertThat(RelayChannel.isChannelPrefix('#')).isTrue();
        assertThat(RelayChannel.isChannelPrefix('&')).isTrue();
        assertThat(RelayChannel.isChannelPrefix('!')).isTrue();
        // TODO - any other character should be false
    }
}