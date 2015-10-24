package org.fs.utils.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.fs.utils.Closer;

/**
 * This class handles streaming transfer. It doesnt buffers the whole file content, it writes a read
 * part to a provided output stream. Class is thread-safe.
 * <p>
 * The transfer is executed in a separate thread.
 * <p>
 * While transfer in progress, class provides info about transfer progress. Understands gzipped
 * data. Supports download speed limit.
 * <p>
 * {@link #getResult()} method will always return {@code null}.
 * <p>
 * NOTE: Provided {@link OutputStream} will be closed in the end.
 * 
 * @author FS
 * @see BufferingTransferer
 */
public class StreamingTransferer extends AbstractTransferer {
	
	protected static int			checkPeriod	= 1000;
	
	protected final URLConnection	urlCon;
	protected final long			readSpeedLimit;
	protected final int				bufferLen	= 8192;
	protected final OutputStream	outputStream;
	protected final boolean			closeOutputStream;
	
	/**
	 * Constructs a StreamingTransferer from the URLConnection.
	 * 
	 * @param urlCon
	 *            an URL connection to use. It's recommended to set the read timeout for the
	 *            connection.
	 * @param outputStream
	 *            an output stream to write to
	 * @param closeOutputStream
	 *            close the output stream after job is done
	 * @param readSpeedLimit
	 *            speed limit in bytes
	 */
	public StreamingTransferer(
			final URLConnection urlCon,
			final OutputStream outputStream,
			final boolean closeOutputStream,
			final long readSpeedLimit) {
		super(null, true);
		this.urlCon = urlCon;
		this.readSpeedLimit = readSpeedLimit;
		this.closeOutputStream = closeOutputStream;
		this.outputStream = outputStream;
	}
	
	/**
	 * Constructs a new BufferingTransferer from the provided input stream.
	 * 
	 * @param inputStream
	 *            an input stream to read from
	 * @param closeInputStream
	 *            close the input stream after job is done
	 * @param outputStream
	 *            an output stream to write to
	 * @param closeOutputStream
	 *            close the output stream after job is done
	 * @param readSpeedLimit
	 *            speed limit in bytes
	 */
	public StreamingTransferer(
			final InputStream inputStream,
			final boolean closeInputStream,
			final OutputStream outputStream,
			final boolean closeOutputStream,
			final long readSpeedLimit) {
		super(inputStream, closeInputStream);
		this.urlCon = null;
		this.readSpeedLimit = readSpeedLimit;
		this.closeOutputStream = closeOutputStream;
		this.outputStream = outputStream;
	}
	
	@Override
	protected TransferExecutor createTransferExecutor() {
		return new StreamingTransferExecutor();
	}
	
	@Override
	protected void preStartInit() throws Exception {
		if (inputStream == null) {
			inputStream = urlCon.getInputStream();
			if ("gzip".equals(urlCon.getHeaderField("Content-Encoding"))) {
				inputStream = new GZIPInputStream(inputStream);
			}
		}
	}
	
	/** Will always return {@code null} */
	@Override
	public byte[] getResult() {
		return null;
	}
	
	private class StreamingTransferExecutor implements TransferExecutor {
		
		@Override
		public void executeTransfer() throws Exception {
			try {
				long total = -1;
				if (urlCon != null) {
					total = urlCon.getContentLength();
				}
				remainsAmount.set(total);
				if (total != -1) {
					completePerc = 0.0f;
				}
				final byte[] buf = new byte[bufferLen];
				long remains = readSpeedLimit;
				int toRead = bufferLen;
				long lastTimeMark = System.currentTimeMillis();
				int len;
				while (true) {
					if (readSpeedLimit != 0) {
						toRead = (int) (remains > bufferLen ? bufferLen : remains);
					}
					len = inputStream.read(buf, 0, toRead);
					if (len == -1) {
						break;
					}
					outputStream.write(buf, 0, len);
					final long curr = completeAmount.addAndGet(len);
					if (total != -1L) {
						remainsAmount.addAndGet(-len);
						completePerc = 1.0f * curr / total;
					}
					if (readSpeedLimit != 0) {
						remains -= len;
					}
					if (readSpeedLimit != 0 && remains == 0) {
						final long currTimeMark = System.currentTimeMillis();
						final long dif = currTimeMark - lastTimeMark;
						if (dif < checkPeriod) {
							Thread.sleep(checkPeriod - dif);
						}
						lastTimeMark = System.currentTimeMillis();
						remains = readSpeedLimit;
					}
				}
			} finally {
				if (closeOutputStream) {
					Closer.close(outputStream);
				}
			}
		}
	}
}
