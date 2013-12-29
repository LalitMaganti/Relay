package com.fusionx.relay;

import com.fusionx.relay.constants.Theme;
import com.fusionx.relay.interfaces.EventPreferences;
import com.fusionx.relay.interfaces.EventResponses;

public class TestMisc {

    public static class DefaultEventPreferences implements EventPreferences {

        @Override
        public int getReconnectAttemptsCount() {
            return 0;
        }

        @Override
        public String getPartReason() {
            return null;
        }

        @Override
        public String getQuitReason() {
            return null;
        }

        @Override
        public boolean getShouldTimestampMessages() {
            return false;
        }

        @Override
        public Theme getTheme() {
            return Theme.LIGHT;
        }

        @Override
        public boolean shouldIgnoreUser(String nick) {
            return false;
        }

        @Override
        public boolean shouldLogUserListChanges() {
            return false;
        }

        @Override
        public boolean isSelfEventBroadcast() {
            return false;
        }

        @Override
        public boolean isMOTDShown() {
            return false;
        }

        @Override
        public boolean shouldHighlightLine() {
            return false;
        }

        @Override
        public boolean shouldHandleInitialPrivateMessage() {
            return false;
        }
    }

    public static class DefaultEventResponses implements EventResponses {

        @Override
        public String getConnectedStatus() {
            return null;
        }

        @Override
        public String getDisconnectedStatus() {
            return null;
        }

        @Override
        public String getConnectingStatus() {
            return null;
        }

        @Override
        public String getOnConnectedMessage(String serverUrl) {
            return null;
        }

        @Override
        public String getJoinMessage(String nick) {
            return null;
        }

        @Override
        public String getModeChangedMessage(String mode, String triggerNick,
                String recipientNick) {
            return null;
        }

        @Override
        public String getNickChangedMessage(String oldNick, String newNick, boolean isUser) {
            return null;
        }

        @Override
        public String getTopicChangedMessage(String setterNick, String oldTopic,
                String newTopic) {
            return null;
        }

        @Override
        public String getUserKickedMessage(String kickedUserNick, String kickingUserNick,
                String reason) {
            return null;
        }

        @Override
        public String getOnUserKickedMessage(String name, String nick,
                String reason) {
            return null;
        }

        @Override
        public String getPartMessage(String nick, String reason) {
            return null;
        }

        @Override
        public String getQuitMessage(String nick, String reason) {
            return null;
        }

        @Override
        public String getMessage(String sendingNick, String rawMessage) {
            return null;
        }

        @Override
        public String getNoticeMessage(String sendingUser, String notice) {
            return null;
        }

        @Override
        public String getActionMessage(String sendingNick, String action) {
            return null;
        }

        @Override
        public String getInitialTopicMessage(String topic, String topicSetter) {
            return null;
        }

        @Override
        public String getSlapMessage(String receivingNick) {
            return null;
        }

        @Override
        public void onUserMentioned(Server server, String messageDestination) {

        }

        @Override
        public String getNickInUserError() {
            return null;
        }
    }
}
