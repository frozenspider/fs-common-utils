package org.fs.utils.collection.table;

public class SubtableBoundsBrokenException extends RuntimeException {

	private static final long	serialVersionUID	= 1268215029834004976L;

	public SubtableBoundsBrokenException(){}

	public SubtableBoundsBrokenException(final String message, final Throwable cause){
		super(message, cause);
	}

	public SubtableBoundsBrokenException(final String message){
		super(message);
	}

	public SubtableBoundsBrokenException(final Throwable cause){
		super(cause);
	}
}

