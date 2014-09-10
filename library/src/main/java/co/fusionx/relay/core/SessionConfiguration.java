package co.fusionx.relay.core;

import com.google.common.base.Preconditions;

import co.fusionx.relay.internal.core.DefaultSettingsProvider;

public class SessionConfiguration {

    private final ConnectionConfiguration mConnectionConfiguration;

    private final SettingsProvider mSettingsProvider;

    private SessionConfiguration(final ConnectionConfiguration connectionConfiguration,
            final SettingsProvider settingsProvider) {
        mConnectionConfiguration = connectionConfiguration;
        mSettingsProvider = settingsProvider;
    }

    public SettingsProvider getSettingsProvider() {
        return mSettingsProvider;
    }

    public ConnectionConfiguration getConnectionConfiguration() {
        return mConnectionConfiguration;
    }

    public static class Builder {

        private ConnectionConfiguration mConnectionConfiguration;

        private SettingsProvider mSettingsProvider;

        public Builder() {
            mSettingsProvider = new DefaultSettingsProvider();
        }

        public Builder setConnectionConfiguration(final ConnectionConfiguration configuration) {
            Preconditions.checkNotNull(configuration, "Configuration cannot be null");
            mConnectionConfiguration = configuration;
            return this;
        }

        public Builder setSettingsProvider(final SettingsProvider configuration) {
            Preconditions.checkNotNull(configuration, "Configuration cannot be null");
            mSettingsProvider = configuration;
            return this;
        }

        public SessionConfiguration build() {
            Preconditions.checkNotNull(mConnectionConfiguration,
                    "Connection configuration cannot be null");
            return new SessionConfiguration(mConnectionConfiguration, mSettingsProvider);
        }
    }
}