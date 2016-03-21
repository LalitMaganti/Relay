package co.fusionx.relay.internal.parser.main.command;

import android.util.Pair;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.event.channel.ChannelTopicEvent;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.Utils;

public class TopicParser extends CommandParser {

    public TopicParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String prefix) {
        final ChannelUser user = mUserChannelInterface.getUserFromPrefix(prefix);
        final Optional<RelayChannel> optChan = mUserChannelInterface.getChannel(parsedArray.get(0));

        LogUtils.logOptionalBug(optChan, mServer);
        Optionals.ifPresent(optChan, channel -> {
            final String newTopic = parsedArray.get(1);
            final Pair<String, List<FormatSpanInfo>> topicAndColors =
                    Utils.parseAndStripColorsFromMessage(newTopic);
            channel.postAndStoreEvent(new ChannelTopicEvent(channel, user,
                    topicAndColors.first, topicAndColors.second));
        });
    }
}