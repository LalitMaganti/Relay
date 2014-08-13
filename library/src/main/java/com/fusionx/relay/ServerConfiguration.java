package com.fusionx.relay;

import com.fusionx.relay.misc.NickStorage;
import com.fusionx.relay.util.Utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ServerConfiguration implements Parcelable {

    public static final Parcelable.Creator<ServerConfiguration> CREATOR =
            new Parcelable.Creator<ServerConfiguration>() {
                public ServerConfiguration createFromParcel(Parcel in) {
                    return new ServerConfiguration(in);
                }

                public ServerConfiguration[] newArray(int size) {
                    return new ServerConfiguration[size];
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
    private final NickStorage mNickStorage;

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

    private ServerConfiguration(final Parcel in) {
        mTitle = in.readString();
        mUrl = in.readString();
        mPort = in.readInt();

        mSsl = in.readInt() == 1;
        mSslAcceptAllCertificates = in.readInt() == 1;

        mNickStorage = in.readParcelable(NickStorage.class.getClassLoader());
        mRealName = in.readString();
        mNickChangeable = in.readInt() == 1;

        mServerUserName = in.readString();
        mServerPassword = in.readString();

        mSaslUsername = in.readString();
        mSaslPassword = in.readString();

        mNickservPassword = in.readString();

        mAutoJoinChannels = new ArrayList<>();
        in.readStringList(mAutoJoinChannels);
    }

    private ServerConfiguration(final Builder builder) {
        mTitle = builder.getTitle();
        mUrl = builder.getUrl();
        mPort = builder.getPort();

        mSsl = builder.isSsl();
        mSslAcceptAllCertificates = builder.isSslAcceptAllCertificates();

        mNickStorage = builder.getNickStorage();
        mRealName = builder.getRealName();
        mNickChangeable = builder.isNickChangeable();

        mServerUserName = builder.getServerUserName();
        mServerPassword = builder.getServerPassword();

        mSaslUsername = builder.getSaslUsername();
        mSaslPassword = builder.getSaslPassword();

        mNickservPassword = builder.getNickservPassword();

        mAutoJoinChannels = builder.getAutoJoinChannels();
    }

    public int describeContents() {
        return 0;
    }

    // Helper methods
    public boolean isSaslAvailable() {
        return Utils.isNotEmpty(mSaslUsername) && Utils.isNotEmpty(mSaslPassword);
    }

    public void writeToParcel(final Parcel out, final int flags) {
        out.writeString(mTitle);
        out.writeString(mUrl);
        out.writeInt(mPort);

        out.writeInt(mSsl ? 1 : 0);
        out.writeInt(mSslAcceptAllCertificates ? 1 : 0);

        out.writeParcelable(mNickStorage, 0);
        out.writeString(mRealName);
        out.writeInt(mNickChangeable ? 1 : 0);

        out.writeString(mServerUserName);
        out.writeString(mServerPassword);

        out.writeString(mSaslUsername);
        out.writeString(mSaslPassword);

        out.writeString(mNickservPassword);

        out.writeStringList(mAutoJoinChannels);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ServerConfiguration) {
            final ServerConfiguration configuration = (ServerConfiguration) o;
            return mTitle.equals(configuration.getTitle());
        }
        return false;
    }

    // Getters and setters
    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public int hashCode() {
        return mTitle.hashCode();
    }

    public int getPort() {
        return mPort;
    }

    public boolean isSslEnabled() {
        return mSsl;
    }

    public boolean shouldAcceptAllSSLCertificates() {
        return mSslAcceptAllCertificates;
    }

    public NickStorage getNickStorage() {
        return mNickStorage;
    }

    public String getRealName() {
        return mRealName;
    }

    public boolean isNickChangeable() {
        return mNickChangeable;
    }

    public String getServerUserName() {
        return mServerUserName;
    }

    public String getServerPassword() {
        return mServerPassword;
    }

    public String getSaslUsername() {
        return mSaslUsername;
    }

    public String getSaslPassword() {
        return mSaslPassword;
    }

    public String getNickservPassword() {
        return mNickservPassword;
    }

    public List<String> getAutoJoinChannels() {
        return mAutoJoinChannels;
    }

    public static class Builder implements Parcelable {

        public static final Parcelable.Creator<Builder> CREATOR =
                new Parcelable.Creator<Builder>() {
                    public Builder createFromParcel(final Parcel in) {
                        return new Builder(in);
                    }

                    public Builder[] newArray(final int size) {
                        return new Builder[size];
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
        private NickStorage mNickStorage;

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

            mNickStorage = new NickStorage("relay", "", "");
            mRealName = "";
            mNickChangeable = true;

            mServerUserName = "relay";
            mServerPassword = "";

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

            mNickStorage = in.readParcelable(NickStorage.class.getClassLoader());
            mRealName = in.readString();
            mNickChangeable = in.readInt() == 1;

            mServerUserName = in.readString();
            mServerPassword = in.readString();

            mSaslUsername = in.readString();
            mSaslPassword = in.readString();

            mNickservPassword = in.readString();

            mAutoJoinChannels = new ArrayList<>();
            in.readStringList(mAutoJoinChannels);
        }

        public ServerConfiguration build() {
            if (TextUtils.isEmpty(mTitle)) {
                throw new IllegalArgumentException("The server title cannot be empty");
            } else if (TextUtils.isEmpty(mUrl)) {
                throw new IllegalArgumentException("The server URL cannot be empty");
            }
            return new ServerConfiguration(this);
        }

        public int describeContents() {
            return 0;
        }

        // Getters and setters
        public int getId() {
            return mId;
        }

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

            out.writeParcelable(mNickStorage, 0);
            out.writeString(mRealName);
            out.writeInt(mNickChangeable ? 1 : 0);

            out.writeString(mServerUserName);
            out.writeString(mServerPassword);

            out.writeString(mSaslUsername);
            out.writeString(mSaslPassword);

            out.writeString(mNickservPassword);

            out.writeStringList(mAutoJoinChannels);
        }

        public String getTitle() {
            return mTitle;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Builder) {
                final Builder builder = (Builder) o;
                return mTitle.equals(builder.getTitle());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return mTitle.hashCode();
        }

        public String getUrl() {
            return mUrl;
        }

        public Builder setUrl(String url) {
            mUrl = url;
            return this;
        }

        public int getPort() {
            return mPort;
        }

        public Builder setPort(int port) {
            mPort = port;
            return this;
        }

        public boolean isSsl() {
            return mSsl;
        }

        public Builder setSsl(boolean ssl) {
            mSsl = ssl;
            return this;
        }

        public boolean isSslAcceptAllCertificates() {
            return mSslAcceptAllCertificates;
        }

        public Builder setSslAcceptAllCertificates(boolean sslAcceptAllCertificates) {
            mSslAcceptAllCertificates = sslAcceptAllCertificates;
            return this;
        }

        public NickStorage getNickStorage() {
            return mNickStorage;
        }

        public Builder setNickStorage(NickStorage nickStorage) {
            mNickStorage = nickStorage;
            return this;
        }

        public String getRealName() {
            return mRealName;
        }

        public Builder setRealName(String realName) {
            mRealName = realName;
            return this;
        }

        public boolean isNickChangeable() {
            return mNickChangeable;
        }

        public Builder setNickChangeable(boolean nickChangeable) {
            mNickChangeable = nickChangeable;
            return this;
        }

        public String getServerUserName() {
            return mServerUserName;
        }

        public Builder setServerUserName(String serverUserName) {
            mServerUserName = serverUserName;
            return this;
        }

        public String getServerPassword() {
            return mServerPassword;
        }

        public Builder setServerPassword(String serverPassword) {
            mServerPassword = serverPassword;
            return this;
        }

        public String getSaslUsername() {
            return mSaslUsername;
        }

        public Builder setSaslUsername(String saslUsername) {
            mSaslUsername = saslUsername;
            return this;
        }

        public String getSaslPassword() {
            return mSaslPassword;
        }

        public Builder setSaslPassword(String saslPassword) {
            mSaslPassword = saslPassword;
            return this;
        }

        public String getNickservPassword() {
            return mNickservPassword;
        }

        public Builder setNickservPassword(String nickservPassword) {
            mNickservPassword = nickservPassword;
            return this;
        }

        public List<String> getAutoJoinChannels() {
            return mAutoJoinChannels;
        }

        public Builder addAutoJoinChannel(final String channelName) {
            mAutoJoinChannels.add(channelName);
            return this;
        }
    }
}