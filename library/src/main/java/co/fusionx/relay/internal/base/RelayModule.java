package co.fusionx.relay.internal.base;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.core.ConnectionConfiguration;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.bus.EventBus;
import co.fusionx.relay.internal.constants.CommandConstants;
import co.fusionx.relay.internal.constants.ServerReplyCodes;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalStatusManager;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.dcc.RelayDCCManager;
import co.fusionx.relay.internal.parser.AccountParser;
import co.fusionx.relay.internal.parser.AwayParser;
import co.fusionx.relay.internal.parser.CTCPParser;
import co.fusionx.relay.internal.parser.CodeParser;
import co.fusionx.relay.internal.parser.CommandParser;
import co.fusionx.relay.internal.parser.DCCParser;
import co.fusionx.relay.internal.parser.ErrorCommandParser;
import co.fusionx.relay.internal.parser.ErrorParser;
import co.fusionx.relay.internal.parser.InitalTopicParser;
import co.fusionx.relay.internal.parser.InviteParser;
import co.fusionx.relay.internal.parser.JoinParser;
import co.fusionx.relay.internal.parser.KickParser;
import co.fusionx.relay.internal.parser.ModeParser;
import co.fusionx.relay.internal.parser.MotdParser;
import co.fusionx.relay.internal.parser.NameParser;
import co.fusionx.relay.internal.parser.NickParser;
import co.fusionx.relay.internal.parser.NoticeParser;
import co.fusionx.relay.internal.parser.PartParser;
import co.fusionx.relay.internal.parser.PingParser;
import co.fusionx.relay.internal.parser.PongParser;
import co.fusionx.relay.internal.parser.PrivmsgParser;
import co.fusionx.relay.internal.parser.QuitParser;
import co.fusionx.relay.internal.parser.TopicChangeParser;
import co.fusionx.relay.internal.parser.WallopsParser;
import co.fusionx.relay.internal.sender.InternalPacketSender;
import co.fusionx.relay.internal.sender.InternalSender;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.sender.RelayServerSender;
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
public class RelayModule {

    private final ConnectionConfiguration mConfiguration;

    public RelayModule(final ConnectionConfiguration connectionConfiguration) {
        mConfiguration = connectionConfiguration;
    }

    // Base
    @Provides
    public ConnectionConfiguration provideConfiguration() {
        return mConfiguration;
    }

    @Provides
    public Set<CapCapability> provideCapabilitySet() {
        return new HashSet<>();
    }

    @Provides
    @Singleton
    public InternalQueryUserGroup provideUserGroup(final RelayQueryUserGroup group) {
        return group;
    }

    @Provides
    @Singleton
    public InternalServer provideServer(final RelayServer relayServer) {
        return relayServer;
    }

    @Provides
    @Singleton
    public InternalStatusManager provideStatusManager(final RelayStatusManager statusManager) {
        return statusManager;
    }

    @Provides
    @Singleton
    public InternalUserChannelGroup provideUserChannelGroup(final RelayUserChannelGroup group) {
        return group;
    }

    // Bus
    @Singleton
    @Provides
    public GenericBus<Event> provideSessionBus() {
        return new EventBus<>();
    }

    // Sender
    @Singleton
    @Provides
    public PacketSender provideBaseSender() {
        return new PacketSender(Executors.newCachedThreadPool());
    }

    @Provides
    public InternalSender provideInternalSender(final InternalPacketSender packetSender) {
        return packetSender;
    }

    @Provides
    public ServerSender provideServerSender(final RelayServerSender sender) {
        return sender;
    }

    // Parser
    @Provides
    @Singleton
    public Map<String, CommandParser> provideCommandParserMap(final InternalServer server,
            final InternalUserChannelGroup dao, final PacketSender sender,
            final RelayDCCManager dccManager, final InternalQueryUserGroup queryManager) {
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
            final InternalServer server, final InternalUserChannelGroup dao,
            final InternalQueryUserGroup queryManager, final PacketSender sender) {
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