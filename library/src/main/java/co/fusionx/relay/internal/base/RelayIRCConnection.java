package co.fusionx.relay.internal.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.fusionx.relay.core.ConnectionConfiguration;
import co.fusionx.relay.internal.core.InternalStatusManager;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.parser.connection.ConnectionParser;
import co.fusionx.relay.internal.parser.main.ServerLineParser;
import co.fusionx.relay.internal.sender.base.RelayServerSender;
import co.fusionx.relay.internal.sender.packet.CapPacketSender;
import co.fusionx.relay.internal.sender.packet.InternalPacketSender;
import co.fusionx.relay.internal.sender.packet.PacketSender;
import co.fusionx.relay.util.SocketUtils;
import co.fusionx.relay.util.Utils;

import static co.fusionx.relay.internal.parser.connection.ConnectionParser.ConnectionLineParseStatus;
import static co.fusionx.relay.internal.parser.connection.ConnectionParser.ParseStatus;
import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

@Singleton
public class RelayIRCConnection {

    private final ConnectionConfiguration mConnectionConfiguration;

    private final InternalStatusManager mInternalStatusManager;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final ConnectionParser mConnectionParser;

    private final ServerLineParser mLineParser;

    private final RelayServerSender mServerSender;

    private final PacketSender mPacketSender;

    private final InternalPacketSender mInternalSender;

    private final CapPacketSender mCapSender;

    private Socket mSocket;

    private boolean mStopped;

    @Inject
    RelayIRCConnection(final ConnectionConfiguration connectionConfiguration,
            final InternalUserChannelGroup userChannelGroup,
            final InternalStatusManager internalStatusManager,
            final ConnectionParser connectionParser, final ServerLineParser lineParser,
            final PacketSender sender, final RelayServerSender serverSender,
            final InternalPacketSender internalPacketSender,
            final CapPacketSender capPacketSender) {
        mConnectionConfiguration = connectionConfiguration;
        mUserChannelGroup = userChannelGroup;
        mInternalStatusManager = internalStatusManager;

        mConnectionParser = connectionParser;
        mLineParser = lineParser;

        mPacketSender = sender;
        mServerSender = serverSender;
        mInternalSender = internalPacketSender;
        mCapSender = capPacketSender;
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
        mInternalSender.quitServer(getPreferences().getQuitReason());
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
        if (status.getStatus() == ParseStatus.NICK && Utils.isNotEmpty(status.getNick())) {
            onStartParsing(status.getNick(), socketReader);
        }
    }

    private void sendRegistrationMessages() {
        // By sending this line, the server *should* wait until we end the CAP negotiation
        // That is if the server supports IRCv3
        mCapSender.sendLs();

        // Follow RFC2812's recommended order of sending - PASS -> NICK -> USER
        if (Utils.isNotEmpty(mConnectionConfiguration.getServerPassword())) {
            mInternalSender.sendServerPassword(mConnectionConfiguration.getServerPassword());
        }
        mServerSender.sendNick(mConnectionConfiguration.getNickStorage().getFirst());
        mInternalSender.sendUser(mConnectionConfiguration.getServerUserName(),
                Utils.returnNonEmpty(mConnectionConfiguration.getRealName(), "RelayUser"));
    }

    private void sendPostRegisterMessages() {
        // Identifies with NickServ if the password exists
        if (Utils.isNotEmpty(mConnectionConfiguration.getNickservPassword())) {
            mInternalSender.sendNickServPassword(mConnectionConfiguration.getNickservPassword());
        }
    }

    private void onStartParsing(final String nick, final BufferedReader reader) throws IOException {
        mUserChannelGroup.getUser().setNick(nick);
        sendPostRegisterMessages();

        mInternalStatusManager.onConnected();

        // Loops forever until broken
        mLineParser.parseMain(reader);
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