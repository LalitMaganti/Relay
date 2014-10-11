package co.fusionx.relay.internal.statechanger.rfc;

import com.google.common.base.Optional;

import javax.inject.Inject;

import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.event.channel.ChannelTopicEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.parser.rfc.TopicParser;
import co.fusionx.relay.util.LogUtils;

public class TopicStateChanger implements TopicParser.TopicObserver {

    private final InternalServer mServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    @Inject
    public TopicStateChanger(final InternalServer server,
            final InternalUserChannelGroup userChannelGroup) {
        mServer = server;
        mUserChannelGroup = userChannelGroup;
    }

    @Override
    public void onTopic(final String prefix, final String channelName, final String newTopic) {
        final ChannelUser user = mUserChannelGroup.getUserFromPrefix(prefix);
        final Optional<InternalChannel> optChan = mUserChannelGroup.getChannel(channelName);

        Optionals.run(optChan,
                channel -> channel.postEvent(new ChannelTopicEvent(channel, user, newTopic)),
                () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
    }
}