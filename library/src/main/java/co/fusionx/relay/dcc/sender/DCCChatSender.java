package co.fusionx.relay.dcc.sender;

public interface DCCChatSender {

    public void startChat();

    public void sendMessage(String message);

    public void sendAction(String action);

    public void closeChat();
}