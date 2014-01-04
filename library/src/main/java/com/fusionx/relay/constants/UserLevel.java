package com.fusionx.relay.constants;

public enum UserLevel {
    OWNER('~', "owners"),
    SUPEROP('&', "super-ops"),
    HALFOP('%', "half-ops"),
    OP('@', "ops"),
    VOICE('+', "voices"),
    NONE('\0', "users");

    private final char mPrefix;

    private final String mName;

    private UserLevel(final char c, final String users) {
        mPrefix = c;
        mName = users;
    }

    public char getPrefix() {
        return mPrefix;
    }

    public String getName() {
        return mName;
    }

    public static UserLevel getLevelFromPrefix(final char prefix) {
        switch (prefix) {
            case '~':
                return UserLevel.OWNER;
            case '&':
                return UserLevel.SUPEROP;
            case '%':
                return UserLevel.HALFOP;
            case '@':
                return UserLevel.OP;
            case '+':
                return UserLevel.VOICE;
            default:
                return UserLevel.NONE;
        }
    }

    public static UserLevel getLevelFromMode(final char modeCharacter) {
        switch (modeCharacter) {
            case 'q':
                return UserLevel.OWNER;
            case 'a':
                return UserLevel.SUPEROP;
            case 'h':
                return UserLevel.HALFOP;
            case 'o':
                return UserLevel.OP;
            case 'v':
                return UserLevel.VOICE;
            default:
                return UserLevel.NONE;
        }
    }
}