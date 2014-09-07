package co.fusionx.relay.internal.parser.connection;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.event.server.NoticeEvent;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.constants.ServerCommands;
import co.fusionx.relay.internal.constants.ServerReplyCodes;
import co.fusionx.relay.internal.parser.connection.cap.CapParser;
import co.fusionx.relay.internal.sender.RelayInternalSender;
import co.fusionx.relay.misc.NickStorage;
import co.fusionx.relay.util.ParseUtils;

public class ConnectionParser {

    private final RelayServer mServer;

    private final ServerConfiguration mConfiguration;

    private final BufferedReader mBufferedReader;

    private final RelayInternalSender mInternalSender;

    private final CapParser mCapParser;

    private int mIndex;

    private int mSuffix;

    public ConnectionParser(final RelayServer server, final BufferedReader bufferedReader) {
        mServer = server;
        mConfiguration = server.getConfiguration();

        mBufferedReader = bufferedReader;

        mInternalSender = new RelayInternalSender(server.getRelayBaseSender());
        mCapParser = new CapParser(server);

        mIndex = 1;
        mSuffix = 1;
    }

    public ConnectionLineParseStatus parseConnect() throws IOException {
        String line;
        while ((line = mBufferedReader.readLine()) != null) {
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
            case ServerCommands.PING:
                parsePing(parsedArray);
                break;
            case ServerCommands.ERROR:
                // We are finished - the server has kicked us out for some reason
                return new ConnectionLineParseStatus(ParseStatus.ERROR, null);
            case ServerCommands.NOTICE:
                parseNotice(parsedArray, prefix);
                break;
            case ServerCommands.CAP:
                mCapParser.parseCAP(parsedArray);
                break;
            case ServerCommands.AUTHENTICATE:
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
        mServer.postAndStoreEvent(new NoticeEvent(mServer, sender, notice));
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