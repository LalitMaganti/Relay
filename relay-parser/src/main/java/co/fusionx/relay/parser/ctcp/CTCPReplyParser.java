package co.fusionx.relay.parser.ctcp;

import co.fusionx.relay.parser.rfc.NoticeParser;

public class CTCPReplyParser implements NoticeParser.NoticeObserver {

    public void CTCPReplyParser(final NoticeParser noticeParser) {
        noticeParser.addObserver(this);
    }

    @Override
    public void onNotice(final String prefix, final String recipient, final String rawNotice) {
        final String message = rawNotice.substring(1, rawNotice.length() - 1);

        if (message.startsWith("ACTION")) {
            // This is invalid - ignore this
        } else if (message.startsWith("FINGER")) {
            parseFinger(recipient, prefix);
        } else if (message.startsWith("VERSION")) {
            parseVersion(recipient, prefix, message);
        } else if (message.startsWith("SOURCE")) {
            parseSource(recipient, prefix, message);
        } else if (message.startsWith("USERINFO")) {
            parseUserInfo(recipient, prefix, message);
        } else if (message.startsWith("ERRMSG")) {
            parseErrmsg(recipient, prefix, message);
        } else if (message.startsWith("PING")) {
            parsePing(recipient, prefix, message);
        } else if (message.startsWith("TIME")) {
            parseTime(recipient, prefix, message);
        }
    }

    private void parseFinger(final String recipient, final String prefix) {

    }

    private void parseVersion(final String recipient, final String prefix, final String message) {

    }

    private void parseSource(final String recipient, final String prefix, final String message) {

    }

    private void parseUserInfo(final String recipient, final String prefix, final String message) {

    }

    private void parseErrmsg(final String recipient, final String prefix, final String message) {

    }

    private void parsePing(final String recipient, final String prefix, final String message) {

    }

    private void parseTime(final String recipient, final String prefix, final String message) {

    }
}