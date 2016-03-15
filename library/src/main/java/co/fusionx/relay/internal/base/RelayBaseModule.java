package co.fusionx.relay.internal.base;

import javax.inject.Singleton;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.internal.sender.BaseSender;
import co.fusionx.relay.internal.sender.RelayBaseSender;
import co.fusionx.relay.internal.sender.RelayServerSender;
import co.fusionx.relay.sender.ServerSender;
import dagger.Module;
import dagger.Provides;

@Module(injects = {
        RelayIRCConnection.class, RelayServer.class
})
public class RelayBaseModule {

    private final ServerConfiguration mConfiguration;

    public RelayBaseModule(final ServerConfiguration serverConfiguration) {
        mConfiguration = serverConfiguration;
    }

    @Provides
    public ServerConfiguration provideConfiguration() {
        return mConfiguration;
    }

    @Singleton
    @Provides
    public BaseSender provideBaseSender() {
        return new RelayBaseSender();
    }

    @Provides
    public ServerSender provideServerSender(final BaseSender sender) {
        return new RelayServerSender(sender);
    }
}