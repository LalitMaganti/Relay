package co.fusionx.relay.parser.command;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.RelayServerTest;
import co.fusionx.relay.TestMisc;
import co.fusionx.relay.misc.RelayConfigurationProvider;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class PrivmsgParserTest {

    private RelayServer mServer;

    private PrivmsgParser mPrivmsgParser;

    // Setup work for the tests
    public PrivmsgParserTest() {
        RelayConfigurationProvider.onInterfaceReceived(new TestMisc.DefaultRelayConfiguration());

        mServer = RelayServerTest.getDefaultServer();
        mPrivmsgParser = new PrivmsgParser(mServer, null);
    }

    @Test
    public void onParsePrivateMessageTest() {

    }

    @Test
    public void onParsePrivateChannelTest() {

    }
}