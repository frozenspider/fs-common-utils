package org.fs.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.fs.utils.Closer;

public class IOUtils {

	/**
	 * Reades all data from the stream and (optionally) closes it.
	 *
	 * @param inputStream
	 * @param close
	 *            whether or not close the stream after reading complete
	 * @return byte originally contained in stream
	 * @throws IOException
	 */
	public static byte[] readFully(final InputStream inputStream, final boolean close) throws IOException {
		try {
			final byte[] buf = new byte[8192];
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len;
			while ((len = inputStream.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			return baos.toByteArray();
		} finally {
			if (close) {
				Closer.close(inputStream);
			}
		}
	}

	public static byte[] readNoMoreThan(final InputStream inputStream, final int limit, final boolean close)
			throws IOException {
		try {
			int remains = limit;
			final int bufSize = 8192;
			final byte[] buf = new byte[bufSize];
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len;
			while (true) {
				final int toRead = remains > bufSize ? bufSize : remains;
				len = inputStream.read(buf, 0, toRead);
				if (len == -1) {
					break;
				}
				baos.write(buf, 0, len);
				remains -= len;
				if (remains <= 0) {
					break;
				}
			}
			return baos.toByteArray();
		} finally {
			if (close) {
				Closer.close(inputStream);
			}
		}
	}
}

