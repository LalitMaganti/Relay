package com.fusionx.relay.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLUtils {

    public static SSLSocketFactory getAppropriateSSLFactory(final boolean acceptAll) {
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
            } catch (final NoSuchAlgorithmException e) {
                return (SSLSocketFactory) SSLSocketFactory.getDefault();
            } catch (final KeyManagementException e) {
                return (SSLSocketFactory) SSLSocketFactory.getDefault();
            }
        } else {
            return (SSLSocketFactory) SSLSocketFactory.getDefault();
        }
    }
}