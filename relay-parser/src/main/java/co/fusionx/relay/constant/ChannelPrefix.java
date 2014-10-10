package co.fusionx.relay.constant;

import com.google.common.collect.ImmutableList;

public final class ChannelPrefix {

    // As set out in RFC2812
    private final static ImmutableList<Character> VALID_CHANNEL_PREFIXES = ImmutableList
            .of('#', '&', '+', '!');

    private ChannelPrefix() {
    }

    public static ImmutableList<Character> getValidPrefixes() {
        return VALID_CHANNEL_PREFIXES;
    }

    public static boolean isPrefix(final char firstCharacter) {
        return VALID_CHANNEL_PREFIXES.contains(firstCharacter);
    }
}