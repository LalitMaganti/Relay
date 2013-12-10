package com.fusionx.relay.constants;

public enum Theme {
    LIGHT(0),
    DARK(255);

    private final int getTextColourOffset;

    private Theme(int i) {
        getTextColourOffset = i;
    }

    public int getGetTextColourOffset() {
        return getTextColourOffset;
    }
}