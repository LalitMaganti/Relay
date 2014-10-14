package co.fusionx.relay.parser.rfc;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class PrivmsgParser implements CommandParser {

    private final ObserverHelper<PrivmsgObserver> mObserverHelper = new ObserverHelper<>();

    public PrivmsgParser addObserver(final PrivmsgObserver observer) {
        mObserverHelper.addObserver(observer);
        return this;
    }

    public PrivmsgParser addObservers(final Collection<? extends PrivmsgObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String recipient = parsedArray.get(0);
        final String message = parsedArray.get(1);

        mObserverHelper.notifyObservers(observer -> observer.onPrivmsg(prefix, recipient, message));
    }

    public static interface PrivmsgObserver {

        public void onPrivmsg(final String prefix, final String recipient, final String message);
    }
}