package org.fs.utils.exception;

public class LockedException extends Exception {

	private static final long	serialVersionUID	= 3903804857178482521L;

	public LockedException() {}

	public LockedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public LockedException(final String message) {
		super(message);
	}

	public LockedException(final Throwable cause) {
		super(cause);
	}
}

