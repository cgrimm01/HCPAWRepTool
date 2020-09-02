package com.hitachivantara.hcpaw.certutil;


import java.security.cert.CertificateParsingException;
import java.util.LinkedList;
import java.util.List;

public class ExtendedKeyUsageFilter extends AbstractListFilter<String> {
	/**
	 * Constructs a new ExtendedKeyUsageFilter where the oneOfKeyUsages
	 * parameter is a list of {@link String} values, where at least one need to
	 * exist in the extendedKeyUsage extension of the certificate.
	 * 
	 * @param oneOfKeyUsages
	 */
	public ExtendedKeyUsageFilter(List<String> oneOfKeyUsages) {
		setOneOfList(oneOfKeyUsages);
	}

	@Override
	protected List<String> getList(CertificateWrapper certificate) throws CertificateParsingException {
		List<String> retval = certificate.getExtendedKeyUsages();
		
		if (null == retval) {
			retval = new LinkedList<String>();
		}
		
		return retval;
	}

}
