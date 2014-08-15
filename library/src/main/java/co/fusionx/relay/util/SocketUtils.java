package co.fusionx.relay.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import co.fusionx.relay.ServerConfiguration;

public class SocketUtils {

    public static Socket openSocketConnection(final ServerConfiguration configuration) throws
            IOException {
        final Socket socket;
        final InetSocketAddress address = new InetSocketAddress(configuration.getUrl(),
                configuration.getPort());
        if (configuration.isSslEnabled()) {
            final SSLSocketFactory sslSocketFactory = getSSLSocketFactory(configuration
                    .shouldAcceptAllSSLCertificates());
            socket = sslSocketFactory.createSocket();
        } else {
            socket = new Socket();
        }

        socket.setKeepAlive(true);
        socket.connect(address, 5000);

        return socket;
    }

    private static SSLSocketFactory getSSLSocketFactory(final boolean acceptAll) {
        if (acceptAll) {
            try {
                final TrustManager[] tm = new TrustManager[]{new X509TrustManager() {
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
                SSLContext context = SSLContext.getInstance("SSL");
                context.init(new KeyManager[0], tm, new SecureRandom());
                return context.getSocketFactory();
            } catch (final NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }
        }
        return (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    public static BufferedWriter getSocketWriter(final Socket socket) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public static BufferedReader getSocketBufferedReader(final Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
}