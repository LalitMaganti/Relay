package co.fusionx.relay.internal.parser;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.event.query.QueryNoSuchNickEvent;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.parser.ReplyCodeParser;

public class ErrorReplyCodeParser implements ReplyCodeParser {

    private final InternalUserChannelGroup mUserChannelInterface;

    private final InternalServer mServer;

    private final InternalQueryUserGroup mQueryManager;

    public ErrorReplyCodeParser(final InternalServer server,
            final InternalUserChannelGroup userChannelInterface,
            final InternalQueryUserGroup queryManager) {
        mServer = server;
        mUserChannelInterface = userChannelInterface;
        mQueryManager = queryManager;
    }

    @Override
    public void parseReplyCode(final List<String> parsedArray, final int code) {
        switch (code) {
            case ReplyCodes.ERR_NOSUCHNICK:
                onNoSuchNickError(parsedArray);
                break;
            case ReplyCodes.ERR_NICKNAMEINUSE:
                onNickInUse(parsedArray);
                break;
        }
    }

    // TODO - implement this
    private void onNickInUse(final List<String> parsedArray) {
    }

    /**
     * Example line:
     * :some.server.url 401 relay relaytester :No such nick/channel
     *
     * @param parsedArray - the array of the line (split by spaces)
     */
    private void onNoSuchNickError(final List<String> parsedArray) {
        final String nick = parsedArray.get(0);
        final String message = parsedArray.get(1);
        final Optional<InternalQueryUser> optional = mQueryManager.getQueryUser(nick);

        // If the user is null then this no such nick event happened for another reason
        if (optional.isPresent()) {
            final InternalQueryUser user = optional.get();
            user.postEvent(new QueryNoSuchNickEvent(user, message));
        } else {
            mServer.postEvent(new GenericServerEvent(mServer, message));
        }
    }
}