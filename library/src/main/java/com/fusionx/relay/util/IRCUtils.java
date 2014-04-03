package com.fusionx.relay.util;

import com.google.common.base.CharMatcher;

import com.fusionx.relay.constants.Theme;

import org.apache.commons.lang3.StringUtils;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class IRCUtils {

    public static String getNickFromRaw(final String rawSource) {
        String nick;
        if (rawSource.contains("!") && rawSource.contains("@")) {
            nick = StringUtils.substringBefore(rawSource, "!");
        } else {
            nick = rawSource;
        }
        return nick;
    }

    /**
     * Split the line received from the server into it's components
     *
     * @param input          the line received from the server
     * @param colonDelimiter whether a colon means the rest of the line should be added in one go
     * @return the parsed list
     */
    public static List<String> splitRawLine(final String input, final boolean colonDelimiter) {
        final List<String> stringParts = new ArrayList<>();
        if (input == null || input.length() == 0) {
            return stringParts;
        }

        final String colonLessLine = input.charAt(0) == ':' ? input.substring(1) : input;
        // Heavily optimized version string split by space with all characters after :
        // added as a single entry. Under benchmarks, its faster than StringTokenizer,
        // String.split, toCharArray, and charAt
        String trimmedInput = CharMatcher.WHITESPACE.trimFrom(colonLessLine);
        int pos = 0, end;
        while ((end = trimmedInput.indexOf(' ', pos)) >= 0) {
            stringParts.add(trimmedInput.substring(pos, end));
            pos = end + 1;
            if (trimmedInput.charAt(pos) == ':' && colonDelimiter) {
                stringParts.add(trimmedInput.substring(pos + 1));
                return stringParts;
            }
        }
        // No more spaces, add last part of line
        stringParts.add(trimmedInput.substring(pos));
        return stringParts;
    }

    public static void removeFirstElementFromList(final List<String> list, final int noOfTimes) {
        for (int i = 1; i <= noOfTimes; i++) {
            list.remove(0);
        }
    }

    public static String concatenateStringList(final Collection<String> list) {
        final StringBuilder builder = new StringBuilder();
        for (final String item : list) {
            builder.append(item).append(" ");
        }
        return builder.toString().trim();
    }
}
