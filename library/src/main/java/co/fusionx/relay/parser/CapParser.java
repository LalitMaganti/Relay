package co.fusionx.relay.parser;

import java.util.List;

import co.fusionx.relay.Server;
import co.fusionx.relay.ServerConfiguration;
import co.fusionx.relay.bus.ServerCallHandler;
import co.fusionx.relay.constants.ServerReplyCodes;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.util.IRCUtils;

class CapParser {

    static void parseCommand(final List<String> parsedArray, final ServerConfiguration
            configuration, final Server server, final ServerCallHandler callHandler) {
        final String command = parsedArray.get(0);
        if (command.equals("AUTHENTICATE")) {
            callHandler.sendSaslAuthentication(configuration.getSaslUsername(),
                    configuration.getSaslPassword());
        } else {
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
            } else {
                switch (command) {
                    case "NAK":
                        // This is non-fatal
                        break;
                    default:
                        // TODO - change this
                        final ServerEvent event = new GenericServerEvent(server, "SASL not "
                                + "supported by server");
                        server.getServerEventBus().postAndStoreEvent(event);
                        callHandler.sendEndCap();
                        break;
                }
            }
        }
    }

    static void parseCode(final int code, final List<String> parsedArray,
            final Server server, final ServerCallHandler callHandler) {
        final ServerEvent event;
        switch (code) {
            case ServerReplyCodes.RPL_SASL_SUCCESSFUL:
                final String successful = parsedArray.get(3);

                event = new GenericServerEvent(server, successful);
                server.getServerEventBus().postAndStoreEvent(event);
                break;
            case ServerReplyCodes.RPL_SASL_LOGGED_IN:
                final String loginMessage = parsedArray.get(5);

                event = new GenericServerEvent(server, loginMessage);
                server.getServerEventBus().postAndStoreEvent(event);
                break;
            case ServerReplyCodes.ERR_SASL_FAILED:
            case ServerReplyCodes.ERR_SASL_FAILED_2:
                final String error = parsedArray.get(3);

                event = new GenericServerEvent(server, error);
                server.getServerEventBus().postAndStoreEvent(event);
                break;
            default:
                return;
        }
        callHandler.sendEndCap();
    }
}