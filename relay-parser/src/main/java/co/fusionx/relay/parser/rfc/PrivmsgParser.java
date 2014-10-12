package co.fusionx.relay.parser.rfc;

import java.util.List;

import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class PrivmsgParser implements CommandParser {

    public final ObserverHelper<PrivmsgObserver> mObserverHelper = new ObserverHelper<>();

    public PrivmsgParser addObserver(final PrivmsgObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String recipient = parsedArray.get(0);
        final String message = parsedArray.get(1);

        mObserverHelper.notifyObservers(new Consumer<PrivmsgObserver>() {
            @Override
            public void apply(final PrivmsgObserver observer) {
                observer.onPrivmsg(prefix, recipient, message);
            }
        });
    }

    public static interface PrivmsgObserver {

        public void onPrivmsg(final String prefix, final String recipient, final String message);
    }
}