package co.fusionx.relay.provider;

public class DefaultSettingsProvider implements SettingsProvider {

    @Override
    public boolean isSelfEventHidden() {
        return false;
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
    public int getReconnectAttempts() {
        return 3;
    }
}