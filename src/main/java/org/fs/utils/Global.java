package org.fs.utils;

import static org.fs.utils.character.UTF.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import org.fs.utils.io.IOUtils;

/** <i>Warning:</i> functions from here can later be moved to other utility classes */
public final class Global {
	
	/**
	 * Pretty-printed version of a time between two events, in form {@code HH:MM:ss}, e.g. 112:35:16
	 * (112 hours, 35 minutes and 16 seconds). Will round the value to the closest integer.
	 * <p>
	 * This method only useful with {@link System#currentTimeMillis()}.
	 * 
	 * @param elapsedMilliSeconds
	 *            the difference between two time marks, e.g.
	 *            {@code System.currentTimeMills() - oldMark}
	 * @return {@code HH:MM:ss} string
	 */
	public static String millsToPrettyString(final long elapsedMilliSeconds) {
		final long totalSeconds = Math.round((double) elapsedMilliSeconds / 1000);
		final long hours = totalSeconds / 3600;
		final long remainingSeconds = totalSeconds % 3600;
		final long minutes = remainingSeconds / 60;
		final long seconds = remainingSeconds % 60;
		return String.format("%d:%02d:%02d", hours, minutes, seconds);
	}
	
	/**
	 * @param data
	 *            bytes to be transformed
	 * @return uppercase string with length exactly twice of data length
	 */
	public static String toHexString(final byte[] data) {
		final StringBuffer sb = new StringBuffer(data.length * 2);
		for (final byte b : data) {
			final String t = Integer.toHexString(b > 0 ? b : 256 + b).toUpperCase();
			if (t.length() == 1) {
				sb.append("0");
			}
			if (t.length() == 3) {
				sb.append("00");
			} else {
				sb.append(t);
			}
		}
		return sb.toString();
	}
	
	public static String toBitString(final int data) {
		final StringBuilder s = new StringBuilder(32);
		for (int i = 31; i >= 0; i--) {
			s.append(data >> i & 1);
		}
		return s.toString();
	}
	
	public static String toBitString(final long data) {
		final StringBuilder s = new StringBuilder(64);
		for (int i = 63; i >= 0; i--) {
			s.append(data >> i & 1);
		}
		return s.toString();
	}
	
	/**
	 * Writes the provided content to the given file. Will create a path to the file, if it's not
	 * exists.
	 * 
	 * @param data
	 *            data to write to file.
	 * @param file
	 *            target file.
	 * @throws FileNotFoundException
	 *             if the file exists but is a directory rather than a regular file, does not exist
	 *             but cannot be created, or cannot be opened for any other reason
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void writeFile(final byte[] data, final File file) throws FileNotFoundException,
			IOException {
		if (file.getParentFile() != null) {
			file.getParentFile().mkdirs();
		}
		final FileOutputStream fos = new FileOutputStream(file, false);
		try {
			fos.write(data);
		} finally {
			Closer.close(fos);
		}
	}
	
	/**
	 * Writes the provided content to the given file. Will create a path to the file, if it's not
	 * exists.
	 * 
	 * @param data
	 *            data to write to file.
	 * @param filename
	 *            target file name.
	 * @throws FileNotFoundException
	 *             if the file exists but is a directory rather than a regular file, does not exist
	 *             but cannot be created, or cannot be opened for any other reason
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void writeFile(final byte[] data, final String filename)
			throws FileNotFoundException, IOException {
		writeFile(data, new File(filename));
	}
	
	/**
	 * Reads file content into a byte array.
	 * 
	 * @param filepath
	 * @return a byte array
	 * @throws FileNotFoundException
	 *             if the file does not exist, is a directory rather than a regular file, or for
	 *             some other reason cannot be opened for reading.
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws SecurityException
	 *             if a security manager exists and its {@code checkRead} method denies read access
	 *             to the file.
	 */
	public static byte[] readFile(final String filepath) throws FileNotFoundException, IOException,
			SecurityException {
		return readFile(new File(filepath));
	}
	
	/**
	 * Reads file content into a byte array.
	 * 
	 * @param file
	 * @return a byte array
	 * @throws FileNotFoundException
	 *             if the file does not exist, is a directory rather than a regular file, or for
	 *             some other reason cannot be opened for reading.
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws SecurityException
	 *             if a security manager exists and its {@code checkRead} method denies read access
	 *             to the file.
	 */
	public static byte[] readFile(final File file) throws FileNotFoundException, IOException,
			SecurityException {
		final FileInputStream fis = new FileInputStream(file);
		final byte[] result = IOUtils.readFully(fis, true);
		return result;
	}
	
	/** @see #readTextFile(File) */
	@SuppressWarnings("javadoc")
	public static String readTextFile(final String filepath) throws FileNotFoundException,
			IOException, SecurityException {
		return readTextFile(filepath, UTF8);
	}
	
	/**
	 * Reads file content into a String, decoding it using UTF-8 encoding.
	 * 
	 * @param file
	 * @return a brand new String
	 * @throws FileNotFoundException
	 *             if the file does not exist, is a directory rather than a regular file, or for
	 *             some other reason cannot be opened for reading.
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws SecurityException
	 *             if a security manager exists and its {@code checkRead} method denies read access
	 *             to the file.
	 */
	public static String readTextFile(final File file) throws FileNotFoundException, IOException,
			SecurityException {
		return readTextFile(file, UTF8);
	}
	
	/** @see #readTextFile(File) */
	@SuppressWarnings("javadoc")
	public static String readTextFile(final String filepath, final Charset charset)
			throws FileNotFoundException, IOException, SecurityException {
		return readTextFile(new File(filepath), charset);
	}
	
	/** @see #readTextFile(File) */
	@SuppressWarnings("javadoc")
	public static String readTextFile(final File file, final Charset charset)
			throws FileNotFoundException, IOException, SecurityException {
		return new String(readFile(file), charset);
	}
	
	/** @see #readTextFile(File) */
	@SuppressWarnings("javadoc")
	public static String readTextFile(final String filepath, final String encoding)
			throws FileNotFoundException, IOException, SecurityException,
			IllegalCharsetNameException {
		final Charset charset = Charset.forName(encoding);
		return readTextFile(filepath, charset);
	}
	
	/** @see #readTextFile(File) */
	@SuppressWarnings("javadoc")
	public static String readTextFile(final File file, final String encoding)
			throws FileNotFoundException, IOException, SecurityException,
			IllegalCharsetNameException {
		final Charset charset = Charset.forName(encoding);
		return readTextFile(file, charset);
	}
	
	/**
	 * Archives the content with GZIP.
	 * 
	 * @param source
	 *            content to be archived
	 * @return archived content
	 * @throws RuntimeException
	 *             if something went wrong
	 * @see #gunzip(byte[])
	 */
	public static byte[] gzip(final byte[] source) {
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream(
					(int) (source.length * 0.66f));
			final GZIPOutputStream gzOs = new GZIPOutputStream(baos);
			gzOs.write(source);
			gzOs.close();
			return baos.toByteArray();
		} catch(final IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Unarchive the GZIPped content.
	 * 
	 * @param source
	 *            archived content
	 * @return original content
	 * @throws ZipException
	 *             if data wasn't a proper GZIP archive
	 * @see #gzip(byte[])
	 */
	public static byte[] gunzip(final byte[] source) throws ZipException {
		try {
			final GZIPInputStream gzIs = new GZIPInputStream(new ByteArrayInputStream(source));
			final byte[] result = IOUtils.readFully(gzIs, true);
			return result;
		} catch(final ZipException ex) {
			throw ex;
		} catch(final IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static String beautifyThrowable(Throwable th) {
		final StringBuilder sb = new StringBuilder();
		sb.append(th.getClass().getSimpleName());
		Throwable cause;
		for (int iter = 0; th.getCause() != null; ++iter) {
			cause = th.getCause();
			if (th == cause) {
				sb.append(" => (itself)");
				break;
			}
			th = cause;
			if (iter <= 10) {
				sb.append(" => ");
				sb.append(th.getClass().getSimpleName());
			} else {
				sb.append(" => (and so on)");
				break;
			}
		}
		final String message = th.getLocalizedMessage();
		if (message != null) {
			sb.append(": ");
			sb.append(th.getLocalizedMessage());
		}
		return sb.toString();
	}
	
	/** @see Thread#start() */
	@SuppressWarnings("javadoc")
	public static void startAll(final Collection <? extends Thread> threads) {
		for (final Thread thread : threads) {
			thread.start();
		}
	}
	
	/** @see Thread#join() */
	@SuppressWarnings("javadoc")
	public static void joinAll(final Collection <? extends Thread> threads)
			throws InterruptedException {
		for (final Thread thread : threads) {
			thread.join();
		}
	}
}
