package com.fusionx.relay.parser.command;

import com.fusionx.relay.Server;
import com.fusionx.relay.ServerTest;
import com.fusionx.relay.TestMisc;
import com.fusionx.relay.interfaces.EventResponses;
import com.fusionx.relay.misc.InterfaceHolders;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PrivmsgParserTest {

    private Server mServer;

    private PrivmsgParser mPrivmsgParser;

    // Setup work for the tests
    public PrivmsgParserTest() {
        final EventResponses eventResponses = new TestMisc.DefaultEventResponses() {
            @Override
            public String getMessage(String sendingNick, String rawMessage) {
                return sendingNick + " " + rawMessage;
            }
        };
        InterfaceHolders.onInterfaceReceived(new TestMisc.DefaultEventPreferences(),
                eventResponses);

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