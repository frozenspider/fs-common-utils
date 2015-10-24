package org.fs.utils.exception;

import java.io.IOException;

public class SerializationException extends IOException {

	private static final long	serialVersionUID	= 1L;

	public SerializationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
