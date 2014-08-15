package co.fusionx.relay;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

import co.fusionx.relay.bus.ServerCallHandler;
import co.fusionx.relay.call.server.JoinCall;
import co.fusionx.relay.call.server.NickChangeCall;
import co.fusionx.relay.call.server.QuitCall;
import co.fusionx.relay.call.server.UserCall;
import co.fusionx.relay.event.channel.ChannelConnectEvent;
import co.fusionx.relay.event.channel.ChannelDisconnectEvent;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelStopEvent;
import co.fusionx.relay.event.query.QueryConnectEvent;
import co.fusionx.relay.event.query.QueryDisconnectEvent;
import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.event.query.QueryStopEvent;
import co.fusionx.relay.event.server.ConnectEvent;
import co.fusionx.relay.event.server.ConnectingEvent;
import co.fusionx.relay.event.server.DisconnectEvent;
import co.fusionx.relay.event.server.ReconnectEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.event.server.StopEvent;
import co.fusionx.relay.function.FluentIterables;
import co.fusionx.relay.misc.RelayConfigurationProvider;
import co.fusionx.relay.parser.ServerConnectionParser;
import co.fusionx.relay.parser.ServerLineParser;
import co.fusionx.relay.util.SocketUtils;
import co.fusionx.relay.util.Utils;

public class ServerConnection {

    private final Handler mUiThreadHandler;

    private final Handler mCallHandler;

    private final ServerConfiguration mServerConfiguration;

    private final ServerCallHandler mServerCallHandler;

    private final RelayServer mServer;

    private Thread mMainThread;

    private ConnectionStatus mStatus = ConnectionStatus.DISCONNECTED;

    private Socket mSocket;

    private int mReconnectAttempts;

    private ServerLineParser mLineParser;

    private boolean mStopped;

    ServerConnection(final ServerConfiguration serverConfiguration, final Handler handler,
            final Collection<String> ignoreList) {
        mServerConfiguration = serverConfiguration;
        mUiThreadHandler = handler;

        final HandlerThread handlerThread = new HandlerThread("ServerCalls");
        handlerThread.start();
        mCallHandler = new Handler(handlerThread.getLooper());

        mServer = new RelayServer(serverConfiguration, this, mCallHandler, ignoreList);
        mServerCallHandler = mServer.getServerCallHandler();
    }

    void startConnection() {
        mMainThread = new Thread(() -> {
            try {
                connectToServer();
            } catch (final Exception ex) {
                mUiThreadHandler.post(() -> {
                    throw new RuntimeException(getCurrentLine(), ex);
                });
            }
        });
        mMainThread.start();
    }

    void stopConnection() {
        mCallHandler.post(() -> {
            if (mStatus == ConnectionStatus.CONNECTED) {
                mStopped = true;
                mServerCallHandler.postImmediately(new QuitCall(
                        RelayConfigurationProvider.getPreferences().getQuitReason()));
            } else if (mMainThread.isAlive()) {
                mMainThread.interrupt();
            }
            onStopped();
            closeSocket();
            mServer.onConnectionTerminated();
        });
    }

    RelayServer getServer() {
        return mServer;
    }

    ConnectionStatus getStatus() {
        return mStatus;
    }

    public String getCurrentLine() {
        if (mLineParser != null) {
            return mLineParser.getCurrentLine();
        }
        return "";
    }

    /**
     * Method which keeps trying to reconnect to the server the number of times specified and if
     * the user has not explicitly tried to disconnect
     */
    private void connectToServer() {
        connect();

        for (mReconnectAttempts = 0; !mStopped && isReconnectNeeded(); mReconnectAttempts++) {
            onReconnecting();

            try {
                Thread.sleep(5000);
            } catch (final InterruptedException e) {
                // This interrupt will *should* only ever occur if the user explicitly kills
                // reconnection
                return;
            }
            connect();
        }
        if (mStopped) {
            return;
        }
        onDisconnected("Disconnected from server (no reconnect pending).", false);
    }

    /**
     * Closes the socket if it is not already closed
     */
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

    private void connect() {
        String disconnectMessage = "";
        try {
            mSocket = SocketUtils.openSocketConnection(mServerConfiguration);

            final BufferedWriter socketWriter = SocketUtils.getSocketWriter(mSocket);
            mServer.onOutputStreamCreated(socketWriter);

            // We are now in the phase where we can say we are connecting to the server
            onConnecting();

            if (mServerConfiguration.isSaslAvailable()) {
                // By sending this line, the server *should* wait until we end the CAP stuff with
                // CAP END
                mServerCallHandler.sendSupportedCAP();
            }

            if (Utils.isNotEmpty(mServerConfiguration.getServerPassword())) {
                mServerCallHandler.sendServerPassword(mServerConfiguration.getServerPassword());
            }

            // Send NICK and USER lines to the server
            mServer.getServerCallHandler().post(new NickChangeCall(mServerConfiguration
                    .getNickStorage().getFirstChoiceNick()));
            mServer.getServerCallHandler().post(new UserCall(mServerConfiguration
                    .getServerUserName(), Utils.returnNonEmpty(mServerConfiguration.getRealName(),
                    "RelayUser")));

            final BufferedReader reader = SocketUtils.getSocketBufferedReader(mSocket);
            final ServerConnectionParser parser = new ServerConnectionParser(mServer,
                    mServerConfiguration, reader, mServerCallHandler);
            final String nick = parser.parseConnect();

            // This nick may well be different from any of the nicks in storage - get the
            // *official* nick from the server itself and use it
            // If the nick is null then we have no hope of progressing
            if (!TextUtils.isEmpty(nick)) {
                onStartParsing(nick, mServerCallHandler, reader);
            }
        } catch (final IOException ex) {
            // Usually occurs when WiFi/3G is turned off on the device - usually fruitless to try
            // to reconnect but hey ho
            disconnectMessage = ex.getMessage();
        }

        // If it was stopped then this cleanup would have already been performed
        if (mStopped) {
            return;
        }
        onDisconnected(disconnectMessage, isReconnectNeeded());
        closeSocket();
        mServer.onConnectionTerminated();
    }

    private void onStartParsing(final String nick, final ServerCallHandler callHandler,
            final BufferedReader reader) throws IOException {
        // Since we are now connected, reset the reconnect attempts
        mReconnectAttempts = 0;
        mServer.getUser().setNick(nick);
        onConnected();

        // Identifies with NickServ if the password exists
        if (Utils.isNotEmpty(mServerConfiguration.getNickservPassword())) {
            callHandler.sendNickServPassword(mServerConfiguration.getNickservPassword());
        }

        final Collection<RelayChannel> channels = mServer.getUser().getChannels();
        if (channels.isEmpty()) {
            // Automatically join the channels specified in the configuration
            FluentIterables.forEach(FluentIterable.from(mServerConfiguration.getAutoJoinChannels())
                            .transform(JoinCall::new),
                    mServer.getServerCallHandler()::post);
        } else {
            FluentIterables.forEach(FluentIterable.from(channels)
                            .transform(Channel::getName)
                            .transform(JoinCall::new),
                    mServer.getServerCallHandler()::post);
        }

        // Initialise the parser used to parse any lines from the server
        mLineParser = new ServerLineParser(mServer);
        // Loops forever until broken
        mLineParser.parseMain(reader, callHandler);
    }

    private void onConnecting() {
        mStatus = ConnectionStatus.CONNECTING;

        mServer.getServerEventBus().postAndStoreEvent(new ConnectingEvent());
    }

    private void onReconnecting() {
        mStatus = ConnectionStatus.RECONNECTING;

        mServer.getServerEventBus().postAndStoreEvent(new ReconnectEvent());
    }

    private void onConnected() {
        onStatusChanged(ConnectionStatus.CONNECTED,
                ChannelConnectEvent::new,
                QueryConnectEvent::new,
                server -> new ConnectEvent(mServerConfiguration.getUrl()));
    }

    private void onDisconnected(final String serverMessage, final boolean retryPending) {
        onStatusChanged(ConnectionStatus.DISCONNECTED,
                channel -> new ChannelDisconnectEvent(channel, serverMessage),
                user -> new QueryDisconnectEvent(user, serverMessage),
                server -> new DisconnectEvent(serverMessage, retryPending));
    }

    private void onStopped() {
        onStatusChanged(ConnectionStatus.STOPPED,
                ChannelStopEvent::new,
                QueryStopEvent::new,
                server -> new StopEvent());
    }

    private void onStatusChanged(final ConnectionStatus status,
            final Function<RelayChannel, ChannelEvent> channelFunction,
            final Function<RelayQueryUser, QueryEvent> queryFunction,
            final Function<Server, ServerEvent> serverFunction) {
        mStatus = status;

        for (final RelayChannel channel : mServer.getUser().getChannels()) {
            channel.postAndStoreEvent(channelFunction.apply(channel));
        }

        for (final RelayQueryUser user : mServer.getUserChannelInterface().getQueryUsers()) {
            user.postAndStoreEvent(queryFunction.apply(user));
        }

        mServer.getServerEventBus().postAndStoreEvent(serverFunction.apply(mServer));
    }

    private boolean isReconnectNeeded() {
        return mReconnectAttempts < RelayConfigurationProvider.getPreferences()
                .getReconnectAttemptsCount();
    }
}