package com.fusionx.relay.parser.command;

import com.fusionx.relay.Server;
import com.fusionx.relay.UserChannelInterface;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.constants.ServerCommands;
import com.fusionx.relay.interfaces.EventResponses;
import com.fusionx.relay.misc.InterfaceHolders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CommandParser {

    Server mServer;

    UserChannelInterface mUserChannelInterface;

    ServerEventBus mServerEventBus;

    EventResponses mEventResponses;

    public CommandParser(final Server server) {
        mServer = server;
        mUserChannelInterface = server.getUserChannelInterface();
        mServerEventBus = server.getServerEventBus();
        mEventResponses = InterfaceHolders.getEventResponses();
    }

    public static Map<String, CommandParser> getParserMap(final Server server) {
        final CtcpParser ctcpParser = new CtcpParser(server);

        final Map<String, CommandParser> parserMap = new HashMap<>();
        parserMap.put(ServerCommands.Join, new JoinParser(server));
        parserMap.put(ServerCommands.Privmsg, new PrivmsgParser(server, ctcpParser));
        parserMap.put(ServerCommands.Notice, new NoticeParser(server, ctcpParser));
        parserMap.put(ServerCommands.Part, new PartParser(server));
        parserMap.put(ServerCommands.Mode, new ModeParser(server));
        parserMap.put(ServerCommands.Quit, new QuitParser(server));
        parserMap.put(ServerCommands.Nick, new NickParser(server));
        parserMap.put(ServerCommands.Topic, new TopicParser(server));
        parserMap.put(ServerCommands.Kick, new KickParser(server));
        parserMap.put(ServerCommands.Kick, new InviteParser(server));

        return parserMap;
    }

    public abstract void onParseCommand(final List<String> parsedArray, final String rawSource);
}