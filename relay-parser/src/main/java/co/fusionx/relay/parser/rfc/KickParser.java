package co.fusionx.relay.parser.rfc;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class KickParser implements CommandParser {

    public final ObserverHelper<KickObserver> mObserverHelper = new ObserverHelper<>();

    public KickParser addObserver(final KickObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);
        final String kickedNick = parsedArray.get(1);
        final String reason = parsedArray.size() == 3 ? parsedArray.get(2).replace("\"", "") : null;

        final Optional<String> optionalReason = Optional.fromNullable(reason);

        mObserverHelper.notifyObservers(new Consumer<KickObserver>() {
            @Override
            public void apply(final KickObserver observer) {
                observer.onKick(prefix, channelName, kickedNick, optionalReason);
            }
        });
    }

    public static interface KickObserver {

        public void onKick(final String prefix, final String channelName,
                final String kickedNick, final Optional<String> optionalReason);
    }
}