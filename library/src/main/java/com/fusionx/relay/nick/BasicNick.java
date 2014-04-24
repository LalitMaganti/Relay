package com.fusionx.relay.nick;

public final class BasicNick implements Nick {

    private final String mNick;

    public BasicNick(final String nick) {
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
        if (o == null || !(o instanceof BasicNick)) {
            return false;
        }

        final BasicNick other = (BasicNick) o;
        return mNick.equals(other.getNickAsString());
    }
}