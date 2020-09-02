package com.hitachivantara.hcpaw.certutil;
import java.security.cert.CertificateParsingException;

public interface CertificateFilter {
	/**
	 * The main filter function. If called, the return value should be a boolean
	 * where true means, that the certificate should be included and false that
	 * it should not be included in a newly created certificate store.
	 * 
	 * @param certificate
	 * @return
	 */
	boolean accept(CertificateWrapper certificate) throws CertificateParsingException;
}
