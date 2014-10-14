package co.fusionx.relay.parser.rfc;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class NoticeParser implements CommandParser {

    private final ObserverHelper<NoticeObserver> mObserverHelper = new ObserverHelper<>();

    public NoticeParser addObserver(final NoticeObserver observer) {
        mObserverHelper.addObserver(observer);
        return this;
    }

    public NoticeParser addObservers(final Collection<? extends NoticeObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String recipient = parsedArray.get(0);
        final String notice = parsedArray.get(1);

        mObserverHelper.notifyObservers(observer -> observer.onNotice(prefix, recipient, notice));
    }

    public static interface NoticeObserver {

        public void onNotice(final String prefix, final String recipient, final String notice);
    }
}