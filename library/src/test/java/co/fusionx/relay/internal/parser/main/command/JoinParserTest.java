package co.fusionx.relay.internal.parser.main.command;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import co.fusionx.relay.internal.core.InternalServer;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class JoinParserTest {

    private InternalServer mServer;

    private JoinParser mJoinParser;

    // This method tests that when our user joins, everything is set up correctly
    @Test
    public void testOnJoin() {

    }

    // This method tests that when another user joins, everything is set up correctly
    @Test
    public void testOnUserJoin() {

    }

    // TODO - simulate disconnection/reconnection and test what happens then
}