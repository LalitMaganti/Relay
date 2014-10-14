package co.fusionx.relay.parser.rfc;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class WallopsParser implements CommandParser {

    private final ObserverHelper<WallopsObserver> mObserverHelper = new ObserverHelper<>();

    public WallopsParser addObserver(final WallopsObserver observer) {
        mObserverHelper.addObserver(observer);
        return this;
    }

    public WallopsParser addObservers(final Collection<? extends WallopsObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String message = parsedArray.get(0);

        mObserverHelper.notifyObservers(object -> object.onWallops(prefix, message));
    }

    public static interface WallopsObserver {

        public void onWallops(final String prefix, final String message);
    }
}