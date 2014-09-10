package co.fusionx.relay.internal.core;

import co.fusionx.relay.core.SettingsProvider;

public class DefaultSettingsProvider implements SettingsProvider {

    @Override
    public boolean isSelfEventHidden() {
        return false;
    }

    @Override
    public void logNonFatalError(final String nonFatalError) {
    }

    @Override
    public String getPartReason() {
        return null;
    }

    @Override
    public String getQuitReason() {
        return null;
    }

    @Override
    public void handleFatalError(final RuntimeException ex) {

    }

    @Override
    public int getReconnectAttempts() {
        return 3;
    }
}