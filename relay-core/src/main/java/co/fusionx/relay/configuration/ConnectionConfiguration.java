package co.fusionx.relay.configuration;

import java.util.List;

import co.fusionx.relay.provider.NickProvider;

public interface ConnectionConfiguration {

    // Helper methods
    boolean shouldSendSasl();

    // Getters and setters
    String getTitle();

    String getUrl();

    int getPort();

    boolean isSslEnabled();

    boolean shouldAcceptAllSSLCertificates();

    NickProvider getNickProvider();

    String getRealName();

    boolean isNickChangeable();

    String getServerUserName();

    String getServerPassword();

    String getClientAuthenticationKeyPath();

    String getSaslUsername();

    String getSaslPassword();

    String getNickservPassword();

    List<String> getAutoJoinChannels();

    public static interface Builder {

        ConnectionConfiguration build();

        // Getters and setters
        int getId();

        void setId(int id);

        String getTitle();

        Builder setTitle(String title);

        String getUrl();

        Builder setUrl(String url);

        int getPort();

        Builder setPort(int port);

        boolean isSsl();

        Builder setSsl(boolean ssl);

        boolean isSslAcceptAllCertificates();

        Builder setSslAcceptAllCertificates(boolean sslAcceptAllCertificates);

        NickProvider getNickProvider();

        Builder setNickStorage(NickProvider nickProvider);

        String getRealName();

        Builder setRealName(String realName);

        boolean isNickChangeable();

        Builder setNickChangeable(boolean nickChangeable);

        String getServerUserName();

        Builder setServerUserName(String serverUserName);

        String getServerPassword();

        Builder setServerPassword(String serverPassword);

        String getClientAuthenticationKeyPath();

        Builder setClientAuthenticationKeyPath(String clientAuthenticationKeyPath);

        String getSaslUsername();

        Builder setSaslUsername(String saslUsername);

        String getSaslPassword();

        Builder setSaslPassword(String saslPassword);

        String getNickservPassword();

        Builder setNickservPassword(String nickservPassword);

        List<String> getAutoJoinChannels();

        Builder addAutoJoinChannel(String channelName);
    }
}
