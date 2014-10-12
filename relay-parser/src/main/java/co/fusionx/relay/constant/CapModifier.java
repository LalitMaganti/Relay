package co.fusionx.relay.constant;

import org.apache.commons.lang3.tuple.Pair;

import co.fusionx.relay.parser.ircv3.CapParser;

public enum CapModifier {
    DISABLE('-'),
    ACK('~'),
    STICKY('=');

    private final char mModifier;

    CapModifier(final char modifier) {
        mModifier = modifier;
    }

    public static Pair<String, CapModifier> consumeModifier(final String modifiedString) {
        for (final CapModifier capModifier : CapModifier.values()) {
            if (modifiedString.charAt(0) == capModifier.getModifier()) {
                return Pair.of(modifiedString.substring(1), capModifier);
            }
        }
        return Pair.of(modifiedString, null);
    }

    public char getModifier() {
        return mModifier;
    }
}