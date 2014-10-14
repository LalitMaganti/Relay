package co.fusionx.relay.core;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.parser.UserInputParser;

public interface Session extends Registerable {

    public SessionStatus getStatus();

    public Server getServer();

    public UserInputParser getUserInputParser();

    public UserChannelGroup getUserChannelManager();

    public QueryUserGroup getQueryManager();
}