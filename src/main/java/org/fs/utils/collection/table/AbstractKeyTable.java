package org.fs.utils.collection.table;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.fs.utils.ObjectUtils;
import org.fs.utils.collection.iter.KeyIterator;
import org.fs.utils.collection.map.IndexedMap;

/**
 * This class easiers an efforts of table creation, implementivg some methods via result of others.
 * However, much likely, such implementation will not be optimal and may require later optimization.
 * 
 * @author FS
 * @param <Rt>
 * @param <Ct>
 * @param <T>
 */
public abstract class AbstractKeyTable <Rt,Ct,T> implements KeyTable <Rt, Ct, T> {
	
	@Override
	public T get(Rt rowKey, Ct colKey, T valueToInsertIfNull) {
		T value = get(rowKey, colKey);
		if (value != null) return value;
		put(rowKey, colKey, valueToInsertIfNull);
		return null;
	}
	
	@Override
	public int getRowCount() {
		return rowKeyList().size();
	}
	
	@Override
	public int getColCount() {
		return colKeyList().size();
	}
	
	@Override
	public void clear() {
		for (final Rt rowKey : rowKeyList()) {
			for (final Ct colKey : colKeyList()) {
				remove(rowKey, colKey);
			}
		}
	}
	
	@Override
	public boolean isEmpty() {
		for (final Ct colKey : colKeyList()) {
			if (!isEmptyCol(colKey)) return false;
		}
		return true;
	}
	
	@Override
	public IndexedMap <Ct, T> removeRow(final Rt kx) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public IndexedMap <Rt, T> removeCol(final Ct ky) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isEmptyRow(final Rt kx) {
		final IndexedMap <Ct, T> row = getRow(kx);
		return Collections.frequency(row.values(), null) == row.size();
	}
	
	@Override
	public boolean isEmptyCol(final Ct ky) {
		final IndexedMap <Rt, T> col = getCol(ky);
		return Collections.frequency(col.values(), null) == col.size();
	}
	
	@Override
	public Rt getRowKeyAt(final int rowIdx) {
		if (rowIdx < 0) throw new IndexOutOfBoundsException("rowIdx < 0");
		if (rowIdx >= getRowCount())
			throw new IndexOutOfBoundsException("rowIdx >= getRowCount()");
		return rowKeyList().get(rowIdx);
	}
	
	@Override
	public Ct getColKeyAt(final int colIdx) {
		if (colIdx < 0) throw new IndexOutOfBoundsException("colIdx < 0");
		if (colIdx >= getColCount())
			throw new IndexOutOfBoundsException("colIdx >= getColCount()");
		return colKeyList().get(colIdx);
	}
	
	@Override
	public int getRowKeyIndex(final Rt kx) {
		return rowKeyList().indexOf(kx);
	}
	
	@Override
	public int getColKeyIndex(final Ct ky) {
		return colKeyList().indexOf(ky);
	}
	
	@Override
	public KeyIterator <T, Rt> valuesInCol(final Ct colKey) {
		return new KeyTableLineIterator <Rt, Ct>(rowKeyList(), colKey, false);
	}
	
	@Override
	public KeyIterator <T, Ct> valuesInRow(final Rt rowKey) {
		return new KeyTableLineIterator <Ct, Rt>(colKeyList(), rowKey, true);
	}
	
	@Override
	public Object[][] toArray() {
		final Object[][] result = new Object[getColCount()][getRowCount()];
		final List <Rt> rowKeyList = rowKeyList();
		final List <Ct> colKeyList = colKeyList();
		int colIdx = 0;
		int rowIdx = 0;
		for (final Ct colKey : colKeyList) {
			for (final Rt rowKey : rowKeyList) {
				final T value = get(rowKey, colKey);
				result[colIdx][rowIdx] = value;
				++rowIdx;
			}
			++colIdx;
		}
		return result;
	}
	
	@Override
	public T[][] toArray(final T[] instance) {
		final T[][] result = (T[][]) Array.newInstance(instance.getClass().getComponentType(),
				getColCount(), getRowCount());
		final List <Rt> rowKeyList = rowKeyList();
		final List <Ct> colKeyList = colKeyList();
		int colIdx = 0;
		int rowIdx = 0;
		for (final Ct colKey : colKeyList) {
			for (final Rt rowKey : rowKeyList) {
				final T value = get(rowKey, colKey);
				result[colIdx][rowIdx] = value;
				++rowIdx;
			}
			++colIdx;
		}
		return result;
	}
	
	@Override
	public IndexedMap <Ct, T> insertRow(final int pos, final Rt rowKey) {
		rowKeyList().add(pos, rowKey);
		return getRow(rowKey);
	}
	
	@Override
	public IndexedMap <Rt, T> insertCol(final int pos, final Ct colKey) {
		colKeyList().add(pos, colKey);
		return getCol(colKey);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof KeyTable <?, ?, ?>)) return false;
		final KeyTable <Object, Object, Object> cast = (KeyTable <Object, Object, Object>) obj;
		final List <?> rowKeyList = rowKeyList();
		final List <?> colKeyList = colKeyList();
		if (!rowKeyList.equals(cast.rowKeyList())) return false;
		if (!colKeyList.equals(cast.colKeyList())) return false;
		for (final Object colKey : colKeyList) {
			for (final Object rowKey : rowKeyList) {
				final T value1 = get((Rt) rowKey, (Ct) colKey);
				final Object value2 = cast.get(rowKey, colKey);
				if (!ObjectUtils.eq(value1, value2)) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final List <Rt> rowKeyList = rowKeyList();
		final List <Ct> colKeyList = colKeyList();
		int hashCode = rowKeyList.hashCode();
		hashCode = hashCode * 31 + colKeyList.hashCode();
		for (final Ct ct : colKeyList) {
			for (final Rt rt : rowKeyList) {
				final T value = get(rt, ct);
				hashCode = hashCode * 31 + (value == null ? 0 : value.hashCode());
			}
		}
		return hashCode;
	}
	
	public class KeyTableLineIterator <Key1,Key2> implements KeyIterator <T, Key1> {
		
		private final List <Key1>	keysList;
		private final Key2			fixedKey;
		private int					currPos	= -2;
		private final boolean		horizontal;
		
		/**
		 * @param keysList
		 *            list of keys. Must be backed by the map (at least, it's {@code remove} method)
		 * @param fixedKey
		 *            fixed second key
		 * @param horizontal
		 *            is this a horizontal line iterator
		 */
		public KeyTableLineIterator(
				final List <Key1> keysList,
				final Key2 fixedKey,
				final boolean horizontal) {
			this.keysList = keysList;
			this.fixedKey = fixedKey;
			this.horizontal = horizontal;
		}
		
		@Override
		public boolean hasNext() {
			if (currPos + 1 >= keysList.size()) return false;
			return true;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			if (!hasNext()) throw new NoSuchElementException();
			final Key1 next = keysList.get(++currPos);
			if (horizontal) {
				return get((Rt) fixedKey, (Ct) next);
			}
			return get((Rt) next, (Ct) fixedKey);
		}
		
		@Override
		public void remove() {
			if (currPos < 0) throw new IllegalStateException();
			keysList.remove(currPos);
			--currPos;
		}
		
		@Override
		public Key1 getKey() throws IllegalStateException {
			if (currPos < 0) throw new IllegalStateException();
			return keysList.get(currPos);
		}
	}
}
