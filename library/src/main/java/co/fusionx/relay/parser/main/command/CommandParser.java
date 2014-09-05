package co.fusionx.relay.parser.main.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.base.relay.RelayUserChannelInterface;
import co.fusionx.relay.misc.EventBus;
import co.fusionx.relay.constants.ServerCommands;

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
        final CTCPParser CTCPParser = new CTCPParser(server, dccParser);

        final Map<String, CommandParser> parserMap = new HashMap<>();
        parserMap.put(ServerCommands.JOIN, new JoinParser(server));
        parserMap.put(ServerCommands.PRIVMSG, new PrivmsgParser(server, CTCPParser));
        parserMap.put(ServerCommands.NOTICE, new NoticeParser(server, CTCPParser));
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