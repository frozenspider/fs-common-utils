package org.fs.utils.event;

public final class LockerThread extends Thread {

	private final Lockable	lockedInstance;
	private final Thread	watchedInstance;
	private Throwable		ex;

	public LockerThread(final Lockable lockedInstance, final Thread watchedInstance) {
		super("LockerThread");
		this.lockedInstance = lockedInstance;
		this.watchedInstance = watchedInstance;
		start();
	}

	public Throwable getThrowable() {
		return ex;
	}

	@Override
	public void run() {
		try {
			watchedInstance.join();
			lockedInstance.unlock();
		} catch(final Throwable ex) {
			this.ex = ex;
		}
	}
}

