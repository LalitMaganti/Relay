package com.fusionx.relay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ServerTest {

    public static Server getDefaultServer() {
        final Server server;
        final ServerConfiguration freenode = ServerConfigurationTest
                .getFreenodeConfiguration();
        server = new Server(freenode, null, null);
        final String nick = freenode.getNickStorage().getFirstChoiceNick();
        final AppUser user = new AppUser(nick, server.getUserChannelInterface());
        server.setUser(user);
        return server;
    }

    @Test
    public void stub() {

    }
}