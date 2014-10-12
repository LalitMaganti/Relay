package co.fusionx.relay.parser.rfc;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class JoinParser implements CommandParser {

    public final ObserverHelper<JoinObserver> mObserverHelper = new ObserverHelper<>();

    public JoinParser addObserver(final JoinObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    public JoinParser addObservers(final Collection<? extends JoinObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);

        mObserverHelper.notifyObservers(observer -> observer.onJoin(prefix, channelName));
    }

    public static interface JoinObserver {

        public void onJoin(final String prefix, final String channelName);
    }
}