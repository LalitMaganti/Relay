package com.fusionx.relay;

import com.fusionx.relay.connection.ConnectionUtils;
import com.fusionx.relay.connection.ServerConnection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.fusionx.relay.ServerConfigurationTest.getFreenodeConfiguration;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ServerTest {

    public static Server getDefaultServer() {
        final ServerConfiguration freenode = getFreenodeConfiguration();
        final ServerConnection connection = ConnectionUtils.getConnection(freenode);
        final Server server = ConnectionUtils.getServerFromConnection(connection);
        final String nick = freenode.getNickStorage().getFirstChoiceNick();
        final AppUser user = new AppUser(nick, server.getUserChannelInterface());
        server.setUser(user);
        return server;
    }

    @Test
    public void toDo() {

    }
}