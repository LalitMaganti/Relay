package com.fusionx.relay;

import com.fusionx.relay.event.server.GenericServerEvent;
import com.fusionx.relay.event.server.ServerEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.fusionx.relay.ServerConfigurationTest.getFreenodeConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ServerTest {

    public static Server getDefaultServer() {
        final ServerConfiguration freenode = getFreenodeConfiguration();
        final ServerConnection connection = ConnectionUtils.getConnection(freenode);
        return ConnectionUtils.getServerFromConnection(connection);
    }

    @Test
    public void testOnServerEvent() {
        final Server server = getDefaultServer();
        final ServerEvent event = new GenericServerEvent("This is a test message");
        server.onServerEvent(event);
        assertThat(server.getBuffer())
                .isNotNull()
                .isNotEmpty()
                .containsExactly(event);
    }
}