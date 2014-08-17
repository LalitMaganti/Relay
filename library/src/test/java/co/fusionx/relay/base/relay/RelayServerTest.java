package co.fusionx.relay.base.relay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.event.server.ServerEvent;

import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RelayServerTest {

    public static RelayServer getDefaultServer() {
        final ServerConfiguration freenode = ServerConfigurationTest.getFreenodeConfiguration();
        final RelayIRCConnection connection = ConnectionUtils.getConnection(freenode);
        return ConnectionUtils.getServerFromConnection(connection);
    }

    @Test
    public void testOnServerEvent() {
        final RelayServer server = getDefaultServer();
        final ServerEvent event = new GenericServerEvent(server, "This is a test message");
        server.onServerEvent(event);
        assertThat(server.getBuffer())
                .isNotNull()
                .isNotEmpty()
                .containsExactly(event);
    }
}