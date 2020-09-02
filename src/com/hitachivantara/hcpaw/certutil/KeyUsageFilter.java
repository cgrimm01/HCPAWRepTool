package com.hitachivantara.hcpaw.certutil;


import java.util.LinkedList;
import java.util.List;

public class KeyUsageFilter extends AbstractListFilter<KeyUsage> {
	/**
	 * Constructs a new KeyUsageFilter where the oneOfKeyUsages parameter is a
	 * list of {@link KeyUsage} values, where at least one need to exist in the
	 * key usages of the certificate.
	 * 
	 * @param oneOfKeyUsages
	 */
	public KeyUsageFilter(List<KeyUsage> oneOfKeyUsages) {
		setOneOfList(oneOfKeyUsages);
	}

	@Override
	protected List<KeyUsage> getList(CertificateWrapper certificate) {
		List<KeyUsage> retval = certificate.getKeyUsage();
		
		if (null == retval) {
			retval = new LinkedList<KeyUsage>();
		}
		
		return retval;
	}
}
