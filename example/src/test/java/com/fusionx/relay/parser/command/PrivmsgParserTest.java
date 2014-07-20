package com.fusionx.relay.parser.command;

import com.fusionx.relay.RelayServer;
import com.fusionx.relay.RelayServerTest;
import com.fusionx.relay.TestMisc;
import com.fusionx.relay.misc.InterfaceHolders;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class PrivmsgParserTest {

    private RelayServer mServer;

    private PrivmsgParser mPrivmsgParser;

    // Setup work for the tests
    public PrivmsgParserTest() {
        InterfaceHolders.onInterfaceReceived(new TestMisc.DefaultEventPreferences());

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