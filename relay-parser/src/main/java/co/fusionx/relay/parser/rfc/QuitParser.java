package co.fusionx.relay.parser.rfc;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class QuitParser implements CommandParser {

    public final ObserverHelper<QuitObserver> mObserverHelper = new ObserverHelper<>();

    public QuitParser addObserver(final QuitObserver observer) {
        mObserverHelper.addObserver(observer);
        return this;
    }

    public QuitParser addObservers(final Collection<? extends QuitObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String reason = parsedArray.size() == 1 ? parsedArray.get(0).replace("\"", "") : null;

        final Optional<String> optionalReason = Optional.fromNullable(reason);

        mObserverHelper.notifyObservers(new Consumer<QuitObserver>() {
            @Override
            public void apply(final QuitObserver observer) {
                observer.onQuit(prefix, optionalReason);
            }
        });
    }

    public static interface QuitObserver {

        public void onQuit(final String prefix, final Optional<String> optionalReason);
    }
}