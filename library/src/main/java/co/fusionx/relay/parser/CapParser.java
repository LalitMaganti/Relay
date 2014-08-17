package co.fusionx.relay.parser;

import java.util.List;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.bus.ServerCallHandler;
import co.fusionx.relay.constants.ServerReplyCodes;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.util.IRCUtils;

class CapParser {

    static void parseCommand(final List<String> parsedArray, final ServerConfiguration
            configuration, final RelayServer server, final ServerCallHandler callHandler) {
        final String command = parsedArray.get(0);
        if (command.equals("AUTHENTICATE")) {
            callHandler.sendSaslAuthentication(configuration.getSaslUsername(),
                    configuration.getSaslPassword());
            return;
        }

        final List<String> capabilities = IRCUtils.splitRawLine(parsedArray.get(1), true);
        if (capabilities.contains("sasl")) {
            switch (command) {
                case "LS":
                    callHandler.requestSasl();
                    break;
                case "ACK":
                    callHandler.sendPlainSaslAuthentication();
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
                server.postAndStoreEvent(new GenericServerEvent(server,
                        "SASL not supported by server"));
                callHandler.sendEndCap();
                break;
        }
    }

    static void parseCode(final int code, final List<String> parsedArray,
            final RelayServer server, final ServerCallHandler callHandler) {
        switch (code) {
            case ServerReplyCodes.RPL_SASL_SUCCESSFUL:
                final String successful = parsedArray.get(3);
                server.postAndStoreEvent(new GenericServerEvent(server, successful));
                break;
            case ServerReplyCodes.RPL_SASL_LOGGED_IN:
                final String loginMessage = parsedArray.get(5);
                server.postAndStoreEvent(new GenericServerEvent(server, loginMessage));
                break;
            case ServerReplyCodes.ERR_SASL_FAILED:
            case ServerReplyCodes.ERR_SASL_FAILED_2:
                final String error = parsedArray.get(3);
                server.postAndStoreEvent(new GenericServerEvent(server, error));
                break;
            default:
                return;
        }
        callHandler.sendEndCap();
    }
}