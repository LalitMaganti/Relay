package co.fusionx.relay.parser.ctcp;

import java.util.Collection;

import co.fusionx.relay.parser.ObserverHelper;
import co.fusionx.relay.parser.rfc.PrivmsgParser;

public class CTCPCommandParser implements PrivmsgParser.PrivmsgObserver {

    private final ObserverHelper<CTCPObserver> mObserverHelper = new ObserverHelper<>();

    private final ObserverHelper<CTCPExtender> mExtenderHelper = new ObserverHelper<>();

    public CTCPCommandParser addObserver(final CTCPObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    public CTCPCommandParser addObservers(final Collection<? extends CTCPObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    public CTCPCommandParser addExtender(final CTCPExtender extender) {
        mExtenderHelper.addObserver(extender);
        return this;
    }

    @Override
    public void onPrivmsg(final String prefix, final String recipient, final String rawMessage) {
        final String message = rawMessage.substring(1, rawMessage.length() - 1);

        if (message.startsWith("ACTION")) {
            parseAction(prefix, recipient, message);
        } else if (message.startsWith("FINGER")) {
            parseFinger(prefix, recipient);
        } else if (message.startsWith("VERSION")) {
            parseVersion(prefix, recipient);
        } else if (message.startsWith("SOURCE")) {
            parseSource(prefix, recipient);
        } else if (message.startsWith("USERINFO")) {
            parseUserInfo(prefix, recipient);
        } else if (message.startsWith("CLIENTINFO")) {
            parseClientInfo(prefix, recipient);
        } else if (message.startsWith("ERRMSG")) {
            parseErrmsg(prefix, recipient, message);
        } else if (message.startsWith("PING")) {
            parsePing(prefix, recipient, message);
        } else if (message.startsWith("TIME")) {
            parseTime(prefix, recipient);
        } else {
            parseUnknown(prefix, recipient, message);
        }
    }

    private void parseAction(final String prefix, final String recipient,
            final String message) {
        final String action = message.replace("ACTION ", "");

        mObserverHelper.notifyObservers(observer -> observer.onAction(prefix, recipient, action));
    }

    private void parseFinger(final String prefix, final String recipient) {
        mObserverHelper.notifyObservers(observer -> observer.onFinger(prefix, recipient));
    }

    private void parseVersion(final String prefix, final String recipient) {
        mObserverHelper.notifyObservers(observer -> observer.onVersion(prefix, recipient));
    }

    private void parseSource(final String prefix, final String recipient) {
        mObserverHelper.notifyObservers(observer -> observer.onSource(prefix, recipient));
    }

    private void parseUserInfo(final String prefix, final String recipient) {
        mObserverHelper.notifyObservers(observer -> observer.onUserInfo(prefix, recipient));
    }

    private void parseClientInfo(final String prefix, final String recipient) {
        mObserverHelper.notifyObservers(observer -> observer.onClientInfo(prefix, recipient));
    }

    private void parseErrmsg(final String prefix, final String recipient, final String message) {
        final String errmsg = message.replace("ERRMSG ", "");

        mObserverHelper.notifyObservers(observer -> observer.onErrMsg(prefix, recipient, errmsg));
    }

    private void parsePing(final String prefix, final String recipient, final String message) {
        final String ping = message.replace("PING ", "");

        mObserverHelper.notifyObservers(observer -> observer.onPing(prefix, recipient, ping));
    }

    private void parseTime(final String prefix, final String recipient) {
        mObserverHelper.notifyObservers(observer -> observer.onTime(prefix, recipient));
    }

    private void parseUnknown(final String prefix, final String recipient, final String message) {
        mExtenderHelper.notifyObservers(
                extender -> extender.parseExtendedCtcp(prefix, recipient, message));
    }

    public static interface CTCPExtender {

        public void parseExtendedCtcp(final String prefix, final String target,
                final String rawCtcp);
    }

    public static interface CTCPObserver {

        public void onAction(final String prefix, final String target, final String action);

        public void onFinger(final String prefix, final String target);

        public void onVersion(final String prefix, final String target);

        public void onSource(final String prefix, final String target);

        public void onUserInfo(final String prefix, final String target);

        public void onClientInfo(final String prefix, final String target);

        public void onErrMsg(final String prefix, final String target, final String action);

        public void onPing(final String prefix, final String target, final String action);

        public void onTime(final String prefix, final String target);
    }
}