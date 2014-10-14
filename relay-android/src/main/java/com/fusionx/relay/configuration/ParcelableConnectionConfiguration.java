package com.fusionx.relay.configuration;

import com.fusionx.relay.core.ParcelableNickProvider;

import org.apache.commons.lang3.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.provider.NickProvider;

public class ParcelableConnectionConfiguration implements Parcelable, ConnectionConfiguration {

    public static final Creator<ParcelableConnectionConfiguration> CREATOR =
            new Creator<ParcelableConnectionConfiguration>() {
                public ParcelableConnectionConfiguration createFromParcel(Parcel in) {
                    return new ParcelableConnectionConfiguration(in);
                }

                public ParcelableConnectionConfiguration[] newArray(int size) {
                    return new ParcelableConnectionConfiguration[size];
                }
            };

    /**
     * The informal name of the server that is being connected to
     */
    private final String mTitle;

    /**
     * The URL/hostname of the server that is being connected to
     */
    private final String mUrl;

    /**
     * The port to connect to the server on
     */
    private final int mPort;

    /**
     * Whether SSL should be used for the connection
     */
    private final boolean mSsl;

    /**
     * Whether all SSL certificates should be accepted rather than just those trusted on the device
     * This option is meaningless without SSL being enabled
     */
    private final boolean mSslAcceptAllCertificates;

    /**
     * The object containing the first, second and third choice nicks
     */
    private final NickProvider mParcelableNickProvider;

    /**
     * The real name of the user
     */
    private final String mRealName;

    /**
     * Whether the library can change the nick on the user's behalf if needed
     */
    private final boolean mNickChangeable;

    /**
     * The username to connect to the server with - unless the server is password protected this is
     * meaningless in most cases
     */
    private final String mServerUserName;

    /**
     * The password to connect to the server with
     */
    private final String mServerPassword;

    /**
     * The path to the PEM file which contains the certificate and private key
     */
    private final String mClientAuthenticationKeyPath;

    /**
     * The username for SASL authentication
     */
    private final String mSaslUsername;

    /**
     * The password for SASL authentication
     */
    private final String mSaslPassword;

    /**
     * The password for NickServ authentication
     */
    private final String mNickservPassword;

    /**
     * The list of all the channels that will be joined when connected to the server
     */
    private final List<String> mAutoJoinChannels;

    private ParcelableConnectionConfiguration(final Parcel in) {
        mTitle = in.readString();
        mUrl = in.readString();
        mPort = in.readInt();

        mSsl = in.readInt() == 1;
        mSslAcceptAllCertificates = in.readInt() == 1;

        mParcelableNickProvider = (NickProvider) in
                .readParcelable(ParcelableNickProvider.class.getClassLoader());
        mRealName = in.readString();
        mNickChangeable = in.readInt() == 1;

        mServerUserName = in.readString();
        mServerPassword = in.readString();
        mClientAuthenticationKeyPath = in.readString();

        mSaslUsername = in.readString();
        mSaslPassword = in.readString();

        mNickservPassword = in.readString();

        mAutoJoinChannels = new ArrayList<>();
        in.readStringList(mAutoJoinChannels);
    }

    private ParcelableConnectionConfiguration(final Builder builder) {
        mTitle = builder.getTitle();
        mUrl = builder.getUrl();
        mPort = builder.getPort();

        mSsl = builder.isSsl();
        mSslAcceptAllCertificates = builder.isSslAcceptAllCertificates();

        mParcelableNickProvider = builder.getNickProvider();
        mRealName = builder.getRealName();
        mNickChangeable = builder.isNickChangeable();

        mServerUserName = builder.getServerUserName();
        mServerPassword = builder.getServerPassword();
        mClientAuthenticationKeyPath = builder.getClientAuthenticationKeyPath();

        mSaslUsername = builder.getSaslUsername();
        mSaslPassword = builder.getSaslPassword();

        mNickservPassword = builder.getNickservPassword();

        mAutoJoinChannels = builder.getAutoJoinChannels();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel out, final int flags) {
        out.writeString(mTitle);
        out.writeString(mUrl);
        out.writeInt(mPort);

        out.writeInt(mSsl ? 1 : 0);
        out.writeInt(mSslAcceptAllCertificates ? 1 : 0);

        out.writeParcelable((Parcelable) mParcelableNickProvider, 0);
        out.writeString(mRealName);
        out.writeInt(mNickChangeable ? 1 : 0);

        out.writeString(mServerUserName);
        out.writeString(mServerPassword);
        out.writeString(mClientAuthenticationKeyPath);

        out.writeString(mSaslUsername);
        out.writeString(mSaslPassword);

        out.writeString(mNickservPassword);

        out.writeStringList(mAutoJoinChannels);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConnectionConfiguration) {
            final ConnectionConfiguration
                    configuration = (ConnectionConfiguration) o;
            return mTitle.equals(configuration.getTitle());
        }
        return false;
    }

    // Getters and setters
    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public int hashCode() {
        return mTitle.hashCode();
    }

    @Override
    public int getPort() {
        return mPort;
    }

    @Override
    public boolean isSslEnabled() {
        return mSsl;
    }

    @Override
    public boolean shouldAcceptAllSSLCertificates() {
        return mSslAcceptAllCertificates;
    }

    @Override
    public NickProvider getNickProvider() {
        return mParcelableNickProvider;
    }

    @Override
    public String getRealName() {
        return mRealName;
    }

    @Override
    public boolean isNickChangeable() {
        return mNickChangeable;
    }

    @Override
    public String getServerUserName() {
        return mServerUserName;
    }

    @Override
    public String getServerPassword() {
        return mServerPassword;
    }

    @Override
    public String getClientAuthenticationKeyPath() {
        return mClientAuthenticationKeyPath;
    }

    @Override
    public String getSaslUsername() {
        return mSaslUsername;
    }

    @Override
    public String getSaslPassword() {
        return mSaslPassword;
    }

    @Override
    public String getNickservPassword() {
        return mNickservPassword;
    }

    @Override
    public List<String> getAutoJoinChannels() {
        return mAutoJoinChannels;
    }

    public static class Builder implements Parcelable, ConnectionConfiguration.Builder {

        public static final Creator<ParcelableConnectionConfiguration.Builder> CREATOR =
                new Creator<ParcelableConnectionConfiguration.Builder>() {
                    public ParcelableConnectionConfiguration.Builder createFromParcel(
                            final Parcel in) {
                        return new ParcelableConnectionConfiguration.Builder(in);
                    }

                    public ParcelableConnectionConfiguration.Builder[] newArray(final int size) {
                        return new ParcelableConnectionConfiguration.Builder[size];
                    }
                };

        /**
         * The list of all the channels that will be joined when connected to the server
         */
        private final List<String> mAutoJoinChannels;

        /**
         * An integer identifier for this server - not used by the library but useful for storing
         * Builders in a database
         */
        private int mId;

        /**
         * The informal name of the server that is being connected to
         */
        private String mTitle;

        /**
         * The URL/hostname of the server that is being connected to
         */
        private String mUrl;

        /**
         * The port to connect to the server on
         */
        private int mPort;

        /**
         * Whether SSL should be used for the connection
         */
        private boolean mSsl;

        /**
         * Whether all SSL certificates should be accepted rather than just those trusted on the
         * device This option is meaningless without SSL being enabled
         */
        private boolean mSslAcceptAllCertificates;

        /**
         * The object containing the first, second and third choice nicks
         */
        private NickProvider mNickProvider;

        /**
         * The real name of the user
         */
        private String mRealName;

        /**
         * Whether the library can change the nick on the user's behalf if needed
         */
        private boolean mNickChangeable;

        /**
         * The username to connect to the server with - unless the server is password protected
         * this
         * is meaningless in most cases
         */
        private String mServerUserName;

        /**
         * The password to connect to the server with
         */
        private String mServerPassword;

        /**
         * The path to the PEM file which contains the certificate and private key
         */
        private String mClientAuthenticationKeyPath;

        /**
         * The username for SASL authentication
         */
        private String mSaslUsername;

        /**
         * The password for SASL authentication
         */
        private String mSaslPassword;

        /**
         * The password for NickServ authentication
         */
        private String mNickservPassword;

        public Builder() {
            mId = -1;

            mTitle = "";
            mUrl = "";
            mPort = 6667;

            mSsl = false;
            mSslAcceptAllCertificates = false;

            mNickProvider = new ParcelableNickProvider("relay", "", "");
            mRealName = "";
            mNickChangeable = true;

            mServerUserName = "relay";
            mServerPassword = "";
            mClientAuthenticationKeyPath = "";

            mSaslUsername = "";
            mSaslPassword = "";

            mNickservPassword = "";

            mAutoJoinChannels = new ArrayList<>();
        }

        private Builder(final Parcel in) {
            mId = in.readInt();

            mTitle = in.readString();
            mUrl = in.readString();
            mPort = in.readInt();

            mSsl = in.readInt() == 1;
            mSslAcceptAllCertificates = in.readInt() == 1;

            mNickProvider = (NickProvider) in
                    .readParcelable(ParcelableNickProvider.class.getClassLoader());
            mRealName = in.readString();
            mNickChangeable = in.readInt() == 1;

            mServerUserName = in.readString();
            mServerPassword = in.readString();
            mClientAuthenticationKeyPath = in.readString();

            mSaslUsername = in.readString();
            mSaslPassword = in.readString();

            mNickservPassword = in.readString();

            mAutoJoinChannels = new ArrayList<>();
            in.readStringList(mAutoJoinChannels);
        }

        @Override
        public ParcelableConnectionConfiguration build() {
            if (StringUtils.isEmpty(mTitle)) {
                throw new IllegalArgumentException("The server title cannot be empty");
            } else if (StringUtils.isEmpty(mUrl)) {
                throw new IllegalArgumentException("The server URL cannot be empty");
            }
            return new ParcelableConnectionConfiguration(this);
        }

        public int describeContents() {
            return 0;
        }

        // Getters and setters
        @Override
        public int getId() {
            return mId;
        }

        @Override
        public void setId(int id) {
            mId = id;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(mId);

            out.writeString(mTitle);
            out.writeString(mUrl);
            out.writeInt(mPort);

            out.writeInt(mSsl ? 1 : 0);
            out.writeInt(mSslAcceptAllCertificates ? 1 : 0);

            out.writeParcelable((Parcelable) mNickProvider, 0);
            out.writeString(mRealName);
            out.writeInt(mNickChangeable ? 1 : 0);

            out.writeString(mServerUserName);
            out.writeString(mServerPassword);
            out.writeString(mClientAuthenticationKeyPath);

            out.writeString(mSaslUsername);
            out.writeString(mSaslPassword);

            out.writeString(mNickservPassword);

            out.writeStringList(mAutoJoinChannels);
        }

        @Override
        public String getTitle() {
            return mTitle;
        }

        @Override
        public ConnectionConfiguration.Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ConnectionConfiguration.Builder) {
                final ConnectionConfiguration.Builder
                        builder = (ConnectionConfiguration.Builder) o;
                return mTitle.equals(builder.getTitle());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return mTitle.hashCode();
        }

        @Override
        public String getUrl() {
            return mUrl;
        }

        @Override
        public ConnectionConfiguration.Builder setUrl(String url) {
            mUrl = url;
            return this;
        }

        @Override
        public int getPort() {
            return mPort;
        }

        @Override
        public ConnectionConfiguration.Builder setPort(int port) {
            mPort = port;
            return this;
        }

        @Override
        public boolean isSsl() {
            return mSsl;
        }

        @Override
        public ConnectionConfiguration.Builder setSsl(boolean ssl) {
            mSsl = ssl;
            return this;
        }

        @Override
        public boolean isSslAcceptAllCertificates() {
            return mSslAcceptAllCertificates;
        }

        @Override
        public ConnectionConfiguration.Builder setSslAcceptAllCertificates(
                boolean sslAcceptAllCertificates) {
            mSslAcceptAllCertificates = sslAcceptAllCertificates;
            return this;
        }

        @Override
        public NickProvider getNickProvider() {
            return mNickProvider;
        }

        @Override
        public ConnectionConfiguration.Builder setNickProvider(final NickProvider nickProvider) {
            if (nickProvider instanceof Parcelable) {
                mNickProvider = nickProvider;
                return this;
            }

            throw new IllegalArgumentException("Only a parcelable nick provider can be passed in");
        }

        @Override
        public String getRealName() {
            return mRealName;
        }

        @Override
        public ConnectionConfiguration.Builder setRealName(String realName) {
            mRealName = realName;
            return this;
        }

        @Override
        public boolean isNickChangeable() {
            return mNickChangeable;
        }

        @Override
        public ConnectionConfiguration.Builder setNickChangeable(boolean nickChangeable) {
            mNickChangeable = nickChangeable;
            return this;
        }

        @Override
        public String getServerUserName() {
            return mServerUserName;
        }

        @Override
        public ConnectionConfiguration.Builder setServerUserName(String serverUserName) {
            mServerUserName = serverUserName;
            return this;
        }

        @Override
        public String getServerPassword() {
            return mServerPassword;
        }

        @Override
        public ConnectionConfiguration.Builder setServerPassword(String serverPassword) {
            mServerPassword = serverPassword;
            return this;
        }

        @Override
        public String getClientAuthenticationKeyPath() {
            return mClientAuthenticationKeyPath;
        }

        @Override
        public ConnectionConfiguration.Builder setClientAuthenticationKeyPath(
                final String clientAuthenticationKeyPath) {
            mClientAuthenticationKeyPath = clientAuthenticationKeyPath;
            return this;
        }

        @Override
        public String getSaslUsername() {
            return mSaslUsername;
        }

        @Override
        public ConnectionConfiguration.Builder setSaslUsername(String saslUsername) {
            mSaslUsername = saslUsername;
            return this;
        }

        @Override
        public String getSaslPassword() {
            return mSaslPassword;
        }

        @Override
        public ConnectionConfiguration.Builder setSaslPassword(String saslPassword) {
            mSaslPassword = saslPassword;
            return this;
        }

        @Override
        public String getNickservPassword() {
            return mNickservPassword;
        }

        @Override
        public ConnectionConfiguration.Builder setNickservPassword(String nickservPassword) {
            mNickservPassword = nickservPassword;
            return this;
        }

        @Override
        public List<String> getAutoJoinChannels() {
            return mAutoJoinChannels;
        }

        @Override
        public ConnectionConfiguration.Builder addAutoJoinChannel(final String channelName) {
            mAutoJoinChannels.add(channelName);
            return this;
        }
    }
}