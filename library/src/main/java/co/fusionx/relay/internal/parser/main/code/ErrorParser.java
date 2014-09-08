package co.fusionx.relay.internal.parser.main.code;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.event.query.QueryNoSuchNickEvent;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.internal.base.RelayQueryUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;
import co.fusionx.relay.internal.constants.ServerReplyCodes;

public class ErrorParser extends CodeParser {

    public ErrorParser(final RelayServer server,
            final RelayUserChannelDao userChannelInterface) {
        super(server, userChannelInterface);
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
        final Optional<RelayQueryUser> optional = mUser.getQueryUser(nick);

        // If the user is null then this no such nick event happened for another reason
        if (optional.isPresent()) {
            final RelayQueryUser user = optional.get();
            user.postAndStoreEvent(new QueryNoSuchNickEvent(user, message));
        } else {
            mServer.postAndStoreEvent(new GenericServerEvent(mServer, message));
        }
    }
}