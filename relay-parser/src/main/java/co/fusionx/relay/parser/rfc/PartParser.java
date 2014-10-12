package co.fusionx.relay.parser.rfc;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class PartParser implements CommandParser {

    public final ObserverHelper<PartObserver> mObserverHelper = new ObserverHelper<>();

    public PartParser addObserver(final PartObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    public PartParser addObservers(final Collection<? extends PartObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);
        final String reason = parsedArray.size() == 2 ? parsedArray.get(1).replace("\"", "") : null;

        final Optional<String> optionalReason = Optional.fromNullable(reason);

        mObserverHelper.notifyObservers(new Consumer<PartObserver>() {
            @Override
            public void apply(final PartObserver observer) {
                observer.onPart(prefix, channelName, optionalReason);
            }
        });
    }

    public static interface PartObserver {

        public void onPart(final String prefix, final String channelName,
                final Optional<String> optionalReason);
    }
}