package co.fusionx.relay.internal.sender;

public interface InternalSender {

    void pongServer(String source);

    void sendServerPassword(String password);

    void sendNickServPassword(String password);

    void sendUser(String serverUserName, String realName);

    void quitServer(String quitReason);
}