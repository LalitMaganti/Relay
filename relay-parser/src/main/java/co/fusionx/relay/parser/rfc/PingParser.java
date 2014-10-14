package co.fusionx.relay.parser.rfc;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class PingParser implements CommandParser {

    private final ObserverHelper<PingObserver> mObserverHelper = new ObserverHelper<>();

    public PingParser addObserver(final PingObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    public PingParser addObservers(final Collection<? extends PingObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String serverHostname = parsedArray.get(0);

        mObserverHelper.notifyObservers(observer -> observer.onPing(serverHostname));
    }

    public static interface PingObserver {

        public void onPing(final String serverHostname);
    }
}