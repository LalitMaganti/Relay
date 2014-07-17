package com.fusionx.relay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.fusionx.relay.ServerTest.getDefaultServer;
import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ChannelUserTest {

    public static ChannelUser getTestChannelUser() {
        return getTestChannelUser("#relay");
    }

    public static ChannelUser getTestChannelUser(final String channelName) {
        return new ChannelUser(channelName);
    }

    @Test
    public void test() {
    }
}