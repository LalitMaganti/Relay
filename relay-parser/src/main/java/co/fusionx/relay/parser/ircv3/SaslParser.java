package co.fusionx.relay.parser.ircv3;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ObserverHelper;
import co.fusionx.relay.parser.ReplyCodeParser;

public class SaslParser implements CommandParser, ReplyCodeParser {

    public final ObserverHelper<SaslObserver> mObserverHelper = new ObserverHelper<>();

    public SaslParser addObserver(final SaslObserver observer) {
        mObserverHelper.addObserver(observer);
        return this;
    }

    public SaslParser addObservers(final Collection<? extends SaslObserver> observers) {
        mObserverHelper.addObservers(observers);
        return this;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String argument = parsedArray.get(0);
        switch (argument) {
            case "+":
                mObserverHelper.notifyObservers(new Consumer<SaslObserver>() {
                    @Override
                    public void apply(final SaslObserver observer) {
                        observer.onAuthenticatePlus();
                    }
                });
                break;
        }
    }

    @Override
    public void parseReplyCode(final List<String> parsedArray, final int code) {
        switch (code) {
            case ReplyCodes.RPL_SASL_LOGGED_IN:
                final String loginMessage = parsedArray.get(2);
                mObserverHelper.notifyObservers(new Consumer<SaslObserver>() {
                    @Override
                    public void apply(final SaslObserver observer) {
                        observer.onLoggedIn(loginMessage);
                    }
                });
                break;
            case ReplyCodes.RPL_SASL_SUCCESSFUL:
                final String message = parsedArray.get(0);
                mObserverHelper.notifyObservers(new Consumer<SaslObserver>() {
                    @Override
                    public void apply(final SaslObserver observer) {
                        observer.onSuccess(message);
                    }
                });
                break;
            case ReplyCodes.ERR_SASL_FAIL:
            case ReplyCodes.ERR_SASL_TOO_LONG:
                final String error = parsedArray.get(0);
                mObserverHelper.notifyObservers(new Consumer<SaslObserver>() {
                    @Override
                    public void apply(final SaslObserver observer) {
                        observer.onError(error);
                    }
                });
                break;
        }
    }

    @Override
    public List<Integer> parsableCodes() {
        return ImmutableList.of(ReplyCodes.RPL_SASL_LOGGED_IN, ReplyCodes.RPL_SASL_SUCCESSFUL,
                ReplyCodes.ERR_SASL_FAIL, ReplyCodes.ERR_SASL_TOO_LONG);
    }

    public interface SaslObserver {

        public void onAuthenticatePlus();

        public void onSuccess(final String message);

        public void onLoggedIn(final String message);

        public void onError(final String message);
    }
}