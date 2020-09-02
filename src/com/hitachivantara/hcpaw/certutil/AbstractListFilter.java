package com.hitachivantara.hcpaw.certutil;


import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link CertificateFilter} that takes a list of values.
 * One of these values must be in the list of values in a property of the
 * certificate, which is returned by the {@link #getList(Certificate)} method
 * (which needs to be implemented by an implementation).
 * 
 * The values of the list are connected through a logical OR by default (only
 * one of the requested values need to be part of the certificate property).
 * This can be changed to a logical AND or a logical NOT by the
 * {@link #setLogicalOperator(LogicalOperator)} method.
 *
 * @param <T>
 *            The type of the values in the lists to compare.
 */
abstract class AbstractListFilter<T> implements CertificateFilter {
	private List<T> oneOfList;
	private LogicalOperator operator = LogicalOperator.OR;

	protected void setOneOfList(List<T> list) {
		this.oneOfList = list;
	}

	public void setLogicalOperator(LogicalOperator operator) {
		this.operator = operator;
	}

	@Override
	public boolean accept(CertificateWrapper certificate) throws CertificateParsingException {
		List<T> certList = getList(certificate);
		List<T> remainingList = new ArrayList<>(oneOfList);
		remainingList.removeAll(certList);

		switch (operator) {
		case OR:
			return remainingList.size() != oneOfList.size();
		case AND:
			return (remainingList.size() + oneOfList.size()) == certList.size();
		case NOT:
			return remainingList.size() == oneOfList.size();
		default:
			throw new IllegalArgumentException("No logical operator given.");
		}
	}

	protected abstract List<T> getList(CertificateWrapper certificate) throws CertificateParsingException;
}
