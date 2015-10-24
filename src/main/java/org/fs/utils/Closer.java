package org.fs.utils;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.stream.ImageInputStream;
import javax.naming.Context;

/**
 * Null-safe closer class, which does not throws exception, yet it does invoke a printStackTrace()
 * method if it occurs.
 * 
 * @author FS
 */
public class Closer {
	
	/**
	 * Null-safe closer for {@link Closeable}. Does not throw an {@link IOException}, but will
	 * invoke printStackTrace() if it occurs.
	 * 
	 * @param toClose
	 *            Closeable to close (may be {@code null})
	 */
	public static void close(final Closeable toClose) {
		if (toClose == null) return;
		try {
			toClose.close();
		} catch(final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Null-safe closer for {@link Socket}. Does not throw an {@link Exception}, but will invoke
	 * printStackTrace() if it occurs.
	 * 
	 * @param toClose
	 *            Socket to close (may be {@code null})
	 */
	public static void close(final Socket toClose) {
		if (toClose == null) return;
		try {
			toClose.close();
		} catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Null-safe closer for {@link ServerSocket}. Does not throw an {@link Exception}, but will
	 * invoke printStackTrace() if it occurs.
	 * 
	 * @param toClose
	 *            ServerSocket to close (may be {@code null})
	 */
	public static void close(final ServerSocket toClose) {
		if (toClose == null) return;
		try {
			toClose.close();
		} catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Null-safe closer for {@link Context}. Does not throw an {@link Exception}, but will invoke
	 * printStackTrace() if it occurs.
	 * 
	 * @param toClose
	 *            Context to close (may be {@code null})
	 */
	public static void close(final Context toClose) {
		if (toClose == null) return;
		try {
			toClose.close();
		} catch(final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Null-safe closer for {@link ImageInputStream}. Does not throw an {@link IOException}, but
	 * will invoke printStackTrace() if it occurs.
	 * 
	 * @param toClose
	 *            Closeable to close (may be {@code null})
	 */
	public static void close(final ImageInputStream toClose) {
		if (toClose == null) return;
		try {
			toClose.close();
		} catch(final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Null-safe closer for {@link Connection}. Does not throw an {@link SQLException}, but will
	 * invoke printStackTrace() if it occurs.
	 * 
	 * @param toClose
	 *            Closeable to close (may be {@code null})
	 */
	public static void close(final Connection toClose) {
		if (toClose == null) return;
		try {
			toClose.close();
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Null-safe closer for {@link Statement}. Does not throw an {@link SQLException}, but will
	 * invoke printStackTrace() if it occurs.
	 * 
	 * @param toClose
	 *            Closeable to close (may be {@code null})
	 */
	public static void close(final Statement toClose) {
		if (toClose == null) return;
		try {
			toClose.close();
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
}
