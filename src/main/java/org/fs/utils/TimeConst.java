package org.fs.utils;

/**
 * Defines the basic time constants (second, minute, hour, etc) from milliseconds.
 * <p>
 * All of these are supplied with warning about their incostitency. Mankind has done a great job
 * messing things up in date/time matters, so use this with care.
 * <p>
 * If you need to get EXACTLY the date/time, get yourself a <a
 * href="http://joda-time.sourceforge.net/">JodaTime</a>, dammit!
 * <p>
 * For more info, refer to <a
 * href="http://www.exit109.com/~ghealton/y2k/yrexamples.html">http://www.
 * exit109.com/~ghealton/y2k/yrexamples.html</a>
 *
 * @author FS
 */
public class TimeConst {

	/**
	 * 1000 ms constant.
	 * <p>
	 * The only one that is truly consistent, thank God for that.
	 */
	public static final int		SEC		= 1000;
	/**
	 * 60 seconds constant.
	 * <p>
	 * Beware: leap seconds can make some minutes actually last 61 or 59 seconds!
	 */
	public static final int		MIN		= 60 * SEC;
	/**
	 * 3600 seconds constant (60*60).
	 * <p>
	 * Beware: leap seconds can make some hours actually last 3601 or 3599 seconds.
	 */
	public static final long	HOUR	= 60 * MIN;
	/**
	 * 24 hours (or 24 * 3600 seconds constant, where 3600 is a hour length in seconds (60*60)).
	 * <p>
	 * Beware: leap seconds can make some hours actually last 3601 or 3599 seconds.
	 * <p>
	 * Beware-2: local DST (Daylight Saving Time) rules can make a full day last 23 or 25 hours!
	 */
	public static final long	DAY		= 24 * HOUR;
}

