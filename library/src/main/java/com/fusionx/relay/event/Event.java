package com.fusionx.relay.event;

import android.text.format.Time;

public class Event {

    public final Time timestamp;

    public Object store;

    public Event() {
        timestamp = new Time();
        timestamp.setToNow();
    }
}