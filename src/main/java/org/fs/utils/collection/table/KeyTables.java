package org.fs.utils.collection.table;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fs.utils.ArrayUtils;
import org.fs.utils.collection.CollectionUtils;
import org.fs.utils.collection.iter.KeyIterator;
import org.fs.utils.collection.list.SortedArrayList;
import org.fs.utils.collection.map.IndexedMap;
import org.fs.utils.structure.wrap.Pair;

public class KeyTables {
	
	/**
	 * Populates the table column with the map values in the order provided by map
	 * {@code entrySet()}
	 * 
	 * @param table
	 * @param map
	 * @param column
	 * @return table argument
	 */
	public static <Rt,Ct,T>KeyTable <Rt, Ct, T> addMapToTable(
			final KeyTable <Rt, Ct, T> table,
			final Map <Rt, T> map,
			final Ct column) {
		if (table == null) throw new NullPointerException("table");
		if (map == null) throw new NullPointerException("map");
		for (final Entry <Rt, T> entry : map.entrySet()) {
			table.put(entry.getKey(), column, entry.getValue());
		}
		return table;
	}
	
	public static <Rt extends Comparable <Rt>,Ct,T>void sortByRowHeaders(
			final KeyTable <Rt, Ct, T> table,
			final boolean ascending) {
		sortByRowHeaders(table, null, ascending);
	}
	
	public static <Rt,Ct extends Comparable <Ct>,T>void sortByColHeaders(
			final KeyTable <Rt, Ct, T> table,
			final boolean ascending) {
		sortByColHeaders(table, null, ascending);
	}
	
	/** @see #sortByCol(KeyTable, Object, Comparator, boolean) */
	@SuppressWarnings("javadoc")
	public static <Rt,Ct,T>void sortByRowHeaders(
			final KeyTable <Rt, Ct, T> table,
			final Comparator <? super Rt> cmp,
			final boolean ascending) {
		final int sz = table.getRowCount();
		if (sz == 0) return;
		final List <Rt> oldRowKeys = new ArrayList <Rt>(sz);
		for (int i = 0; i < sz; i++) {
			oldRowKeys.add(table.getRowKeyAt(i));
		}
		final List <Rt> newKeys = new ArrayList <Rt>(oldRowKeys);
		Collections.sort(newKeys, cmp);
		if (!ascending) Collections.reverse(newKeys);
		final int[] swapList = new int[sz * 2];
		final boolean[] complete = new boolean[sz];
		Arrays.fill(complete, false);
		int iter = -1;
		swapList[0] = 0;
		int oldIdx = 0;
		int newIdx = 0;
		while (iter < sz * 2) {
			if (complete[oldIdx]) {
				oldIdx = ArrayUtils.indexOf(complete, false);
				if (oldIdx == -1) break;
			}
			complete[oldIdx] = true;
			newIdx = oldRowKeys.indexOf(newKeys.get(oldIdx));
			if (complete[newIdx]) {
				oldIdx = newIdx;
				continue;
			}
			swapList[++iter] = oldIdx;
			swapList[++iter] = newIdx;
			oldIdx = newIdx;
		}
		final List <Rt> tableKeys = table.rowKeyList();
		int x1, x2;
		for (iter = -1; iter + 2 < swapList.length;) {
			x1 = swapList[++iter];
			x2 = swapList[++iter];
			if (x1 == x2) continue;
			table.swapRows(tableKeys.get(x1), tableKeys.get(x2));
		}
	}
	
	/** @see #sortByRow(KeyTable, Object, Comparator, boolean) */
	@SuppressWarnings("javadoc")
	public static <Rt,Ct,T>void sortByColHeaders(
			final KeyTable <Rt, Ct, T> table,
			final Comparator <? super Ct> cmp,
			final boolean ascending) {
		final int sz = table.getColCount();
		if (sz == 0) return;
		final List <Ct> oldColKeys = new ArrayList <Ct>(sz);
		for (int i = 0; i < sz; i++) {
			oldColKeys.add(table.getColKeyAt(i));
		}
		final List <Ct> newKeys = new ArrayList <Ct>(oldColKeys);
		Collections.sort(newKeys, cmp);
		if (!ascending) Collections.reverse(newKeys);
		final int[] swapList = new int[sz * 2];
		final boolean[] complete = new boolean[sz];
		Arrays.fill(complete, false);
		int iter = -1;
		swapList[0] = 0;
		int oldIdx = 0;
		int newIdx = 0;
		while (iter < sz * 2) {
			if (complete[oldIdx]) {
				oldIdx = ArrayUtils.indexOf(complete, false);
				if (oldIdx == -1) break;
			}
			complete[oldIdx] = true;
			newIdx = oldColKeys.indexOf(newKeys.get(oldIdx));
			if (complete[newIdx]) {
				oldIdx = newIdx;
				continue;
			}
			swapList[++iter] = oldIdx;
			swapList[++iter] = newIdx;
			oldIdx = newIdx;
		}
		final List <Ct> tableKeys = table.colKeyList();
		int y1, y2;
		for (iter = -1; iter + 2 < swapList.length;) {
			y1 = swapList[++iter];
			y2 = swapList[++iter];
			if (y1 == y2) continue;
			table.swapCols(tableKeys.get(y1), tableKeys.get(y2));
		}
	}
	
	/**
	 * Reorders the table columns in a way to make selected row become a sequence.
	 * <p>
	 * Sorting is stable (will preserve equal element order).
	 * 
	 * @param table
	 *            table to sort
	 * @param colKey
	 *            column key
	 * @param cmp
	 *            comparator (or {@code null})
	 * @param ascending
	 *            {@code true} if result must go from lesser to greater
	 */
	public static <Rt,Ct,T>void sortByCol(
			final KeyTable <Rt, Ct, T> table,
			final Ct colKey,
			final Comparator <? super T> cmp,
			final boolean ascending) {
		final int sz = table.getRowCount();
		if (sz == 0) return;
		final List <T> oldValues = new ArrayList <T>(sz);
		for (int i = 0; i < sz; ++i) {
			final Rt rKey = table.getRowKeyAt(i);
			final T value = table.get(rKey, colKey);
			oldValues.add(value);
		}
		final int[] swapList = getSortIdx(oldValues, cmp, ascending, sz);
		final List <Rt> rowKeys = table.rowKeyList();
		int rowIdx1, rowIdx2;
		for (int iter = -1; iter + 2 < swapList.length;) {
			rowIdx1 = swapList[++iter];
			rowIdx2 = swapList[++iter];
			if (rowIdx1 == rowIdx2) continue;
			table.swapRows(rowKeys.get(rowIdx1), rowKeys.get(rowIdx2));
		}
	}
	
	/**
	 * Reorders the table rows in a way to make selected column become a sequence.
	 * <p>
	 * Sorting is stable (will preserve equal element order).
	 * 
	 * @param table
	 *            table to sort
	 * @param rowKey
	 *            sorting row key
	 * @param cmp
	 *            comparator (or {@code null})
	 * @param ascending
	 *            {@code true} if result must go from lesser to greater
	 */
	public static <Rt,Ct,T>void sortByRow(
			final KeyTable <Rt, Ct, T> table,
			final Rt rowKey,
			final Comparator <? super T> cmp,
			final boolean ascending) {
		final int sz = table.getColCount();
		if (sz == 0) return;
		final List <T> oldValues = new ArrayList <T>(sz);
		for (int i = 0; i < sz; i++) {
			final Ct cKey = table.getColKeyAt(i);
			oldValues.add(table.get(rowKey, cKey));
		}
		final int[] swapList = getSortIdx(oldValues, cmp, ascending, sz);
		final List <Ct> colKeys = table.colKeyList();
		int colIdx1, colIdx2;
		for (int iter = -1; iter + 2 < swapList.length;) {
			colIdx1 = swapList[++iter];
			colIdx2 = swapList[++iter];
			if (colIdx1 == colIdx2) continue;
			table.swapCols(colKeys.get(colIdx1), colKeys.get(colIdx2));
		}
	}
	
	/** Searches for the index of a minimum element, which is greater then the given value */
	private static Integer findMinIdx(final int[] weights, final int minimumExclusive) {
		int lmin = 0;
		for (int i = weights.length - 1; i > 0; --i)
			if (weights[lmin] <= minimumExclusive || weights[i] < weights[lmin]
					&& weights[i] > minimumExclusive) lmin = i;
		if (weights[lmin] <= minimumExclusive) return null;
		return lmin;
	}
	
	/** Sorting core method */
	private static <T>int[] getSortIdx(
			final List <T> oldValues,
			final Comparator <? super T> cmp,
			final boolean ascending,
			final int sz) {
		final List <T> newValues = new ArrayList <T>(oldValues);
		Collections.sort(newValues, cmp);
		if (!ascending) Collections.reverse(newValues);
		final int[] swapList = new int[sz * 2];
		int iter = -1;
		swapList[0] = 0;
		int oldIdx = 0;
		int newIdx = 0;
		final int[] weights = new int[sz];
		for (int i = 0; i < sz; ++i)
			weights[i] = i;
		final Set <Integer> usedSourceIdx = new SortedArrayList <Integer>();
		final Set <Integer> usedDestIdx = new SortedArrayList <Integer>();
		main: while (true) {
			if (usedSourceIdx.size() == sz) break;
			if (usedDestIdx.size() == sz) break;
			oldIdx = -1;
			while (true) {
				final Integer minIdx = findMinIdx(weights, oldIdx == -1 ? -1 : weights[oldIdx]);
				if (minIdx == null) break main;
				oldIdx = minIdx;
				if (!usedSourceIdx.contains(oldIdx)) break;
			}
			//
			usedSourceIdx.add(oldIdx);
			//
			newIdx = -1;
			final T oldValue = oldValues.get(oldIdx);
			while (true) {
				newIdx = CollectionUtils.indexOfFrom(newValues, oldValue, newIdx + 1);
				if (newIdx == -1) continue main;
				if (!usedDestIdx.contains(newIdx)) {
					usedDestIdx.add(newIdx);
					break;
				}
			}
			if (oldIdx != newIdx) {
				swapList[++iter] = oldIdx;
				swapList[++iter] = newIdx;
				Collections.swap(oldValues, oldIdx, newIdx);
				final int t = weights[oldIdx];
				weights[oldIdx] = weights[newIdx];
				weights[newIdx] = t;
				usedSourceIdx.remove(oldIdx);
				usedSourceIdx.add(newIdx);
			}
		}
		return swapList;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void removeEmptyRows(final KeyTable keyTable) {
		final List <Object> toRemove = new ArrayList <Object>();
		for (final Object key : keyTable.rowKeyList()) {
			if (keyTable.isEmptyRow(key)) {
				toRemove.add(key);
			}
		}
		for (final Object key : toRemove) {
			keyTable.removeRow(key);
		}
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void removeEmptyColumns(final KeyTable keyTable) {
		final List <Object> toRemove = new ArrayList <Object>();
		for (final Object key : keyTable.colKeyList()) {
			if (keyTable.isEmptyCol(key)) {
				toRemove.add(key);
			}
		}
		for (final Object key : toRemove) {
			keyTable.removeCol(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <Rtf,Ctf,Vf,Rt,Ct,V>void checkedCast(
			final KeyTable <Rtf, Ctf, Vf> from,
			final KeyTable <Rt, Ct, V> to,
			final Class <Rt> rowType,
			final Class <Ct> colType,
			final Class <V> valType) {
		final List <Rtf> rowKeys = from.rowKeyList();
		final List <Ctf> colKeys = from.colKeyList();
		for (final Ctf colKey : colKeys) {
			if (!allowed(colKey, colType)) {
				throw new ClassCastException("Col key " + colKey + " is not of type " + colType);
			}
			final Ct colKeyCast = (Ct) colKey;
			for (final Rtf rowKey : rowKeys) {
				if (!allowed(rowKey, rowType)) {
					throw new ClassCastException("Row key " + rowKey + " is not of type " + rowType);
				}
				final Rt rowKeyCast = (Rt) rowKey;
				final Vf value = from.get(rowKey, colKey);
				if (!allowed(value, valType)) {
					throw new ClassCastException("Value " + value + " is not of type " + valType);
				}
				final V valueCast = (V) value;
				to.put(rowKeyCast, colKeyCast, valueCast);
			}
		}
	}
	
	private static boolean allowed(final Object obj, final Class <?> type) {
		return obj == null || type.isInstance(obj);
	}
	
	@SuppressWarnings("rawtypes")
	public static final KeyTable	EMPTY_KEY_TABLE	= new EmptyKeyTable <Object, Object, Object>();
	
	@SuppressWarnings("unchecked")
	public static <Rt,Ct,V>KeyTable <Rt, Ct, V> emptyTable() {
		return EMPTY_KEY_TABLE;
	}
	
	private static class EmptyKeyTable<RowType,ColType,T> implements
			KeyTable <RowType, ColType, T>, Serializable {
		
		private static final long	serialVersionUID	= -2994577456857860798L;
		
		@Override
		public void putAll(final KeyTable <? extends RowType, ? extends ColType, ? extends T> other) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public T put(final RowType rowKey, final ColType colKey, final T data) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public T get(final RowType rowKey, final ColType colKey) {
			return null;
		}
		
		@Override
		public T get(RowType rowKey, ColType colKey, T valueToInsertIfNull) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public T remove(final RowType rowKey, final ColType colKey) {
			return null;
		}
		
		@Override
		public IndexedMap <ColType, T> removeRow(final RowType rowKey) {
			return null;
		}
		
		@Override
		public IndexedMap <RowType, T> removeCol(final ColType colKey) {
			return null;
		}
		
		@Override
		public IndexedMap <ColType, T> insertRow(final int pos, final RowType rowKey) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public IndexedMap <RowType, T> insertCol(final int pos, final ColType colKey) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public IndexedMap <ColType, T> getRow(final RowType rowKey) {
			return CollectionUtils.emptyMap();
		}
		
		@Override
		public IndexedMap <RowType, T> getCol(final ColType colKey) {
			return CollectionUtils.emptyMap();
		}
		
		@Override
		public int getRowCount() {
			return 0;
		}
		
		@Override
		public int getColCount() {
			return 0;
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public boolean isEmptyRow(final RowType rowKey) {
			return true;
		}
		
		@Override
		public boolean isEmptyCol(final ColType colKey) {
			return true;
		}
		
		@Override
		public boolean contains(final Object o) {
			return false;
		}
		
		@Override
		public Pair <RowType, ColType> indexOf(final Object o) {
			return null;
		}
		
		@Override
		public List <Pair <RowType, ColType>> indicesOf(final Object o) {
			return Collections.emptyList();
		}
		
		@Override
		public KeyIterator <T, ColType> valuesInRow(final RowType rowKey) {
			return CollectionUtils.emptyIterator();
		}
		
		@Override
		public KeyIterator <T, RowType> valuesInCol(final ColType colKey) {
			return CollectionUtils.emptyIterator();
		}
		
		@Override
		public Object[][] toArray() {
			return new Object[0][0];
		}
		
		@Override
		public T[][] toArray(final T[] instance) {
			return (T[][]) Array.newInstance(instance.getClass(), 0, 0);
		}
		
		@Override
		public List <RowType> rowKeyList() {
			return Collections.emptyList();
		}
		
		@Override
		public List <ColType> colKeyList() {
			return Collections.emptyList();
		}
		
		@Override
		public RowType getRowKeyAt(final int rowIndex) {
			throw new IndexOutOfBoundsException();
		}
		
		@Override
		public ColType getColKeyAt(final int colIndex) {
			throw new IndexOutOfBoundsException();
		}
		
		@Override
		public int getRowKeyIndex(final RowType rowKey) {
			return -1;
		}
		
		@Override
		public int getColKeyIndex(final ColType colKey) {
			return -1;
		}
		
		@Override
		public void swapRows(final RowType rowKey1, final RowType rowKey2) {}
		
		@Override
		public void swapCols(final ColType colKey1, final ColType colKey2) {}
		
		@Override
		public void clear() {}
		
		@Override
		public KeyTable <RowType, ColType, T> subTable(
				final RowType fromRow,
				final ColType fromCol,
				final RowType toRow,
				final ColType toCol) {
			if (fromRow != null || fromCol != null || toRow != null || toCol != null)
				throw new IndexOutOfBoundsException();
			return this;
		}
		
		@Override
		public String toString() {
			return "+";
		}
		
		@Override
		public int hashCode() {
			return 965598754;
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof KeyTable <?, ?, ?>)) return false;
			final KeyTable <?, ?, ?> cast = (KeyTable <?, ?, ?>) obj;
			return cast.getRowCount() == 0 && cast.getColCount() == 0 && cast.isEmpty();
		}
	}
}
