package co.fusionx.relay.internal.parser.connection;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import co.fusionx.relay.base.ConnectionConfiguration;
import co.fusionx.relay.event.server.NoticeEvent;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.constants.CommandConstants;
import co.fusionx.relay.internal.constants.ServerReplyCodes;
import co.fusionx.relay.internal.parser.connection.cap.CapParser;
import co.fusionx.relay.internal.sender.packet.PacketSender;
import co.fusionx.relay.internal.sender.packet.InternalPacketSender;
import co.fusionx.relay.misc.NickStorage;
import co.fusionx.relay.util.ParseUtils;

public class ConnectionParser {

    private final RelayServer mServer;

    private final ConnectionConfiguration mConfiguration;

    private final InternalPacketSender mInternalSender;

    private final CapParser mCapParser;

    private int mIndex;

    private int mSuffix;

    @Inject
    public ConnectionParser(final ConnectionConfiguration configuration, final RelayServer server,
            final PacketSender sender) {
        mConfiguration = configuration;
        mServer = server;

        mInternalSender = new InternalPacketSender(sender);
        mCapParser = new CapParser(server, sender);

        mIndex = 1;
        mSuffix = 1;
    }

    public ConnectionLineParseStatus parseConnect(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            final ConnectionLineParseStatus parseStatus = parseLine(line);
            if (parseStatus.getStatus() != ParseStatus.OTHER) {
                return parseStatus;
            }
        }
        return new ConnectionLineParseStatus(ParseStatus.ERROR, null);
    }

    ConnectionLineParseStatus parseLine(final String line) {
        // RFC2812 states that an empty line should be silently ignored
        if (TextUtils.isEmpty(line)) {
            return new ConnectionLineParseStatus(ParseStatus.OTHER, null);
        }

        final List<String> parsedArray = ParseUtils.splitRawLine(line, true);
        final String prefix = ParseUtils.extractAndRemovePrefix(parsedArray);
        final String command = parsedArray.remove(0);

        if (ParseUtils.isCommandCode(command)) {
            final int code = Integer.parseInt(command);
            return parseConnectionCode(parsedArray, code);
        } else {
            return parseConnectionCommand(parsedArray, prefix, command);
        }
    }

    private ConnectionLineParseStatus parseConnectionCommand(final List<String> parsedArray,
            final String prefix, final String command) {
        switch (command) {
            case CommandConstants.PING:
                parsePing(parsedArray);
                break;
            case CommandConstants.ERROR:
                // We are finished - the server has kicked us out for some reason
                return new ConnectionLineParseStatus(ParseStatus.ERROR, null);
            case CommandConstants.NOTICE:
                parseNotice(parsedArray, prefix);
                break;
            case CommandConstants.CAP:
                mCapParser.parseCAP(parsedArray);
                break;
            case CommandConstants.AUTHENTICATE:
                mCapParser.parseAuthenticate(parsedArray);
                break;
        }
        return new ConnectionLineParseStatus(ParseStatus.OTHER, null);
    }

    private void parsePing(final List<String> parsedArray) {
        // Immediately return
        final String source = parsedArray.get(0);
        mInternalSender.pongServer(source);
    }

    private void parseNotice(final List<String> parsedArray, final String prefix) {
        final String sender = ParseUtils.getNickFromPrefix(prefix);

        // final String target = parsedArray.get(0);
        final String notice = parsedArray.get(1);
        mServer.getBus().post(new NoticeEvent(mServer, sender, notice));
    }

    private ConnectionLineParseStatus parseConnectionCode(final List<String> parsedArray,
            final int code) {
        final String target = parsedArray.remove(0); // Remove the target of the reply - ourselves
        switch (code) {
            case ServerReplyCodes.RPL_WELCOME:
                // We are now logged in.
                return new ConnectionLineParseStatus(ParseStatus.NICK, target);
            case ServerReplyCodes.ERR_NICKNAMEINUSE:
                onNicknameInUse();
                break;
            case ServerReplyCodes.ERR_NONICKNAMEGIVEN:
                mServer.sendNick(mConfiguration.getNickStorage().getFirst());
                break;
        }
        if (ServerReplyCodes.saslCodes.contains(code)) {
            mCapParser.parseCode(code, parsedArray);
        }
        return new ConnectionLineParseStatus(ParseStatus.OTHER, null);
    }

    private void onNicknameInUse() {
        final NickStorage nickStorage = mConfiguration.getNickStorage();
        if (mIndex < nickStorage.getNickCount()) {
            mServer.sendNick(nickStorage.getNickAtPosition(mIndex));
            mIndex++;
        } else if (mConfiguration.isNickChangeable()) {
            mServer.sendNick(nickStorage.getFirst() + mSuffix);
            mSuffix++;
        } else {
            // TODO - fix this
            //sender.sendNickInUseMessage();
        }
    }

    public static enum ParseStatus {
        NICK,
        ERROR,
        OTHER
    }

    public static class ConnectionLineParseStatus {

        private final ParseStatus mStatus;

        private final String mNick;

        public ConnectionLineParseStatus(final ParseStatus status, final String nick) {
            mStatus = status;
            mNick = nick;
        }

        public String getNick() {
            return mNick;
        }

        public ParseStatus getStatus() {
            return mStatus;
        }
    }
}