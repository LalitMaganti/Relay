package com.fusionx.relay.util;

import com.google.common.base.CharMatcher;

import com.fusionx.relay.constants.Theme;

import org.apache.commons.lang3.StringUtils;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IRCUtils {

    public static boolean areNicksEqual(final String firstNick, final String secondNick) {
        return firstNick.equals(secondNick) || (firstNick.equalsIgnoreCase(secondNick) &&
                (firstNick.equalsIgnoreCase("nickserv") || firstNick.equalsIgnoreCase
                        ("chanserv")));
    }

    public static String getNickFromRaw(final String rawSource) {
        String nick;
        if (rawSource.contains("!") && rawSource.contains("@")) {
            nick = StringUtils.substringBefore(rawSource, "!");
        } else {
            nick = rawSource;
        }
        return nick;
    }

    public static int generateRandomColor(final Theme theme) {
        final int colorOffset = theme.getGetTextColourOffset();

        final Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        // mix the color
        red = (red + colorOffset) / 2;
        green = (green + colorOffset) / 2;
        blue = (blue + colorOffset) / 2;

        return Color.rgb(red, green, blue);
    }

    /**
     * Split the line received from the server into it's components
     *
     * @param input          the line received from the server
     * @param careAboutColon - whether a colon means the rest of the line should be added in one go
     * @return the parsed list
     */
    public static ArrayList<String> splitRawLine(final String input,
            final boolean careAboutColon) {
        final ArrayList<String> stringParts = new ArrayList<String>();
        if (input == null || input.length() == 0) {
            return stringParts;
        }

        final String colonLessLine = input.charAt(0) == ':' ? input.substring(1) : input;
        //Heavily optimized version string split by space with all characters after :
        //added as a single entry. Under benchmarks, its faster than StringTokenizer,
        //String.split, toCharArray, and charAt
        String trimmedInput = CharMatcher.WHITESPACE.trimFrom(colonLessLine);
        int pos = 0, end;
        while ((end = trimmedInput.indexOf(' ', pos)) >= 0) {
            stringParts.add(trimmedInput.substring(pos, end));
            pos = end + 1;
            if (trimmedInput.charAt(pos) == ':' && careAboutColon) {
                stringParts.add(trimmedInput.substring(pos + 1));
                return stringParts;
            }
        }
        //No more spaces, add last part of line
        stringParts.add(trimmedInput.substring(pos));
        return stringParts;
    }

    public static void removeFirstElementFromList(final List<String> list, final int noOfTimes) {
        for (int i = 1; i <= noOfTimes; i++) {
            list.remove(0);
        }
    }

    public static String convertArrayListToString(final ArrayList<String> list) {
        final StringBuilder builder = new StringBuilder();
        for (final String item : list) {
            builder.append(item).append(" ");
        }
        return builder.toString().trim();
    }
}
