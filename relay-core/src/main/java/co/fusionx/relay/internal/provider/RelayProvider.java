package co.fusionx.relay.internal.provider;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.configuration.SessionConfiguration;
import co.fusionx.relay.constant.Capability;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.base.IRCConnection;
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
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.sender.RelayServerSender;
import co.fusionx.relay.parser.InputParser;
import co.fusionx.relay.parser.ParserProvider;
import co.fusionx.relay.provider.DebuggingProvider;
import co.fusionx.relay.provider.SettingsProvider;
import co.fusionx.relay.sender.ServerSender;
import dagger.Module;
import dagger.Provides;

@Module(injects = {
        RelaySession.class, IRCConnection.class, RelayServer.class,
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
    public ConnectionConfiguration provideConnectionConfig(final SessionConfiguration config) {
        return config.getConnectionConfiguration();
    }

    @Provides
    public DebuggingProvider provideDebbugingProvider(final SessionConfiguration configuration) {
        return configuration.getDebuggingProvider();
    }

    @Singleton
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
    public PacketSender provideBaseSender(final DebuggingProvider debuggingProvider) {
        return new PacketSender(debuggingProvider, Executors.newCachedThreadPool());
    }

    @Provides
    public ServerSender provideServerSender(final RelayServerSender sender) {
        return sender;
    }

    // Parser
    @Provides
    public ParserProvider provideParserFactory(final ParserObserverProvider provider) {
        return new DefaultParserProvider(provider);
    }

    @Provides
    public InputParser provideInputParser(final ParserProvider parserProvider) {
        return new InputParser(parserProvider);
    }
}