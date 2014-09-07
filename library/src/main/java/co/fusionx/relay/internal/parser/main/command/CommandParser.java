package co.fusionx.relay.internal.parser.main.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelInterface;
import co.fusionx.relay.internal.constants.CommandConstants;
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
        parserMap.put(CommandConstants.JOIN, new JoinParser(server));
        parserMap.put(CommandConstants.PRIVMSG, new PrivmsgParser(server, ctcpParser));
        parserMap.put(CommandConstants.NOTICE, new NoticeParser(server, ctcpParser));
        parserMap.put(CommandConstants.PART, new PartParser(server));
        parserMap.put(CommandConstants.MODE, new ModeParser(server));
        parserMap.put(CommandConstants.QUIT, new QuitParser(server));
        parserMap.put(CommandConstants.NICK, new NickParser(server));
        parserMap.put(CommandConstants.TOPIC, new TopicParser(server));
        parserMap.put(CommandConstants.KICK, new KickParser(server));
        parserMap.put(CommandConstants.INVITE, new InviteParser(server));
        parserMap.put(CommandConstants.PONG, new PongParser(server));
        parserMap.put(CommandConstants.WALLOPS, new WallopsParser(server));

        // IRCv3 parsers
        if (server.getCapabilities().contains(CapCapability.ACCOUNTNOTIFY)) {
            parserMap.put(CommandConstants.ACCOUNT, new AccountParser(server));
        }
        if (server.getCapabilities().contains(CapCapability.AWAYNOTIFY)) {
            parserMap.put(CommandConstants.AWAY, new AwayParser(server));
        }

        return parserMap;
    }

    public abstract void onParseCommand(final List<String> parsedArray, final String prefix);
}