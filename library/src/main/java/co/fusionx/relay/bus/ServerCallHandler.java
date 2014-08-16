package co.fusionx.relay.bus;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import android.os.Handler;
import android.util.Base64;

import java.io.BufferedWriter;
import java.io.IOException;

import co.fusionx.relay.QueryUser;
import co.fusionx.relay.RelayChannel;
import co.fusionx.relay.RelayQueryUser;
import co.fusionx.relay.RelayServer;
import co.fusionx.relay.RelayUserChannelInterface;
import co.fusionx.relay.call.Call;
import co.fusionx.relay.call.channel.ChannelActionCall;
import co.fusionx.relay.call.channel.ChannelKickCall;
import co.fusionx.relay.call.channel.ChannelMessageCall;
import co.fusionx.relay.call.channel.ChannelPartCall;
import co.fusionx.relay.call.channel.ChannelTopicCall;
import co.fusionx.relay.call.server.JoinCall;
import co.fusionx.relay.call.server.ModeCall;
import co.fusionx.relay.call.server.NickChangeCall;
import co.fusionx.relay.call.server.RawCall;
import co.fusionx.relay.call.server.WhoisCall;
import co.fusionx.relay.call.user.PrivateActionCall;
import co.fusionx.relay.call.user.PrivateMessageCall;
import co.fusionx.relay.event.channel.ChannelActionEvent;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelMessageEvent;
import co.fusionx.relay.event.query.QueryActionSelfEvent;
import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.event.query.QueryMessageSelfEvent;
import co.fusionx.relay.event.server.NewPrivateMessageEvent;
import co.fusionx.relay.event.server.PrivateMessageClosedEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.misc.RelayConfigurationProvider;
import co.fusionx.relay.util.Utils;

public class ServerCallHandler {

    private final RelayServer mServer;

    private final Handler mCallHandler;

    private final RelayUserChannelInterface mUserChannelInterface;

    private BufferedWriter mBufferedWriter;

    public ServerCallHandler(final RelayServer server, final Handler callHandler) {
        mServer = server;
        mCallHandler = callHandler;

        mUserChannelInterface = server.getUserChannelInterface();
    }

    void writeLineToServer(final String line) {
        try {
            mBufferedWriter.write(line + "\r\n");
            mBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void post(final Call call) {
        mCallHandler.post(() -> postImmediately(call));
    }

    public void postImmediately(final Call call) {
        writeLineToServer(call.getLineToSendServer());
    }

    public void onOutputStreamCreated(final BufferedWriter writer) {
        mBufferedWriter = writer;
    }

    public void onConnectionTerminated() {
        mBufferedWriter = null;
    }

    public void pongServer(final String absoluteURL) {
        writeLineToServer("PONG " + absoluteURL);
    }

    public void sendServerPassword(final String password) {
        writeLineToServer("PASS " + password);
    }

    public void sendNickServPassword(final String password) {
        writeLineToServer("NICKSERV IDENTIFY " + password);
    }

    public void sendSupportedCAP() {
        writeLineToServer("CAP LS");
    }

    public void sendEndCap() {
        writeLineToServer("CAP END");
    }

    public void requestSasl() {
        writeLineToServer("CAP REQ : sasl multi-prefix");
    }

    public void sendPlainSaslAuthentication() {
        writeLineToServer("AUTHENTICATE PLAIN");
    }

    public void sendSaslAuthentication(final String saslUsername, final String saslPassword) {
        final String authentication = saslUsername + "\0" + saslUsername + "\0" + saslPassword;
        final String encoded = Base64.encodeToString(authentication.getBytes(), Base64.DEFAULT);
        writeLineToServer("AUTHENTICATE " + encoded);
    }

    public void sendMode(final String channelName, final String destination, final String mode) {
        post(new ModeCall(channelName, destination, mode));
    }

    public void sendUserWhois(final String nick) {
        post(new WhoisCall(nick));
    }

    public void sendRawLine(final String rawLine) {
        post(new RawCall(rawLine));
    }

    public void sendMessageToQueryUser(final String nick, final String message) {
        if (Utils.isNotEmpty(message)) {
            post(new PrivateMessageCall(nick, message));
        }

        sendSelfEventToQueryUser(user -> new QueryMessageSelfEvent(user, mServer.getUser(),
                message), nick, message, false);
    }

    public void sendActionToQueryUser(final String nick, final String action) {
        if (Utils.isNotEmpty(action)) {
            post(new PrivateActionCall(nick, action));
        }

        sendSelfEventToQueryUser(user -> new QueryActionSelfEvent(user, mServer.getUser(),
                action), nick, action, true);
    }

    private void sendSelfEventToQueryUser(final Function<RelayQueryUser, QueryEvent> function,
            final String nick, final String message, final boolean action) {
        final Optional<RelayQueryUser> optional = mUserChannelInterface.getQueryUser(nick);
        if (optional.isPresent()) {
            final RelayQueryUser user = optional.get();
            mServer.getServerEventBus().postAndStoreEvent(new NewPrivateMessageEvent(user));

            if (Utils.isNotEmpty(message)) {
                mServer.getServerEventBus().postAndStoreEvent(function.apply(user), user);
            }
        } else {
            final RelayQueryUser user = mUserChannelInterface
                    .addQueryUser(nick, message, action, true);
            mServer.getServerEventBus().postAndStoreEvent(new NewPrivateMessageEvent(user));
        }
    }

    public void sendNickChange(final String newNick) {
        post(new NickChangeCall(newNick));
    }

    public void sendPart(final String channelName) {
        post(new ChannelPartCall(channelName,
                RelayConfigurationProvider.getPreferences().getPartReason()));
    }

    public void sendCloseQuery(final QueryUser rawUser) {
        if (!(rawUser instanceof RelayQueryUser)) {
            // TODO - this is invalid and unexpected. What should be done here?
            return;
        }
        final RelayQueryUser user = (RelayQueryUser) rawUser;
        mUserChannelInterface.removeQueryUser(user);

        if (RelayConfigurationProvider.getPreferences().isSelfEventHidden()) {
            return;
        }
        final ServerEvent event = new PrivateMessageClosedEvent(user);
        mServer.getServerEventBus().postAndStoreEvent(event);
    }

    public void sendJoin(final String channelName) {
        post(new JoinCall(channelName));
    }

    public void sendMessageToChannel(final String channelName, final String message) {
        post(new ChannelMessageCall(channelName, message));

        sendChannelSelfMessage(channel -> new ChannelMessageEvent(channel, message,
                mServer.getUser()), channelName);
    }

    public void sendActionToChannel(final String channelName, final String action) {
        post(new ChannelActionCall(channelName, action));

        sendChannelSelfMessage(channel -> new ChannelActionEvent(channel, action,
                mServer.getUser()), channelName);
    }

    private void sendChannelSelfMessage(final Function<RelayChannel, ChannelEvent> function,
            final String channelName) {
        if (RelayConfigurationProvider.getPreferences().isSelfEventHidden()) {
            return;
        }

        final Optional<RelayChannel> optional = mUserChannelInterface.getChannel(channelName);
        if (optional.isPresent()) {
            final RelayChannel channel = optional.get();
            mServer.getServerEventBus().postAndStoreEvent(function.apply(channel), channel);
        } else {
            // TODO - some sort of logging should be done here
        }
    }

    public void sendKick(final String channelName, final String nick, final String reason) {
        post(new ChannelKickCall(channelName, nick, reason));
    }

    public void sendTopic(final String channelName, final String newTopic) {
        post(new ChannelTopicCall(channelName, newTopic));
    }
}