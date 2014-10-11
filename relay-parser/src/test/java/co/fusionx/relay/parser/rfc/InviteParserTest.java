package co.fusionx.relay.parser.rfc;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InviteParserTest {

    private static final String PREFIX = "relaynick!relayusername:relayhostname";

    @Test
    public void testInvite() {
        final String invited = "relaynick2";
        final String channel = "#relay";

        final List<String> list = ImmutableList.of(invited, channel);
        final InviteParser inviteParser = new InviteParser(new InviteParser.InviteObserver() {
            @Override
            public void onInvite(final String invitingPrefix, final String invitedNick,
                    final String channelName) {
                assertThat(invitingPrefix).isEqualTo(PREFIX);
                assertThat(invitedNick).isEqualTo(invited);
                assertThat(channelName).isEqualTo(channel);
            }
        });
        inviteParser.parseCommand(list, PREFIX);
    }
}