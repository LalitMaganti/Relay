package com.fusionx.relay.parser.command;

import com.google.common.collect.Iterables;

import com.fusionx.relay.Channel;
import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfigurationTest;
import com.fusionx.relay.ServerTest;
import com.fusionx.relay.TestMisc;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.event.Event;
import com.fusionx.relay.event.channel.WorldJoinEvent;
import com.fusionx.relay.event.server.JoinEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.IRCUtils;
import com.squareup.otto.Subscribe;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static com.fusionx.relay.ServerConfigurationTest.getFreenodeConfiguration;
import static org.fest.assertions.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class JoinParserTest {

    private Server mServer;

    private JoinParser mJoinParser;

    // Setup work for the tests
    public JoinParserTest() {
        InterfaceHolders.onInterfaceReceived(new TestMisc.DefaultEventPreferences());

        mServer = ServerTest.getDefaultServer();
        mJoinParser = new JoinParser(mServer);
    }

    // This method tests that when another user joins, everything is set up correctly
    @Test
    public void testOnUserJoin() {
        final String joinLine = ":otheruser!otheruser@test JOIN #holoirc";
        final List<String> list = IRCUtils.splitRawLine(joinLine, false);
        mJoinParser.onParseCommand(list, "otheruser!otheruser@test");

        final Channel channel = mServer.getUserChannelInterface().getChannel("#holoirc");
        final WorldUser user = mServer.getUserChannelInterface().getUserIfExists("otheruser");

        // Check that the channel exists and has the correct message in buffer
        assertThat(channel).isNotNull();
        assertThat(channel.getBuffer()).hasSize(1);
        assertThat(Iterables.getLast(channel.getBuffer())).isNotNull().isInstanceOf
                (WorldJoinEvent.class);
        assertThat(channel.getUsers()).contains(user);

        // Check that the user exists and has been added to the channel and that the
        // correct user has been sent in the event
        assertThat(user).isNotNull();
        assertThat(user.getChannels()).contains(channel);
    }

    // This method tests that when our user joins, everything is set up correctly
    @Test
    public void testOnJoin() {
        final String nick = getFreenodeConfiguration().getNickStorage().getFirstChoiceNick();

        // TODO - check the events

        final String joinLine = ":holoirctester!holoirctester@test JOIN #holoirc";
        final List<String> list = IRCUtils.splitRawLine(joinLine, false);
        mJoinParser.onParseCommand(list, "holoirctester!holoirctester@test");

        final Channel channel = mServer.getUserChannelInterface().getChannel("#holoirc");
        final WorldUser user = mServer.getUserChannelInterface().getUserIfExists(nick);

        // Check that the channel exists and has the correct message in buffer
        assertThat(channel).isNotNull();
        assertThat(channel.getBuffer()).hasSize(1);
        assertThat(Iterables.getLast(channel.getBuffer())).isNotNull().isInstanceOf
                (WorldJoinEvent.class);
        assertThat(channel.getUsers()).contains(user);

        // Check that the user exists and has been added to the channel and that the
        // correct user has been sent in the event
        assertThat(user).isNotNull();
        assertThat(user.getChannels()).contains(channel);
    }
}