package com.fusionx.relay.parser.command;

import com.google.common.collect.Iterables;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelTest;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.ServerTest;
import com.fusionx.relay.TestMisc;
import com.fusionx.relay.event.channel.ChannelWorldJoinEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.IRCUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static com.fusionx.relay.ServerConfigurationTest.getFreenodeConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

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
        // Make sure the channel is set up first
        mServer.getUserChannelInterface().coupleUserAndChannel(mServer.getUser(),
                ChannelTest.getTestChannel());

        final String joinLine = ":otheruser!otheruser@test JOIN #relay";
        final List<String> list = IRCUtils.splitRawLine(joinLine, false);
        mJoinParser.onParseCommand(list, "otheruser!otheruser@test");

        final Channel channel = mServer.getUserChannelInterface().getChannel("#relay");
        final ChannelUser user = mServer.getUserChannelInterface().getUser("otheruser");

        // Check that the channel exists and has the correct message in buffer
        assertThat(channel).isNotNull();
        assertThat(channel.getBuffer())
                .hasSize(1);
        assertThat(Iterables.getLast(channel.getBuffer()))
                .isNotNull()
                .isInstanceOf(ChannelWorldJoinEvent.class);
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

        final String joinLine = ":holoirctester!holoirctester@test JOIN #relay";
        final List<String> list = IRCUtils.splitRawLine(joinLine, false);
        mJoinParser.onParseCommand(list, "holoirctester!holoirctester@test");

        final Channel channel = mServer.getUserChannelInterface().getChannel("#relay");
        final ChannelUser user = mServer.getUserChannelInterface().getUser(nick);

        // Check that the channel exists and has the correct message in buffer
        assertThat(channel).isNotNull();
        assertThat(channel.getBuffer())
                .hasSize(1);
        assertThat(Iterables.getLast(channel.getBuffer()))
                .isNotNull()
                .isInstanceOf(ChannelWorldJoinEvent.class);
        assertThat(channel.getUsers()).contains(user);

        // Check that the user exists and has been added to the channel and that the
        // correct user has been sent in the event
        assertThat(user).isNotNull();
        assertThat(user.getChannels()).contains(channel);
    }
}