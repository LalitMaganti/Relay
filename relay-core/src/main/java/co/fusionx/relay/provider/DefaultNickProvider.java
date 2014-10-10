package co.fusionx.relay.provider;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DefaultNickProvider implements NickProvider {

    private final List<String> mNicks = new ArrayList<>();

    public DefaultNickProvider(final List<String> choices) {
        addAll(FluentIterable.from(choices));
    }

    public DefaultNickProvider(final String... choices) {
        addAll(FluentIterable.of(choices));
    }

    @Override
    public String getFirst() {
        return mNicks.get(0);
    }

    @Override
    public String getNickAtPosition(final int position) {
        return position < getNickCount() ? mNicks.get(position) : "";
    }

    @Override
    public int getNickCount() {
        return mNicks.size();
    }

    private void addAll(final FluentIterable<String> fluentIterable) {
        fluentIterable.filter(new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
                return StringUtils.isNotEmpty(input);
            }
        }).copyInto(mNicks);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefaultNickProvider) {
            final DefaultNickProvider defaultNickProvider = (DefaultNickProvider) o;
            return mNicks.equals(defaultNickProvider.mNicks);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mNicks.hashCode();
    }
}