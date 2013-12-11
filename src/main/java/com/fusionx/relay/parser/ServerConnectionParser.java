package com.fusionx.relay.parser;

import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfiguration;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.constants.ServerCommands;
import com.fusionx.relay.event.NickChangeEvent;
import com.fusionx.relay.misc.CoreListener;
import com.fusionx.relay.misc.NickStorage;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.writers.ServerWriter;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import static com.fusionx.relay.constants.ServerReplyCodes.ERR_NICKNAMEINUSE;
import static com.fusionx.relay.constants.ServerReplyCodes.ERR_NONICKNAMEGIVEN;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_WELCOME;
import static com.fusionx.relay.constants.ServerReplyCodes.saslCodes;

public class ServerConnectionParser {

    private static boolean triedSecondNick = false;

    private static boolean triedThirdNick = false;

    private static int suffix = 0;

    public static String parseConnect(final Server server, final ServerConfiguration
            configuration, final BufferedReader reader, final ServerWriter writer)
            throws IOException {

        String line;
        suffix = 0;
        triedSecondNick = false;
        triedThirdNick = false;
        final ServerEventBus sender = server.getServerEventBus();

        while ((line = reader.readLine()) != null) {
            final ArrayList<String> parsedArray = IRCUtils.splitRawLine(line, true);
            String s = parsedArray.get(0);
            if (s.equals(ServerCommands.Ping)) {// Immediately return
                final String source = parsedArray.get(1);
                CoreListener.respondToPing(writer, source);
            } else if (s.equals(ServerCommands.Error)) {// We are finished - the server has
                // kicked us out for some reason
                return null;
            } else if (s.equals(ServerCommands.Authenticate)) {
                CapParser.parseCommand(parsedArray, configuration, server, sender, writer);
            } else {
                if (StringUtils.isNumeric(parsedArray.get(1))) {
                    final String nick = parseConnectionCode(configuration.isNickChangable(),
                            parsedArray, sender, server,
                            configuration.getNickStorage(), writer);
                    if (nick != null) {
                        return nick;
                    }
                } else {
                    parseConnectionCommand(parsedArray, configuration, sender,
                            server, writer);
                }
            }
        }
        return null;
    }

    private static String parseConnectionCode(final boolean canChangeNick,
            final ArrayList<String> parsedArray, final ServerEventBus sender,
            final Server server, final NickStorage nickStorage,
            final ServerWriter writer) {
        final int code = Integer.parseInt(parsedArray.get(1));
        switch (code) {
            case RPL_WELCOME:
                // We are now logged in.
                final String nick = parsedArray.get(2);
                IRCUtils.removeFirstElementFromList(parsedArray, 3);
                return nick;
            case ERR_NICKNAMEINUSE:
                if (!triedSecondNick && StringUtils.isNotEmpty(nickStorage.getSecondChoiceNick())) {
                    server.getServerCallBus().post(new NickChangeEvent("", nickStorage
                            .getSecondChoiceNick()));
                    triedSecondNick = true;
                } else if (!triedThirdNick && StringUtils.isNotEmpty(nickStorage
                        .getThirdChoiceNick())) {
                    server.getServerCallBus().post(new NickChangeEvent("",
                            nickStorage.getThirdChoiceNick()));
                    triedThirdNick = true;
                } else {
                    if (canChangeNick) {
                        ++suffix;
                        server.getServerCallBus().post(new NickChangeEvent("",
                                nickStorage.getFirstChoiceNick() + suffix));
                    } else {
                        sender.sendNickInUseMessage(server);
                    }
                }
                break;
            case ERR_NONICKNAMEGIVEN:
                server.getServerCallBus().post(new NickChangeEvent("",
                        nickStorage.getFirstChoiceNick()));
                break;
            default:
                if (saslCodes.contains(code)) {
                    CapParser.parseCode(code, parsedArray, sender, server, writer);
                }
                break;
        }
        return null;
    }

    private static void parseConnectionCommand(final ArrayList<String> parsedArray,
            final ServerConfiguration configuration, final ServerEventBus sender,
            final Server server, final ServerWriter writer) {
        final String s = parsedArray.get(1).toUpperCase();
        if (s.equals(ServerCommands.Notice)) {
            IRCUtils.removeFirstElementFromList(parsedArray, 3);
            sender.sendGenericServerEvent(server, parsedArray.get(0));
        } else if (s.equals(ServerCommands.Cap)) {
            IRCUtils.removeFirstElementFromList(parsedArray, 3);
            CapParser.parseCommand(parsedArray, configuration, server, sender, writer);
        }
    }

    /**
     * Not to be instantiated
     */
    private ServerConnectionParser() {
    }
}
