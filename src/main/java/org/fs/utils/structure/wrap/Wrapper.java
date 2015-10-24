package org.fs.utils.structure.wrap;

import java.io.Serializable;

import org.fs.utils.ObjectUtils;

/**
 * This class wraps any object inside of it. Can be used to retrieve additional results from
 * methods.
 *
 * @author FS
 * @param <T>
 */
public class Wrapper<T> implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private T					object;

	public Wrapper() {}

	public Wrapper(final T object) {
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	public void setObject(final T object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "(" + (object == null ? "null" : object.toString()) + ")";
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Wrapper)) return false;
		return ObjectUtils.equals(object, ((Wrapper <T>)obj).getObject());
	}

	public boolean objectEquals(final Object obj) {
		if (this == obj) return true;
		return ObjectUtils.equals(object, obj);
	}

	@Override
	public int hashCode() {
		return 12573 + ObjectUtils.hashCode(object);
	}
}

