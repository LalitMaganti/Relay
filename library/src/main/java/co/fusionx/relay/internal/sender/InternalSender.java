package co.fusionx.relay.internal.sender;

/**
 * Created by lalit on 09/09/14.
 */
public interface InternalSender {

    void pongServer(String source);

    void sendServerPassword(String password);

    void sendNickServPassword(String password);

    void sendUser(String serverUserName, String realName);

    void quitServer(String quitReason);
}
