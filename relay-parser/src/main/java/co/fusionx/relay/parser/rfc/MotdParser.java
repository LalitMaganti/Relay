package co.fusionx.relay.parser.rfc;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.parser.ObserverHelper;
import co.fusionx.relay.parser.ReplyCodeParser;

public class MotdParser implements ReplyCodeParser {

    public final ObserverHelper<MotdObserver> mObserverHelper = new ObserverHelper<>();

    public MotdParser addObserver(final MotdObserver wallopsObserver) {
        mObserverHelper.addObserver(wallopsObserver);
        return this;
    }

    public MotdParser addObservers(final Collection<? extends MotdObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseReplyCode(final List<String> parsedArray, final int code) {
        final String message = parsedArray.get(0);

        mObserverHelper.notifyObservers(new Consumer<MotdObserver>() {
            @Override
            public void apply(final MotdObserver observer) {
                observer.onMotd(code, message);
            }
        });
    }

    @Override
    public List<Integer> parsableCodes() {
        return ImmutableList.of(ReplyCodes.RPL_MOTDSTART, ReplyCodes.RPL_MOTD,
                ReplyCodes.RPL_ENDOFMOTD);
    }

    public static interface MotdObserver {

        public void onMotd(final int code, final String motdMessage);
    }
}