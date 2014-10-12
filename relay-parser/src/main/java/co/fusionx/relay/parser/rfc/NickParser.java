package co.fusionx.relay.parser.rfc;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class NickParser implements CommandParser {

    public final ObserverHelper<NickObserver> mObserverHelper = new ObserverHelper<>();

    public NickParser addObserver(final NickObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    public NickParser addObservers(final Collection<? extends NickObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String newNick = parsedArray.get(0);

        mObserverHelper.notifyObservers(observer -> observer.onNick(prefix, newNick));
    }

    public static interface NickObserver {

        public void onNick(final String prefix, final String newNick);
    }
}
