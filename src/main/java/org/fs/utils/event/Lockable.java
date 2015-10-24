package org.fs.utils.event;

import org.fs.utils.exception.LockedException;

public interface Lockable {

	public void lock() throws LockedException;

	public void unlock();

	public boolean isLocked();
}

