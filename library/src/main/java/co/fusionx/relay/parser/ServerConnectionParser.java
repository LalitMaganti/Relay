package co.fusionx.relay.parser;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import co.fusionx.relay.Server;
import co.fusionx.relay.ServerConfiguration;
import co.fusionx.relay.bus.ServerCallHandler;
import co.fusionx.relay.bus.ServerEventBus;
import co.fusionx.relay.call.server.NickChangeCall;
import co.fusionx.relay.constants.ServerCommands;
import co.fusionx.relay.constants.ServerReplyCodes;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.misc.CoreListener;
import co.fusionx.relay.misc.NickStorage;
import co.fusionx.relay.util.IRCUtils;
import co.fusionx.relay.util.Utils;

public class ServerConnectionParser {

    private final Server mServer;

    private final ServerConfiguration mConfiguration;

    private final BufferedReader mBufferedReader;

    private final ServerCallHandler mServerCallHandler;

    private boolean triedSecondNick;

    private boolean triedThirdNick;

    private int suffix;

    public ServerConnectionParser(final Server server, final ServerConfiguration configuration,
            final BufferedReader bufferedReader, final ServerCallHandler callHandler) {
        suffix = 0;
        triedSecondNick = false;
        triedThirdNick = false;

        mServer = server;
        mConfiguration = configuration;
        mBufferedReader = bufferedReader;
        mServerCallHandler = callHandler;
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
                    CoreListener.respondToPing(mServerCallHandler, source);
                    break;
                case ServerCommands.ERROR:
                    // We are finished - the server has kicked us out for some reason
                    return null;
                case ServerCommands.AUTHENTICATE:
                    CapParser.parseCommand(parsedArray, mConfiguration, mServer,
                            mServerCallHandler);
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
            case ServerReplyCodes.RPL_WELCOME:
                // We are now logged in.
                final String nick = parsedArray.get(2);
                IRCUtils.removeFirstElementFromList(parsedArray, 3);
                return nick;
            case ServerReplyCodes.ERR_NICKNAMEINUSE:
                onNicknameInUser(canChangeNick, nickStorage);
                break;
            case ServerReplyCodes.ERR_NONICKNAMEGIVEN:
                mServer.getServerCallHandler().post(new NickChangeCall(nickStorage
                        .getFirstChoiceNick()));
                break;
            default:
                if (ServerReplyCodes.saslCodes.contains(code)) {
                    CapParser.parseCode(code, parsedArray, mServer, mServerCallHandler);
                }
                break;
        }
        return null;
    }

    private void onNicknameInUser(final boolean canChangeNick, final NickStorage nickStorage) {
        if (!triedSecondNick && Utils.isNotEmpty(nickStorage.getSecondChoiceNick())) {
            mServer.getServerCallHandler().post(new NickChangeCall(nickStorage
                    .getSecondChoiceNick()));
            triedSecondNick = true;
        } else if (!triedThirdNick && Utils.isNotEmpty(nickStorage.getThirdChoiceNick())) {
            mServer.getServerCallHandler().post(new NickChangeCall(nickStorage
                    .getThirdChoiceNick()));
            triedThirdNick = true;
        } else if (canChangeNick) {
            ++suffix;
            mServer.getServerCallHandler().post(new NickChangeCall(nickStorage
                    .getFirstChoiceNick() + suffix));
        } else {
            // TODO - fix this
            //sender.sendNickInUseMessage();
        }
    }

    private void parseConnectionCommand(final List<String> parsedArray,
            final ServerEventBus sender) {
        final String command = parsedArray.get(1).toUpperCase();
        IRCUtils.removeFirstElementFromList(parsedArray, 3);

        switch (command) {
            case ServerCommands.NOTICE:
                final GenericServerEvent event = new GenericServerEvent(mServer,
                        parsedArray.get(0));
                sender.postAndStoreEvent(event);
                break;
            case ServerCommands.CAP:
                CapParser.parseCommand(parsedArray, mConfiguration, mServer, mServerCallHandler);
                break;
        }
    }
}
