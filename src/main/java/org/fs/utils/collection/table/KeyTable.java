package org.fs.utils.collection.table;

import java.util.AbstractList;
import java.util.List;
import java.util.Map;

import org.fs.utils.collection.iter.KeyIterator;
import org.fs.utils.collection.map.IndexedMap;
import org.fs.utils.structure.wrap.Pair;

/**
 * A {@link Table}, which elements can be accessed through two custom defined keys. Keys are unique.
 * It can be viewed as a sort of two-dimensional {@link Map} (although it's keys have indices, so
 * it's closer to an {@link IndexedMap})
 * 
 * @author FS
 * @param <RowType>
 *            type of a row index
 * @param <ColType>
 *            type of a column index
 * @param <T>
 *            stored value type
 */
public interface KeyTable <RowType,ColType,T> {
	
	/**
	 * Inserts all data from the given table on the same positions in this table. If some element
	 * cannot be inserted, an exception will be trown and this table will remain unaffected.
	 * 
	 * @param other
	 */
	public void putAll(KeyTable <? extends RowType, ? extends ColType, ? extends T> other);
	
	/**
	 * Puts the value at a given position, replacing and returning old value or {@code null} if
	 * none.
	 * <p>
	 * Putting {@code null} in a table, that does not support {@code null}s, is equivalent to
	 * {@link #remove(Object, Object)}
	 * 
	 * @param rowKey
	 *            row key
	 * @param colKey
	 *            column key
	 * @param data
	 *            an entity to put at the given position
	 * @return replaced value
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and indices are out of bounds or not found within bounds
	 */
	public T put(RowType rowKey, ColType colKey, T data);
	
	/**
	 * Returns a value with a given position, {@code null} if none.
	 * 
	 * @param rowKey
	 *            row key
	 * @param colKey
	 *            column key
	 * @return value ({@code null} if cell is empty)
	 */
	public T get(RowType rowKey, ColType colKey);
	
	/**
	 * Returns a value with a given position, or inserts and returns a supplied default value.
	 * 
	 * @param rowKey
	 *            row key
	 * @param colKey
	 *            column key
	 * @param defaultValue
	 *            an entity to put at the given position if none is present
	 * @return value on a given position or inserted default value if cell was empty
	 */
	public T get(RowType rowKey, ColType colKey, T defaultValue);
	
	/**
	 * Removes the element at the specified position, returning old value.
	 * <p>
	 * Not supported by the subtables.
	 * 
	 * @param rowKey
	 *            row key
	 * @param colKey
	 *            column key
	 * @return removed element value or {@code null}
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and indices are out of bounds
	 */
	public T remove(RowType rowKey, ColType colKey);
	
	/**
	 * Removes an entire row, including row key itself, from the table.
	 * <p>
	 * Not supported by the subtables.
	 * 
	 * @param rowKey
	 *            row key
	 * @return a non-backed map representing the deleted row or {@code null} if key is invalid
	 * @throws UnsupportedOperationException
	 *             if invoked on a subtable
	 */
	public IndexedMap <ColType, T> removeRow(RowType rowKey);
	
	/**
	 * Removes an entire column, including column key itself, from the table.
	 * 
	 * @param colKey
	 *            column key
	 * @return a non-backed map representing the deleted column or {@code null} if key is invalid
	 * @throws UnsupportedOperationException
	 *             if invoked on a subtable
	 */
	public IndexedMap <RowType, T> removeCol(ColType colKey);
	
	/**
	 * Inserts the data row (horizontal line), represented by the map. Map is backed by the table,
	 * so changing the map will change the row and vice versa.
	 * <p>
	 * Map size will be exactly the same, as the table {@link #getColCount()}
	 * <p>
	 * Map's {@code keySet()} method will return an unmodifiable set.
	 * <p>
	 * Not supported by the subtables.
	 * 
	 * @param pos
	 *            position of a new key
	 * @param rowKey
	 *            row key
	 * @return a backed map representing table column
	 * @throws IndexOutOfBoundsException
	 *             if (pos < 0) or (pos > getColCount());
	 * @throws IllegalArgumentException
	 *             if that key already exist
	 */
	public IndexedMap <ColType, T> insertRow(int pos, RowType rowKey);
	
	/**
	 * Inserts the data column (vertical line), represented by the map. Map is backed by the table,
	 * so changing the map will change the row and vice versa.
	 * <p>
	 * Map size will be exactly the same, as the table {@link #getRowCount()}
	 * <p>
	 * Map's {@code keySet()} method will return an unmodifiable set.
	 * <p>
	 * Not supported by the subtables.
	 * 
	 * @param pos
	 *            position of a new key
	 * @param colKey
	 *            column key
	 * @return a backed map representing table row
	 * @throws IndexOutOfBoundsException
	 *             if (pos < 0) or (pos > getRowCount());
	 * @throws IllegalArgumentException
	 *             if that key already exist
	 */
	public IndexedMap <RowType, T> insertCol(int pos, ColType colKey);
	
	/**
	 * Return the data row (horizontal line), represented by the map. Map is backed by the table, so
	 * changing the map will change the column and vice versa.
	 * <p>
	 * Map size will be exactly the same, as the table {@link #getRowCount()}.
	 * <p>
	 * Order of keys in map is the same, as table column key order.
	 * <p>
	 * Map's {@code keySet()} method will return an unmodifiable set and {@code setKeyAt(...)} will
	 * throw an {@link UnsupportedOperationException}.
	 * 
	 * @param rowKey
	 *            row key
	 * @return a backed map representing table column
	 */
	public IndexedMap <ColType, T> getRow(RowType rowKey);
	
	/**
	 * Return the data column (vertical line), represented by the map. Map is backed by the table,
	 * so changing the map will change the row and vice versa.
	 * <p>
	 * Map size will be exactly the same, as the table {@link #getColCount()}.
	 * <p>
	 * Order of keys in map is the same, as table row key order.
	 * <p>
	 * Map's {@code keySet()} method will return an unmodifiable set and {@code setKeyAt(...)} will
	 * throw an {@link UnsupportedOperationException}.
	 * 
	 * @param colKey
	 *            column key
	 * @return a backed map representing table row
	 */
	public IndexedMap <RowType, T> getCol(ColType colKey);
	
	/** @return number of rows */
	public int getRowCount();
	
	/** @return number of columns */
	public int getColCount();
	
	/** @return {@code true} if no elements is stored inside */
	public boolean isEmpty();
	
	public boolean isEmptyRow(RowType rowKey);
	
	public boolean isEmptyCol(ColType colKey);
	
	public boolean contains(Object o);
	
	public Pair <RowType, ColType> indexOf(Object o);
	
	/**
	 * Retrieves pairs of all indices of a given value.
	 * 
	 * @param o
	 *            object to find
	 * @return never null
	 */
	public List <Pair <RowType, ColType>> indicesOf(Object o);
	
	/**
	 * Two KeyTables are equals, if their keys lists and values on their crossing are equals.
	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * {@code KeyTable} hash code is defined as follows:
	 * 
	 * <pre>
	 * List &lt;Rt&gt; rowKeyList = rowKeyList();
	 * List &lt;Ct&gt; colKeyList = colKeyList();
	 * int hashCode = rowKeyList.hashCode();
	 * hashCode = hashCode * 31 + colKeyList.hashCode();
	 * for (Ct ct : colKeyList) {
	 * 	for (Rt rt : rowKeyList) {
	 * 		T value = get(rt, ct);
	 * 		hashCode = hashCode * 31 + (value == null ? 0 : value.hashCode());
	 * 	}
	 * }
	 * return hashCode;
	 * </pre>
	 */
	@Override
	public int hashCode();
	
	/** @see Table#valuesInRow(int) */
	@SuppressWarnings("javadoc")
	public KeyIterator <T, ColType> valuesInRow(RowType rowKey);
	
	/** @see Table#valuesInCol(int) */
	@SuppressWarnings("javadoc")
	public KeyIterator <T, RowType> valuesInCol(ColType colKey);
	
	/**
	 * Returns the array representing table content (keys are not included)
	 * 
	 * @return array containing the table content
	 * @see Table#toArray()
	 */
	public Object[][] toArray();
	
	/**
	 * Returns the array representing table content (keys are not included).
	 * 
	 * @param instance
	 *            sample array of a target type(may be zero-length)
	 * @return table content in an array
	 * @see Table#toArray(Object[])
	 */
	public T[][] toArray(T[] instance);
	
	/**
	 * Returns a {@code List} representing table row keys. List is backed by the map, so you can
	 * change it to change table keys. Removing an element from the list will remove an entire table
	 * column (not supported by the subables). List supports all basic operations and is iterable.
	 * <p>
	 * Note, however, that you cannot place two equal elements in the list. Attempting to do so will
	 * raise an {@link IllegalArgumentException}.
	 * 
	 * @return backed row keys list
	 * @see #colKeyList
	 */
	public List <RowType> rowKeyList();
	
	/**
	 * Returns a {@code List} representing table column keys. List is backed by the map, so you can
	 * change it to change table keys. Removing an element from the list will remove an entire table
	 * row (not supported by the subables). List supports all basic operations and is iterable.
	 * <p>
	 * Note, however, that you cannot place two equal elements in the list. Attempting to do so will
	 * raise an {@link IllegalArgumentException}.
	 * 
	 * @return backed column keys list
	 * @see #colKeyList
	 */
	public List <ColType> colKeyList();
	
	/**
	 * Returns the row key on a given position
	 * 
	 * @param rowIndex
	 *            0 to {@link #getRowCount()}-1
	 * @return key or {@code null}
	 * @throws IndexOutOfBoundsException
	 *             if rowIndex < 0 or rowIndex > getRowCount()-1
	 */
	public RowType getRowKeyAt(int rowIndex);
	
	/**
	 * Returns the column on a given position
	 * 
	 * @param colIndex
	 *            0 to {@link #getColCount()}-1
	 * @return key or {@code null}
	 * @throws IndexOutOfBoundsException
	 *             if colIndex < 0 or colIndex > getColCount()-1
	 */
	public ColType getColKeyAt(int colIndex);
	
	public int getRowKeyIndex(RowType rowKey);
	
	public int getColKeyIndex(ColType colKey);
	
	/**
	 * Swaps two table rows.
	 * 
	 * @param rowKey1
	 *            first row key
	 * @param rowKey2
	 *            second row key
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and key is out of bounds
	 */
	public void swapRows(RowType rowKey1, RowType rowKey2);
	
	/**
	 * Swaps two table columns.
	 * 
	 * @param colKey1
	 *            first column key
	 * @param colKey2
	 *            second column key
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and key is out of bounds
	 */
	public void swapCols(ColType colKey1, ColType colKey2);
	
	/**
	 * Pretty-printing method. Returns something like:
	 * 
	 * <pre>
	 * +----+------+-------------+---------+
	 * |    |name  |status       |salary   |
	 * +----+------+-------------+---------+
	 * |rec1|Rotti |Still working|176895E17|
	 * +----+------+-------------+---------+
	 * |rec2|Nathan|Fired        |         |
	 * +----+------+-------------+---------+
	 * </pre>
	 * 
	 * Where first column and row are headers:
	 * <ul>
	 * <li>{@code rec1} and {@code rec2} are row keys</li>
	 * <li>{@code name}, {@code status} and {@code salary} are column keys</li>
	 * </ul>
	 * 
	 * @see Table#toString()
	 */
	@Override
	public String toString();
	
	public void clear();
	
	/**
	 * Returns a view of the portion of this table whose keys range from ({@code fromX},{@code fromY}
	 * ), inclusive, to ({@code toX},{@code toY}), exclusive. Any bound may be set to {@code null}
	 * to remove a limit. (If {@code from} and {@code to} are equal, the returned table is empty.)
	 * The returned table is backed by this table, so changes in the returned table are reflected in
	 * this table, and vice-versa. The returned table supports all optional table operations that
	 * this table supports.
	 * <p>
	 * The returned table will throw an {@code IllegalArgumentException} on an attempt to insert a
	 * key outside its range.
	 * <p>
	 * Implementation note: the returned table is not a wrapper and uses the same data storage, as
	 * the original table. Unlike {@link AbstractList#subList(int, int)}, recursively calling the
	 * {@code subTable} will not cause any performance issues.
	 * 
	 * @param fromRow
	 *            low endpoint (inclusive) of the row keys in the returned map
	 * @param fromCol
	 *            low endpoint (inclusive) of the column keys in the returned map
	 * @param toRow
	 *            high endpoint (exclusive) of the row keys in the returned map
	 * @param toCol
	 *            high endpoint (exclusive) of the column keys in the returned map
	 * @return a view of the portion of this map whose keys range from <tt>fromKey</tt>, inclusive,
	 *         to <tt>toKey</tt>, exclusive
	 * @throws SubtableBoundsBrokenException
	 *             if any key isnt found
	 */
	public KeyTable <RowType, ColType, T> subTable(
			RowType fromRow,
			ColType fromCol,
			RowType toRow,
			ColType toCol);
	
	public static interface KeyTableEntry <RowType,ColType,T> {
		
		public RowType getRowKey();
		
		public ColType getColKey();
		
		public T getValue();
		
		public T setValue(T newValue);
		
		/**
		 * Returns {@code true}, if both key table entries point at the cell with the same
		 * coordinates, which have the same value.
		 * 
		 * @param obj
		 * @return {@code true}, if this table entry is the same, as the provided
		 */
		@Override
		public boolean equals(Object obj);
		
		/**
		 * Hash code is defined as follows:
		 * 
		 * <pre>
		 * T value = getValue();
		 * return (getRowKey().hashCode() * 29) &circ; (getColKey().hashCode() * 47)
		 * 		+ (value == null ? 0 : value.hashCode());
		 * </pre>
		 * 
		 * @return entry hash code
		 */
		@Override
		public int hashCode();
	}
}
