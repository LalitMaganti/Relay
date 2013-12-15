package com.fusionx.relay.parser;

import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfiguration;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.constants.ServerReplyCodes;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.writers.ServerWriter;

import java.util.ArrayList;

class CapParser {

    static void parseCommand(final ArrayList<String> parsedArray, final ServerConfiguration
            configuration, final Server server, final ServerEventBus sender,
            final ServerWriter writer) {
        final String command = parsedArray.get(0);
        if (command.equals("AUTHENTICATE")) {
            writer.sendSaslAuthentication(configuration.getSaslUsername(),
                    configuration.getSaslPassword());
        } else {
            final ArrayList<String> capabilities = IRCUtils.splitRawLine(parsedArray.get(1),
                    true);
            if (capabilities.contains("sasl")) {
                switch (command) {
                    case "LS":
                        writer.requestSasl();
                        break;
                    case "ACK":
                        writer.sendPlainSaslAuthentication();
                        break;
                }
            } else {
                sender.sendGenericServerEvent(server, "SASL not supported by server");
                writer.sendEndCap();
            }
        }
    }

    static void parseCode(final int code, final ArrayList<String> parsedArray,
            final ServerEventBus sender, final Server server, final ServerWriter writer) {
        switch (code) {
            case ServerReplyCodes.RPL_SASL_SUCCESSFUL:
                final String successful = parsedArray.get(3);
                sender.sendGenericServerEvent(server, successful);
                break;
            case ServerReplyCodes.RPL_SASL_LOGGED_IN:
                final String loginMessage = parsedArray.get(5);
                sender.sendGenericServerEvent(server, loginMessage);
                break;
            case ServerReplyCodes.ERR_SASL_FAILED:
            case ServerReplyCodes.ERR_SASL_FAILED_2:
                final String error = parsedArray.get(3);
                sender.sendGenericServerEvent(server, error);
                break;
            default:
                return;
        }
        writer.sendEndCap();
    }
}