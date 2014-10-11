package co.fusionx.relay.internal.provider;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.constant.Capability;
import co.fusionx.relay.configuration.SessionConfiguration;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.base.RelayIRCConnection;
import co.fusionx.relay.internal.base.RelayQueryUserGroup;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelaySession;
import co.fusionx.relay.internal.base.RelayStatusManager;
import co.fusionx.relay.internal.base.RelayUserChannelGroup;
import co.fusionx.relay.internal.bus.DefaultBus;
import co.fusionx.relay.internal.bus.EventBus;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalStatusManager;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.core.Postable;
import co.fusionx.relay.internal.sender.InternalSender;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.sender.RelayInternalSender;
import co.fusionx.relay.internal.sender.RelayServerSender;
import co.fusionx.relay.parser.InputParser;
import co.fusionx.relay.parser.ParserProvider;
import co.fusionx.relay.provider.SettingsProvider;
import co.fusionx.relay.sender.ServerSender;
import dagger.Module;
import dagger.Provides;

@Module(injects = {
        RelaySession.class, RelayIRCConnection.class, RelayServer.class,
        RelayUserChannelGroup.class
})
public class RelayProvider {

    private final SessionConfiguration mConfiguration;

    public RelayProvider(final SessionConfiguration sessionConfiguration) {
        mConfiguration = sessionConfiguration;
    }

    // Base
    @Provides
    public SessionConfiguration provideConfiguration() {
        return mConfiguration;
    }

    @Provides
    public SettingsProvider provideSettingsProvider(final SessionConfiguration configuration) {
        return configuration.getSettingsProvider();
    }

    @Provides
    public ConnectionConfiguration provideConnectionConfig(
            final SessionConfiguration config) {
        return config.getConnectionConfiguration();
    }

    @Provides
    public Set<Capability> provideCapabilitySet() {
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
    public EventBus<Event> provideSessionBus() {
        return new DefaultBus<>();
    }

    @Provides
    public Postable<Event> providePostableSessionBus(final EventBus<Event> bus) {
        return bus;
    }

    // Sender
    @Singleton
    @Provides
    public PacketSender provideBaseSender(final SettingsProvider settingsProvider) {
        return new PacketSender(settingsProvider, Executors.newCachedThreadPool());
    }

    @Provides
    public InternalSender provideInternalSender(final RelayInternalSender packetSender) {
        return packetSender;
    }

    @Provides
    public ServerSender provideServerSender(final RelayServerSender sender) {
        return sender;
    }

    // Parser
    @Provides
    public ParserProvider provideParserFactory(final ParserObserverProvider provider) {
        return new CoreParserProvider(provider);
    }

    @Provides
    public InputParser provideInputParser(final ParserProvider parserProvider) {
        return new InputParser(parserProvider);
    }

    /*@Provides
    @Singleton
    public Map<String, CommandParser> provideCommandParserMap(final InternalServer server,
            final InternalUserChannelGroup userChannelGroup, final PacketSender sender,
            final InternalDCCManager dccManager, final InternalQueryUserGroup queryUserGroup) {
        final DCCParser dccParser = new DCCParser(server, dccManager);
        final CTCPParser ctcpParser = new CTCPParser(server, userChannelGroup, queryUserGroup,
                sender, dccParser);

        final Map<String, CommandParser> parserMap = new HashMap<>();

        // Core RFC parsers
        parserMap.put(PING, new PingParser(sender));
        parserMap.put(ERROR, new ErrorCommandParser());

        // RFC parsers
        parserMap.put(JOIN, new JoinStateChanger(server, userChannelGroup));
        parserMap.put(PRIVMSG, new PrivmsgParser(server, userChannelGroup, queryUserGroup,
                ctcpParser));
        parserMap.put(NOTICE, new NoticeStateChanger(server, userChannelGroup, queryUserGroup,
                ctcpParser));
        parserMap.put(PART, new PartParser(server, userChannelGroup, queryUserGroup));
        parserMap.put(MODE, new ModeParser(server, userChannelGroup));
        parserMap.put(QUIT, new QuitParser(userChannelGroup, queryUserGroup));
        parserMap.put(NICK, new NickStateChanger(server, userChannelGroup));
        parserMap.put(TOPIC, new TopicStateChanger(server, userChannelGroup));
        parserMap.put(KICK, new KickParser(server, userChannelGroup, queryUserGroup));
        parserMap.put(INVITE, new InviteParser(server));
        parserMap.put(PONG, new PongParser());
        parserMap.put(WALLOPS, new WallopsParser(server));

        // Optional IRCv3 parsers
        if (server.getCapabilities().contains(CapCapability.ACCOUNTNOTIFY)) {
            parserMap.put(CommandConstants.ACCOUNT, new AccountParser());
        }
        if (server.getCapabilities().contains(CapCapability.AWAYNOTIFY)) {
            parserMap.put(CommandConstants.AWAY, new AwayParser());
        }

        return parserMap;
    }

    @Provides
    @Singleton
    public SparseArray<ReplyCodeParser> provideCodeParserMap(final InternalServer server,
            final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryUserGroup) {
        final SparseArray<ReplyCodeParser> parserMap = new SparseArray<>();

        final InitalTopicParser topicChangeParser = new InitalTopicParser(server, userChannelGroup,
                null);
        parserMap.put(ReplyCodes.RPL_TOPIC, topicChangeParser);
        parserMap.put(ReplyCodes.RPL_TOPICWHOTIME, topicChangeParser);

        final NameStateChanger nameParser = new NameStateChanger(server, userChannelGroup);
        parserMap.put(ReplyCodes.RPL_NAMREPLY, nameParser);
        parserMap.put(ReplyCodes.RPL_ENDOFNAMES, nameParser);

        final MotdParser motdParser = new MotdParser(server, userChannelGroup, queryUserGroup);
        parserMap.put(ReplyCodes.RPL_MOTDSTART, motdParser);
        parserMap.put(ReplyCodes.RPL_MOTD, motdParser);
        parserMap.put(ReplyCodes.RPL_ENDOFMOTD, motdParser);

        final ErrorParser errorParser = new ErrorParser(server, userChannelGroup, null);
        parserMap.put(ReplyCodes.ERR_NOSUCHNICK, errorParser);
        parserMap.put(ReplyCodes.ERR_NICKNAMEINUSE, errorParser);

        return parserMap;
    }*/
}