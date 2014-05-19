package com.fusionx.relay.parser;

import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfiguration;
import com.fusionx.relay.call.NickChangeCall;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.constants.ServerCommands;
import com.fusionx.relay.event.server.GenericServerEvent;
import com.fusionx.relay.misc.CoreListener;
import com.fusionx.relay.misc.NickStorage;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.util.Utils;
import com.fusionx.relay.writers.ServerWriter;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

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
        final ServerEventBus eventBus = mServer.getServerEventBus();

        String line;
        while ((line = mBufferedReader.readLine()) != null) {
            final List<String> parsedArray = IRCUtils.splitRawLine(line, true);
            final String command = parsedArray.get(0);
            switch (command) {
                case ServerCommands.PING:
                    // Immediately return
                    final String source = parsedArray.get(1);
                    CoreListener.respondToPing(mWriter, source);
                    break;
                case ServerCommands.ERROR:
                    // We are finished - the server has kicked us out for some reason
                    return null;
                case ServerCommands.AUTHENTICATE:
                    CapParser.parseCommand(parsedArray, mConfiguration, eventBus, mWriter);
                    break;
                default:
                    if (StringUtils.isNumeric(parsedArray.get(1))) {
                        final String nick = parseConnectionCode(mConfiguration.isNickChangeable(),
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
            final List<String> parsedArray, final ServerEventBus sender,
            final NickStorage nickStorage) {
        final int code = Integer.parseInt(parsedArray.get(1));
        switch (code) {
            case RPL_WELCOME:
                // We are now logged in.
                final String nick = parsedArray.get(2);
                IRCUtils.removeFirstElementFromList(parsedArray, 3);
                return nick;
            case ERR_NICKNAMEINUSE:
                if (!triedSecondNick && Utils.isNotEmpty(nickStorage.getSecondChoiceNick())) {
                    mWriter.sendNick(new NickChangeCall(nickStorage.getSecondChoiceNick()));
                    triedSecondNick = true;
                } else if (!triedThirdNick && Utils.isNotEmpty(nickStorage.getThirdChoiceNick())) {
                    mWriter.sendNick(new NickChangeCall(nickStorage.getThirdChoiceNick()));
                    triedThirdNick = true;
                } else {
                    if (canChangeNick) {
                        ++suffix;
                        mWriter.sendNick(new NickChangeCall(nickStorage.getFirstChoiceNick() +
                                suffix));
                    } else {
                        // TODO - fix this
                        //sender.sendNickInUseMessage();
                    }
                }
                break;
            case ERR_NONICKNAMEGIVEN:
                mWriter.sendNick(new NickChangeCall(nickStorage.getFirstChoiceNick()));
                break;
            default:
                if (saslCodes.contains(code)) {
                    CapParser.parseCode(code, parsedArray, sender, mWriter);
                }
                break;
        }
        return null;
    }

    private void parseConnectionCommand(final List<String> parsedArray,
            final ServerEventBus sender) {
        final String command = parsedArray.get(1).toUpperCase();
        IRCUtils.removeFirstElementFromList(parsedArray, 3);

        switch (command) {
            case ServerCommands.NOTICE:
                final GenericServerEvent event = new GenericServerEvent(parsedArray.get(0));
                sender.postAndStoreEvent(event);
                break;
            case ServerCommands.CAP:
                CapParser.parseCommand(parsedArray, mConfiguration, sender, mWriter);
                break;
        }
    }
}
