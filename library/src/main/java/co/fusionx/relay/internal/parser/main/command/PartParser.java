package co.fusionx.relay.internal.parser.main.command;

import android.util.Pair;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelWorldPartEvent;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.event.channel.PartEvent;
import co.fusionx.relay.util.ParseUtils;
import co.fusionx.relay.util.Utils;

public class PartParser extends RemoveUserParser {

    public PartParser(final RelayServer server) {
        super(server);
    }

    @Override
    public Optional<RelayChannelUser> getRemovedUser(final List<String> parsedArray,
            final String rawSource) {
        final String userNick = ParseUtils.getNickFromPrefix(rawSource);
        return mUserChannelInterface.getUser(userNick);
    }

    @Override
    public ChannelWorldUserEvent getEvent(final List<String> parsedArray, final String rawSource,
            final RelayChannel channel, final ChannelUser user) {
        final UserLevel level = user.getChannelPrivileges(channel);
        final String reason = parsedArray.size() == 2 ? parsedArray.get(1).replace("\"", "") : "";
        final Pair<String, List<FormatSpanInfo>> reasonAndColors =
                Utils.parseAndStripColorsFromMessage(reason);
        return new ChannelWorldPartEvent(channel, user, level,
                reasonAndColors.first, reasonAndColors.second);
    }

    @Override
    void onRemoved(final List<String> parsedArray, final String rawSource,
            final RelayChannel channel) {
        // ZNCs can be stupid and can sometimes send PART commands for channels they didn't send
        // JOIN commands for...
        if (channel == null) {
            return;
        }

        final Collection<RelayChannelUser> users = mUserChannelInterface.removeChannel(channel);
        for (final RelayChannelUser user : users) {
            mUserChannelInterface.removeChannelFromUser(channel, user);
        }
        channel.postAndStoreEvent(new PartEvent(channel));
    }
}