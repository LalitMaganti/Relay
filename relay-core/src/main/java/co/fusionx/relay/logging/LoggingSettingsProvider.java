package co.fusionx.relay.logging;

public interface LoggingSettingsProvider {

    public boolean shouldLogTimestamps();

    public String getLoggingPath();
}
