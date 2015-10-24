package org.fs.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.fs.utils.exception.SerializationException;

/**
 * Class that handles (de)serialization.
 *
 * @author FS
 */
public class Serializer {

	/**
	 * Will serialize supplied object into a sequence of bytes.
	 *
	 * @param obj
	 *            the object to serialize
	 * @return a {@code byte[]}
	 * @throws SerializationException
	 *             if any exception occurs during serialization
	 */
	public static byte[] serialize(final Object obj) throws SerializationException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			final ObjectOutputStream oos = new ObjectOutputStream(baos);
			try {
				oos.writeObject(obj);
				return baos.toByteArray();
			} finally {
				Closer.close(oos);
			}
		} catch(final Exception ex) {
			throw new SerializationException(ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * Will recreate an object from the given byte sequence.
	 *
	 * @param bytes
	 *            object in it's serialized form
	 * @return deserialized object
	 * @throws SerializationException
	 *             if the given bytes sequence is not a serialized object or if any exception occurs
	 *             during deserialization
	 */
	public static Object deserialize(final byte[] bytes) throws SerializationException {
		final ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
		try {
			final ObjectInputStream oos = new ObjectInputStream(baos);
			try {
				return oos.readObject();
			} finally {
				Closer.close(oos);
			}
		} catch(final Exception ex) {
			throw new SerializationException(ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * Will recreate an object from the given byte sequence and cast it to a given class.
	 *
	 * @param bytes
	 *            object in it's serialized form
	 * @param castClass
	 *            desired class
	 * @return deserialized object of a provided class
	 * @throws SerializationException
	 *             if the given bytes sequence is not a serialized object, if it cannot be cast to a
	 *             given class or if any exception occurs during deserialization
	 */
	public static <T>T deserialize(final byte[] bytes, final Class <T> castClass) throws SerializationException {
		try {
			return castClass.cast(deserialize(bytes));
		} catch(final ClassCastException ex) {
			throw new SerializationException(ex.getLocalizedMessage(), ex);
		}
	}
}

