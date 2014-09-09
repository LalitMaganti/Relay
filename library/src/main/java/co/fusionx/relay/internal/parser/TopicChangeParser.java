package co.fusionx.relay.internal.parser;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.event.channel.ChannelTopicEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.LogUtils;

public class TopicChangeParser extends CommandParser {

    public TopicChangeParser(final InternalServer server,
            final InternalUserChannelGroup userChannelInterface,
            final InternalQueryUserGroup queryManager) {
        super(server, userChannelInterface, queryManager);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String prefix) {
        final ChannelUser user = mUserChannelGroup.getUserFromPrefix(prefix);
        final Optional<InternalChannel> optChan = mUserChannelGroup.getChannel(parsedArray.get(0));

        LogUtils.logOptionalBug(optChan, mServer);
        Optionals.ifPresent(optChan, channel -> {
            final String newTopic = parsedArray.get(1);
            channel.getBus().post(new ChannelTopicEvent(channel, user, newTopic));
        });
    }
}