package co.fusionx.relay.internal.base;

import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import javax.inject.Inject;

import co.fusionx.relay.configuration.SessionConfiguration;
import co.fusionx.relay.internal.core.InternalStatusManager;
import co.fusionx.relay.internal.sender.InternalSender;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.sender.RelayInternalSender;
import co.fusionx.relay.provider.SettingsProvider;

public class IRCConnection {

    private final RegistrationFacade mRegistrationFacade;

    private final BufferedInputParser mBufferedInputParser;

    private final PacketSender mPacketSender;

    private final SocketConnection mSocketConnection;

    private final SettingsProvider mSettingsProvider;

    private final InternalSender mInternalSender;

    private boolean mStopped;

    @Inject
    IRCConnection(final SettingsProvider settingsProvider,
            final SocketConnection socketConnection,
            final RegistrationFacade registrationFacade,
            final BufferedInputParser bufferedInputParser,
            final PacketSender sender) {
        mRegistrationFacade = registrationFacade;
        mBufferedInputParser = bufferedInputParser;

        mPacketSender = sender;

        mSocketConnection = socketConnection;
        mSettingsProvider = settingsProvider;

        mInternalSender = new RelayInternalSender(sender);
    }

    public boolean isStopped() {
        return mStopped;
    }

    String connect() {
        String disconnectMessage = null;
        try {
            connectQuietly();
        } catch (final IOException ex) {
            disconnectMessage = ex.getMessage();
        }
        return disconnectMessage;
    }

    void disconnect() {
        mStopped = true;
        mInternalSender.quitServer(mSettingsProvider.getQuitReason());
    }

    void close() {
        mSocketConnection.close();
    }

    private void connectQuietly() throws IOException {
        // Open the socket
        final Pair<BufferedReader, BufferedWriter> pair = mSocketConnection.open();

        // And tell our packet sender about the new writer
        mPacketSender.updateBufferedWriter(pair.getRight());

        // Send the registration messages to the server
        mRegistrationFacade.registerConnection();

        // Loop forever until broken
        mBufferedInputParser.parseMain(pair.getLeft());
    }
}