package co.fusionx.relay.parser.rfc;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.parser.ObserverHelper;
import co.fusionx.relay.parser.ReplyCodeParser;

public class WelcomeParser implements ReplyCodeParser {

    private final ObserverHelper<WelcomeObserver> mObserverHelper = new ObserverHelper<>();

    public WelcomeParser addObserver(final WelcomeObserver observer) {
        mObserverHelper.addObserver(observer);
        return this;
    }

    public WelcomeParser addObservers(final Collection<? extends WelcomeObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseReplyCode(final String target, final List<String> parsedArray,
            final int code) {
        final String message = parsedArray.get(0);

        mObserverHelper.notifyObservers(observer -> observer.onWelcome(target, code, message));
    }

    @Override
    public List<Integer> parsableCodes() {
        return ImmutableList.of(ReplyCodes.RPL_WELCOME, ReplyCodes.RPL_YOURHOST,
                ReplyCodes.RPL_CREATED, ReplyCodes.RPL_MYINFO);
    }

    public static interface WelcomeObserver {

        public void onWelcome(final String target, final int code, final String message);
    }
}