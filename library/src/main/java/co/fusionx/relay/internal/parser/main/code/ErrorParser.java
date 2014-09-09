package co.fusionx.relay.internal.parser.main.code;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.event.query.QueryNoSuchNickEvent;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.internal.constants.ServerReplyCodes;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;

public class ErrorParser extends CodeParser {

    public ErrorParser(final InternalServer server,
            final InternalUserChannelGroup userChannelInterface,
            final InternalQueryUserGroup queryManager) {
        super(server, userChannelInterface, queryManager);
    }

    @Override
    public void onParseCode(final List<String> parsedArray, final int code) {
        switch (code) {
            case ServerReplyCodes.ERR_NOSUCHNICK:
                onNoSuchNickError(parsedArray);
                break;
            case ServerReplyCodes.ERR_NICKNAMEINUSE:
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
            user.getBus().post(new QueryNoSuchNickEvent(user, message));
        } else {
            mServer.getBus().post(new GenericServerEvent(mServer, message));
        }
    }
}