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

    private boolean triedSecondNick;

    private boolean triedThirdNick;

    private int suffix;

    public ServerConnectionParser() {
        suffix = 0;
        triedSecondNick = false;
        triedThirdNick = false;
    }

    public String parseConnect(final Server server, final ServerConfiguration
            configuration, final BufferedReader reader, final ServerWriter writer) throws
            IOException {
        String line;
        final ServerEventBus eventBus = server.getServerEventBus();

        while ((line = reader.readLine()) != null) {
            final ArrayList<String> parsedArray = IRCUtils.splitRawLine(line, true);
            String s = parsedArray.get(0);
            switch (s) {
                case ServerCommands.Ping: // Immediately return
                    final String source = parsedArray.get(1);
                    CoreListener.respondToPing(writer, source);
                    break;
                case ServerCommands.Error:
                    // We are finished - the server has kicked us out for some reason
                    return null;
                case ServerCommands.Authenticate:
                    CapParser.parseCommand(parsedArray, configuration, server, eventBus, writer);
                    break;
                default:
                    if (StringUtils.isNumeric(parsedArray.get(1))) {
                        final String nick = parseConnectionCode(configuration.isNickChangable(),
                                parsedArray, eventBus, server, configuration.getNickStorage(),
                                writer);
                        if (nick != null) {
                            return nick;
                        }
                    } else {
                        parseConnectionCommand(parsedArray, configuration, eventBus, server,
                                writer);
                    }
                    break;
            }
        }
        return null;
    }

    private String parseConnectionCode(final boolean canChangeNick,
            final ArrayList<String> parsedArray, final ServerEventBus sender,
            final Server server, final NickStorage nickStorage, final ServerWriter writer) {
        final int code = Integer.parseInt(parsedArray.get(1));
        switch (code) {
            case RPL_WELCOME:
                // We are now logged in.
                final String nick = parsedArray.get(2);
                IRCUtils.removeFirstElementFromList(parsedArray, 3);
                return nick;
            case ERR_NICKNAMEINUSE:
                if (!triedSecondNick && StringUtils.isNotEmpty(nickStorage.getSecondChoiceNick())) {
                    writer.changeNick(new NickChangeEvent("", nickStorage.getSecondChoiceNick()));
                    triedSecondNick = true;
                } else if (!triedThirdNick && StringUtils.isNotEmpty(nickStorage
                        .getThirdChoiceNick())) {
                    writer.changeNick(new NickChangeEvent("", nickStorage.getThirdChoiceNick()));
                    triedThirdNick = true;
                } else {
                    if (canChangeNick) {
                        ++suffix;
                        writer.changeNick(new NickChangeEvent("",
                                nickStorage.getFirstChoiceNick() + suffix));
                    } else {
                        sender.sendNickInUseMessage(server);
                    }
                }
                break;
            case ERR_NONICKNAMEGIVEN:
                writer.changeNick(new NickChangeEvent("", nickStorage.getFirstChoiceNick()));
                break;
            default:
                if (saslCodes.contains(code)) {
                    CapParser.parseCode(code, parsedArray, sender, server, writer);
                }
                break;
        }
        return null;
    }

    private void parseConnectionCommand(final ArrayList<String> parsedArray,
            final ServerConfiguration configuration, final ServerEventBus sender,
            final Server server, final ServerWriter writer) {
        final String command = parsedArray.get(1).toUpperCase();
        IRCUtils.removeFirstElementFromList(parsedArray, 3);
        switch (command) {
            case ServerCommands.Notice:
                sender.sendGenericServerEvent(server, parsedArray.get(0));
                break;
            case ServerCommands.Cap:
                CapParser.parseCommand(parsedArray, configuration, server, sender, writer);
                break;
        }
    }
}
