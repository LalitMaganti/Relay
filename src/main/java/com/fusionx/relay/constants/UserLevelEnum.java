package com.fusionx.relay.constants;

public enum UserLevelEnum {
    OWNER('~', "owners"),
    SUPEROP('&', "super-ops"),
    HALFOP('%', "half-ops"),
    OP('@', "ops"),
    VOICE('+', "voices"),
    NONE('\0', "users");

    private final char mPrefix;

    private final String mName;

    private UserLevelEnum(char c, String users) {
        mPrefix = c;
        mName = users;
    }

    public char getPrefix() {
        return mPrefix;
    }

    public String getName() {
        return mName;
    }

    public static UserLevelEnum getLevelFromPrefix(final char prefix) {
        switch (prefix) {
            case '~':
                return UserLevelEnum.OWNER;
            case '&':
                return UserLevelEnum.SUPEROP;
            case '%':
                return UserLevelEnum.HALFOP;
            case '@':
                return UserLevelEnum.OP;
            case '+':
                return UserLevelEnum.VOICE;
            default:
                return UserLevelEnum.NONE;
        }
    }

    public static UserLevelEnum getLevelFromMode(final char modeCharacter) {
        switch (modeCharacter) {
            case 'q':
                return UserLevelEnum.OWNER;
            case 'a':
                return UserLevelEnum.SUPEROP;
            case 'h':
                return UserLevelEnum.HALFOP;
            case 'o':
                return UserLevelEnum.OP;
            case 'v':
                return UserLevelEnum.VOICE;
            default:
                return UserLevelEnum.NONE;
        }
    }
}