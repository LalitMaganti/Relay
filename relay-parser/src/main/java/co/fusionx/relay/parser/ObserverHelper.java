package co.fusionx.relay.parser;

import com.google.common.collect.FluentIterable;

import java.util.HashSet;
import java.util.Set;

import co.fusionx.relay.function.Consumer;
import co.fusionx.relay.function.FluentIterables;

public class ObserverHelper<T> {

    private final Set<T> mObservers = new HashSet<>();

    public void addObserver(final T observer) {
        mObservers.add(observer);
    }

    public void notifyObservers(final Consumer<T> consumer) {
        FluentIterables.forEach(FluentIterable.from(mObservers), consumer);
    }
}