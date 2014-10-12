package co.fusionx.relay.parser.rfc;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;

public class InviteParser implements CommandParser {

    public final ObserverHelper<InviteObserver> mObserverHelper = new ObserverHelper<>();

    public InviteParser addObserver(final InviteObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    public InviteParser addObservers(final Collection<? extends InviteObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String invitedNick = parsedArray.get(0);
        final String channelName = parsedArray.get(1);

        mObserverHelper.notifyObservers(new Consumer<InviteObserver>() {
            @Override
            public void apply(final InviteObserver observer) {
                observer.onInvite(prefix, invitedNick, channelName);
            }
        });
    }

    public interface InviteObserver {

        public void onInvite(final String invitingPrefix, final String invitedNick,
                final String channelName);
    }
}