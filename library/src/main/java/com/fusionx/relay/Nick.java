package com.fusionx.relay;

import com.fusionx.relay.misc.InterfaceHolders;

import android.graphics.Color;

final class Nick {

    private final String mNick;

    private final String mColourCode;

    protected Nick(final String nick) {
        mNick = nick;
        mColourCode = "<color=" + getColorFromNick() + ">%1$s</color>";
    }

    public String getColorfulNick() {
        if (InterfaceHolders.getPreferences().shouldNickBeColourful()) {
            return String.format(mColourCode, mNick);
        } else {
            return mNick;
        }
    }

    @Override
    public String toString() {
        return mNick;
    }

    // Getters and setters
    public String getNick() {
        return mNick;
    }

    public String getColourCode() {
        return mColourCode;
    }

    private int getColorFromNick() {
        final int colorOffset = InterfaceHolders.getPreferences().getTheme()
                .getTextColourOffset();

        final int hash = mNick.hashCode();

        int red = (hash) & 0xFF;
        int green = (hash >> 16) & 0xFF;
        int blue = (hash >> 8) & 0xFF;

        // mix the color
        red = normaliseColourInt((red + colorOffset) / 2);
        green = normaliseColourInt((green + colorOffset) / 2);
        blue = normaliseColourInt((blue + colorOffset) / 2);

        return Color.rgb(red, green, blue);
    }

    private int normaliseColourInt(final int colour) {
        if (colour > 255) {
            return 255;
        } else if (colour < 0) {
            return 0;
        }

        return colour;
    }
}