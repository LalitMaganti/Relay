package co.fusionx.relay.internal.statechanger.rfc;

import com.google.common.base.Optional;

import co.fusionx.relay.constant.ChannelPrefix;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldMessageEvent;
import co.fusionx.relay.event.query.QueryMessageWorldEvent;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.parser.rfc.PrivmsgParser;
import co.fusionx.relay.internal.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;
import co.fusionx.relay.internal.util.Utils;

public class PrivmsgStateChanger implements PrivmsgParser.PrivmsgObserver {

    private final InternalServer mInternalServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final InternalQueryUserGroup mQueryManager;

    public PrivmsgStateChanger(final InternalServer internalServer,
            final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryManager) {
        mInternalServer = internalServer;
        mUserChannelGroup = userChannelGroup;
        mQueryManager = queryManager;
    }

    @Override
    public void onPrivmsg(final String prefix, final String recipient, final String message) {
        final String nick = ParseUtils.getNickFromPrefix(prefix);
        if (ChannelPrefix.isPrefix(recipient.charAt(0))) {
            onParseChannelMessage(nick, recipient, message);
        } else {
            onParsePrivateMessage(nick, message);
        }
    }

    private void onParsePrivateMessage(final String nick, final String message) {
        final InternalQueryUser user = mQueryManager.getOrAddQueryUser(nick);
        user.postEvent(new QueryMessageWorldEvent(user, message));
    }

    private void onParseChannelMessage(final String sendingNick, final String channelName,
            final String rawMessage) {
        final Optional<InternalChannel> optChannel = mUserChannelGroup.getChannel(channelName);

        Optionals.run(optChannel, channel -> {
            // TODO - actually parse the colours
            final String message = Utils.stripColorsFromMessage(rawMessage);
            // TODO - fix mentioning
            // final boolean mention = MentionParser.onMentionableCommand(message,
            //mUserChannelGroup.getUser().getNick().getNickAsString());

            final Optional<InternalChannelUser> optUser = mUserChannelGroup.getUser(sendingNick);
            final ChannelEvent event;
            if (optUser.isPresent()) {
                event = new ChannelWorldMessageEvent(channel, message, optUser.get(), false);
            } else {
                event = new ChannelWorldMessageEvent(channel, message, sendingNick, false);
            }
            channel.postEvent(event);
        }, () -> LogUtils.logOptionalBug(mInternalServer.getConfiguration()));
    }
}