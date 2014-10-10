package co.fusionx.relay.sender;

public interface DCCChatSender {

    public void startChat();

    public void sendMessage(String message);

    public void sendAction(String action);

    public void closeChat();
}