package co.fusionx.relay.parser.command;

import java.util.List;
import java.util.Map;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.RelayUserChannelInterface;
import co.fusionx.relay.bus.ServerEventBus;
import co.fusionx.relay.constants.ServerCommands;
import gnu.trove.map.hash.THashMap;

public abstract class CommandParser {

    final RelayServer mServer;

    final RelayUserChannelInterface mUserChannelInterface;

    final ServerEventBus mServerEventBus;

    CommandParser(final RelayServer server) {
        mServer = server;
        mUserChannelInterface = server.getUserChannelInterface();
        mServerEventBus = server.getServerEventBus();
    }

    public static Map<String, CommandParser> getParserMap(final RelayServer server) {
        final DCCParser dccParser = new DCCParser(server);
        final CtcpParser ctcpParser = new CtcpParser(server, dccParser);

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
        parserMap.put(ServerCommands.WALLOPS, new WallopsParser(server));

        return parserMap;
    }

    public abstract void onParseCommand(final List<String> parsedArray, final String rawSource);
}