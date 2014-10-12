package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class WallopsParser implements CommandParser {

    public final ObserverHelper<WallopsObserver> mObserverHelper = new ObserverHelper<>();

    public WallopsParser addObserver(final WallopsObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String message = parsedArray.get(0);

        mObserverHelper.notifyObservers(new Consumer<WallopsObserver>() {
            @Override
            public void apply(final WallopsObserver object) {
                object.onWallops(prefix, message);
            }
        });
    }

    public static interface WallopsObserver {

        public void onWallops(final String prefix, final String message);
    }
}