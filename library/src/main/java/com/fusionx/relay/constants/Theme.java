package com.fusionx.relay.constants;

public enum Theme {
    LIGHT(100),
    DARK(300);

    private final int mTextColourOffset;

    private Theme(final int i) {
        mTextColourOffset = i;
    }

    public int getTextColourOffset() {
        return mTextColourOffset;
    }
}