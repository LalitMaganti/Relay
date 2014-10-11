package co.fusionx.relay.parser.rfc;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JoinParserTest {

    private static final String PREFIX = "relaynick!relayusername:relayhostname";

    @Test
    public void testJoin() {
        final String channelName = "#relay";

        final List<String> list = ImmutableList.of(channelName);
        final JoinParser joinParser = new JoinParser(new JoinParser.JoinObserver() {
            @Override
            public void onJoin(final String prefix, final String channelName) {
                assertThat(prefix).isEqualTo(PREFIX);
                assertThat(channelName).isEqualTo(channelName);
            }
        });
        joinParser.parseCommand(list, PREFIX);
    }
}