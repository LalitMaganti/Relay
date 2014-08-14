package com.fusionx.relay.parser;

import com.fusionx.relay.ServerConfiguration;
import com.fusionx.relay.bus.ServerCallHandler;
import com.fusionx.relay.bus.ServerEventBus;
import com.fusionx.relay.constants.ServerReplyCodes;
import com.fusionx.relay.event.server.GenericServerEvent;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

class CapParser {

    static void parseCommand(final List<String> parsedArray, final ServerConfiguration
            configuration, final ServerEventBus sender, final ServerCallHandler callHandler) {
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
                        final ServerEvent event = new GenericServerEvent("SASL not supported by "
                                + "server");
                        sender.postAndStoreEvent(event);
                        callHandler.sendEndCap();
                        break;
                }
            }
        }
    }

    static void parseCode(final int code, final List<String> parsedArray,
            final ServerEventBus sender, final ServerCallHandler callHandler) {
        final ServerEvent event;
        switch (code) {
            case ServerReplyCodes.RPL_SASL_SUCCESSFUL:
                final String successful = parsedArray.get(3);

                event = new GenericServerEvent(successful);
                sender.postAndStoreEvent(event);
                break;
            case ServerReplyCodes.RPL_SASL_LOGGED_IN:
                final String loginMessage = parsedArray.get(5);

                event = new GenericServerEvent(loginMessage);
                sender.postAndStoreEvent(event);
                break;
            case ServerReplyCodes.ERR_SASL_FAILED:
            case ServerReplyCodes.ERR_SASL_FAILED_2:
                final String error = parsedArray.get(3);

                event = new GenericServerEvent(error);
                sender.postAndStoreEvent(event);
                break;
            default:
                return;
        }
        callHandler.sendEndCap();
    }
}