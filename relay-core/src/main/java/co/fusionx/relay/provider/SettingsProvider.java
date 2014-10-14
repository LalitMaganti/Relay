package co.fusionx.relay.provider;

public interface SettingsProvider {

    public boolean isSelfEventHidden();

    public String getPartReason();

    public String getQuitReason();

    public int getReconnectAttempts();
}