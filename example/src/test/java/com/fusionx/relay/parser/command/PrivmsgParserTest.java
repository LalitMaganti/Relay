package com.fusionx.relay.parser.command;

import com.fusionx.relay.Server;
import com.fusionx.relay.ServerTest;
import com.fusionx.relay.TestMisc;
import com.fusionx.relay.misc.InterfaceHolders;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class PrivmsgParserTest {

    private Server mServer;

    private PrivmsgParser mPrivmsgParser;

    // Setup work for the tests
    public PrivmsgParserTest() {
        InterfaceHolders.onInterfaceReceived(new TestMisc.DefaultEventPreferences());

        mServer = ServerTest.getDefaultServer();
        mPrivmsgParser = new PrivmsgParser(mServer, null);
    }

    @Test
    public void onParsePrivateMessageTest() {

    }

    @Test
    public void onParsePrivateChannelTest() {

    }
}