package com.fusionx.androidirclibrary.interfaces;

public interface EventStringResponses {

    // Status constants
    public String getConnectedStatus();

    public String getDisconnectedStatus();

    public String getConnectingStatus();

    // Messages
    public String getOnConnectedMessage(final String serverUrl);

    public String getJoinMessage(final String nick);

    public String getMessage(final String sendingNick, final String rawMessage);

    public String getActionMessage(final String sendingNick, final String action);

    public String getInitialTopicMessage(final String topic, final String topicSetter);

    // Errors
    public String getNickInUserError();
}