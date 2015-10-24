package org.fs.utils.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An {@link OutputStream}, where u can write anything - it will silently swallow all.
 * <p>
 * This class is a singleton, use {@link NullOutputStream#instance}.
 *
 * @author FS
 */
public class NullOutputStream extends OutputStream {

	public static final NullOutputStream	instance	= new NullOutputStream();

	/** Singleton */
	private NullOutputStream() {}

	@Override
	public void write(final int b) throws IOException {}
}

