package org.fs.utils;

/**
 * A very simple class for a time measurement. Operates with milliseconds, obtained through
 * {@link System#currentTimeMillis()}.
 * <p>
 * The main operating method is {@link #measure()}.
 * 
 * @author FS
 */
public class StopWatch {
	
	private long	time;
	
	/** Creates a new stopwatch instance, setting the time mark to current time. */
	public StopWatch() {
		time = System.currentTimeMillis();
	}
	
	/**
	 * Marks the current time and returns the difference between now and previous time mark.
	 * 
	 * @return difference between now and previous time mark
	 */
	public long measure() {
		final long curr = System.currentTimeMillis();
		final long result = curr - time;
		time = curr;
		return result;
	}
	
	/** @return difference between now and previous time mark without marking current time */
	public long peek() {
		final long curr = System.currentTimeMillis();
		final long result = curr - time;
		return result;
	}
	
	/** @return the last time mark */
	public long getTime() {
		return time;
	}
	
	/**
	 * Sets the time mark to a given value
	 * 
	 * @param time
	 *            a new time mark
	 * @return an old time mark
	 */
	public long setTime(final long time) {
		final long result = this.time;
		this.time = time;
		return result;
	}
	
	//
	// Auto-generated
	//
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (time ^ time >>> 32);
		return result;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof StopWatch)) return false;
		final StopWatch other = (StopWatch) obj;
		if (time != other.time) return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Stopwatch [" + time + "]";
	}
}
