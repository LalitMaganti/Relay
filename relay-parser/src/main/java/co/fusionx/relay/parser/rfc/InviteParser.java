package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.parser.CommandParser;

public class InviteParser implements CommandParser {

    private final InviteObserver mInviteObserver;

    public InviteParser(final InviteObserver inviteObserver) {
        mInviteObserver = inviteObserver;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String invitedNick = parsedArray.get(0);
        final String channelName = parsedArray.get(1);

        mInviteObserver.onInvite(prefix, invitedNick, channelName);
    }

    public interface InviteObserver {

        public void onInvite(final String invitingPrefix, final String invitedNick,
                final String channelName);
    }
}