package co.fusionx.relay.internal.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.internal.parser.connection.ConnectionParser;
import co.fusionx.relay.internal.parser.main.ServerLineParser;
import co.fusionx.relay.internal.sender.BaseSender;
import co.fusionx.relay.internal.sender.RelayCapSender;
import co.fusionx.relay.internal.sender.RelayInternalSender;
import co.fusionx.relay.internal.sender.RelayServerSender;
import co.fusionx.relay.util.SocketUtils;
import co.fusionx.relay.util.Utils;

import static co.fusionx.relay.internal.parser.connection.ConnectionParser.ConnectionLineParseStatus;
import static co.fusionx.relay.internal.parser.connection.ConnectionParser.ParseStatus;
import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

@Singleton
public class RelayIRCConnection {

    private final ServerConfiguration mServerConfiguration;

    private final StatusManager mStatusManager;

    private final RelayUserChannelDao mDao;

    private final BaseSender mBaseSender;

    private final RelayInternalSender mInternalSender;

    private final RelayCapSender mCapSender;

    private final ConnectionParser mConnectionParser;

    private final ServerLineParser mLineParser;

    private final RelayServerSender mServerSender;

    private Socket mSocket;

    private boolean mStopped;

    @Inject
    RelayIRCConnection(final ServerConfiguration serverConfiguration,
            final StatusManager statusManager, final RelayUserChannelDao dao,
            final ConnectionParser connectionParser, final ServerLineParser lineParser,
            final BaseSender baseSender) {
        mServerConfiguration = serverConfiguration;
        mStatusManager = statusManager;
        mDao = dao;
        mConnectionParser = connectionParser;
        mLineParser = lineParser;
        mBaseSender = baseSender;

        mServerSender = new RelayServerSender(baseSender);
        mInternalSender = new RelayInternalSender(baseSender);
        mCapSender = new RelayCapSender(baseSender);
    }

    void connect() {
        String disconnectMessage = "";
        try {
            connectQuietly();
        } catch (final IOException ex) {
            disconnectMessage = ex.getMessage();
        }

        if (mStopped) {
            mStatusManager.onStopped();
            closeSocket();
        } else {
            mStatusManager.onDisconnected(disconnectMessage, mStatusManager.isReconnectNeeded());
            closeSocket();
            mDao.onConnectionTerminated();
        }
    }

    private void connectQuietly() throws IOException {
        mSocket = SocketUtils.openSocketConnection(mServerConfiguration);

        final BufferedReader socketReader = SocketUtils.getSocketBufferedReader(mSocket);
        final BufferedWriter socketWriter = SocketUtils.getSocketBufferedWriter(mSocket);
        mBaseSender.onOutputStreamCreated(socketWriter);

        // We are now in the phase where we can say we are connecting to the server
        mStatusManager.onConnecting();

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
        if (Utils.isNotEmpty(mServerConfiguration.getServerPassword())) {
            mInternalSender.sendServerPassword(mServerConfiguration.getServerPassword());
        }
        mServerSender.sendNick(mServerConfiguration.getNickStorage().getFirst());
        mInternalSender.sendUser(mServerConfiguration.getServerUserName(),
                Utils.returnNonEmpty(mServerConfiguration.getRealName(), "RelayUser"));
    }

    private void sendPostRegisterMessages() {
        // Identifies with NickServ if the password exists
        if (Utils.isNotEmpty(mServerConfiguration.getNickservPassword())) {
            mInternalSender.sendNickServPassword(mServerConfiguration.getNickservPassword());
        }
    }

    private void onStartParsing(final String nick, final BufferedReader reader) throws IOException {
        mDao.getUser().setNick(nick);
        sendPostRegisterMessages();

        mStatusManager.onConnected();

        // Loops forever until broken
        mLineParser.parseMain(reader);
    }

    void disconnect() {
        mStopped = true;
        mInternalSender.quitServer(getPreferences().getQuitReason());
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