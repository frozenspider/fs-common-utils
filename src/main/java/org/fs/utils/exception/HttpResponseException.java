package org.fs.utils.exception;

import java.io.IOException;

public class HttpResponseException extends IOException {

	private static final long	serialVersionUID	= 5200904240428597482L;
	private final int			responseCode;
	private final String		responseMessage;

	public HttpResponseException(final int responseCode) {
		super("Response code " + responseCode);
		this.responseCode = responseCode;
		this.responseMessage = null;
	}

	public HttpResponseException(final int responseCode, final String responseMessage) {
		super("Response code " + responseCode + (responseMessage == null ? "" : ": " + responseMessage));
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}
}

