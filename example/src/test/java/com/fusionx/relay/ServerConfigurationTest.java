package com.fusionx.relay;

import com.fusionx.relay.misc.NickStorage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.os.Parcel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class ServerConfigurationTest {

    // Builder statics
    private static ServerConfiguration.Builder getFreenodeConfigurationBuilder() {
        final ServerConfiguration.Builder builder = new ServerConfiguration.Builder();
        builder.setTitle("Freenode");
        builder.setUrl("irc.freenode.net");
        builder.setPort(6667);
        builder.setNickStorage(new NickStorage("holoirctester", "holoirctester", "holoirctester"));
        builder.setServerUserName("holoirctester");
        return builder;
    }

    // Configuration statics
    public static ServerConfiguration getFreenodeConfiguration() {
        return getFreenodeConfigurationBuilder().build();
    }

    // Builder tests
    @Test
    public void testBuilderParcelling() {
        final Parcel parcel = Parcel.obtain();
        final ServerConfiguration.Builder expected = getFreenodeConfigurationBuilder();
        expected.writeToParcel(parcel, 0);

        // done writing, now reset parcel for reading
        parcel.setDataPosition(0);

        final ServerConfiguration.Builder actual = ServerConfiguration.Builder.CREATOR
                .createFromParcel(parcel);

        assertNotNull(actual);
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getPort(), actual.getPort());

        assertEquals(expected.isSsl(), actual.isSsl());
        assertEquals(expected.isSslAcceptAllCertificates(), actual.isSslAcceptAllCertificates());

        assertEquals(expected.getNickStorage(), actual.getNickStorage());
        assertEquals(expected.getRealName(), actual.getRealName());
        assertEquals(expected.isNickChangeable(), actual.isNickChangeable());

        assertEquals(expected.getServerUserName(), actual.getServerUserName());
        assertEquals(expected.getServerPassword(), actual.getServerPassword());

        assertEquals(expected.getSaslUsername(), actual.getSaslUsername());
        assertEquals(expected.getSaslPassword(), actual.getSaslPassword());

        assertEquals(expected.getNickservPassword(), actual.getNickservPassword());

        assertEquals(expected.getAutoJoinChannels(), actual.getAutoJoinChannels());
    }

    // Configuration tests
    @Test
    public void testBuild() {
        final ServerConfiguration.Builder builder = getFreenodeConfigurationBuilder();
        final ServerConfiguration configuration = getFreenodeConfiguration();
        assertEquals(builder.getTitle(), configuration.getTitle());
    }

    @Test
    public void testParcelling() {
        final Parcel parcel = Parcel.obtain();
        final ServerConfiguration expected = getFreenodeConfiguration();
        expected.writeToParcel(parcel, 0);

        // done writing, now reset parcel for reading
        parcel.setDataPosition(0);

        final ServerConfiguration actual = ServerConfiguration.CREATOR.createFromParcel(parcel);

        assertNotNull(actual);
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getPort(), actual.getPort());

        assertEquals(expected.isSslEnabled(), actual.isSslEnabled());
        assertEquals(expected.shouldAcceptAllSSLCertificates(),
                actual.shouldAcceptAllSSLCertificates());

        assertEquals(expected.getNickStorage(), actual.getNickStorage());
        assertEquals(expected.getRealName(), actual.getRealName());
        assertEquals(expected.isNickChangeable(), actual.isNickChangeable());

        assertEquals(expected.getServerUserName(), actual.getServerUserName());
        assertEquals(expected.getServerPassword(), actual.getServerPassword());

        assertEquals(expected.getSaslUsername(), actual.getSaslUsername());
        assertEquals(expected.getSaslPassword(), actual.getSaslPassword());

        assertEquals(expected.getNickservPassword(), actual.getNickservPassword());

        assertEquals(expected.getAutoJoinChannels(), actual.getAutoJoinChannels());
    }
}