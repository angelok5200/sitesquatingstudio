package org.tafel.squating.adapters.tls;

import org.springframework.stereotype.Component;
import org.tafel.squating.domain.value.TlsSnapshot;
import org.tafel.squating.ports.outbound.TlsInspector;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class SslTlsInspectorAdapter implements TlsInspector {

    @Override
    public TlsSnapshot inspectTls(String domainName) {
        try {
            URL url = new URL("https://" + domainName);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            // Trust-all context only for reading peer certificate details
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }}, new java.security.SecureRandom());

            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.connect();

            Certificate[] certs = conn.getServerCertificates();
            if (certs != null && certs.length > 0 && certs[0] instanceof X509Certificate x509) {
                String subject = x509.getSubjectX500Principal().getName();
                String issuer = x509.getIssuerX500Principal().getName();
                Instant notBefore = x509.getNotBefore().toInstant();
                Instant notAfter = x509.getNotAfter().toInstant();

                List<String> sanList = new ArrayList<>();
                try {
                    var sans = x509.getSubjectAlternativeNames();
                    if (sans != null) {
                        for (var item : sans) {
                            if (item.size() > 1 && item.get(1) != null) {
                                sanList.add(item.get(1).toString());
                            }
                        }
                    }
                } catch (Exception ignored) {}

                boolean isSelfSigned = subject.equals(issuer);

                conn.disconnect();
                return new TlsSnapshot(
                    true,
                    subject,
                    issuer,
                    notBefore,
                    notAfter,
                    sanList,
                    isSelfSigned
                );
            }
            conn.disconnect();
        } catch (Exception ignored) {
        }

        return TlsSnapshot.none();
    }
}
