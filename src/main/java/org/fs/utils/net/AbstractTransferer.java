package org.fs.utils.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

import org.fs.utils.Closer;

public abstract class AbstractTransferer implements Transferer {

	protected InputStream		inputStream;
	protected final boolean		closeInputStream;
	//
	protected volatile int		status				= STATUS_NOT_YET_STARTED;
	protected long				startTime			= 0L;
	protected long				endTime				= 0L;
	protected volatile boolean	started				= false;
	protected volatile float	completePerc		= -1.0f;
	protected final AtomicLong	completeAmount		= new AtomicLong();
	protected final AtomicLong	remainsAmount		= new AtomicLong();
	protected Thread			thread;
	protected Throwable			ex					= null;
	protected String			transferThreadName	= "TransferThread-" + Math.random();

	public AbstractTransferer(final InputStream inputStream, final boolean closeInputStream) {
		this.inputStream = inputStream;
		this.closeInputStream = closeInputStream;
	}

	@Override
	public synchronized Transferer start() {
		if (thread != null && thread.isAlive()) throw new IllegalStateException();
		try {
			preStartInit();
		} catch(final Exception ex) {
			this.ex = ex;
			endTime = -1L;
			status = STATUS_FAILED;
			return this;
		}
		final TransferExecutor actor = createTransferExecutor();
		thread = new Thread(new AbsractTransfererRunnable(actor), transferThreadName);
		thread.start();
		return this;
	}

	@Override
	public synchronized Transferer interrupt() {
		if (thread == null || !thread.isAlive()) throw new IllegalStateException();
		thread.interrupt();
		return this;
	}

	@Override
	public Transferer waitUntilComplete() throws IOException, InterruptedException {
		if (thread == null) {
			start();
		}
		for (;;) {
			if (Thread.interrupted()) throw new InterruptedException();
			final int status = getStatus();
			if (status == STATUS_COMPLETE || status == STATUS_FAILED) return this;
			Thread.sleep(25);
		}
	}

	@Override
	public boolean waitUntilComplete(final long timeout) throws IOException, InterruptedException {
		if (thread == null) {
			start();
		}
		int elapsed = 0;
		for (;;) {
			if (Thread.interrupted()) throw new InterruptedException();
			final int status = getStatus();
			if (status == STATUS_COMPLETE || status == STATUS_FAILED) return true;
			elapsed += 25;
			Thread.sleep(25);
			if (elapsed >= timeout) return false;
		}
	}

	@Override
	public synchronized void setTransferThreadName(final String newName) throws IllegalStateException {
		if (started) throw new IllegalStateException();
		this.transferThreadName = newName;
	}

	//
	// Internal
	//
	protected abstract TransferExecutor createTransferExecutor();

	protected abstract void preStartInit() throws Exception;

	//
	// Getters
	//
	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public float getPercentComplete() {
		return completePerc;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getFinishTime() {
		return endTime;
	}

	@Override
	public long getAmountComplete() {
		return completeAmount.get();
	}

	@Override
	public long getAmountReamining() {
		return remainsAmount.get();
	}

	@Override
	public synchronized String getTransferThreadName() {
		return transferThreadName;
	}

	@Override
	public Throwable getError() {
		return ex;
	}

	//
	// Inner classes
	//
	public class AbsractTransfererRunnable implements Runnable {

		private final TransferExecutor	actor;

		public AbsractTransfererRunnable(final TransferExecutor actor) {
			this.actor = actor;
		}

		@Override
		public void run() {
			try {
				startTime = System.currentTimeMillis();
				started = true;
				status = STATUS_IN_PROGRESS;
				try {
					actor.executeTransfer();
				} finally {
					if (closeInputStream) {
						Closer.close(inputStream);
					}
				}
				endTime = System.currentTimeMillis();
				status = STATUS_COMPLETE;
			} catch(final Throwable ex) {
				AbstractTransferer.this.ex = ex;
				endTime = -1L;
				status = STATUS_FAILED;
			}
		}
	}

	/**
	 * Implementations of this interface executes actual transfer. Instance will be requested during
	 * preStart phase. It must not close any parent resources - only custom ones. It also shouldnt
	 * change status, start or stop time.
	 */
	public static interface TransferExecutor {

		public void executeTransfer() throws Exception;
	}
}

