package org.fs.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimplestFormatter extends Formatter {

	private final Object		args[]		= new Object[1];
	private final MessageFormat	formatter	= new MessageFormat("{0,date,yyyy-MM-dd} {0,time,HH-mm-ss}");
	private final Date			dat			= new Date();
	private final boolean		debug;

	public SimplestFormatter() {
		this(false);
	}

	/**
	 * @param debug
	 *            wether or not show logger name in log record
	 */
	public SimplestFormatter(final boolean debug) {
		this.debug = debug;
	}

	@Override
	public String format(final LogRecord record) {
		final StringBuffer sb = new StringBuffer();
		// Minimize memory allocations here.
		dat.setTime(record.getMillis());
		args[0] = dat;
		final StringBuffer text = new StringBuffer();
		formatter.format(args, text, null);
		sb.append(text);
		if (debug) {
			sb.append(" [");
			sb.append(record.getLoggerName());
			sb.append(']');
		}
		sb.append(' ');
		final String message = formatMessage(record);
		sb.append(record.getLevel().getLocalizedName());
		sb.append(": {");
		sb.append(record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf('.') + 1));
		sb.append(".");
		sb.append(record.getSourceMethodName());
		sb.append(") ");
		sb.append(message);
		sb.append('\n');
		if (record.getThrown() != null) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			record.getThrown().printStackTrace(pw);
			pw.close();
			sb.append(sw.toString());
		}
		return sb.toString();
	}
}

