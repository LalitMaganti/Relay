package co.fusionx.relay.internal.parser;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.event.server.NoticeEvent;
import co.fusionx.relay.internal.constants.Commands;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.sender.CapSender;
import co.fusionx.relay.internal.sender.InternalSender;
import co.fusionx.relay.parser.ircv3.CapParser;
import co.fusionx.relay.provider.NickProvider;
import co.fusionx.relay.util.ParseUtils;

public class ConnectionParser {

    private final InternalServer mServer;

    private final ConnectionConfiguration mConfiguration;

    private final InternalSender mInternalSender;

    private int mIndex;

    private int mSuffix;

    @Inject
    public ConnectionParser(final ConnectionConfiguration configuration,
            final InternalServer server, final InternalSender internalSender) {
        mConfiguration = configuration;
        mServer = server;
        mInternalSender = internalSender;

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
        if (StringUtils.isEmpty(line)) {
            return new ConnectionLineParseStatus(ParseStatus.OTHER, null);
        }

        final List<String> parsedArray = ParseUtils.splitRawLine(line, true);
        final String prefix = ParseUtils.consumePrefixIfPresent(parsedArray);
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
            case Commands.PING:
                parsePing(parsedArray);
                break;
            case Commands.ERROR:
                // We are finished - the server has kicked us out for some reason
                return new ConnectionLineParseStatus(ParseStatus.ERROR, null);
            case Commands.NOTICE:
                parseNotice(parsedArray, prefix);
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
        mServer.postEvent(new NoticeEvent(mServer, sender, notice));
    }

    private ConnectionLineParseStatus parseConnectionCode(final List<String> parsedArray,
            final int code) {
        final String target = parsedArray.remove(0); // Remove the target of the reply - ourselves
        switch (code) {
            case ReplyCodes.RPL_WELCOME:
                // We are now logged in.
                return new ConnectionLineParseStatus(ParseStatus.NICK, target);
            case ReplyCodes.ERR_NICKNAMEINUSE:
                onNicknameInUse();
                break;
            case ReplyCodes.ERR_NONICKNAMEGIVEN:
                mServer.sendNick(mConfiguration.getNickProvider().getFirst());
                break;
        }
        return new ConnectionLineParseStatus(ParseStatus.OTHER, null);
    }

    private void onNicknameInUse() {
        final NickProvider parcelableNickProvider = mConfiguration.getNickProvider();
        if (mIndex < parcelableNickProvider.getNickCount()) {
            mServer.sendNick(parcelableNickProvider.getNickAtPosition(mIndex));
            mIndex++;
        } else if (mConfiguration.isNickChangeable()) {
            mServer.sendNick(parcelableNickProvider.getFirst() + mSuffix);
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