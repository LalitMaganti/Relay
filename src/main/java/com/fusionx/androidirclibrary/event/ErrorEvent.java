package com.fusionx.androidirclibrary.event;

public class ErrorEvent extends Event {

    public ErrorEvent(final String errorMessage) {
        super(errorMessage);
    }
}