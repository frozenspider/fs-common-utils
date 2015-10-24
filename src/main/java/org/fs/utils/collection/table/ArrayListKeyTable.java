package org.fs.utils.collection.table;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import org.fs.utils.ObjectUtils;
import org.fs.utils.collection.CollectionUtils;
import org.fs.utils.collection.iter.KeyIterator;
import org.fs.utils.collection.map.AbstractMapEntry;
import org.fs.utils.collection.map.IndexedMap;
import org.fs.utils.collection.map.UnsortedMap;
import org.fs.utils.collection.set.IndexedSet;
import org.fs.utils.structure.wrap.Pair;

/**
 * A standard implementation of {@link KeyTable}. Not thread-safe. Does support {@code null} keys,
 * but {@code null} values are considered an empty cell.
 * 
 * @author FS
 * @param <Rt>
 *            row key type
 * @param <Ct>
 *            column key type
 * @param <T>
 *            data type
 */
public class ArrayListKeyTable <Rt,Ct,T> extends AbstractKeyTable <Rt, Ct, T> implements
		Serializable, Cloneable {
	
	private static final long				serialVersionUID	= 4440272603703587472L;
	protected volatile transient List <Rt>	_rowKeyList;
	protected volatile transient List <Ct>	_colKeyList;
	// Fields are effectively final and is replaced only during clone operation
	protected ArrayList <Rt>				rowKeys;
	protected ArrayList <Ct>				colKeys;
	protected ArrayListTable <T>			table;
	
	public ArrayListKeyTable() {
		this.table = new ArrayListTable <T>();
		this.rowKeys = new ArrayList <Rt>();
		this.colKeys = new ArrayList <Ct>();
	}
	
	public ArrayListKeyTable(final KeyTable <? extends Rt, ? extends Ct, ? extends T> instancesTable) {
		this();
		this.putAll(instancesTable);
	}
	
	protected Pair <Integer, Integer> getTableKeys(
			final Rt rowKey,
			final Ct colKey,
			final boolean createIfNotExist) {
		int rowIdx = rowKeys.indexOf(rowKey);
		if (rowIdx == -1) {
			if (!createIfNotExist) return null;
			rowKeys.add(rowKey);
			rowIdx = rowKeys.size() - 1;
		}
		int colIdx = colKeys.indexOf(colKey);
		if (colIdx == -1) {
			if (!createIfNotExist) return null;
			colKeys.add(colKey);
			colIdx = colKeys.size() - 1;
		}
		return new Pair <Integer, Integer>(rowIdx, colIdx);
	}
	
	@Override
	public T put(final Rt rowKey, final Ct colKey, final T value) {
		final Pair <Integer, Integer> tableKeys = getTableKeys(rowKey, colKey, true);
		return table.put(tableKeys.getFirst(), tableKeys.getSecond(), value);
	}
	
	@Override
	public void putAll(final KeyTable <? extends Rt, ? extends Ct, ? extends T> other) {
		final List <? extends Rt> rowKeysList = other.rowKeyList();
		final List <? extends Ct> colKeysList = other.colKeyList();
		// What the hell is this generics?!
		@SuppressWarnings("unchecked")
		final KeyTable <Object, Object, Object> cast = (KeyTable <Object, Object, Object>) other;
		for (final Ct ct : colKeysList) {
			for (final Rt rt : rowKeysList) {
				final T value = (T) cast.get(rt, ct);
				put(rt, ct, value);
			}
		}
	}
	
	@Override
	public T get(final Rt rowKey, final Ct colKey) {
		final Pair <Integer, Integer> tableKeys = getTableKeys(rowKey, colKey, false);
		if (tableKeys == null) return null;
		return table.get(tableKeys.getFirst(), tableKeys.getSecond());
	}
	
	@Override
	public T get(final Rt rowKey, final Ct colKey, final T valueToInsertIfNull) {
		final Pair <Integer, Integer> tableKeys = getTableKeys(rowKey, colKey, true);
		return table.get(tableKeys.getFirst(), tableKeys.getSecond(), valueToInsertIfNull);
	}
	
	@Override
	public T remove(final Rt rowKey, final Ct colKey) {
		final Pair <Integer, Integer> tableKeys = getTableKeys(rowKey, colKey, false);
		if (tableKeys == null) return null;
		final T result = table.remove(tableKeys.getFirst(), tableKeys.getSecond());
		// int tableSizeX = table.sizeX(); int tableSizeY = table.sizeY();
		// while (keysX.size() > tableSizeX) keysX.remove(keysX.size() - 1);
		// while (keysY.size() > tableSizeY) keysY.remove(keysY.size() - 1);
		return result;
	}
	
	@Override
	public IndexedMap <Ct, T> removeRow(final Rt rowKey) {
		final int rowIdx = rowKeys.indexOf(rowKey);
		if (rowIdx < 0) return null;
		return removeRow(rowIdx);
	}
	
	@Override
	public IndexedMap <Rt, T> removeCol(final Ct colKey) {
		final int colIdx = colKeys.indexOf(colKey);
		if (colIdx < 0) return null;
		return removeCol(colIdx);
	}
	
	protected IndexedMap <Ct, T> removeRow(final int rowIdx) {
		rowKeys.remove(rowIdx);
		final List <T> col = table.removeRow(rowIdx);
		if (col == null)
			return new UnsortedMap <Ct, T>(colKeys, Collections.<T> nCopies(colKeys.size(), null));
		return new UnsortedMap <Ct, T>(colKeys, col);
	}
	
	protected IndexedMap <Rt, T> removeCol(final int colIdx) {
		colKeys.remove(colIdx);
		final List <T> row = table.removeCol(colIdx);
		if (row == null)
			return new UnsortedMap <Rt, T>(rowKeys, Collections.<T> nCopies(rowKeys.size(), null));
		return new UnsortedMap <Rt, T>(rowKeys, row);
	}
	
	@Override
	public IndexedMap <Ct, T> insertRow(final int pos, final Rt rowKey) {
		if (pos < 0 || pos > getRowCount()) throw new IndexOutOfBoundsException("pos = " + pos);
		final int rowIdx = rowKeys.indexOf(rowKey);
		if (rowIdx >= 0)
			throw new IllegalArgumentException("Row key \"" + rowKey + "\" already exists");
		rowKeys.add(pos, rowKey);
		table.insertRow(pos);
		return getRow(rowKey);
	}
	
	@Override
	public IndexedMap <Rt, T> insertCol(final int pos, final Ct colKey) {
		if (pos < 0 || pos > getColCount()) throw new IndexOutOfBoundsException("pos = " + pos);
		final int colIdx = colKeys.indexOf(colKey);
		if (colIdx >= 0)
			throw new IllegalArgumentException("Column key \"" + colKey + "\" already exists");
		colKeys.add(pos, colKey);
		table.insertCol(pos);
		return getCol(colKey);
	}
	
	@Override
	public IndexedMap <Ct, T> getRow(final Rt rowKey) {
		return new BackedRowMap(rowKey);
	}
	
	@Override
	public IndexedMap <Rt, T> getCol(final Ct colKey) {
		return new BackedColMap(colKey);
	}
	
	@Override
	public int getRowCount() {
		return rowKeys.size();
	}
	
	@Override
	public int getColCount() {
		return colKeys.size();
	}
	
	@Override
	public boolean isEmpty() {
		return table.isEmpty();
	}
	
	@Override
	public boolean isEmptyRow(final Rt rowKey) {
		final IndexedMap <Ct, T> row = getRow(rowKey);
		return Collections.frequency(row.values(), null) == row.size();
	}
	
	@Override
	public boolean isEmptyCol(final Ct colKey) {
		final IndexedMap <Rt, T> col = getCol(colKey);
		return Collections.frequency(col.values(), null) == col.size();
	}
	
	@Override
	public boolean contains(final Object o) {
		return table.contains(o);
	}
	
	@Override
	public Pair <Rt, Ct> indexOf(final Object value) {
		final Pair <Integer, Integer> tableKeys = table.indexOf(value);
		if (tableKeys == null) return null;
		return new Pair <Rt, Ct>(rowKeys.get(tableKeys.getFirst()),
				colKeys.get(tableKeys.getSecond()));
	}
	
	@Override
	public List <Pair <Rt, Ct>> indicesOf(final Object value) {
		final List <Pair <Rt, Ct>> result = new ArrayList <Pair <Rt, Ct>>();
		final List <Pair <Integer, Integer>> tableKeysList = table.indicesOf(value);
		for (final Pair <Integer, Integer> tableKeys : tableKeysList) {
			result.add(new Pair <Rt, Ct>(rowKeys.get(tableKeys.getFirst()),
					colKeys.get(tableKeys.getSecond())));
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof KeyTable <?, ?, ?>)) return false;
		final KeyTable <Object, Object, Object> cast = (KeyTable <Object, Object, Object>) obj;
		final Set <?> rowKeyList = new HashSet <Object>(rowKeyList());
		final Set <?> colKeyList = new HashSet <Object>(colKeyList());
		// TODO: Change API to make row and column key to be IndexedSet's
		if (!rowKeyList.equals(new HashSet <Object>(cast.rowKeyList()))) return false;
		if (!colKeyList.equals(new HashSet <Object>(cast.colKeyList()))) return false;
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
	
	@Override
	public String toString() {
		final ArrayListTable <String> delegate = new ArrayListTable <String>();
		final int rowCount = table.getRowCount();
		final int colCount = table.getColCount();
		for (int colIdx = 0; colIdx < colCount; colIdx++) {
			for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
				final T t = table.get(rowIdx, colIdx);
				delegate.put(rowIdx + 1, colIdx + 1, t == null ? "" : t.toString());
			}
		}
		int i = 0;
		for (final Rt rowKey : rowKeys) {
			delegate.put(++i, 0, String.valueOf(rowKey));
		}
		i = 0;
		for (final Ct colKey : colKeys) {
			delegate.put(0, ++i, String.valueOf(colKey));
		}
		return delegate.toString();
	}
	
	@Override
	public KeyIterator <T, Ct> valuesInRow(final Rt x) {
		final int idx = rowKeys.indexOf(x);
		if (idx == -1) return CollectionUtils.emptyIterator();
		return new HorizontalIterator(table.valuesInRow(idx));
	}
	
	@Override
	public KeyIterator <T, Rt> valuesInCol(final Ct y) {
		final int idx = colKeys.indexOf(y);
		if (idx == -1) return CollectionUtils.emptyIterator();
		return new VerticalIterator(table.valuesInCol(idx));
	}
	
	//
	//
	//
	@Override
	public Object[][] toArray() {
		return table.toArray();
	}
	
	@Override
	public T[][] toArray(final T[] instance) {
		return table.toArray(instance);
	}
	
	@Override
	public List <Rt> rowKeyList() {
		if (_rowKeyList == null) {
			_rowKeyList = new BackedRowKeyList();
		}
		return _rowKeyList;
	}
	
	@Override
	public List <Ct> colKeyList() {
		if (_colKeyList == null) {
			_colKeyList = new BackedColKeyList();
		}
		return _colKeyList;
	}
	
	@Override
	public Rt getRowKeyAt(final int rowIdx) {
		if (rowIdx < 0) throw new IndexOutOfBoundsException("Row " + rowIdx + " < 0");
		if (rowIdx >= rowKeys.size())
			throw new IndexOutOfBoundsException("Row " + rowIdx + " of " + getRowCount());
		return rowKeys.get(rowIdx);
	}
	
	@Override
	public Ct getColKeyAt(final int colIdx) {
		if (colIdx < 0) throw new IndexOutOfBoundsException("Column " + colIdx + " < 0");
		if (colIdx >= colKeys.size())
			throw new IndexOutOfBoundsException("Column " + colIdx + " of " + getColCount());
		return colKeys.get(colIdx);
	}
	
	@Override
	public int getRowKeyIndex(final Rt rowKey) {
		return rowKeys.indexOf(rowKey);
	}
	
	@Override
	public int getColKeyIndex(final Ct colKey) {
		return colKeys.indexOf(colKey);
	}
	
	@Override
	public void clear() {
		rowKeys.clear();
		colKeys.clear();
		table.clear();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		final ArrayListKeyTable <Rt, Ct, T> clone = (ArrayListKeyTable <Rt, Ct, T>) super.clone();
		clone.rowKeys = (ArrayList <Rt>) rowKeys.clone();
		clone.colKeys = (ArrayList <Ct>) colKeys.clone();
		clone.table = (ArrayListTable <T>) table.clone();
		return clone;
	}
	
	@Override
	public void swapRows(final Rt rowKey1, final Rt rowKey2) {
		// int szX = keysX.size();
		// if (x1 >= szX || x2 >= szX) throw new IndexOutOfBoundsException();
		// Collections.swap(keysX, x1, x2);
		// table.swapX(x1, x2);
		int rowIdx1 = rowKeys.indexOf(rowKey1);
		int rowIdx2 = rowKeys.indexOf(rowKey2);
		if (rowIdx1 == -1) {
			rowIdx1 = rowKeys.size();
			rowKeys.add(rowKey1);
		}
		if (rowIdx2 == -1) {
			rowIdx2 = rowKeys.size();
			rowKeys.add(rowKey2);
		}
		Collections.swap(rowKeys, rowIdx1, rowIdx2);
		table.swapRows(rowIdx1, rowIdx2);
	}
	
	@Override
	public void swapCols(final Ct colKey1, final Ct colKey2) {
		int colIdx1 = colKeys.indexOf(colKey1);
		int colIdx2 = colKeys.indexOf(colKey2);
		if (colIdx1 == -1) {
			colIdx1 = colKeys.size();
			colKeys.add(colKey1);
		}
		if (colIdx2 == -1) {
			colIdx2 = colKeys.size();
			colKeys.add(colKey2);
		}
		Collections.swap(colKeys, colIdx1, colIdx2);
		table.swapCols(colIdx1, colIdx2);
	}
	
	@Override
	public KeyTable <Rt, Ct, T> subTable(
			final Rt fromRow,
			final Ct fromCol,
			final Rt toRow,
			final Ct toCol) {
		return new ArrayListKeySubTable(fromRow, fromCol, toRow, toCol);
	}
	
	protected class HorizontalIterator implements KeyIterator <T, Ct> {
		
		private final KeyIterator <T, Integer>	tableIter;
		
		public HorizontalIterator(final KeyIterator <T, Integer> tableIter) {
			this.tableIter = tableIter;
		}
		
		@Override
		public boolean hasNext() {
			return tableIter.hasNext();
		}
		
		@Override
		public T next() {
			return tableIter.next();
		}
		
		@Override
		public void remove() {
			tableIter.remove();
		}
		
		@Override
		public Ct getKey() {
			return colKeys.get(tableIter.getKey());
		}
	}
	
	protected class VerticalIterator implements KeyIterator <T, Rt> {
		
		private final KeyIterator <T, Integer>	tableIter;
		
		public VerticalIterator(final KeyIterator <T, Integer> tableIter) {
			this.tableIter = tableIter;
		}
		
		@Override
		public boolean hasNext() {
			return tableIter.hasNext();
		}
		
		@Override
		public T next() {
			return tableIter.next();
		}
		
		@Override
		public void remove() {
			tableIter.remove();
		}
		
		@Override
		public Rt getKey() {
			return rowKeys.get(tableIter.getKey());
		}
	}
	
	protected class BackedRowMap extends AbstractMap <Ct, T> implements IndexedMap <Ct, T> {
		
		private transient volatile IndexedSet <Ct>	_keySet	= null;
		private final Rt							rowKey;
		
		public BackedRowMap(final Rt kx) {
			this.rowKey = kx;
		}
		
		@Override
		public T put(final Ct key, final T value) {
			return ArrayListKeyTable.this.put(rowKey, key, value);
		}
		
		@Override
		public Ct getKeyAt(final int index) {
			return colKeys.get(index);
		}
		
		@Override
		public T getValueAt(final int index) {
			final Ct ky = getColKeyAt(index);
			if (ky == null) throw new IndexOutOfBoundsException();
			return ArrayListKeyTable.this.get(rowKey, ky);
		}
		
		@Override
		public Ct setKeyAt(final int index, final Ct newKey) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public T setValueAt(final int index, final T newValue) {
			final Ct ky = getColKeyAt(index);
			if (ky == null) throw new IndexOutOfBoundsException();
			return ArrayListKeyTable.this.put(rowKey, ky, newValue);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public int indexOfKey(final Object key) {
			try {
				return getColKeyIndex((Ct) key);
			} catch(final ClassCastException ex) {
				return -1;
			}
		}
		
		@Override
		public int indexOfValue(final Object value) {
			final int sz = size();
			for (int i = 0; i < sz; ++i) {
				if (value.equals(ArrayListKeyTable.this.get(rowKey, getColKeyAt(i)))) return i;
			}
			return -1;
		}
		
		@Override
		public int size() {
			return ArrayListKeyTable.this.getColCount();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public T remove(final Object key) {
			try {
				return ArrayListKeyTable.this.remove(rowKey, (Ct) key);
			} catch(final ClassCastException ex) {
				return null;
			}
		}
		
		@Override
		public Entry <Ct, T> removeAt(final int index) {
			final Ct ky = getColKeyAt(index);
			if (ky == null) throw new IndexOutOfBoundsException();
			return new Pair <Ct, T>(ky, ArrayListKeyTable.this.remove(rowKey, ky));
		}
		
		@Override
		public IndexedSet <Ct> keySet() {
			if (_keySet == null) {
				_keySet = new BackedRowMapKeySet();
			}
			return _keySet;
		}
		
		@Override
		public Set <Entry <Ct, T>> entrySet() {
			return new BackedRowSet(rowKey);
		}
		
		protected class BackedRowMapKeySet extends AbstractSet <Ct> implements IndexedSet <Ct> {
			
			@Override
			public int addAndGetPos(final Ct object) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public Ct get(final int index) {
				if (index < 0) throw new IndexOutOfBoundsException("index < 0");
				if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
				return colKeys.get(index);
			}
			
			@Override
			public Ct remove(final int index) {
				final Entry <Ct, T> removed = BackedRowMap.this.removeAt(index);
				return removed == null ? null : removed.getKey();
			}
			
			@Override
			public int indexOf(final Object o) {
				return BackedRowMap.this.indexOfKey(o);
			}
			
			@Override
			public Iterator <Ct> iterator() {
				return new Iterator <Ct>() {
					
					private final Iterator <Entry <Ct, T>>	i	= entrySet().iterator();
					
					@Override
					public boolean hasNext() {
						return i.hasNext();
					}
					
					@Override
					public Ct next() {
						return i.next().getKey();
					}
					
					@Override
					public void remove() {
						i.remove();
					}
				};
			}
			
			@Override
			public int size() {
				return BackedRowMap.this.size();
			}
		}
	}
	
	protected class BackedColMap extends AbstractMap <Rt, T> implements IndexedMap <Rt, T> {
		
		private transient volatile IndexedSet <Rt>	_keySet	= null;
		private final Ct							colKey;
		
		public BackedColMap(final Ct colKey) {
			this.colKey = colKey;
		}
		
		@Override
		public T put(final Rt key, final T value) {
			return ArrayListKeyTable.this.put(key, colKey, value);
		}
		
		@Override
		public Rt getKeyAt(final int index) {
			return rowKeys.get(index);
		}
		
		@Override
		public T getValueAt(final int index) {
			final Rt kx = getRowKeyAt(index);
			if (kx == null) throw new IndexOutOfBoundsException();
			return ArrayListKeyTable.this.get(kx, colKey);
		}
		
		@Override
		public Rt setKeyAt(final int index, final Rt newKey) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public T setValueAt(final int index, final T newValue) {
			final Rt kx = getRowKeyAt(index);
			if (kx == null) throw new IndexOutOfBoundsException();
			return ArrayListKeyTable.this.put(kx, colKey, newValue);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public int indexOfKey(final Object key) {
			try {
				return getRowKeyIndex((Rt) key);
			} catch(final ClassCastException ex) {
				return -1;
			}
		}
		
		@Override
		public int indexOfValue(final Object value) {
			final int sz = size();
			for (int i = 0; i < sz; ++i) {
				if (value.equals(ArrayListKeyTable.this.get(getRowKeyAt(i), colKey))) return i;
			}
			return -1;
		}
		
		@Override
		public int size() {
			return ArrayListKeyTable.this.getRowCount();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public T remove(final Object key) {
			try {
				return ArrayListKeyTable.this.remove((Rt) key, colKey);
			} catch(final ClassCastException ex) {
				return null;
			}
		}
		
		@Override
		public Entry <Rt, T> removeAt(final int index) {
			final Rt kx = getRowKeyAt(index);
			if (kx == null) throw new IndexOutOfBoundsException();
			return new Pair <Rt, T>(kx, ArrayListKeyTable.this.remove(kx, colKey));
		}
		
		@Override
		public IndexedSet <Rt> keySet() {
			if (_keySet == null) {
				_keySet = new BackedColMapKeySet();
			}
			return _keySet;
		}
		
		@Override
		public Set <Entry <Rt, T>> entrySet() {
			return new BackedColSet(colKey);
		}
		
		protected class BackedColMapKeySet extends AbstractSet <Rt> implements IndexedSet <Rt> {
			
			@Override
			public int addAndGetPos(final Rt object) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public Rt get(final int index) {
				if (index < 0) throw new IndexOutOfBoundsException("index < 0");
				if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
				return rowKeys.get(index);
			}
			
			@Override
			public Rt remove(final int index) {
				final Entry <Rt, T> removed = BackedColMap.this.removeAt(index);
				return removed == null ? null : removed.getKey();
			}
			
			@Override
			public int indexOf(final Object o) {
				return BackedColMap.this.indexOfKey(o);
			}
			
			@Override
			public Iterator <Rt> iterator() {
				return new Iterator <Rt>() {
					
					private final Iterator <Entry <Rt, T>>	i	= entrySet().iterator();
					
					@Override
					public boolean hasNext() {
						return i.hasNext();
					}
					
					@Override
					public Rt next() {
						return i.next().getKey();
					}
					
					@Override
					public void remove() {
						i.remove();
					}
				};
			}
			
			@Override
			public int size() {
				return BackedColMap.this.size();
			}
		}
	}
	
	protected class BackedRowSet extends AbstractSet <Entry <Ct, T>> {
		
		private final Rt	rowKeyFixed;
		
		public BackedRowSet(final Rt rowKeyFixed) {
			this.rowKeyFixed = rowKeyFixed;
		}
		
		@Override
		public boolean add(final Entry <Ct, T> e) {
			ArrayListKeyTable.this.put(rowKeyFixed, e.getKey(), e.getValue());
			return true;
		}
		
		@Override
		public boolean remove(final Object o) {
			if (!(o instanceof Entry <?, ?>)) return false;
			final Entry <?, ?> cast = (Entry <?, ?>) o;
			try {
				return ArrayListKeyTable.this.remove(rowKeyFixed, (Ct) cast.getKey()) != null;
			} catch(final ClassCastException ex) {
				return false;
			}
		}
		
		@Override
		public Iterator <Entry <Ct, T>> iterator() {
			return new BackedRowIterator(rowKeyFixed);
		}
		
		@Override
		public int size() {
			return colKeys.size();
		}
	}
	
	protected class BackedColSet extends AbstractSet <Entry <Rt, T>> {
		
		private final Ct	colKeyFixed;
		
		public BackedColSet(final Ct colKeyFixed) {
			this.colKeyFixed = colKeyFixed;
		}
		
		@Override
		public boolean add(final Entry <Rt, T> e) {
			ArrayListKeyTable.this.put(e.getKey(), colKeyFixed, e.getValue());
			return true;
		}
		
		@Override
		public boolean remove(final Object o) {
			if (!(o instanceof Entry <?, ?>)) return false;
			final Entry <?, ?> cast = (Entry <?, ?>) o;
			try {
				return ArrayListKeyTable.this.remove((Rt) cast.getKey(), colKeyFixed) != null;
			} catch(final ClassCastException ex) {
				return false;
			}
		}
		
		@Override
		public Iterator <Entry <Rt, T>> iterator() {
			return new BackedColIterator(colKeyFixed);
		}
		
		@Override
		public int size() {
			return rowKeys.size();
		}
	}
	
	protected class BackedRowIterator implements Iterator <Entry <Ct, T>> {
		
		private final Rt			rowKeyFixed;
		private final Iterator <Ct>	colKeysIter;
		private Ct					colKeyLast;
		private boolean				removed	= true;
		
		public BackedRowIterator(final Rt rowKeyFixed) {
			this.rowKeyFixed = rowKeyFixed;
			colKeysIter = colKeys.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return colKeysIter.hasNext();
		}
		
		@Override
		public Entry <Ct, T> next() {
			colKeyLast = colKeysIter.next();
			removed = false;
			return new BackedMapEntryCol(rowKeyFixed, colKeyLast);
		}
		
		@Override
		public void remove() {
			if (removed) throw new IllegalStateException();
			ArrayListKeyTable.this.remove(rowKeyFixed, colKeyLast);
		}
	}
	
	protected class BackedColIterator implements Iterator <Entry <Rt, T>> {
		
		private final Ct			colKeyFixed;
		private final Iterator <Rt>	rowKeysIter;
		private Rt					rowKeyLast;
		private boolean				removed	= true;
		
		public BackedColIterator(final Ct colKeyFixed) {
			this.colKeyFixed = colKeyFixed;
			rowKeysIter = rowKeys.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return rowKeysIter.hasNext();
		}
		
		@Override
		public Entry <Rt, T> next() {
			rowKeyLast = rowKeysIter.next();
			removed = false;
			return new BackedMapEntryRow(rowKeyLast, colKeyFixed);
		}
		
		@Override
		public void remove() {
			if (removed) throw new IllegalStateException();
			ArrayListKeyTable.this.remove(rowKeyLast, colKeyFixed);
		}
	}
	
	protected class BackedMapEntryRow extends AbstractMapEntry <Rt, T> {
		
		final Rt	rowKey;
		final Ct	colKey;
		
		public BackedMapEntryRow(final Rt kx, final Ct ky) {
			this.rowKey = kx;
			this.colKey = ky;
		}
		
		@Override
		public Rt getKey() {
			return rowKey;
		}
		
		@Override
		public T getValue() {
			return ArrayListKeyTable.this.get(rowKey, colKey);
		}
		
		@Override
		public T setValue(final T value) {
			return ArrayListKeyTable.this.put(rowKey, colKey, value);
		}
	}
	
	protected class BackedMapEntryCol extends AbstractMapEntry <Ct, T> {
		
		final Rt	rowKey;
		final Ct	colKey;
		
		public BackedMapEntryCol(final Rt kx, final Ct ky) {
			this.rowKey = kx;
			this.colKey = ky;
		}
		
		@Override
		public Ct getKey() {
			return colKey;
		}
		
		@Override
		public T getValue() {
			return ArrayListKeyTable.this.get(rowKey, colKey);
		}
		
		@Override
		public T setValue(final T value) {
			return ArrayListKeyTable.this.put(rowKey, colKey, value);
		}
	}
	
	protected class BackedRowKeyList extends AbstractList <Rt> {
		
		@Override
		public Rt get(final int index) {
			return rowKeys.get(index);
		}
		
		@Override
		public int size() {
			return rowKeys.size();
		}
		
		@Override
		public boolean add(final Rt e) {
			if (rowKeys.contains(e)) throw new IllegalArgumentException("Such key already exists");
			return rowKeys.add(e);
		}
		
		@Override
		public void add(final int index, final Rt e) {
			if (rowKeys.contains(e)) throw new IllegalArgumentException("Such key already exists");
			final int last = rowKeys.size();
			rowKeys.add(index, e);
			for (int i = index; i < last; ++i) {
				table.swapRows(i, last);
			}
		}
		
		@Override
		public Rt set(final int index, final Rt e) {
			final int idx = rowKeys.indexOf(e);
			if (idx != -1 && idx != index)
				throw new IllegalArgumentException("Such key already exists");
			return rowKeys.set(index, e);
		}
		
		@Override
		public Rt remove(final int index) {
			final Rt result = rowKeys.get(index);
			removeRow(index);
			return result;
		}
	}
	
	protected class BackedColKeyList extends AbstractList <Ct> {
		
		@Override
		public Ct get(final int index) {
			return colKeys.get(index);
		}
		
		@Override
		public int size() {
			return colKeys.size();
		}
		
		@Override
		public boolean add(final Ct e) {
			if (colKeys.contains(e)) throw new IllegalArgumentException("Such key already exists");
			return colKeys.add(e);
		}
		
		@Override
		public void add(final int index, final Ct e) {
			if (colKeys.contains(e)) throw new IllegalArgumentException("Such key already exists");
			final int last = colKeys.size();
			colKeys.add(index, e);
			for (int i = index; i < last; ++i) {
				table.swapCols(i, last);
			}
		}
		
		@Override
		public Ct set(final int index, final Ct e) {
			final int idx = colKeys.indexOf(e);
			if (idx != -1 && idx != index)
				throw new IllegalArgumentException("Such key already exists");
			return colKeys.set(index, e);
		}
		
		@Override
		public Ct remove(final int index) {
			final Ct result = colKeys.get(index);
			removeCol(index);
			return result;
		}
	}
	
	protected class ArrayListKeySubTable extends AbstractKeyTable <Rt, Ct, T> {
		
		private final Rt	fromRowKey;
		private final Ct	fromColKey;
		private final Rt	toRowKey;
		private final Ct	toColKey;
		
		public ArrayListKeySubTable(
				final Rt fromRowKey,
				final Ct fromColKey,
				final Rt toRowKey,
				final Ct toColKey) {
			this.fromRowKey = fromRowKey;
			this.fromColKey = fromColKey;
			this.toRowKey = toRowKey;
			this.toColKey = toColKey;
			// Sanity check
			getRowBoundIndices();
			getColBoundIndices();
		}
		
		//
		// Internal
		//
		protected RowIndices getRowBoundIndices() {
			final RowIndices result = new RowIndices();
			//
			result.rowIdx1 = fromRowKey == null ? 0
					: ArrayListKeyTable.this.rowKeys.indexOf(fromRowKey);
			if (result.rowIdx1 == -1)
				throw new SubtableBoundsBrokenException("No " + fromRowKey + " found");
			//
			result.rowIdx2 = toRowKey == null ? ArrayListKeyTable.this.rowKeys.size()
					: ArrayListKeyTable.this.rowKeys.indexOf(toRowKey);
			if (result.rowIdx2 == -1)
				throw new SubtableBoundsBrokenException("No " + toRowKey + " found");
			//
			if (fromRowKey != null && toRowKey != null && result.rowIdx1 > result.rowIdx2) {
				throw new SubtableBoundsBrokenException(fromRowKey + " must stand before "
						+ toRowKey);
			}
			return result;
		}
		
		protected ColIndices getColBoundIndices() {
			final ColIndices result = new ColIndices();
			//
			result.colIdx1 = fromColKey == null ? 0
					: ArrayListKeyTable.this.colKeys.indexOf(fromColKey);
			if (result.colIdx1 == -1)
				throw new SubtableBoundsBrokenException("No " + fromColKey + " found");
			//
			result.colIdx2 = toColKey == null ? ArrayListKeyTable.this.colKeys.size()
					: ArrayListKeyTable.this.colKeys.indexOf(toColKey);
			if (result.colIdx2 == -1)
				throw new SubtableBoundsBrokenException("No " + toColKey + " found");
			//
			if (fromColKey != null && toColKey != null && result.colIdx1 > result.colIdx2) {
				throw new SubtableBoundsBrokenException(fromColKey + " must stand before "
						+ toColKey);
			}
			return result;
		}
		
		protected int realIndexOfRow(final Rt rowKey) {
			return ArrayListKeyTable.this.rowKeys.indexOf(rowKey);
		}
		
		protected int realIndexOfCol(final Ct colKey) {
			return ArrayListKeyTable.this.colKeys.indexOf(colKey);
		}
		
		protected boolean checkBounds(
				final Rt rowKey,
				final Ct colKey,
				final boolean allowUpper,
				final boolean throwEx) throws IndexOutOfBoundsException {
			return checkBounds(getRowBoundIndices(), getColBoundIndices(), rowKey, colKey, -10,
					-10, allowUpper, throwEx);
		}
		
		protected boolean checkBounds(
				final RowIndices xIndices,
				final ColIndices yIndices,
				final Rt rowKey,
				final Ct colKey,
				final boolean allowUpper,
				final boolean throwEx) throws IndexOutOfBoundsException {
			return checkBounds(xIndices, yIndices, rowKey, colKey, -10, -10, allowUpper, throwEx);
		}
		
		protected boolean checkBounds(
				final RowIndices xIndices,
				final ColIndices yIndices,
				final Rt rowKey,
				final Ct colKey,
				int rowIdx,
				int colIdx,
				final boolean allowUpper,
				final boolean throwEx) throws IndexOutOfBoundsException {
			if (rowIdx == -10) rowIdx = realIndexOfRow(rowKey);
			if (rowIdx == -1) {
				if (throwEx)
					throw new IndexOutOfBoundsException("x key not found within bounds: " + rowKey);
				return false;
			}
			if (xIndices.rowIdx1 >= 0 && rowIdx < xIndices.rowIdx1) {
				if (throwEx)
					throw new IndexOutOfBoundsException("x key out of lower bound: " + rowKey);
				return false;
			}
			if (xIndices.rowIdx2 >= 0
					&& (allowUpper && rowIdx > xIndices.rowIdx2 || !allowUpper
							&& rowIdx >= xIndices.rowIdx2)) {
				if (throwEx)
					throw new IndexOutOfBoundsException("x key out of upper bound: " + rowKey);
				return false;
			}
			if (colIdx == -10) colIdx = realIndexOfCol(colKey);
			if (colIdx == -1) {
				if (throwEx)
					throw new IndexOutOfBoundsException("y key not found within bounds: " + colKey);
				return false;
			}
			if (yIndices.colIdx1 >= 0 && colIdx < yIndices.colIdx1) {
				if (throwEx)
					throw new IndexOutOfBoundsException("y key out of lower bound: " + colKey);
				return false;
			}
			if (yIndices.colIdx2 >= 0
					&& (allowUpper && colIdx > yIndices.colIdx2 || !allowUpper
							&& colIdx >= yIndices.colIdx2)) {
				if (throwEx)
					throw new IndexOutOfBoundsException("y key out of upper bound: " + colKey);
				return false;
			}
			return true;
		}
		
		protected boolean checkRowBound(
				final RowIndices indices,
				final Rt rowKey,
				final boolean allowUpper,
				final boolean throwEx) throws IndexOutOfBoundsException {
			final int rowIdx = realIndexOfRow(rowKey);
			if (rowIdx == -1) {
				if (throwEx)
					throw new IndexOutOfBoundsException("x key nbot found within bounds: " + rowKey);
				return false;
			}
			if (indices.rowIdx1 >= 0 && rowIdx < indices.rowIdx1) {
				if (throwEx)
					throw new IndexOutOfBoundsException("x key out of lower bound: " + rowKey);
				return false;
			}
			if (indices.rowIdx2 >= 0
					&& (allowUpper && rowIdx > indices.rowIdx2 || !allowUpper
							&& rowIdx >= indices.rowIdx2)) {
				if (throwEx)
					throw new IndexOutOfBoundsException("x key out of upper bound: " + rowKey);
				return false;
			}
			return true;
		}
		
		protected boolean checkColBound(
				final ColIndices indices,
				final Ct colKey,
				final boolean allowUpper,
				final boolean throwEx) throws IndexOutOfBoundsException {
			final int y = realIndexOfCol(colKey);
			if (y == -1) {
				if (throwEx)
					throw new IndexOutOfBoundsException("y key not found within bounds: " + colKey);
				return false;
			}
			if (indices.colIdx1 >= 0 && y < indices.colIdx1) {
				if (throwEx)
					throw new IndexOutOfBoundsException("y key out of lower bound: " + colKey);
				return false;
			}
			if (indices.colIdx2 >= 0
					&& (allowUpper && y > indices.colIdx2 || !allowUpper && y >= indices.colIdx2)) {
				if (throwEx)
					throw new IndexOutOfBoundsException("y key out of upper bound: " + colKey);
				return false;
			}
			return true;
		}
		
		//
		// External
		//
		@SuppressWarnings("unchecked")
		@Override
		public void putAll(final KeyTable <? extends Rt, ? extends Ct, ? extends T> other) {
			final RowIndices xIndices = getRowBoundIndices();
			final ColIndices yIndices = getColBoundIndices();
			final List <? extends Rt> rowKeysList = other.rowKeyList();
			final List <? extends Ct> colKeysList = other.colKeyList();
			final KeyTable <Object, Object, Object> cast = (KeyTable <Object, Object, Object>) other;
			for (final Ct ct : colKeysList) {
				for (final Rt rt : rowKeysList) {
					checkBounds(xIndices, yIndices, rt, ct, false, true);
				}
			}
			for (final Ct ct : colKeysList) {
				for (final Rt rt : rowKeysList) {
					final T value = (T) cast.get(rt, ct);
					put(rt, ct, value);
				}
			}
		}
		
		@Override
		public T put(final Rt rowKey, final Ct colKey, final T data) {
			return put(rowKey, colKey, data, true);
		}
		
		protected T put(final Rt rowKey, final Ct colKey, final T data, final boolean doCheck) {
			if (doCheck) {
				checkBounds(rowKey, colKey, false, true);
			}
			return ArrayListKeyTable.this.put(rowKey, colKey, data);
		}
		
		@Override
		public T get(final Rt rowKey, final Ct colKey) {
			checkBounds(rowKey, colKey, false, true);
			return ArrayListKeyTable.this.get(rowKey, colKey);
		}
		
		@Override
		public T remove(final Rt rowKey, final Ct colKey) {
			checkBounds(rowKey, colKey, false, true);
			return ArrayListKeyTable.this.remove(rowKey, colKey);
		}
		
		@Override
		public boolean isEmptyRow(final Rt rowKey) {
			checkRowBound(getRowBoundIndices(), rowKey, false, true);
			return super.isEmptyRow(rowKey);
		}
		
		@Override
		public boolean isEmptyCol(final Ct colKey) {
			checkColBound(getColBoundIndices(), colKey, false, true);
			return super.isEmptyCol(colKey);
		}
		
		@Override
		public IndexedMap <Ct, T> getRow(final Rt kx) {
			checkRowBound(getRowBoundIndices(), kx, false, true);
			return new BackedSubtableRowMap(kx);
		}
		
		@Override
		public IndexedMap <Rt, T> getCol(final Ct ky) {
			checkColBound(getColBoundIndices(), ky, false, true);
			return new BackedSubtableColMap(ky);
		}
		
		@Override
		public boolean contains(final Object o) {
			final List <Pair <Rt, Ct>> superIndices = ArrayListKeyTable.this.indicesOf(o);
			final RowIndices rowIndices = getRowBoundIndices();
			final ColIndices colIndices = getColBoundIndices();
			for (final Pair <Rt, Ct> idxPair : superIndices) {
				if (checkBounds(rowIndices, colIndices, idxPair.getFirst(), idxPair.getSecond(),
						false, false)) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public Pair <Rt, Ct> indexOf(final Object o) {
			final List <Pair <Rt, Ct>> superIndices = ArrayListKeyTable.this.indicesOf(o);
			final RowIndices rowIndices = getRowBoundIndices();
			final ColIndices colIndices = getColBoundIndices();
			for (final Pair <Rt, Ct> idxPair : superIndices) {
				if (checkBounds(rowIndices, colIndices, idxPair.getFirst(), idxPair.getSecond(),
						false, false)) {
					return idxPair;
				}
			}
			return null;
		}
		
		@Override
		public List <Pair <Rt, Ct>> indicesOf(final Object o) {
			final List <Pair <Rt, Ct>> result = ArrayListKeyTable.this.indicesOf(o);
			final RowIndices rowIndices = getRowBoundIndices();
			final ColIndices colIndices = getColBoundIndices();
			for (final Iterator <Pair <Rt, Ct>> iter = result.iterator(); iter.hasNext();) {
				final Pair <Rt, Ct> idxPair = iter.next();
				if (!checkBounds(rowIndices, colIndices, idxPair.getFirst(), idxPair.getSecond(),
						false, true)) {
					iter.remove();
				}
			}
			return result;
		}
		
		@Override
		public List <Rt> rowKeyList() {
			return new SubtableRowKeyList();
		}
		
		@Override
		public List <Ct> colKeyList() {
			return new SubtableColumnKeyList();
		}
		
		@Override
		public void swapRows(final Rt x1, final Rt x2) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void swapCols(final Ct y1, final Ct y2) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public String toString() {
			final ArrayListTable <String> delegate = new ArrayListTable <String>();
			final RowIndices rowIndices = getRowBoundIndices();
			final ColIndices colIndices = getColBoundIndices();
			for (int x = rowIndices.rowIdx1; x < rowIndices.rowIdx2; x++) {
				for (int y = colIndices.colIdx1; y < colIndices.colIdx2; y++) {
					final T t = table.get(x, y);
					delegate.put(x - rowIndices.rowIdx1 + 1, y - colIndices.colIdx1 + 1,
							t == null ? "" : t.toString());
				}
			}
			int i = 0;
			for (final Rt kx : rowKeyList()) {
				delegate.put(++i, 0, String.valueOf(kx));
			}
			i = 0;
			for (final Ct ky : colKeyList()) {
				delegate.put(0, ++i, String.valueOf(ky));
			}
			return delegate.toString();
		}
		
		@Override
		public KeyTable <Rt, Ct, T> subTable(
				final Rt fromRow,
				final Ct fromCol,
				final Rt toRow,
				final Ct toCol) {
			final RowIndices rowIndices = getRowBoundIndices();
			final ColIndices colIndices = getColBoundIndices();
			checkBounds(rowIndices, colIndices, fromRow, fromCol, false, true);
			checkBounds(rowIndices, colIndices, toRow, toCol, true, true);
			return new ArrayListKeySubTable(fromRow, fromCol, toRow, toCol);
		}
		
		//
		// Inner classes
		//
		// Table indices of subtable bounds. -10 for unbounded, >=0 otherwise
		protected class RowIndices {
			
			int	rowIdx1;
			int	rowIdx2;
			
			@Override
			public String toString() {
				return rowIdx1 + "," + rowIdx2;
			}
		}
		
		protected class ColIndices {
			
			int	colIdx1;
			int	colIdx2;
			
			@Override
			public String toString() {
				return colIdx1 + "," + colIdx2;
			}
		}
		
		protected class SubtableRowKeyList extends AbstractList <Rt> {
			
			@Override
			public Rt get(int index) {
				if (index < 0) throw new ArrayIndexOutOfBoundsException();
				final RowIndices indices = getRowBoundIndices();
				index += indices.rowIdx1;
				if (index >= indices.rowIdx2) throw new ArrayIndexOutOfBoundsException();
				return ArrayListKeyTable.this.rowKeys.get(index);
			}
			
			@Override
			public int size() {
				final RowIndices indices = getRowBoundIndices();
				return indices.rowIdx2 - indices.rowIdx1;
			}
		}
		
		protected class SubtableColumnKeyList extends AbstractList <Ct> {
			
			@Override
			public Ct get(int index) {
				if (index < 0) throw new ArrayIndexOutOfBoundsException();
				final ColIndices indices = getColBoundIndices();
				index += indices.colIdx1;
				if (index >= indices.colIdx2) throw new ArrayIndexOutOfBoundsException();
				return ArrayListKeyTable.this.colKeys.get(index);
			}
			
			@Override
			public int size() {
				final ColIndices indices = getColBoundIndices();
				return indices.colIdx2 - indices.colIdx1;
			}
		}
		
		protected class BackedSubtableRowMap extends AbstractMap <Ct, T> implements
				IndexedMap <Ct, T> {
			
			transient volatile IndexedSet <Ct>		keySet		= null;
			transient volatile Set <Entry <Ct, T>>	entrySet	= null;
			protected final Rt						rowKey;
			
			protected BackedSubtableRowMap(final Rt rowKey) {
				this.rowKey = rowKey;
			}
			
			@Override
			public Set <Entry <Ct, T>> entrySet() {
				if (entrySet == null) {
					entrySet = new BackedSubtableColumnEntrySet();
				}
				return entrySet;
			}
			
			@Override
			public IndexedSet <Ct> keySet() {
				if (keySet == null) {
					keySet = new BackedSubtableColumnKeySet();
				}
				return keySet;
			}
			
			@Override
			public int size() {
				final ColIndices indices = getColBoundIndices();
				return indices.colIdx2 - indices.colIdx1;
			}
			
			@Override
			public Ct getKeyAt(final int index) {
				if (index < 0) throw new IndexOutOfBoundsException("index < 0");
				if (index >= ArrayListKeySubTable.this.getRowCount())
					throw new IndexOutOfBoundsException("index >= getRowCount()");
				checkRowBound(getRowBoundIndices(), rowKey, false, true);
				int startIdx = 0;
				if (fromColKey != null) startIdx = realIndexOfCol(fromColKey);
				if (startIdx == -1) throw new SubtableBoundsBrokenException();
				return ArrayListKeyTable.this.colKeys.get(startIdx + index);
			}
			
			@Override
			public T getValueAt(final int index) {
				final Ct ky = getKeyAt(index);
				return ArrayListKeyTable.this.get(rowKey, ky);
			}
			
			@Override
			public Ct setKeyAt(final int index, final Ct newKey) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public T setValueAt(final int index, final T newValue) {
				checkRowBound(getRowBoundIndices(), rowKey, false, true);
				final Ct ky = getKeyAt(index);
				return ArrayListKeyTable.this.put(rowKey, ky, newValue);
			}
			
			@Override
			public int indexOfKey(final Object key) {
				checkRowBound(getRowBoundIndices(), rowKey, false, true);
				return colKeyList().indexOf(key);
			}
			
			@Override
			public int indexOfValue(final Object value) {
				checkRowBound(getRowBoundIndices(), rowKey, false, true);
				int idx = 0;
				for (final KeyIterator <T, Ct> iter = valuesInRow(rowKey); iter.hasNext(); ++idx) {
					if (iter.next().equals(value)) return idx;
				}
				return -1;
			}
			
			@Override
			public Entry <Ct, T> removeAt(final int index) {
				checkRowBound(getRowBoundIndices(), rowKey, false, true);
				final Ct ky = getColKeyAt(index);
				if (ky == null) throw new IndexOutOfBoundsException();
				return new Pair <Ct, T>(ky, ArrayListKeyTable.this.remove(rowKey, ky));
			}
			
			// Unmodifiable
			protected class BackedSubtableColumnKeySet extends AbstractSet <Ct> implements
					IndexedSet <Ct> {
				
				@Override
				public Iterator <Ct> iterator() {
					return new Iterator <Ct>() {
						
						private final Iterator <Entry <Ct, T>>	i	= entrySet().iterator();
						
						@Override
						public boolean hasNext() {
							return i.hasNext();
						}
						
						@Override
						public Ct next() {
							return i.next().getKey();
						}
						
						@Override
						public void remove() {
							i.remove();
						}
					};
				}
				
				@Override
				public int size() {
					return BackedSubtableRowMap.this.size();
				}
				
				@Override
				public boolean contains(final Object k) {
					return BackedSubtableRowMap.this.containsKey(k);
				}
				
				@Override
				public int addAndGetPos(final Ct object) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public Ct remove(final int index) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public Ct get(final int index) {
					return BackedSubtableRowMap.this.getKeyAt(index);
				}
				
				@Override
				public int indexOf(final Object o) {
					return BackedSubtableRowMap.this.indexOfKey(o);
				}
			}
			
			protected class BackedSubtableColumnEntrySet extends AbstractSet <Entry <Ct, T>> {
				
				@Override
				public Iterator <Entry <Ct, T>> iterator() {
					return new BackedSubtableColumnEntrySetIterator();
				}
				
				@Override
				public int size() {
					final RowIndices xBounds = getRowBoundIndices();
					checkRowBound(xBounds, rowKey, false, true);
					final ColIndices yBounds = getColBoundIndices();
					return yBounds.colIdx2 - yBounds.colIdx1;
				}
			}
			
			protected class BackedSubtableColumnEntrySetIterator implements
					Iterator <Entry <Ct, T>> {
				
				private int	currPos	= -1;
				
				@Override
				public boolean hasNext() {
					return currPos + 1 < BackedSubtableRowMap.this.size();
				}
				
				@Override
				public Entry <Ct, T> next() {
					if (!hasNext()) throw new NoSuchElementException();
					return new BackedSubtableColumnEntry(getColKeyAt(++currPos));
				}
				
				@Override
				public void remove() {
					if (currPos == -1) throw new IllegalStateException();
					ArrayListKeyTable.this.remove(rowKey, getColKeyAt(currPos));
				}
			}
			
			protected class BackedSubtableColumnEntry extends AbstractMapEntry <Ct, T> {
				
				private final Ct	key;
				
				public BackedSubtableColumnEntry(final Ct key) {
					this.key = key;
				}
				
				@Override
				public Ct getKey() {
					return key;
				}
				
				@Override
				public T getValue() {
					return ArrayListKeyTable.this.get(rowKey, key);
				}
				
				@Override
				public T setValue(final T value) {
					return ArrayListKeyTable.this.put(rowKey, key, value);
				}
			}
		}
		
		protected class BackedSubtableColMap extends AbstractMap <Rt, T> implements
				IndexedMap <Rt, T> {
			
			transient volatile IndexedSet <Rt>		keySet		= null;
			transient volatile Set <Entry <Rt, T>>	entrySet	= null;
			protected final Ct						colKey;
			
			protected BackedSubtableColMap(final Ct colKey) {
				this.colKey = colKey;
			}
			
			@Override
			public Set <Entry <Rt, T>> entrySet() {
				if (entrySet == null) {
					entrySet = new BackedSubtableRowEntrySet();
				}
				return entrySet;
			}
			
			@Override
			public IndexedSet <Rt> keySet() {
				if (keySet == null) {
					keySet = new BackedSubtableRowKeySet();
				}
				return keySet;
			}
			
			@Override
			public int size() {
				final RowIndices indices = getRowBoundIndices();
				return indices.rowIdx2 - indices.rowIdx1;
			}
			
			@Override
			public Rt getKeyAt(final int index) {
				if (index < 0) throw new IndexOutOfBoundsException("index < 0");
				if (index > ArrayListKeySubTable.this.getColCount())
					throw new IndexOutOfBoundsException("index > getColCount()");
				checkColBound(getColBoundIndices(), colKey, false, true);
				int startIdx = 0;
				if (fromRowKey != null) startIdx = realIndexOfRow(fromRowKey);
				if (startIdx == -1) throw new SubtableBoundsBrokenException();
				return ArrayListKeyTable.this.rowKeys.get(startIdx + index);
			}
			
			@Override
			public T getValueAt(final int index) {
				final Rt kx = getKeyAt(index);
				return ArrayListKeyTable.this.get(kx, colKey);
			}
			
			@Override
			public Rt setKeyAt(final int index, final Rt newKey) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public T setValueAt(final int index, final T newValue) {
				checkColBound(getColBoundIndices(), colKey, false, true);
				final Rt kx = getKeyAt(index);
				return ArrayListKeyTable.this.put(kx, colKey, newValue);
			}
			
			@Override
			public int indexOfKey(final Object key) {
				checkColBound(getColBoundIndices(), colKey, false, true);
				return rowKeyList().indexOf(key);
			}
			
			@Override
			public int indexOfValue(final Object value) {
				checkColBound(getColBoundIndices(), colKey, false, true);
				int idx = 0;
				for (final KeyIterator <T, Rt> iter = valuesInCol(colKey); iter.hasNext(); ++idx) {
					if (iter.next().equals(value)) return idx;
				}
				return -1;
			}
			
			@Override
			public Entry <Rt, T> removeAt(final int index) {
				checkColBound(getColBoundIndices(), colKey, false, true);
				final Rt kx = getRowKeyAt(index);
				if (kx == null) throw new IndexOutOfBoundsException();
				return new Pair <Rt, T>(kx, ArrayListKeyTable.this.remove(kx, colKey));
			}
			
			// Unmodifiable
			protected class BackedSubtableRowKeySet extends AbstractSet <Rt> implements
					IndexedSet <Rt> {
				
				@Override
				public Iterator <Rt> iterator() {
					return new Iterator <Rt>() {
						
						private final Iterator <Entry <Rt, T>>	i	= entrySet().iterator();
						
						@Override
						public boolean hasNext() {
							return i.hasNext();
						}
						
						@Override
						public Rt next() {
							return i.next().getKey();
						}
						
						@Override
						public void remove() {
							i.remove();
						}
					};
				}
				
				@Override
				public int size() {
					return BackedSubtableColMap.this.size();
				}
				
				@Override
				public boolean contains(final Object k) {
					return BackedSubtableColMap.this.containsKey(k);
				}
				
				@Override
				public int addAndGetPos(final Rt object) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public Rt remove(final int index) {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public Rt get(final int index) {
					return BackedSubtableColMap.this.getKeyAt(index);
				}
				
				@Override
				public int indexOf(final Object o) {
					return BackedSubtableColMap.this.indexOfKey(o);
				}
			}
			
			protected class BackedSubtableRowEntrySet extends AbstractSet <Entry <Rt, T>> {
				
				@Override
				public Iterator <Entry <Rt, T>> iterator() {
					return new BackedSubtableRowEntrySetIterator();
				}
				
				@Override
				public int size() {
					final ColIndices yBounds = getColBoundIndices();
					checkColBound(yBounds, colKey, false, true);
					final RowIndices xBounds = getRowBoundIndices();
					return xBounds.rowIdx2 - xBounds.rowIdx1;
				}
			}
			
			protected class BackedSubtableRowEntrySetIterator implements Iterator <Entry <Rt, T>> {
				
				private int	currPos	= -1;
				
				@Override
				public boolean hasNext() {
					return currPos + 1 < BackedSubtableColMap.this.size();
				}
				
				@Override
				public Entry <Rt, T> next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}
					return new BackedSubtableRowEntry(getRowKeyAt(++currPos));
				}
				
				@Override
				public void remove() {
					if (currPos == -1) throw new IllegalStateException();
					ArrayListKeyTable.this.remove(getRowKeyAt(currPos), colKey);
				}
			}
			
			protected class BackedSubtableRowEntry extends AbstractMapEntry <Rt, T> {
				
				private final Rt	key;
				
				public BackedSubtableRowEntry(final Rt key) {
					this.key = key;
				}
				
				@Override
				public Rt getKey() {
					return key;
				}
				
				@Override
				public T getValue() {
					return ArrayListKeyTable.this.get(key, colKey);
				}
				
				@Override
				public T setValue(final T value) {
					return ArrayListKeyTable.this.put(key, colKey, value);
				}
			}
		}
	}
}
