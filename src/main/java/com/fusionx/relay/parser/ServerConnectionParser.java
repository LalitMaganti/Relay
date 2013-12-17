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

    private final Server mServer;

    private final ServerConfiguration mConfiguration;

    private final BufferedReader mBufferedReader;

    private final ServerWriter mWriter;

    private boolean triedSecondNick;

    private boolean triedThirdNick;

    private int suffix;

    public ServerConnectionParser(final Server server, final ServerConfiguration configuration,
            final BufferedReader bufferedReader, final ServerWriter writer) {
        suffix = 0;
        triedSecondNick = false;
        triedThirdNick = false;

        mServer = server;
        mConfiguration = configuration;
        mBufferedReader = bufferedReader;
        mWriter = writer;
    }

    public String parseConnect() throws IOException {
        String line;
        final ServerEventBus eventBus = mServer.getServerEventBus();

        while ((line = mBufferedReader.readLine()) != null) {
            final ArrayList<String> parsedArray = IRCUtils.splitRawLine(line, true);
            String s = parsedArray.get(0);
            switch (s) {
                case ServerCommands.Ping: // Immediately return
                    final String source = parsedArray.get(1);
                    CoreListener.respondToPing(mWriter, source);
                    break;
                case ServerCommands.Error:
                    // We are finished - the server has kicked us out for some reason
                    return null;
                case ServerCommands.Authenticate:
                    CapParser.parseCommand(parsedArray, mConfiguration, mServer, eventBus, mWriter);
                    break;
                default:
                    if (StringUtils.isNumeric(parsedArray.get(1))) {
                        final String nick = parseConnectionCode(mConfiguration.isNickChangable(),
                                parsedArray, eventBus, mConfiguration.getNickStorage());
                        if (nick != null) {
                            return nick;
                        }
                    } else {
                        parseConnectionCommand(parsedArray, eventBus);
                    }
                    break;
            }
        }
        return null;
    }

    private String parseConnectionCode(final boolean canChangeNick,
            final ArrayList<String> parsedArray, final ServerEventBus sender,
            final NickStorage nickStorage) {
        final int code = Integer.parseInt(parsedArray.get(1));
        switch (code) {
            case RPL_WELCOME:
                // We are now logged in.
                final String nick = parsedArray.get(2);
                IRCUtils.removeFirstElementFromList(parsedArray, 3);
                return nick;
            case ERR_NICKNAMEINUSE:
                if (!triedSecondNick && StringUtils.isNotEmpty(nickStorage.getSecondChoiceNick())) {
                    mWriter.changeNick(new NickChangeEvent("", nickStorage.getSecondChoiceNick()));
                    triedSecondNick = true;
                } else if (!triedThirdNick && StringUtils.isNotEmpty(nickStorage
                        .getThirdChoiceNick())) {
                    mWriter.changeNick(new NickChangeEvent("", nickStorage.getThirdChoiceNick()));
                    triedThirdNick = true;
                } else {
                    if (canChangeNick) {
                        ++suffix;
                        mWriter.changeNick(new NickChangeEvent("",
                                nickStorage.getFirstChoiceNick() + suffix));
                    } else {
                        sender.sendNickInUseMessage(mServer);
                    }
                }
                break;
            case ERR_NONICKNAMEGIVEN:
                mWriter.changeNick(new NickChangeEvent("", nickStorage.getFirstChoiceNick()));
                break;
            default:
                if (saslCodes.contains(code)) {
                    CapParser.parseCode(code, parsedArray, sender, mServer, mWriter);
                }
                break;
        }
        return null;
    }

    private void parseConnectionCommand(final ArrayList<String> parsedArray,
            final ServerEventBus sender) {
        final String command = parsedArray.get(1).toUpperCase();
        IRCUtils.removeFirstElementFromList(parsedArray, 3);
        switch (command) {
            case ServerCommands.Notice:
                sender.sendGenericServerEvent(mServer, parsedArray.get(0));
                break;
            case ServerCommands.Cap:
                CapParser.parseCommand(parsedArray, mConfiguration, mServer, sender, mWriter);
                break;
        }
    }
}
