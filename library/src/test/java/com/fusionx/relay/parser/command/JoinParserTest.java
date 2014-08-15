package com.fusionx.relay.parser.command;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayChannelTest;
import com.fusionx.relay.RelayChannelUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.RelayServerTest;
import com.fusionx.relay.TestMisc;
import com.fusionx.relay.event.channel.ChannelWorldJoinEvent;
import com.fusionx.relay.event.server.JoinEvent;
import com.fusionx.relay.misc.RelayConfigurationProvider;
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

        final Optional<RelayChannel> optChannel = mServer.getUserChannelInterface()
                .getChannel("#relay");
        final Optional<RelayChannelUser> optUser = mServer.getUserChannelInterface()
                .getUser("otheruser");

        // Check that the channel exists
        assertThat(optChannel.isPresent()).isTrue();

        // Check that the user exists
        assertThat(optUser.isPresent()).isTrue();

        final RelayChannel channel = optChannel.get();
        final RelayChannelUser user = optUser.get();

        // Check that the user is the the global list
        assertThat(mServer.getUsers())
                .contains(user);

        // Check that the channel has been added to the user
        assertThat(user.getChannels())
                .contains(channel);

        // Check that the user has been added to the channel
        assertThat(channel.getUsers())
                .contains(mServer.getUser(), user);

        // Check that the channel has the correct message in buffer
        assertThat(Iterables.getLast(channel.getBuffer()))
                .isInstanceOf(ChannelWorldJoinEvent.class)
                .isEqualToIgnoringGivenFields(new ChannelWorldJoinEvent(channel, user),
                        "timestamp");
    }

    // This method tests that when our user joins, everything is set up correctly
    @Test
    public void testOnJoin() {
        resetFields();

        final String nick = getFreenodeConfiguration().getNickStorage().getFirstChoiceNick();

        final String joinLine = ":holoirctester!holoirctester@test JOIN #relay";
        final List<String> list = IRCUtils.splitRawLine(joinLine, false);
        mJoinParser.onParseCommand(list, "holoirctester!holoirctester@test");

        final Optional<RelayChannel> optChannel = mServer.getUserChannelInterface()
                .getChannel("#relay");
        final Optional<RelayChannelUser> optUser = mServer.getUserChannelInterface().getUser(nick);

        // Check that the channel exists
        assertThat(optChannel.isPresent()).isTrue();

        // Check that the user exists
        assertThat(optUser.isPresent()).isTrue();

        final RelayChannel channel = optChannel.get();
        final RelayChannelUser user = optUser.get();

        // Check that the user is the the global list
        assertThat(mServer.getUsers())
                .contains(user);

        // Check that the channel has been added to the user
        assertThat(user.getChannels())
                .contains(channel);

        // Check that the user has been added to the channel
        assertThat(channel.getUsers())
                .contains(user);

        // Check that the channel has the correct message in buffer
        assertThat(Iterables.getLast(channel.getBuffer()))
                .isInstanceOf(ChannelWorldJoinEvent.class)
                .isEqualToIgnoringGivenFields(new ChannelWorldJoinEvent(channel, user),
                        "timestamp");

        // Check that the server has the correct message in buffer
        assertThat(Iterables.getLast(mServer.getBuffer()))
                .isInstanceOf(JoinEvent.class)
                .isEqualToIgnoringGivenFields(new JoinEvent(channel), "timestamp");
    }

    // TODO - simulate disconnection/reconnection and test what happens then

    private void resetFields() {
        RelayConfigurationProvider.onInterfaceReceived(new TestMisc.DefaultRelayConfiguration());

        mServer = RelayServerTest.getDefaultServer();
        mJoinParser = new JoinParser(mServer);
    }
}