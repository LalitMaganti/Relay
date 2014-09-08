package co.fusionx.relay.internal.base;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import co.fusionx.relay.base.ConnectionConfiguration;
import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.constants.CommandConstants;
import co.fusionx.relay.internal.constants.ServerReplyCodes;
import co.fusionx.relay.internal.dcc.RelayDCCManager;
import co.fusionx.relay.internal.parser.main.code.CodeParser;
import co.fusionx.relay.internal.parser.main.code.ErrorParser;
import co.fusionx.relay.internal.parser.main.code.InitalTopicParser;
import co.fusionx.relay.internal.parser.main.code.MotdParser;
import co.fusionx.relay.internal.parser.main.code.NameParser;
import co.fusionx.relay.internal.parser.main.command.AccountParser;
import co.fusionx.relay.internal.parser.main.command.AwayParser;
import co.fusionx.relay.internal.parser.main.command.CTCPParser;
import co.fusionx.relay.internal.parser.main.command.CommandParser;
import co.fusionx.relay.internal.parser.main.command.DCCParser;
import co.fusionx.relay.internal.parser.main.command.ErrorCommandParser;
import co.fusionx.relay.internal.parser.main.command.InviteParser;
import co.fusionx.relay.internal.parser.main.command.JoinParser;
import co.fusionx.relay.internal.parser.main.command.KickParser;
import co.fusionx.relay.internal.parser.main.command.ModeParser;
import co.fusionx.relay.internal.parser.main.command.NickParser;
import co.fusionx.relay.internal.parser.main.command.NoticeParser;
import co.fusionx.relay.internal.parser.main.command.PartParser;
import co.fusionx.relay.internal.parser.main.command.PingParser;
import co.fusionx.relay.internal.parser.main.command.PongParser;
import co.fusionx.relay.internal.parser.main.command.PrivmsgParser;
import co.fusionx.relay.internal.parser.main.command.QuitParser;
import co.fusionx.relay.internal.parser.main.command.TopicChangeParser;
import co.fusionx.relay.internal.parser.main.command.WallopsParser;
import co.fusionx.relay.internal.sender.base.RelayServerSender;
import co.fusionx.relay.internal.sender.packet.PacketSender;
import co.fusionx.relay.internal.bus.EventBus;
import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.sender.ServerSender;
import dagger.Module;
import dagger.Provides;

import static co.fusionx.relay.internal.constants.CommandConstants.ERROR;
import static co.fusionx.relay.internal.constants.CommandConstants.INVITE;
import static co.fusionx.relay.internal.constants.CommandConstants.JOIN;
import static co.fusionx.relay.internal.constants.CommandConstants.KICK;
import static co.fusionx.relay.internal.constants.CommandConstants.MODE;
import static co.fusionx.relay.internal.constants.CommandConstants.NICK;
import static co.fusionx.relay.internal.constants.CommandConstants.NOTICE;
import static co.fusionx.relay.internal.constants.CommandConstants.PART;
import static co.fusionx.relay.internal.constants.CommandConstants.PING;
import static co.fusionx.relay.internal.constants.CommandConstants.PONG;
import static co.fusionx.relay.internal.constants.CommandConstants.PRIVMSG;
import static co.fusionx.relay.internal.constants.CommandConstants.QUIT;
import static co.fusionx.relay.internal.constants.CommandConstants.TOPIC;
import static co.fusionx.relay.internal.constants.CommandConstants.WALLOPS;

@Module(injects = {
        RelaySession.class, RelayIRCConnection.class, RelayServer.class,
        RelayUserChannelGroup.class
})
public class RelayBaseModule {

    private final ConnectionConfiguration mConfiguration;

    public RelayBaseModule(final ConnectionConfiguration connectionConfiguration) {
        mConfiguration = connectionConfiguration;
    }

    @Provides
    public ConnectionConfiguration provideConfiguration() {
        return mConfiguration;
    }

    @Provides
    @Singleton
    public StatusManager provideStatusManager(final ConnectionConfiguration configuration,
            final RelayServer server, final RelayUserChannelGroup dao,
            final RelayQueryUserGroup queryManager) {
        return new RelayStatusManager(configuration, server, dao, queryManager);
    }

    @Singleton
    @Provides
    public PacketSender provideBaseSender() {
        return new PacketSender();
    }

    @Singleton
    @Provides
    public GenericBus<Event> provideServerWideEventBus() {
        return new EventBus<>();
    }

    @Provides
    @Singleton
    public Map<String, CommandParser> provideCommandParserMap(final RelayServer server,
            final RelayUserChannelGroup dao, final PacketSender sender,
            final RelayDCCManager dccManager, final RelayQueryUserGroup queryManager) {
        final DCCParser dccParser = new DCCParser(server, dccManager);
        final CTCPParser ctcpParser = new CTCPParser(server, dao, queryManager, sender, dccParser);

        final Map<String, CommandParser> parserMap = new HashMap<>();

        // Core RFC parsers
        parserMap.put(PING, new PingParser(server, dao, queryManager, sender));
        parserMap.put(ERROR, new ErrorCommandParser(server, dao, queryManager));

        // RFC parsers
        parserMap.put(JOIN, new JoinParser(server, dao, queryManager));
        parserMap.put(PRIVMSG, new PrivmsgParser(server, dao, queryManager, ctcpParser));
        parserMap.put(NOTICE, new NoticeParser(server, dao, queryManager, ctcpParser));
        parserMap.put(PART, new PartParser(server, dao, queryManager));
        parserMap.put(MODE, new ModeParser(server, dao, queryManager));
        parserMap.put(QUIT, new QuitParser(server, dao, queryManager));
        parserMap.put(NICK, new NickParser(server, dao, queryManager));
        parserMap.put(TOPIC, new TopicChangeParser(server, dao, queryManager));
        parserMap.put(KICK, new KickParser(server, dao, queryManager));
        parserMap.put(INVITE, new InviteParser(server, dao, queryManager));
        parserMap.put(PONG, new PongParser(server, dao, queryManager));
        parserMap.put(WALLOPS, new WallopsParser(server, dao, queryManager));

        // Optional IRCv3 parsers
        if (server.getCapabilities().contains(CapCapability.ACCOUNTNOTIFY)) {
            parserMap.put(CommandConstants.ACCOUNT, new AccountParser(server, dao, queryManager));
        }
        if (server.getCapabilities().contains(CapCapability.AWAYNOTIFY)) {
            parserMap.put(CommandConstants.AWAY, new AwayParser(server, dao, queryManager));
        }

        return parserMap;
    }

    @Provides
    @Singleton
    public SparseArray<CodeParser> provideCodeParserMap(final GenericBus<Event> superBus,
            final RelayServer server, final RelayUserChannelGroup dao,
            final RelayQueryUserGroup queryManager, final PacketSender sender) {
        final SparseArray<CodeParser> parserMap = new SparseArray<>();

        final InitalTopicParser topicChangeParser = new InitalTopicParser(server, dao, null);
        parserMap.put(ServerReplyCodes.RPL_TOPIC, topicChangeParser);
        parserMap.put(ServerReplyCodes.RPL_TOPICWHOTIME, topicChangeParser);

        final NameParser nameParser = new NameParser(server, dao, queryManager);
        parserMap.put(ServerReplyCodes.RPL_NAMREPLY, nameParser);
        parserMap.put(ServerReplyCodes.RPL_ENDOFNAMES, nameParser);

        final MotdParser motdParser = new MotdParser(server, dao, queryManager);
        parserMap.put(ServerReplyCodes.RPL_MOTDSTART, motdParser);
        parserMap.put(ServerReplyCodes.RPL_MOTD, motdParser);
        parserMap.put(ServerReplyCodes.RPL_ENDOFMOTD, motdParser);

        final ErrorParser errorParser = new ErrorParser(server, dao, null);
        parserMap.put(ServerReplyCodes.ERR_NOSUCHNICK, errorParser);
        parserMap.put(ServerReplyCodes.ERR_NICKNAMEINUSE, errorParser);

        return parserMap;
    }
}