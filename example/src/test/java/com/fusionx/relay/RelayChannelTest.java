package com.fusionx.relay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.fusionx.relay.RelayServerTest.getDefaultServer;
import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RelayChannelTest {

    public static RelayChannel getTestChannel() {
        return getTestChannel("#relay");
    }

    public static RelayChannel getTestChannel(final String channelName) {
        return new RelayChannel(getDefaultServer(), channelName);
    }

    public static void populateTestChannel(final Channel channel) {

    }

    @Test
    public void testIsChannelPrefix() {
        assertThat(RelayChannel.isChannelPrefix('+'))
                .isTrue();
        assertThat(RelayChannel.isChannelPrefix('#'))
                .isTrue();
        assertThat(RelayChannel.isChannelPrefix('&'))
                .isTrue();
        assertThat(RelayChannel.isChannelPrefix('!'))
                .isTrue();
        // TODO - any other character should be false
    }

    @Test
    public void testWipeChannelData() {

    }

    @Test
    public void testOnChannelEvent() {

    }
}