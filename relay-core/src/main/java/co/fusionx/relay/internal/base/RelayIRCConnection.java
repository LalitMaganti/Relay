package co.fusionx.relay.internal.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import javax.inject.Inject;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.configuration.SessionConfiguration;
import co.fusionx.relay.internal.core.InternalStatusManager;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.parser.BufferedInputParser;
import co.fusionx.relay.internal.sender.InternalSender;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.sender.RelayInternalSender;
import co.fusionx.relay.provider.SettingsProvider;
import co.fusionx.relay.util.SocketUtils;

public class RelayIRCConnection {

    private final InternalStatusManager mInternalStatusManager;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final RegistrationFacade mRegistrationFacade;

    private final BufferedInputParser mBufferedInputParser;

    private final PacketSender mPacketSender;

    private final ConnectionConfiguration mConnectionConfiguration;

    private final SettingsProvider mSettingsProvider;

    private final InternalSender mInternalSender;

    private Socket mSocket;

    private boolean mStopped;

    @Inject
    RelayIRCConnection(final SessionConfiguration sessionConfiguration,
            final InternalUserChannelGroup userChannelGroup,
            final InternalStatusManager internalStatusManager,
            final RegistrationFacade registrationFacade,
            final BufferedInputParser bufferedInputParser,
            final PacketSender sender) {
        mUserChannelGroup = userChannelGroup;
        mInternalStatusManager = internalStatusManager;

        mRegistrationFacade = registrationFacade;
        mBufferedInputParser = bufferedInputParser;

        mPacketSender = sender;

        mConnectionConfiguration = sessionConfiguration.getConnectionConfiguration();
        mSettingsProvider = sessionConfiguration.getSettingsProvider();

        mInternalSender = new RelayInternalSender(sender);
    }

    public boolean isStopped() {
        return mStopped;
    }

    void connect() {
        String disconnectMessage = "";
        try {
            connectQuietly();
        } catch (final IOException ex) {
            disconnectMessage = ex.getMessage();
        }

        if (mStopped) {
            mInternalStatusManager.onStopped();
            closeSocket();
        } else {
            mInternalStatusManager.onDisconnected(disconnectMessage,
                    mInternalStatusManager.isReconnectNeeded());
            closeSocket();
            mUserChannelGroup.onConnectionTerminated();
        }
    }

    void disconnect() {
        mStopped = true;
        mInternalSender.quitServer(mSettingsProvider.getQuitReason());
    }

    private void connectQuietly() throws IOException {
        mSocket = SocketUtils.openSocketConnection(mConnectionConfiguration);

        final BufferedReader socketReader = SocketUtils.getSocketBufferedReader(mSocket);
        final BufferedWriter socketWriter = SocketUtils.getSocketBufferedWriter(mSocket);
        mPacketSender.onOutputStreamCreated(socketWriter);

        // We are now in the phase where we can say we are connecting to the server
        mInternalStatusManager.onConnecting();

        // Send the registration messages to the server
        mRegistrationFacade.registerConnection();

        // Setup the connection parser and start parsing
        // final ConnectionLineParseStatus status = mConnectionParser.parseConnect(socketReader);

        // This nick may well be different from any of the nicks in storage - get the
        // *official* nick from the server itself and use it
        // If the nick is null then we have no hope of progressing
        /*if (status.getStatus() == ParseStatus.NICK && StringUtils.isNotEmpty(status.getNick())) {
            onStartParsing(status.getNick(), socketReader);
        }*/
    }

    private void onStartParsing(final String nick, final BufferedReader reader) throws IOException {
        mUserChannelGroup.getUser().setNick(nick);
        mRegistrationFacade.postRegister();

        mInternalStatusManager.onConnected();

        // Loops forever until broken
        mBufferedInputParser.parseMain(reader);
    }

    private void closeSocket() {
        if (mSocket == null || mSocket.isClosed()) {
            mSocket = null;
            return;
        }

        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSocket = null;
    }
}