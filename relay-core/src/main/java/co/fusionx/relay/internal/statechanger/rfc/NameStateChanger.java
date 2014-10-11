package co.fusionx.relay.internal.statechanger.rfc;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import co.fusionx.relay.constant.ChannelType;
import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.event.channel.ChannelNameEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.parser.ReplyCodeParser;
import co.fusionx.relay.parser.ircv3.NickPrefixNameParser;

// TODO - rewrite this class not to assume that the NAME commands will be sent all for one
// channel and then the next - they could be sent as a mixed setup
public class NameStateChanger
        implements NickPrefixNameParser.NickPrefixNameObserver {

    private final InternalUserChannelGroup mUserChannelGroup;

    private InternalChannel mChannel;

    public NameStateChanger(final InternalUserChannelGroup userChannelGroup) {
        mUserChannelGroup = userChannelGroup;
    }

    @Override
    public void onNameReply(final ChannelType type, final String channelName,
            final List<Pair<String, UserLevel>> nickList) {
        if (mChannel == null) {
            // TODO - this needs to be handled properly rather than simply getting
            mChannel = mUserChannelGroup.getChannel(channelName).get();
        }

        for (final Pair<String, UserLevel> pair : nickList) {
            final InternalChannelUser user = mUserChannelGroup.getNonNullUser(pair.getKey());
            mUserChannelGroup.coupleUserAndChannel(user, mChannel, pair.getValue());
        }
    }

    @Override
    public void onNameFinished() {
        mChannel.postEvent(new ChannelNameEvent(mChannel, mChannel.getUsers()));
        mChannel = null;
    }
}