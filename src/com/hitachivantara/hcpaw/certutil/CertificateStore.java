package com.hitachivantara.hcpaw.certutil;


import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.ProviderException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.hitachivantara.hcpaw.Helper;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificateStore implements Iterable<CertificateWrapper> {
	KeyStore mKeyStore;
	
	static String INTERNAL_KEYSTORE_PASSWORD = "SwizzleStick";
	
	public CertificateStore(String inKeyStorePath, String inKeyStorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		switch (inKeyStorePath) {
		case "Windows-MY":
        	mKeyStore = KeyStore.getInstance("Windows-MY");
        	mKeyStore.load(null, null);
			break;
		case "CAC":
	        //Create our certificates from our CAC Card
			String configName = System.getProperty("pkcs11.config", "card.config");

			try {
		        Provider p = new sun.security.pkcs11.SunPKCS11(configName);
		        Security.addProvider(p);
			} catch (ProviderException e) {
				Helper.mylog(Helper.LOG_ERROR,"ERROR: Failed to initialize Common Access Card (CAC) provider.");
				throw e;
			}

	        try {
		        mKeyStore = KeyStore.getInstance("PKCS11");
		        mKeyStore.load(null, inKeyStorePassword.toCharArray());
	        } catch (KeyStoreException e) {
				Helper.mylog(Helper.LOG_ERROR,"ERROR: Unable to read Common Access Card (CAC). Reason: " + e.getMessage());
				throw e;
	        }
	        
			break;
		default:
            mKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            mKeyStore.load(new FileInputStream(inKeyStorePath), inKeyStorePassword.toCharArray());
			break;
		}
	}
	
	public CertificateStore(KeyStore inKeyStore) {
		mKeyStore = inKeyStore;
	}

	/**
	 * Returns the keystore, which contains personal certificates with private keys owned by the user.
	 * 
	 * @return
	 */
	public KeyStore getKeyStore() {
		return mKeyStore;
	}

	/**
	 * Creates a temporary certificate store in the cache/memory to store
	 * certificates for short time of period during the application lifecycle.
	 * The store is not saved automatically somewhere else and is lost after the
	 * application exits.
	 * 
	 * @return
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static CertificateStore newCachedCertStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

		ks.load(null, INTERNAL_KEYSTORE_PASSWORD.toCharArray());

		return new CertificateStore(ks);
	}

	public String getType() {
		return mKeyStore.getType();
	}
	
	public String getProviderName() {
		return mKeyStore.getProvider().getName();
	}
	
	public Provider getProvider() {
		return mKeyStore.getProvider();
	}
	
	public int getSize() throws KeyStoreException {
		return mKeyStore.size();
	}

	/**
	 * Get a list of certificates in this store.
	 * 
	 * @return
	 * @throws KeyStoreException 
	 */
	public List<CertificateWrapper> getCertificatesInStore() {
		List<CertificateWrapper> certContextList = new ArrayList<>();
		
		try {
	        for (Enumeration<String> e = mKeyStore.aliases();
	                e.hasMoreElements(); ) {
	        	
	        	String alias = e.nextElement();

	        	Certificate tmpCert = mKeyStore.getCertificate(alias);
	        	
	        	if (tmpCert instanceof X509Certificate) {
	        		X509Certificate oneCert = (X509Certificate)tmpCert;
	        		
	    			certContextList.add(new CertificateWrapper(oneCert, alias));
	        	}
	        }
		} catch (KeyStoreException e) {
			// Just return non-existant list.
			certContextList = null;
		}

		return certContextList;
	}

	/**
	 * Adds the given certificate to this store.
	 * 
	 * @param certificate
	 * @throws KeyStoreException 
	 */
	public void addCertificateToStore(CertificateWrapper certificate) throws KeyStoreException {
		mKeyStore.setCertificateEntry(certificate.getAlias(), certificate.getX509Certificate());
	}

	public static class Copy {
		private List<CertificateFilter> filters = new ArrayList<>();

		public Copy() {
		}

		/**
		 * Adds a filter to this instance which will be used to filter the list
		 * of certificates that will be copied by
		 * {@link #copyOf(CertificateStore)}. All filters are connected with
		 * AND, so that one certificate must pass all filters to be included in
		 * the new created store.
		 * 
		 * @param filter
		 * @return
		 */
		public Copy addFilter(CertificateFilter filter) {
			filters.add(filter);

			return this;
		}

		/**
		 * Iterates over all certificates of the passed
		 * {@link CertificateStore}, which should be an already populated
		 * certificate store, and links these certificates to a newly created
		 * temporary certificate store.
		 * 
		 * THis method also filters the certificate of the passed
		 * {@link CertificateStore} by all {@link CertificateFilter} passed to
		 * it by the {@link #addFilter(CertificateFilter)} method.
		 * 
		 * @param store
		 * @throws IOException 
		 * @throws CertificateException 
		 * @throws NoSuchAlgorithmException 
		 * @throws KeyStoreException 
		 */
		public CertificateStore copyOf(CertificateStore store) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
			CertificateStore newStore = CertificateStore.newCachedCertStore();

			List<CertificateWrapper> certList = store.getCertificatesInStore();
			for (CertificateWrapper oneCert : certList) {
				boolean include = true;
				for (CertificateFilter filter : filters) {
					if (!filter.accept(oneCert)) {
						include = false;
						break; // Found one filter that did not match, so done.
					}
				}

				if (include) {
					newStore.addCertificateToStore(oneCert);
				}
			}
			
			return newStore;
		}
	}

	@Override
	public Iterator<CertificateWrapper> iterator() {
		return getCertificatesInStore().iterator();
	}

}
