package org.fs.utils.net;

import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

import org.fs.utils.collection.map.BasicSortedMap;
import org.fs.utils.exception.ImmutableInstanceEx;

/**
 * A class, that sets request headers for a given {@link URLConnection}.
 * <p>
 * Not thread-safe, while mutable - concurrent modifications are not allowed.
 * 
 * @author FS
 */
public class UrlConPreparer {
	
	protected boolean				mutable	= true;
	protected Map <String, String>	custom;
	
	/** Default no-arg constructor */
	public UrlConPreparer() {}
	
	/** Copy-constructor */
	@SuppressWarnings("javadoc")
	public UrlConPreparer(final UrlConPreparer source) {
		setRequestProperties(source.getRequestPropertiest());
	}
	
	/**
	 * Prepares an URL connection, setting it's cookies, referrer and all other request properties,
	 * that are defined for this preparer.
	 * 
	 * @param urlCon
	 *            an {@link URLConnection}
	 * @param cookies
	 *            a cookies map (may be {@code null})
	 * @param referrer
	 *            referrer (may be {@code null})
	 * @return urlCon
	 */
	public URLConnection prepare(
			final URLConnection urlCon,
			final Map <String, String> cookies,
			final String referrer) {
		if (custom != null) {
			for (final Entry <String, String> customEntry : custom.entrySet()) {
				urlCon.setRequestProperty(customEntry.getKey(), customEntry.getValue());
			}
		}
		if (referrer != null) {
			urlCon.setRequestProperty("Referrer", referrer);
		}
		if (cookies != null && cookies.size() > 0) {
			final StringBuilder cookieStrB = new StringBuilder();
			for (final Entry <String, String> cookieEntry : cookies.entrySet()) {
				if (cookieStrB.length() != 0) {
					cookieStrB.append("; ");
				}
				cookieStrB.append(cookieEntry.getKey() + "=" + cookieEntry.getValue());
			}
			urlCon.addRequestProperty("Cookie", cookieStrB.toString());
		}
		return urlCon;
	}
	
	/**
	 * Makes this object immutable, disallowing setting/removing request properties. Cannot be
	 * undone.
	 */
	public void makeImmutable() {
		mutable = false;
	}
	
	public boolean isMutable() {
		return mutable;
	}
	
	public void setRequestProperty(final String key, final String value) {
		if (!mutable) throw new ImmutableInstanceEx();
		if (custom == null) {
			custom = new BasicSortedMap <String, String>(7);
		}
		custom.put(key, value);
	}
	
	public void setRequestProperties(final Map <String, String> properties) {
		if (!mutable) throw new ImmutableInstanceEx();
		if (custom == null) {
			custom = new BasicSortedMap <String, String>(properties.size());
		}
		custom.putAll(properties);
	}
	
	public String getRequestProperty(final String key) {
		if (!mutable) throw new ImmutableInstanceEx();
		if (custom == null) return null;
		return custom.get(key);
	}
	
	public String removeRequestProperty(final String key) {
		if (!mutable) throw new ImmutableInstanceEx();
		if (custom == null) return null;
		return custom.remove(key);
	}
	
	/** @return non-backed map of custom properties */
	public Map <String, String> getRequestPropertiest() {
		if (custom == null) return new BasicSortedMap <String, String>(0);
		return ((BasicSortedMap <String, String>) custom).clone();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (custom == null ? 0 : custom.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof UrlConPreparer)) return false;
		final UrlConPreparer other = (UrlConPreparer) obj;
		if (custom == null) {
			if (other.custom != null) return false;
		} else if (!custom.equals(other.custom)) return false;
		return true;
	}
}
