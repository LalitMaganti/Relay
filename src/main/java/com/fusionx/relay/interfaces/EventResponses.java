package com.fusionx.relay.interfaces;

public interface EventResponses {

    // Status constants
    public String getConnectedStatus();

    public String getDisconnectedStatus();

    public String getConnectingStatus();

    // Messages
    public String getOnConnectedMessage(final String serverUrl);

    public String getJoinMessage(final String nick);

    public String getModeChangedMessage(final String mode, final String triggerNick,
            final String recipientNick);

    public String getNickChangedMessage(String oldNick, String newNick, boolean isUser);

    public String getTopicChangedMessage(final String setterNick, final String oldTopic,
            final String newTopic);

    String getUserKickedMessage(final String kickedUserNick, final String kickingUserNick,
            final String reason);

    public String getOnUserKickedMessage(final String name, final String nick,
            final String reason);

    public String getPartMessage(final String nick, final String reason);

    public String getQuitMessage(final String nick, final String reason);

    public String getMessage(final String sendingNick, final String rawMessage);

    public String getNoticeMessage(final String sendingUser, final String notice);

    public String getActionMessage(final String sendingNick, final String action);

    public String getInitialTopicMessage(final String topic, final String topicSetter);

    // Errors
    public String getNickInUserError();
}