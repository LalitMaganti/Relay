package co.fusionx.relay.internal.parser.main.command;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayServerTest;
import co.fusionx.relay.internal.base.TestMisc;
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