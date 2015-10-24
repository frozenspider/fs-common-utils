package org.fs.utils.exception;

import java.io.IOException;

public class CISInterruptedException extends IOException {

	private static final long	serialVersionUID	= 6834035037730898473L;

	public CISInterruptedException() {}

	public CISInterruptedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CISInterruptedException(final String message) {
		super(message);
	}

	public CISInterruptedException(final Throwable cause) {
		super(cause);
	}
}

