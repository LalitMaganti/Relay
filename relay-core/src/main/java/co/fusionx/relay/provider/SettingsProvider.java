package co.fusionx.relay.provider;

public interface SettingsProvider {

    public boolean isSelfEventHidden();

    public void logNonFatalError(String nonFatalError);

    public String getPartReason();

    public String getQuitReason();

    public void handleFatalError(RuntimeException ex);

    public int getReconnectAttempts();
}