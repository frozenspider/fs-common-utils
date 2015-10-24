package org.fs.utils.net;

import static org.fs.utils.character.UTF.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.fs.utils.Closer;
import org.fs.utils.collection.map.BasicSortedMap;
import org.fs.utils.exception.HttpResponseException;

public class UrlConUtils {
	
	protected static int	bufferLen	= 8192;
	public static int		timeout		= 30000;
	
	/**
	 * Returns the map of cookies set for that {@link URLConnection} by server, optionaly copying
	 * old cookies (overwriting matching ones with the newest value);
	 * 
	 * @param urlCon
	 * @param oldCookies
	 *            old cookies to merge with, may be {@code null}
	 * @return a cookies map
	 */
	public static Map <String, String> getCookies(
			final URLConnection urlCon,
			final Map <String, String> oldCookies) {
		String headerName = null;
		final List <String> cookies = new ArrayList <String>();
		// 0-header is a special case - it may or may not return what we need
		if ((headerName = urlCon.getHeaderFieldKey(0)) != null && headerName.equals("Set-Cookie")) {
			cookies.add(urlCon.getHeaderField(0));
		}
		for (int i = 1; (headerName = urlCon.getHeaderFieldKey(i)) != null; i++) {
			if (headerName.equals("Set-Cookie")) {
				cookies.add(urlCon.getHeaderField(i));
			}
		}
		final Map <String, String> result;
		if (oldCookies == null) {
			result = new BasicSortedMap <String, String>(cookies.size());
		} else {
			result = new BasicSortedMap <String, String>(cookies.size() + oldCookies.size());
			result.putAll(oldCookies);
		}
		for (final String next : cookies) {
			final String[] split = next.split(";", 2)[0].split("=", 2);
			result.put(split[0], split.length == 1 ? "" : split[1]);
		}
		return result;
	}
	
	/**
	 * Returns the cookies as a single contated string (ex. {@code a=b; c=d; e=f}).
	 * 
	 * @param cookies
	 *            a cookies map
	 * @return cookies as a single string
	 */
	public static String getCookiesString(final Map <String, String> cookies) {
		final StringBuilder cookiesSb = new StringBuilder();
		for (final Entry <String, String> cookieEntry : cookies.entrySet()) {
			if (cookiesSb.length() != 0) {
				cookiesSb.append("; ");
			}
			cookiesSb.append(cookieEntry.getKey());
			cookiesSb.append('=');
			cookiesSb.append(cookieEntry.getValue());
		}
		return cookiesSb.toString();
	}
	
	/**
	 * Retrieves the content from the supplied URL as a UTF-8 string.
	 * <p>
	 * If the connection have no read timeout, it will be set to 30 seconds.
	 * <p>
	 * Supports GZIP encoding.
	 * 
	 * @param urlCon
	 *            an {@link URLConnection} from which the content will be downloaded
	 * @return content as a encoded string
	 * @throws HttpResponseException
	 *             if this is an {@link HttpURLConnection} and server returns abnormal response code
	 * @throws IOException
	 *             if an I/O error has occurred
	 * @throws UnsupportedCharsetException
	 *             (unchecked) if UTF-8 is somehow not supported (nearly impossible)
	 * @see #getContentBytes(URLConnection)
	 * @see #getContentString(URLConnection, String)
	 * @see #getContentString(URLConnection, Charset)
	 */
	public static String getContentString(final URLConnection urlCon) throws HttpResponseException,
			UnsupportedCharsetException, IOException {
		return getContentString(urlCon, UTF8);
	}
	
	/**
	 * Retrieves the content from the supplied URL as a string, encoded in a specified charset.
	 * <p>
	 * If the connection have no read timeout, it will be set to 30 seconds.
	 * <p>
	 * Supports GZIP encoding.
	 * 
	 * @param urlCon
	 *            an {@link URLConnection} from which the content will be downloaded
	 * @param charset
	 *            name of the charset to be used to decode the content
	 * @return content as a encoded string
	 * @throws HttpResponseException
	 *             if this is an {@link HttpURLConnection} and server returns abnormal response code
	 * @throws IOException
	 *             if an I/O error has occurred
	 * @throws IllegalCharsetNameException
	 *             (unchecked) if the given charset name is illegal
	 * @throws IllegalArgumentException
	 *             (unchecked) if the given charsetName is null
	 * @throws UnsupportedCharsetException
	 *             (unchecked) if no support for the named charset is available in this instance of
	 *             the Java virtual machine
	 * @see #getContentBytes(URLConnection)
	 * @see #getContentString(URLConnection)
	 * @see #getContentString(URLConnection, Charset)
	 */
	public static String getContentString(final URLConnection urlCon, final String charset)
			throws HttpResponseException, IOException {
		return new String(getContentBytes(urlCon), Charset.forName(charset));
	}
	
	/**
	 * Retrieves the content from the supplied URL as a string, encoded in a specified charset.
	 * <p>
	 * If the connection have no read timeout, it will be set to 30 seconds.
	 * <p>
	 * Supports GZIP encoding.
	 * 
	 * @param urlCon
	 *            an {@link URLConnection} from which the content will be downloaded
	 * @param charset
	 *            the {@link Charset} to be used to decode the content
	 * @return content as a encoded string
	 * @throws HttpResponseException
	 *             if this is an {@link HttpURLConnection} and server returns abnormal response code
	 * @throws IOException
	 *             if an I/O error has occurred
	 * @see #getContentBytes(URLConnection)
	 * @see #getContentString(URLConnection)
	 * @see #getContentString(URLConnection, String)
	 */
	public static String getContentString(final URLConnection urlCon, final Charset charset)
			throws HttpResponseException, IOException {
		return new String(getContentBytes(urlCon), charset);
	}
	
	/**
	 * Retrieves the content from the supplied URL as a byte array.
	 * <p>
	 * If the connection have no read timeout, it will be set to 30 seconds.
	 * <p>
	 * Supports GZIP encoding.
	 * 
	 * @param urlCon
	 *            an {@link URLConnection} from which the content will be downloaded
	 * @return content as a byte array
	 * @throws HttpResponseException
	 *             if this is an {@link HttpURLConnection} and server returns abnormal response code
	 * @throws IOException
	 *             if an I/O error has occurred
	 * @see #getContentString(URLConnection)
	 * @see #getContentString(URLConnection, String)
	 * @see #getContentString(URLConnection, Charset)
	 */
	public static byte[] getContentBytes(final URLConnection urlCon) throws HttpResponseException,
			IOException {
		if (urlCon.getReadTimeout() == 0) {
			urlCon.setReadTimeout(timeout);
		}
		if (urlCon.getConnectTimeout() == 0) {
			urlCon.setConnectTimeout(timeout);
		}
		final int responseCode;
		final String responseMessage;
		if (urlCon instanceof HttpURLConnection) {
			responseCode = ((HttpURLConnection) urlCon).getResponseCode();
			responseMessage = ((HttpURLConnection) urlCon).getResponseMessage();
		} else {
			responseCode = -1;
			responseMessage = null;
		}
		final ByteArrayOutputStream contentOS = new ByteArrayOutputStream();
		int len;
		final byte[] buf = new byte[bufferLen];
		final InputStream is;
		try {
			is = urlCon.getInputStream();
		} catch(final IOException ex) {
			if (ex.getMessage() != null
					&& ex.getMessage().startsWith("Server returned HTTP response code"))
				throw new HttpResponseException(responseCode, responseMessage);
			throw ex;
		}
		try {
			while ((len = is.read(buf)) != -1) {
				contentOS.write(buf, 0, len);
			}
		} finally {
			Closer.close(is);
		}
		if ("gzip".equals(urlCon.getHeaderField("Content-Encoding"))) {
			final ByteArrayInputStream gZippedContentIS = new ByteArrayInputStream(
					contentOS.toByteArray());
			final GZIPInputStream gzipIS = new GZIPInputStream(gZippedContentIS);
			try {
				contentOS.reset();
				while ((len = gzipIS.read(buf)) != -1) {
					contentOS.write(buf, 0, len);
				}
			} finally {
				Closer.close(gzipIS);
			}
		}
		return contentOS.toByteArray();
	}
	
	/*-
	public static String getContentString(final URLConnection urlCon, final Interruptible caller)
			throws UnsupportedCharsetException, IOException{
		return getContentString(urlCon, CharConst.UTF8, caller);
	}
	public static String getContentString(final URLConnection urlCon, String charset, final Interruptible caller)
			throws UnsupportedCharsetException, IOException{
		try {
			return new String(getContentBytes(urlCon, caller), Charset.forName(charset));
		} catch(final InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new IOException(ex); // Cannot happen
		}
	}
	public static byte[] getContentBytes(final URLConnection urlCon, final Interruptible caller)
			throws IOException, InterruptedException{
		urlCon.setReadTimeout(timeout);
		// ((java.net.HttpURLConnection)urlCon).getResponseCode()
		final InputStream is = urlCon.getInputStream();
		final ByteArrayOutputStream contentOS = new ByteArrayOutputStream();
		int len;
		final byte[] buf = new byte[bufferLen];
		try {
			while ((len = is.read(buf)) != -1) {
				contentOS.write(buf, 0, len);
				if (caller != null && caller.isInterrupted()) throw new InterruptedException();
			}
		} finally {
			Closer.close(is);
		}
		if ("gzip".equals(urlCon.getHeaderField("Content-Encoding"))) {
			final ByteArrayInputStream gZippedContentIS = new ByteArrayInputStream(contentOS.toByteArray());
			GZIPInputStream gzipIS = null;
			try {
				gzipIS = new GZIPInputStream(gZippedContentIS);
				contentOS.reset();
				while ((len = gzipIS.read(buf)) != -1) {
					contentOS.write(buf, 0, len);
				}
			} finally {
				Closer.close(gzipIS);
			}
		}
		return contentOS.toByteArray();
	}*/
	/**
	 * Streams the content to a given file.
	 * <p>
	 * If the connection have no read timeout, it will be set to 30 seconds.
	 * <p>
	 * Supports GZIP encoding.
	 * 
	 * @param urlCon
	 *            an {@link URLConnection} to read from
	 * @param file
	 *            a target file
	 * @throws IOException
	 * @see #streamContent(URLConnection, OutputStream)
	 */
	public static void streamContent(final URLConnection urlCon, final File file)
			throws IOException {
		streamContent(urlCon, new FileOutputStream(file));
	}
	
	/**
	 * Streams the content to a given output stream.
	 * <p>
	 * If the connection have no read timeout, it will be set to 30 seconds.
	 * <p>
	 * Supports GZIP encoding.
	 * 
	 * @param urlCon
	 *            an {@link URLConnection} to read from
	 * @param outputStream
	 *            a target output stream
	 * @throws IOException
	 * @see #streamContent(URLConnection, File)
	 */
	public static void streamContent(final URLConnection urlCon, final OutputStream outputStream)
			throws IOException {
		urlCon.setReadTimeout(timeout);
		int len;
		final byte[] buf = new byte[bufferLen];
		InputStream is = urlCon.getInputStream();
		if ("gzip".equals(urlCon.getHeaderField("Content-Encoding"))) {
			is = new GZIPInputStream(is);
		}
		try {
			while ((len = is.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
		} finally {
			Closer.close(is);
			Closer.close(outputStream);
		}
	}
	
	/*-
	public static void streamContent(final URLConnection urlCon, final File file, final Interruptible caller)
			throws IOException, InterruptedException{
		urlCon.setReadTimeout(timeout);
		final FileOutputStream fileOS = new FileOutputStream(file);
		InputStream is = urlCon.getInputStream();
		if ("gzip".equals(urlCon.getHeaderField("Content-Encoding"))) {
			is = new GZIPInputStream(is);
		}
		int len;
		final byte[] buf = new byte[bufferLen];
		try {
			while ((len = is.read(buf)) != -1) {
				fileOS.write(buf, 0, len);
				if (caller != null && caller.isInterrupted()) throw new InterruptedException();
			}
		} finally {
			Closer.close(is);
			Closer.close(fileOS);
		}
	}*/
	
	/**
	 * Encodes input with {@link URLEncoder#encode(String, String)}, using "UTF-8" as an encoding
	 * and rethrowing exception as unchecked
	 * 
	 * @param input
	 * @return encoded string
	 * @throws UnsupportedCharsetException
	 *             on UnsupportedEncodingException. UTF-8 not supported? You bet!
	 * @see #decode(String)
	 */
	public static String encode(final String input) {
		try {
			return URLEncoder.encode(input, UTF8);
		} catch(final UnsupportedEncodingException ex) {
			throw new UnsupportedCharsetException("UTF-8 is not supported? Noooooo!");
		}
	}
	
	/**
	 * Decodes input with {@link URLDecoder#decode(String, String)}, using "UTF-8" as an encoding
	 * and rethrowing exception as unchecked
	 * 
	 * @param input
	 * @return decoded string
	 * @throws UnsupportedCharsetException
	 *             on UnsupportedEncodingException. UTF-8 not supported? You bet!
	 * @see #encode(String)
	 */
	public static String decode(final String input) {
		try {
			return URLDecoder.decode(input, UTF8);
		} catch(final UnsupportedEncodingException ex) {
			throw new UnsupportedCharsetException("UTF-8 is not supported? How could this happen?");
		}
	}
	
	/**
	 * Creates a {@link URL} object from the String representation, rethrowing MalformedURLException
	 * as an unchecked exception.
	 * <p>
	 * Use this, if you shure, that URL is correct.
	 * 
	 * @param spec
	 *            the {@code String} to parse as a URL
	 * @return a newly crated URL
	 * @throws RuntimeException
	 *             if MalformedURLException happens
	 * @see URL#URL(String)
	 */
	public static URL makeUrl(final String spec) {
		try {
			return new URL(spec);
		} catch(final MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
	}
}
