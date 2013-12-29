package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfigurationTest;
import com.fusionx.relay.ServerTest;
import com.fusionx.relay.TestMisc;
import com.fusionx.relay.event.ChannelEvent;
import com.fusionx.relay.event.JoinEvent;
import com.fusionx.relay.interfaces.EventResponses;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.IRCUtils;
import com.squareup.otto.Subscribe;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class JoinParserTest {

    private Server mServer;

    private JoinParser mJoinParser;

    // Setup work for the tests
    public JoinParserTest() {
        final EventResponses eventResponses = new TestMisc.DefaultEventResponses() {
            @Override
            public String getJoinMessage(final String nick) {
                return nick;
            }
        };
        InterfaceHolders.onInterfaceReceived(new TestMisc.DefaultEventPreferences(),
                eventResponses);

        mServer = ServerTest.getDefaultServer();
        mJoinParser = new JoinParser(mServer);
    }

    // This method tests that when another user joins, everything is set up correctly
    @Test
    public void testOnUserJoin() {
        mServer.getServerEventBus().register(new Object() {
            @Subscribe
            public void onJoinEvent(final ChannelEvent event) {
                // Check that the channel we get out is the one intended to be joined
                assertEquals("#holoirc", event.channelName);

                final Channel channel = mServer.getUserChannelInterface().getChannelIfExists
                        (event.channelName);
                final ChannelUser user = mServer.getUserChannelInterface().getUserIfExists(event
                        .user.getNick());

                // Check that the channel exists and has the correct message in buffer
                assertNotNull(channel);
                assertEquals(channel.getBuffer().size(), 1);
                assertEquals(event.user.getNick(), channel.getBuffer().get(0).message.toString());
                assertTrue(channel.getUsers().contains(user));

                // Check that the user exists and has been added to the channel and that the
                // correct user has been sent in the event
                assertNotNull(user);
                assertEquals(event.user, user);
                assertTrue(user.getChannels().contains(channel));

                mServer.getServerEventBus().unregister(this);
            }
        });
        final String joinLine = ":otheruser!otheruser@test JOIN #holoirc";
        final List<String> list = IRCUtils.splitRawLine(joinLine, false);
        mJoinParser.onParseCommand(list, "otheruser!otheruser@test");
    }

    // This method tests that when our user joins, everything is set up correctly
    @Test
    public void testOnJoin() {
        final String nick = ServerConfigurationTest.getFreenodeConfiguration()
                .getNickStorage().getFirstChoiceNick();
        mServer.getServerEventBus().register(new Object() {
            @Subscribe
            public void onJoin(final JoinEvent event) {
                // Check that the channel we get out is the one we intended to join
                assertEquals("#holoirc", event.channelToJoin);

                final Channel channel = mServer.getUserChannelInterface().getChannelIfExists(event
                        .channelToJoin);
                final ChannelUser user = mServer.getUserChannelInterface().getUserIfExists(nick);

                // Check that the channel exists and has the correct message in buffer
                assertNotNull(channel);
                assertEquals(channel.getBuffer().size(), 1);
                assertEquals(nick, channel.getBuffer().get(0).message.toString());
                assertTrue(channel.getUsers().contains(user));

                // Check that the user exists and has been added to the channel
                assertNotNull(user);
                assertTrue(user.getChannels().contains(channel));

                mServer.getServerEventBus().unregister(this);
            }
        });
        final String joinLine = ":holoirctester!holoirctester@test JOIN #holoirc";
        final List<String> list = IRCUtils.splitRawLine(joinLine, false);
        mJoinParser.onParseCommand(list, "holoirctester!holoirctester@test");
    }
}