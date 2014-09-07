package co.fusionx.relay.internal.parser.main.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelInterface;
import co.fusionx.relay.internal.constants.ServerCommands;
import co.fusionx.relay.misc.EventBus;

public abstract class CommandParser {

    final RelayServer mServer;

    final RelayUserChannelInterface mUserChannelInterface;

    final EventBus mEventBus;

    CommandParser(final RelayServer server) {
        mServer = server;
        mUserChannelInterface = server.getUserChannelInterface();
        mEventBus = server.getServerWideBus();
    }

    public static Map<String, CommandParser> getParserMap(final RelayServer server) {
        final DCCParser dccParser = new DCCParser(server);
        final CTCPParser ctcpParser = new CTCPParser(server, dccParser);

        final Map<String, CommandParser> parserMap = new HashMap<>();
        parserMap.put(ServerCommands.ACCOUNT, new AccountParser(server));
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
        parserMap.put(ServerCommands.WALLOPS, new WallopsParser(server));

        return parserMap;
    }

    public abstract void onParseCommand(final List<String> parsedArray, final String prefix);
}