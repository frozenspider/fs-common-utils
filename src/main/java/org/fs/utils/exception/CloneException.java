package org.fs.utils.exception;

public class CloneException extends Exception {

	private static final long	serialVersionUID	= 1L;

	public CloneException() {}

	public CloneException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CloneException(final String message) {
		super(message);
	}

	public CloneException(final Throwable cause) {
		super(cause);
	}
}

