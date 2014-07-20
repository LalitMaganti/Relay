package com.fusionx.relay;

import java.util.Collection;

public interface UserChannelInterface {

    Channel getChannel(String name);

    ChannelUser getUser(String nick);

    Collection<? extends QueryUser> getQueryUsers();

    QueryUser getQueryUser(String nick);
}
