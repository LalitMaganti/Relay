package co.fusionx.relay.configuration;

import com.google.common.base.Preconditions;

import co.fusionx.relay.provider.DebuggingProvider;
import co.fusionx.relay.provider.DefaultDebuggingProvider;
import co.fusionx.relay.provider.DefaultSettingsProvider;
import co.fusionx.relay.provider.SettingsProvider;

public final class SessionConfiguration {

    private final ConnectionConfiguration mConnectionConfiguration;

    private final SettingsProvider mSettingsProvider;

    private final DebuggingProvider mDebuggingProvider;

    private SessionConfiguration(final ConnectionConfiguration connectionConfiguration,
            final SettingsProvider settingsProvider, final DebuggingProvider debuggingProvider) {
        mConnectionConfiguration = connectionConfiguration;
        mSettingsProvider = settingsProvider;
        mDebuggingProvider = debuggingProvider;
    }

    public SettingsProvider getSettingsProvider() {
        return mSettingsProvider;
    }

    public ConnectionConfiguration getConnectionConfiguration() {
        return mConnectionConfiguration;
    }

    public DebuggingProvider getDebuggingProvider() {
        return mDebuggingProvider;
    }

    public static class Builder {

        private ConnectionConfiguration mConnectionConfiguration;

        private SettingsProvider mSettingsProvider;

        private DebuggingProvider mDebuggingProvider;

        public Builder() {
            mSettingsProvider = new DefaultSettingsProvider();
            mDebuggingProvider = new DefaultDebuggingProvider();
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

        public Builder setDebuggingProvider(final DebuggingProvider debuggingProvider) {
            Preconditions.checkNotNull(debuggingProvider, "DebuggingProvider cannot be null");
            mDebuggingProvider = debuggingProvider;
            return this;
        }

        public SessionConfiguration build() {
            Preconditions.checkNotNull(mConnectionConfiguration,
                    "Connection configuration cannot be null");
            return new SessionConfiguration(mConnectionConfiguration, mSettingsProvider,
                    mDebuggingProvider);
        }
    }
}