package com.hitachivantara.hcpaw.certutil;


import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CertificateWrapper {

	private X509Certificate mCertificate;
	private String mAlias;

	public CertificateWrapper(X509Certificate inCert, String inAlias) {
		mCertificate = inCert;
		mAlias = inAlias;
	}

	public X509Certificate getX509Certificate() {
		return mCertificate;
	}

	public String getSubjectDN() {
		String retval = null;
		
		// Subject CN or EMAILADDRESS
		String subjectDN = mCertificate.getSubjectDN().getName();
		Pattern pattern = Pattern.compile("(CN|EMAILADDRESS)=(\".*\"([,\\s]|$)|[^\"][^,]*(,|$))");
	    Matcher matcher = pattern.matcher(subjectDN);
		if (matcher.find()) {
			retval = matcher.group(0).split("=")[1].replaceAll(",$", "").replaceAll("\"",  "");
		}
		
		return retval;
	}
	
	public String getIssuer() {
		String retval = null;
		
		// Issuer CN
		String issuer = mCertificate.getIssuerDN().getName();
		Pattern pattern = Pattern.compile("CN=(\".*\"([,\\s]|$)|[^\"][^,]*(,|$))");
	    Matcher matcher = pattern.matcher(issuer);
		if (matcher.find()) {
			retval = matcher.group(0).split("=")[1].replaceAll(",$", "");
		}
		
		return retval;
	}
	
	public Date getValidNotBefore() {
		return mCertificate.getNotBefore();
	}
	
	public String getValidNotBeforeString(String dateFormat) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

		return simpleDateFormat.format(mCertificate.getNotBefore());
	}
	
	public Date getValidNotAfter() {
		return mCertificate.getNotAfter();
	}
	
	public String getValidNotAfterString(String dateFormat) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

		return simpleDateFormat.format(mCertificate.getNotAfter());
	}
	
	/**
	 * The name/alias of the certificate as returned by the CertGetNameString
	 * method.
	 * 
	 * @return
	 */
	public String getAlias() {
		return mAlias;
	}

	/**
	 * The length of the array returned by {@link #getEncodedByteArray()}.
	 * 
	 * @return
	 * @throws CertificateEncodingException 
	 */
	public int getEncodedLength() throws CertificateEncodingException {
		return mCertificate.getEncoded().length;
	}

	/**
	 * Returns the certificate as an encoded byte array, which represents the
	 * whole certificate with all of it's attributes.
	 * 
	 * @return
	 * @throws CertificateEncodingException 
	 */
	public byte[] getEncodedByteArray() throws CertificateEncodingException {
		return mCertificate.getEncoded();
	}

	/**
	 * Returns a list of KeyUsages of this certificate. Unlike the
	 * {@link X509Certificate#getKeyUsage()} method, it returns a {@link List}
	 * of {@link KeyUsage} instead of an array of Booleans.
	 * 
	 * This method returns an empty list of KeyUsage if the CERT_CONTEXT does
	 * not have the pbCertEncoded or cbCertEncoded fields be set.
	 * 
	 * @return
	 */
	public List<KeyUsage> getKeyUsage() {
		List<KeyUsage> keyUsagesList = new LinkedList<KeyUsage>();

		// Convert native type to X509Certificate, which allows an easier
		// access to the KeyUsage extension
		X509Certificate cert = getX509Certificate();
		boolean[] certKeyUsages = cert.getKeyUsage();
		if (certKeyUsages == null) {
			return keyUsagesList;
		}
		for (int i = 0; i < KeyUsage.values().length; i++) {
			KeyUsage usage = KeyUsage.values()[i];
			if (certKeyUsages[i]) {
				keyUsagesList.add(usage);
			}
		}

		return keyUsagesList;
	}

	/**
	 * Returns a list of OIDs of the enhanced/extended key usage extension of
	 * the passed CERT_CONTEXT structure. The implementation, at the moment,
	 * relies on communication with the native crypto API.
	 * 
	 * @return
	 * @throws CertificateParsingException 
	 */
	public List<String> getExtendedKeyUsages() throws CertificateParsingException {
		return mCertificate.getExtendedKeyUsage();
	}
	
	public String getSummary() {
		StringBuilder output = new StringBuilder();
		Pattern pattern;
		Matcher matcher;

		/*
		// Subject EMAILADDRESS
		String subjectDN = mCertificate.getSubjectDN().getName();
		pattern = Pattern.compile("(EMAILADDRESS|CN)=(.*?)[,|\\s]");
	    matcher = pattern.matcher(subjectDN);
		if (matcher.find()) {
			subjectDN = matcher.group(0).split("=")[1].replaceAll(",", "");
		}
		*/
		String subjectDN = this.getSubjectDN();
		output.append(subjectDN + "\n");
		
		// Issuer CN
		String issuer = mCertificate.getIssuerDN().getName();
		pattern = Pattern.compile("CN=(.*?),");
	    matcher = pattern.matcher(issuer);
		if (matcher.find()) {
			issuer = matcher.group(0).split("=")[1].replaceAll(",", "");
		}
		output.append("Issuer: " + issuer + "\n");

		// Valid Date: MM/dd/YYYY to MM/dd/YYYY
		String dateFormat = "YYYY/MM/dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

		output.append("Valid From: ");
		output.append(simpleDateFormat.format(mCertificate.getNotBefore()));
		output.append(" to ");
		output.append(simpleDateFormat.format(mCertificate.getNotAfter()));
		
		output.append("\n");
		
		return output.toString();
	}
	public String toString() {
		StringBuilder output = new StringBuilder();
		
		output.append("Alias: " + this.mAlias + "\n");
		output.append(this.mCertificate);
		
		return output.toString();
	}
}
