package co.fusionx.relay.parser.connection;

import java.util.List;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.constants.ServerReplyCodes;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.sender.relay.RelayCapSender;
import co.fusionx.relay.util.IRCUtils;

class CapParser {

    private final RelayServer mServer;

    private final ServerConfiguration mServerConfiguration;

    private final RelayCapSender mCapSender;

    public CapParser(final RelayServer server, final ServerConfiguration serverConfiguration) {
        mServer = server;
        mServerConfiguration = serverConfiguration;

        mCapSender = new RelayCapSender(mServer.getRelayPacketSender());
    }

    void parseCode(final int code, final List<String> parsedArray) {
        switch (code) {
            case ServerReplyCodes.RPL_SASL_SUCCESSFUL:
                final String successful = parsedArray.get(3);
                mServer.postAndStoreEvent(new GenericServerEvent(mServer, successful));
                break;
            case ServerReplyCodes.RPL_SASL_LOGGED_IN:
                final String loginMessage = parsedArray.get(5);
                mServer.postAndStoreEvent(new GenericServerEvent(mServer, loginMessage));
                break;
            case ServerReplyCodes.ERR_SASL_FAILED:
            case ServerReplyCodes.ERR_SASL_FAILED_2:
                final String error = parsedArray.get(3);
                mServer.postAndStoreEvent(new GenericServerEvent(mServer, error));
                break;
            default:
                return;
        }
        mCapSender.sendEndCap();
    }

    void parseCommand(final List<String> parsedArray) {
        final String command = parsedArray.get(0);
        if (command.equals("AUTHENTICATE")) {
            mCapSender.sendSaslPlainAuthentication(mServerConfiguration.getSaslUsername(),
                    mServerConfiguration.getSaslPassword());
            return;
        }

        final List<String> capabilities = IRCUtils.splitRawLine(parsedArray.get(1), true);
        if (capabilities.contains("sasl")) {
            switch (command) {
                case "LS":
                    mCapSender.requestSasl();
                    break;
                case "ACK":
                    mCapSender.sendPlainSaslAuthentication();
                    break;
            }
            return;
        }

        switch (command) {
            case "NAK":
                // This is non-fatal
                break;
            default:
                // TODO - change this
                mServer.postAndStoreEvent(new GenericServerEvent(mServer,
                        "SASL not supported by server"));
                mCapSender.sendEndCap();
                break;
        }
    }
}