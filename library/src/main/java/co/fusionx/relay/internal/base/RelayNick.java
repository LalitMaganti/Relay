package co.fusionx.relay.internal.base;

import co.fusionx.relay.base.Nick;

public final class RelayNick implements Nick {

    private final String mNick;

    public RelayNick(final String nick) {
        mNick = nick;
    }

    @Override
    public String toString() {
        return mNick;
    }

    // Getters and setters
    public String getNickAsString() {
        return mNick;
    }

    @Override
    public int hashCode() {
        return mNick.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof RelayNick)) {
            return false;
        }

        final RelayNick other = (RelayNick) o;
        return mNick.equals(other.getNickAsString());
    }
}