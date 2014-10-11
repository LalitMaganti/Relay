package co.fusionx.relay.internal.base;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import javax.inject.Inject;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.provider.SettingsProvider;
import co.fusionx.relay.internal.core.InternalStatusManager;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.parser.BufferedInputParser;
import co.fusionx.relay.internal.parser.ConnectionParser;
import co.fusionx.relay.internal.sender.CapSender;
import co.fusionx.relay.internal.sender.InternalSender;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.sender.RelayServerSender;
import co.fusionx.relay.util.SocketUtils;
import co.fusionx.relay.util.Utils;

import static co.fusionx.relay.internal.parser.ConnectionParser.ConnectionLineParseStatus;
import static co.fusionx.relay.internal.parser.ConnectionParser.ParseStatus;

public class RelayIRCConnection {

    private final InternalStatusManager mInternalStatusManager;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final ConnectionParser mConnectionParser;

    private final BufferedInputParser mBufferedInputParser;

    private final RelayServerSender mServerSender;

    private final PacketSender mPacketSender;

    private final InternalSender mInternalSender;

    private final CapSender mCapSender;

    private final ConnectionConfiguration mConnectionConfiguration;

    private final SettingsProvider mSettingsProvider;

    private Socket mSocket;

    private boolean mStopped;

    @Inject
    RelayIRCConnection(final ConnectionConfiguration connectionConfiguration,
            final SettingsProvider settingsProvider,
            final InternalUserChannelGroup userChannelGroup,
            final InternalStatusManager internalStatusManager,
            final ConnectionParser connectionParser,
            final BufferedInputParser bufferedInputParser,
            final PacketSender sender,
            final RelayServerSender serverSender,
            final InternalSender internalSender,
            final CapSender capSender) {
        mConnectionConfiguration = connectionConfiguration;
        mSettingsProvider = settingsProvider;
        mUserChannelGroup = userChannelGroup;
        mInternalStatusManager = internalStatusManager;

        mConnectionParser = connectionParser;
        mBufferedInputParser = bufferedInputParser;

        mPacketSender = sender;
        mServerSender = serverSender;
        mInternalSender = internalSender;
        mCapSender = capSender;
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
        sendRegistrationMessages();

        // Setup the connection parser and start parsing
        final ConnectionLineParseStatus status = mConnectionParser.parseConnect(socketReader);

        // This nick may well be different from any of the nicks in storage - get the
        // *official* nick from the server itself and use it
        // If the nick is null then we have no hope of progressing
        if (status.getStatus() == ParseStatus.NICK && StringUtils.isNotEmpty(status.getNick())) {
            onStartParsing(status.getNick(), socketReader);
        }
    }

    private void sendRegistrationMessages() {
        // By sending this line, the server *should* wait until we end the CAP negotiation
        // That is if the server supports IRCv3
        mCapSender.sendLs();

        // Follow RFC2812's recommended order of sending - PASS -> NICK -> USER
        if (StringUtils.isNotEmpty(mConnectionConfiguration.getServerPassword())) {
            mInternalSender.sendServerPassword(mConnectionConfiguration.getServerPassword());
        }
        mServerSender.sendNick(mConnectionConfiguration.getNickProvider().getFirst());
        mInternalSender.sendUser(mConnectionConfiguration.getServerUserName(),
                Utils.returnNonEmpty(mConnectionConfiguration.getRealName(), "RelayUser"));
    }

    private void sendPostRegisterMessages() {
        // Identifies with NickServ if the password exists
        if (StringUtils.isNotEmpty(mConnectionConfiguration.getNickservPassword())) {
            mInternalSender.sendNickServPassword(mConnectionConfiguration.getNickservPassword());
        }
    }

    private void onStartParsing(final String nick, final BufferedReader reader) throws IOException {
        mUserChannelGroup.getUser().setNick(nick);
        sendPostRegisterMessages();

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