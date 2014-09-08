package co.fusionx.relay.util;

import com.google.common.io.Files;

import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openssl.PEMParser;
import org.spongycastle.openssl.jcajce.JcaPEMKeyConverter;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import co.fusionx.relay.base.ConnectionConfiguration;

public class SocketUtils {

    public static Socket openSocketConnection(final ConnectionConfiguration configuration) throws
            IOException {
        final Socket socket;
        final InetSocketAddress address = new InetSocketAddress(configuration.getUrl(),
                configuration.getPort());
        if (configuration.isSslEnabled()) {
            final SSLSocketFactory sslSocketFactory = getSSLSocketFactory(configuration);
            socket = sslSocketFactory.createSocket();
        } else {
            socket = new Socket();
        }

        socket.setKeepAlive(true);
        socket.connect(address, 5000);

        return socket;
    }

    private static SSLSocketFactory getSSLSocketFactory(final ConnectionConfiguration configuration) {
        if (!configuration.shouldAcceptAllSSLCertificates() &&
                TextUtils.isEmpty(configuration.getClientAuthenticationKeyPath())) {
            return (SSLSocketFactory) SSLSocketFactory.getDefault();
        }

        TrustManager[] tm = null;
        if (configuration.shouldAcceptAllSSLCertificates()) {
            tm = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(final X509Certificate[] cert, String authType) {
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] cert, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
        }

        try {
            final SSLContext context = SSLContext.getInstance("SSL");
            if (TextUtils.isEmpty(configuration.getClientAuthenticationKeyPath())) {
                context.init(new KeyManager[0], tm, new SecureRandom());
                return context.getSocketFactory();
            }

            Security.addProvider(new BouncyCastleProvider());
            final byte[] certAndKey = Files.toByteArray(new File(
                    configuration.getClientAuthenticationKeyPath()));

            String delimiter = "-----END CERTIFICATE-----";
            String[] tokens = new String(certAndKey).split(delimiter);

            byte[] certBytes = tokens[0].concat(delimiter).getBytes();
            byte[] keyBytes = tokens[1].getBytes();

            PEMParser reader;
            reader = new PEMParser(new InputStreamReader(new ByteArrayInputStream(certBytes)));
            X509CertificateHolder certHolder = (X509CertificateHolder) reader.readObject();
            final X509Certificate certificate = new JcaX509CertificateConverter()
                    .getCertificate(certHolder);

            reader = new PEMParser(new InputStreamReader(new ByteArrayInputStream(keyBytes)));
            PrivateKeyInfo key = (PrivateKeyInfo) reader.readObject();
            final PrivateKey privateKey = new JcaPEMKeyConverter().getPrivateKey(key);

            final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null);
            keystore.setCertificateEntry("cert-alias", certificate);
            keystore.setKeyEntry("key-alias", privateKey, null, new Certificate[]{certificate});

            final KeyManagerFactory kmf = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keystore, null);
            context.init(kmf.getKeyManagers(), tm, new SecureRandom());

            return context.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    public static BufferedWriter getSocketBufferedWriter(final Socket socket) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public static BufferedReader getSocketBufferedReader(final Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
}