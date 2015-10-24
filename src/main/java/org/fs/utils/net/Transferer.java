package org.fs.utils.net;

import java.io.IOException;

/**
 * This interface represents a controllable data up- or download. While transfer is in progress, it
 * provides a plenty of tracking methods.
 *
 * @author FS
 */
public interface Transferer {

	/** Transferer has not yet been started (use the {@link #start()} method) */
	public static final int	STATUS_NOT_YET_STARTED	= 0;
	/** Transferer is in progress */
	public static final int	STATUS_IN_PROGRESS		= 1;
	/** Transferer has successfully finished */
	public static final int	STATUS_COMPLETE			= 2;
	/** An error occured and a transfer has failed (use the {@link #getError()} method) */
	public static final int	STATUS_FAILED			= 3;

	/**
	 * Blocks current thread until transfer is complete.
	 *
	 * @return this object
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Transferer waitUntilComplete() throws IOException, InterruptedException;

	/**
	 * Blocks current thread until transfer is complete or until the time is out.
	 *
	 * @param timeout
	 *            maximum wait time (in mills)
	 * @return {@code true} if transfer is complete, {@code false} if time is out.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean waitUntilComplete(long timeout) throws IOException, InterruptedException;

	//
	// Processing flow controllers (mostly fluent)
	//
	/**
	 * Starts the actual transfer.
	 *
	 * @return this object
	 */
	public Transferer start();

	/**
	 * Interrupts the current transfer. Has no effect if transfer hasn't started or was finished.
	 *
	 * @return this object
	 */
	public Transferer interrupt();

	/** @return whether the transfer has started. */
	public boolean isStarted();

	/**
	 * @return the current status, one of
	 *         <ul>
	 *         <li>{@link #STATUS_NOT_YET_STARTED}</li>
	 *         <li>{@link #STATUS_IN_PROGRESS}</li>
	 *         <li>{@link #STATUS_COMPLETE}</li>
	 *         <li>{@link #STATUS_FAILED}</li>
	 *         </ul>
	 */
	public int getStatus();

	/** @return percent complete (0.0f to 1.0f) or -1.0f if not known */
	public float getPercentComplete();

	/**
	 * @return <ul>
	 *         <li>{@code long} if download was started</li>
	 *         <li>{@code 0L} if not</li>
	 *         </ul>
	 */
	public long getStartTime();

	/**
	 * @return <ul>
	 *         <li>{@code long} if download was finished</li>
	 *         <li>{@code -1L} if an error occured</li>
	 *         <li>{@code 0L} otherwise</li>
	 *         </ul>
	 */
	public long getFinishTime();

	/** @return the amount of bytes currently transfered. */
	public long getAmountComplete();

	/** @return amount of remaining bytes or {@code -1L} if not known. */
	public long getAmountReamining();

	/** @return recieved result or {@code null} if not yet finished/not available at all. */
	public byte[] getResult();

	/**
	 * Sets the name of a transfer thread. Useful mostly for debugging. Only usable before the
	 * transfer starts.
	 *
	 * @param newName
	 * @throws IllegalStateException
	 *             if the transfer has been started
	 */
	public void setTransferThreadName(String newName) throws IllegalStateException;

	/** @return current name of a transfer thread */
	public String getTransferThreadName();

	/** @return an error occured or {@code null} if none. */
	public Throwable getError();
}

