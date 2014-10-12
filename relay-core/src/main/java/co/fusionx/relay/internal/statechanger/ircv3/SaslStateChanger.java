package co.fusionx.relay.internal.statechanger.ircv3;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.sender.CapSender;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.parser.ircv3.SaslParser;

public class SaslStateChanger implements SaslParser.SaslObserver {

    private final ConnectionConfiguration mConnectionConfiguration;

    private final InternalServer mServer;

    private final CapSender mCapSender;

    public SaslStateChanger(final ConnectionConfiguration configuration,
            final InternalServer server,
            final PacketSender sender) {
        mConnectionConfiguration = configuration;
        mServer = server;

        mCapSender = new CapSender(sender);
    }

    @Override
    public void onAuthenticatePlus() {
        final String username = mConnectionConfiguration.getSaslUsername();
        final String password = mConnectionConfiguration.getSaslPassword();
        mCapSender.sendSaslPlainAuthentication(username, password);
    }

    @Override
    public void onSuccess(final String message) {
        mServer.postEvent(new GenericServerEvent(mServer, message));
        mCapSender.sendEnd();
    }

    @Override
    public void onLoggedIn(final String message) {
        mServer.postEvent(new GenericServerEvent(mServer, message));
        mCapSender.sendEnd();
    }

    @Override
    public void onError(final String message) {
        mServer.postEvent(new GenericServerEvent(mServer, message));
        mCapSender.sendEnd();
    }
}