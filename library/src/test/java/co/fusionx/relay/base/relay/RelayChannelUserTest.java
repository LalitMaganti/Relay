package co.fusionx.relay.base.relay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.relay.RelayChannelUser;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RelayChannelUserTest {

    public static ChannelUser getTestChannelUser() {
        return getTestChannelUser("#relay");
    }

    public static ChannelUser getTestChannelUser(final String channelName) {
        return new RelayChannelUser(channelName);
    }

    @Test
    public void test() {
    }
}