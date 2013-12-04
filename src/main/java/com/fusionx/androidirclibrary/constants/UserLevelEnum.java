/*
    HoloIRC - an IRC client for Android

    Copyright 2013 Lalit Maganti

    This file is part of HoloIRC.

    HoloIRC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HoloIRC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with HoloIRC. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fusionx.androidirclibrary.constants;

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