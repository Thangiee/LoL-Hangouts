package com.thangiee.LoLWithFriends.api;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

/**
 * An SSL socket factory that will let any certifacte past, even if it's expired
 * or not singed by a root CA.
 */
public class DummySSLSocketFactory extends SSLSocketFactory {

	public static SocketFactory getDefault() {
		return new DummySSLSocketFactory();
	}

	private SSLSocketFactory factory;

	public DummySSLSocketFactory() {

		try {
			final SSLContext sslcontent = SSLContext.getInstance("TLS");
			sslcontent.init(
					null, // KeyManager not required
					new TrustManager[] { new DummyTrustManager() },
					new java.security.SecureRandom());
			factory = sslcontent.getSocketFactory();
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (final KeyManagementException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Socket createSocket(InetAddress inaddr, int i) throws IOException {
		return factory.createSocket(inaddr, i);
	}

	@Override
	public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr2,
			int j) throws IOException {
		return factory.createSocket(inaddr, i, inaddr2, j);
	}

	@Override
	public Socket createSocket(Socket socket, String s, int i, boolean flag)
			throws IOException {
		return factory.createSocket(socket, s, i, flag);
	}

	@Override
	public Socket createSocket(String s, int i) throws IOException {
		return factory.createSocket(s, i);
	}

	@Override
	public Socket createSocket(String s, int i, InetAddress inaddr, int j)
			throws IOException {
		return factory.createSocket(s, i, inaddr, j);
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return factory.getSupportedCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return factory.getSupportedCipherSuites();
	}
}

/**
 * Trust manager which accepts certificates without any validation except date
 * validation.
 */
class DummyTrustManager implements X509TrustManager {

	public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
			throws CertificateException {
		// Do nothing for now.
	}

	public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
			throws CertificateException {
		// Do nothing for now.
	}

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

	public boolean isClientTrusted(X509Certificate[] cert) {
		return true;
	}

	public boolean isServerTrusted(X509Certificate[] cert) {
		try {
			cert[0].checkValidity();
			return true;
		} catch (final CertificateExpiredException e) {
			return false;
		} catch (final CertificateNotYetValidException e) {
			return false;
		}
	}
}
