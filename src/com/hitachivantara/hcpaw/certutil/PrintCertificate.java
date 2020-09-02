package com.hitachivantara.hcpaw.certutil;

import java.io.PrintStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import sun.security.x509.CertificateExtensions;
import sun.security.x509.Extension;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class PrintCertificate {

	PrintCertificate() {
		
	}
	
    /**
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                            '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /**
     * Converts a byte array to hex string
     */
    private String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
             byte2hex(block[i], buf);
             if (i < len-1) {
                 buf.append(":");
             }
        }
        return buf.toString();
    }

     /**
     * Gets the requested finger print of the certificate.
     */
    private String getCertFingerPrint(String mdAlg, Certificate cert)
        throws Exception
    {
        byte[] encCertInfo = cert.getEncoded();
        MessageDigest md = MessageDigest.getInstance(mdAlg);
        byte[] digest = md.digest(encCertInfo);
        return toHexString(digest);
    }

    /**
     * Prints a certificate in a human readable format.
     */
    private void printX509Cert(X509Certificate cert, PrintStream out)
        throws Exception
    {
        out.println("Owner: "
                    + cert.getSubjectDN().toString()
                    + "\n"
                    + "Issuer: "
                    + cert.getIssuerDN().toString()
                    + "\n"
                    + "Serial number: " + cert.getSerialNumber().toString(16)
                    + "\n"
                    + "Valid from: " + cert.getNotBefore().toString()
                    + " until: " + cert.getNotAfter().toString()
                    + "\n"
                    + "Certificate fingerprints:\n"
                    + "\t MD5:  " + getCertFingerPrint("MD5", cert)
                    + "\n"
                    + "\t SHA1: " + getCertFingerPrint("SHA1", cert));

        if (cert instanceof X509CertImpl) {
            X509CertImpl impl = (X509CertImpl)cert;
            X509CertInfo certInfo = (X509CertInfo)impl.get(X509CertImpl.NAME
                                                           + "." +
                                                           X509CertImpl.INFO);
            CertificateExtensions exts = (CertificateExtensions)
                    certInfo.get(X509CertInfo.EXTENSIONS);
            if (exts != null) {
                printExtensions("Extensions: ", exts, out);
            }
        }
    }

    private void printExtensions(String title, CertificateExtensions exts, PrintStream out)
            throws Exception {
        int extnum = 0;
        Iterator<Extension> i1 = exts.getAllExtensions().iterator();
        Iterator<Extension> i2 = exts.getUnparseableExtensions().values().iterator();
        while (i1.hasNext() || i2.hasNext()) {
            Extension ext = i1.hasNext()?i1.next():i2.next();
            if (extnum == 0) {
                out.println();
                out.println(title);
                out.println();
            }
            out.print("#"+(++extnum)+": "+ ext);
            if (ext.getClass() == Extension.class) {
                byte[] v = ext.getExtensionValue();
                if (v.length == 0) {
                    out.println("Emtpy Value");
                } else {
                    new sun.misc.HexDumpEncoder().encodeBuffer(ext.getExtensionValue(), out);
                    out.println();
                }
            }
            out.println();
        }
    }

    void printEntry(CertificateStore inStore, CertificateWrapper inCert, PrintStream out, boolean printWarning) throws Exception {

    	KeyStore keyStore = inStore.getKeyStore();
    	String alias = inCert.getAlias();
    	
        out.println("Key Alias: " + alias);
        out.println("Creation Date: " + keyStore.getCreationDate(alias));
 
        if (keyStore.entryInstanceOf(alias, KeyStore.SecretKeyEntry.class)) {
            out.println("SecretKeyEntry, ");
        } else if (keyStore.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class)) {
            out.println("PrivateKeyEntry, ");

            // Get the chain
            Certificate[] chain = keyStore.getCertificateChain(alias);
            if (chain != null) {
                out.println("Certificate.chain.length: " + chain.length);
                    for (int i = 0; i < chain.length; i ++) {
                        out.println("Certificate[" + i + "]:");
                            printX509Cert((X509Certificate)(chain[i]), out);
                    }

                // Print the digest of the user cert only
                out.println
                    ("Certificate.fingerprint.SHA1: " +
                    getCertFingerPrint("SHA1", chain[0]));
            }
        } else if (keyStore.entryInstanceOf(alias,
                KeyStore.TrustedCertificateEntry.class)) {
            // We have a trusted certificate entry
            Certificate cert = keyStore.getCertificate(alias);
            out.println("trustedCertEntry, ");
            out.println("Certificate.fingerprint.SHA1: "
                        + getCertFingerPrint("SHA1", cert));
        } else {
            out.println("Unknown.Entry.Type");
        }
    }
}
