package org.fs.utils.net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

/**
 * This class handles buffered file downloading. It reads all the content to a memory buffer, which
 * can later be retrieved via {@link #getResult()}. Class is thread-safe.
 * <p>
 * The transfer is executed in a separate thread.
 * <p>
 * While transfer in progress, class provides info about transfer progress. Understands gzipped
 * data. Supports download speed limit.
 * <p>
 * Note, that this class requires about 2 times more memory, then the file size.
 * 
 * @author FS
 * @see StreamingTransferer
 */
public class BufferingTransferer extends AbstractTransferer {
	
	protected static int			checkPeriod	= 1000;
	
	protected final URLConnection	urlCon;
	protected final long			readSpeedLimit;
	protected final int				bufferLen;
	protected byte[]				result;
	
	//
	// Constructors
	//
	/**
	 * Constructs a new BufferingTransferer from the URLConnection with the buffer size of 65536.
	 * 
	 * @param urlCon
	 *            an URL connection to use. It's recommended to set the read timeout for the
	 *            connection.
	 * @param readSpeedLimit
	 *            speed limit in bytes
	 */
	public BufferingTransferer(final URLConnection urlCon, final long readSpeedLimit) {
		this(urlCon, readSpeedLimit, 65536);
	}
	
	/**
	 * Constructs a new BufferingTransferer from the URLConnection.
	 * 
	 * @param urlCon
	 *            an URL connection to use. It's recommended to set the read timeout for the
	 *            connection.
	 * @param readSpeedLimit
	 *            speed limit in bytes
	 * @param bufferLen
	 *            length of a read-write buffer
	 */
	public BufferingTransferer(
			final URLConnection urlCon,
			final long readSpeedLimit,
			final int bufferLen) {
		super(null, true);
		if (readSpeedLimit < 20 && readSpeedLimit != 0)
			throw new IllegalArgumentException("Speed limit must be 0 or more then 20");
		if (urlCon == null) throw new NullPointerException("Connection cannot be null");
		this.urlCon = urlCon;
		this.readSpeedLimit = readSpeedLimit;
		this.bufferLen = bufferLen;
	}
	
	/**
	 * Constructs a new BufferingTransferer from the provided input stream with the buffer size of
	 * 65536.
	 * 
	 * @param inputStream
	 *            an input stream to read from
	 * @param closeInputStream
	 *            close the input stream after job is done
	 * @param readSpeedLimit
	 *            speed limit in bytes
	 */
	public BufferingTransferer(
			final InputStream inputStream,
			final boolean closeInputStream,
			final long readSpeedLimit) {
		this(inputStream, closeInputStream, readSpeedLimit, 65536);
	}
	
	/**
	 * Constructs a new BufferingTransferer from the provided input stream.
	 * 
	 * @param inputStream
	 *            an input stream to read from
	 * @param closeInputStream
	 *            close the input stream after job is done
	 * @param readSpeedLimit
	 *            speed limit in bytes
	 * @param bufferLen
	 *            length of a read-write buffer
	 */
	public BufferingTransferer(
			final InputStream inputStream,
			final boolean closeInputStream,
			final long readSpeedLimit,
			final int bufferLen) {
		super(inputStream, closeInputStream);
		if (readSpeedLimit < 20 && readSpeedLimit != 0)
			throw new IllegalArgumentException("Speed limit must be 0 or more then 20");
		if (inputStream == null) throw new NullPointerException("Input stream cannot be null");
		this.urlCon = null;
		this.readSpeedLimit = readSpeedLimit;
		this.bufferLen = bufferLen;
	}
	
	@Override
	public byte[] getResult() {
		return result;
	}
	
	//
	// Internal
	//
	/** @return not started transfer thread */
	@Override
	protected TransferExecutor createTransferExecutor() {
		return new BufferingTransferExecutor();
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
	
	//
	// Inner classes
	//
	private class BufferingTransferExecutor implements TransferExecutor {
		
		@Override
		public void executeTransfer() throws Exception {
			final ByteArrayOutputStream contentOS = new ByteArrayOutputStream();
			final long total = urlCon.getContentLength();
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
				try {
					len = inputStream.read(buf, 0, toRead);
				} catch(final Exception ex) {
					throw ex;
				}
				if (len == -1) {
					break;
				}
				contentOS.write(buf, 0, len);
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
			result = contentOS.toByteArray();
		}
	}
}
