package com.fusionx.relay.parser.command;

import com.google.common.collect.Iterables;

import com.fusionx.relay.RelayChannelTest;
import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayChannelUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.RelayServerTest;
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

    private RelayServer mServer;

    private JoinParser mJoinParser;

    // This method tests that when another user joins, everything is set up correctly
    @Test
    public void testOnUserJoin() {
        resetFields();

        // Make sure the channel is set up first
        mServer.getUserChannelInterface().coupleUserAndChannel(mServer.getUser(),
                RelayChannelTest.getTestChannel());

        final String joinLine = ":otheruser!otheruser@test JOIN #relay";
        final List<String> list = IRCUtils.splitRawLine(joinLine, false);
        mJoinParser.onParseCommand(list, "otheruser!otheruser@test");

        final RelayChannel channel = mServer.getUserChannelInterface().getChannel("#relay");
        final RelayChannelUser user = mServer.getUserChannelInterface().getUser("otheruser");

        // Check that the channel exists
        assertThat(channel)
                .isNotNull();

        // Check that the user exists
        assertThat(user)
                .isNotNull();

        // Check that the user is the the global list
        assertThat(mServer.getUsers())
                .isNotNull()
                .containsOnly(mServer.getUser(), user);

        // Check that the channel has been added to the user
        assertThat(user.getChannels())
                .isNotNull()
                .containsOnly(channel);

        // Check that the user has been added to the channel
        assertThat(channel.getUsers())
                .isNotNull()
                .containsOnly(mServer.getUser(), user);

        // Check that the channel has the correct message in buffer
        assertThat(channel.getBuffer())
                .isNotNull()
                .hasSize(1);
        assertThat(Iterables.getLast(channel.getBuffer()))
                .isNotNull()
                .isInstanceOf(ChannelWorldJoinEvent.class);

        // TODO - check events
    }

    // This method tests that when our user joins, everything is set up correctly
    @Test
    public void testOnJoin() {
        resetFields();

        final String nick = getFreenodeConfiguration().getNickStorage().getFirstChoiceNick();

        final String joinLine = ":holoirctester!holoirctester@test JOIN #relay";
        final List<String> list = IRCUtils.splitRawLine(joinLine, false);
        mJoinParser.onParseCommand(list, "holoirctester!holoirctester@test");

        final RelayChannel channel = mServer.getUserChannelInterface().getChannel("#relay");
        final RelayChannelUser user = mServer.getUserChannelInterface().getUser(nick);

        // Check that the channel exists
        assertThat(channel)
                .isNotNull();

        // Check that the user exists
        assertThat(user)
                .isNotNull();

        // Check that the user is the the global list
        assertThat(mServer.getUsers())
                .isNotNull()
                .containsOnly(user);

        // Check that the channel has been added to the user
        assertThat(user.getChannels())
                .isNotNull()
                .containsOnly(channel);

        // Check that the user has been added to the channel
        assertThat(channel.getUsers())
                .isNotNull()
                .containsOnly(user);

        // Check that the channel has the correct message in buffer
        assertThat(channel.getBuffer())
                .isNotNull()
                .hasSize(1);
        assertThat(Iterables.getLast(channel.getBuffer()))
                .isNotNull()
                .isInstanceOf(ChannelWorldJoinEvent.class);

        // TODO - check events
    }

    // TODO - simulate disconnection/reconnection and test what happens then

    private void resetFields() {
        InterfaceHolders.onInterfaceReceived(new TestMisc.DefaultEventPreferences());

        mServer = RelayServerTest.getDefaultServer();
        mJoinParser = new JoinParser(mServer);
    }
}