package co.fusionx.relay.sender;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.bus.ServerCallHandler;
import co.fusionx.relay.call.server.JoinCall;

public class RelayServerSender implements ServerSender {

    private final RelayServer mServer;

    private final ServerCallHandler mCallHandler;

    public RelayServerSender(final RelayServer server, final ServerCallHandler callHandler) {
        mServer = server;
        mCallHandler = callHandler;
    }

    @Override
    public void sendQuery(final String nick, final String message) {
        // This is invalid - we don't have anything to send the server directly
    }

    @Override
    public void sendJoin(final String channelName) {
        mCallHandler.post(new JoinCall(channelName));
    }

    @Override
    public void sendNick(final String newNick) {

    }

    @Override
    public void sendWhois(final String nick) {

    }

    @Override
    public void sendRawLine(final String rawLine) {

    }
}
