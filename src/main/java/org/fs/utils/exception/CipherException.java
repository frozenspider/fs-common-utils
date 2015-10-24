package org.fs.utils.exception;

public class CipherException extends RuntimeException {

	private static final long	serialVersionUID	= -2928743577424994513L;

	public CipherException() {}

	public CipherException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CipherException(final String message) {
		super(message);
	}

	public CipherException(final Throwable cause) {
		super(cause);
	}
}

