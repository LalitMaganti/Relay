package co.fusionx.relay.internal.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.event.server.ServerEvent;

import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RelayServerTest {

    @Test
    public void testOnServerEvent() {
    }
}