package com.fusionx.androidirclibrary.parser;

import com.fusionx.androidirclibrary.Server;
import com.fusionx.androidirclibrary.ServerConfiguration;
import com.fusionx.androidirclibrary.communication.MessageSender;
import com.fusionx.androidirclibrary.constants.ServerReplyCodes;
import com.fusionx.androidirclibrary.util.IRCUtils;
import com.fusionx.androidirclibrary.writers.ServerWriter;

import java.util.ArrayList;

class CapParser {

    static void parseCommand(final ArrayList<String> parsedArray, final ServerConfiguration
            configuration, final Server server, final MessageSender sender) {
        final ServerWriter writer = server.getWriter();
        final String command = parsedArray.get(0);
        if (command.equals("AUTHENTICATE")) {
            writer.sendSaslAuthentication(configuration.getSaslUsername(),
                    configuration.getSaslPassword());
        } else {
            final ArrayList<String> capabilities = IRCUtils.splitRawLine(parsedArray.get(1),
                    true);
            if (capabilities.contains("sasl")) {
                if (command.equals("LS")) {
                    writer.requestSasl();
                } else if (command.equals("ACK")) {
                    writer.sendPlainSaslAuthentication();
                }
            } else {
                sender.sendGenericServerEvent(server, "SASL not supported by server");
                writer.sendEndCap();
            }
        }
    }

    static void parseCode(final int code, final ArrayList<String> parsedArray,
            final MessageSender sender, final Server server) {
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
        final ServerWriter writer = server.getWriter();
        writer.sendEndCap();
    }
}