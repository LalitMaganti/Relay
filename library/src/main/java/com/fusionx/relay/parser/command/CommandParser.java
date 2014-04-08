package com.fusionx.relay.parser.command;

import com.fusionx.relay.Server;
import com.fusionx.relay.UserChannelInterface;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.constants.ServerCommands;

import java.util.List;
import java.util.Map;

import gnu.trove.map.hash.THashMap;

public abstract class CommandParser {

    private final Server mServer;

    private final ServerEventBus mServerEventBus;

    CommandParser(final Server server) {
        mServer = server;
        mServerEventBus = server.getServerEventBus();
    }

    public static Map<String, CommandParser> getParserMap(final Server server) {
        final CtcpParser ctcpParser = new CtcpParser(server);

        final Map<String, CommandParser> parserMap = new THashMap<>();
        parserMap.put(ServerCommands.JOIN, new JoinParser(server));
        parserMap.put(ServerCommands.PRIVMSG, new PrivmsgParser(server, ctcpParser));
        parserMap.put(ServerCommands.NOTICE, new NoticeParser(server, ctcpParser));
        parserMap.put(ServerCommands.PART, new PartParser(server));
        parserMap.put(ServerCommands.MODE, new ModeParser(server));
        parserMap.put(ServerCommands.QUIT, new QuitParser(server));
        parserMap.put(ServerCommands.NICK, new NickParser(server));
        parserMap.put(ServerCommands.TOPIC, new TopicParser(server));
        parserMap.put(ServerCommands.KICK, new KickParser(server));
        parserMap.put(ServerCommands.INVITE, new InviteParser(server));
        parserMap.put(ServerCommands.PONG, new PongParser(server));

        return parserMap;
    }

    public abstract void onParseCommand(final List<String> parsedArray, final String rawSource);

    Server getServer() {
        return mServer;
    }

    UserChannelInterface getUserChannelInterface() {
        return mServer.getUserChannelInterface();
    }

    ServerEventBus getServerEventBus() {
        return mServerEventBus;
    }
}