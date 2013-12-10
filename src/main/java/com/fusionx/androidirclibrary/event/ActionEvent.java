package com.fusionx.androidirclibrary.event;

public class ActionEvent extends ChannelEvent {

    public ActionEvent(String channelName, String message) {
        super(channelName, message, false);
    }
}