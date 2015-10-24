package org.fs.utils.collection.table;

import java.util.Collection;
import java.util.List;

import org.fs.utils.collection.iter.KeyIterator;
import org.fs.utils.structure.wrap.Pair;

/**
 * Table is two-dimensional dynamic array. Table structure can be represented as following:
 * 
 * <pre>
 *     0   1   2   3   4
 *   +---+---+---+---+---+--> (X)
 * 0 |0x0|1x0|2x0|3x0|4x0
 *   +---+---+---+---+---
 * 1 |0x1|1x1|2x1|3x1|4x1
 *   +---+---+---+---+---
 * 2 |0x2|1x2|2x2|3x2|4x2
 *   +
 *   |
 *   V
 *  (Y)
 * </pre>
 * 
 * <u>Definitions:</u> <br>
 * X-axis, or abscissa, is a horizontal coordinate axis.<br>
 * Y-axis, or ordinate, is a vertical coordinate axis.<br>
 * Row is a data vector representing table cells with the same Y coordinates.<br>
 * Column is a data vector representing table cells with the same X coordinates.<br>
 * 
 * @author FS
 * @param <T>
 *            stored value type
 * @see List
 * @see KeyTable
 */
public interface Table <T> {
	
	/**
	 * Puts the value at a given position, replacing and returning old value or {@code null} if
	 * none.
	 * <p>
	 * Putting {@code null} in a table, that does not support {@code null}s, is equivalent to
	 * {@link #remove(int, int)}
	 * 
	 * @param row
	 *            vetical index
	 * @param col
	 *            horizontal index
	 * @param value
	 *            an entity to put at the given position
	 * @return replaced value
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and indices are out of bounds
	 */
	public T put(int row, int col, T value);
	
	/**
	 * Puts all values into this table with the same indices.
	 * <p>
	 * Will not throw {@link IndexOutOfBoundsException} exception, if supplied table's values are
	 * out of bounds, but will return {@code false} instead.
	 * 
	 * @param table
	 * @return {@code true} if operation fully succeeded
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and indices of some element in the target table is out
	 *             of bounds
	 */
	public boolean putAll(Table <? extends T> table) throws IndexOutOfBoundsException;
	
	/**
	 * Returns the value at the specified position, or {@code null} if none. {@code null} may also
	 * mean a {@code null} element, if table supports {@code null}s.
	 * 
	 * @param row
	 *            row index
	 * @param col
	 *            column index
	 * @return value or {@code null}
	 * @throws IndexOutOfBoundsException
	 *             if any index is negative or if this is bounded table and indices are out of
	 *             bounds
	 */
	public T get(int row, int col);
	
	/**
	 * Returns the value at the specified position, or inserts and returns a default value if none
	 * was present.
	 * 
	 * @param row
	 *            row index
	 * @param col
	 *            column index
	 * @param defaultValue
	 *            value to be inserted and returned if the slot is free
	 * @return value or {@code null}
	 * @throws IndexOutOfBoundsException
	 *             if any index is negative or if this is bounded table and indices are out of
	 *             bounds
	 */
	public T get(int row, int col, T defaultValue);
	
	/**
	 * Removes the element at the specified position, returning old value.
	 * 
	 * @param row
	 *            row index
	 * @param col
	 *            column index
	 * @return removed element value or {@code null}
	 * @throws IndexOutOfBoundsException
	 *             if any index is negative or if this is bounded table and indices are out of
	 *             bounds
	 */
	public T remove(int row, int col);
	
	/**
	 * @return number of rows. For a bounded subtable, this should return a maximum allowed row
	 *         count
	 */
	public int getRowCount();
	
	/**
	 * @return number of columns. For a bounded subtables, this should return a maximum allowed
	 *         column count
	 */
	public int getColCount();
	
	/** @return {@code true} if this table contains no elements */
	public boolean isEmpty();
	
	/** @return {@code true} if this table row contains no elements */
	@SuppressWarnings("javadoc")
	public boolean isEmptyRow(int rowIdx);
	
	/** @return {@code true} if this table column contains no elements */
	@SuppressWarnings("javadoc")
	public boolean isEmptyCol(int colIdx);
	
	/**
	 * Return the data row (horizontal line), represented by the list. List is backed by the table,
	 * so changing the list will change the table and vice versa.
	 * <p>
	 * Returned list behaves unusually: list does not supports {@code add(int, value)} and removing
	 * an element from the list will not necessary change it's size - it's size constantly equals
	 * {@link #getColCount()} of a table.
	 * <p>
	 * Removed elements will not shift subsequent elements and will be simply replaced by
	 * {@code null}.
	 * 
	 * @param rowIdx
	 *            ordinate, row index
	 * @return a backed {@link List} representing a row
	 * @throws IndexOutOfBoundsException
	 *             if index is negative or out of subtable bounds
	 */
	public List <T> getRow(int rowIdx);
	
	/**
	 * Return the data column (vertical line), represented by the list. List is backed by the table,
	 * so changing the list will change the table and vice versa.
	 * <p>
	 * Returned list behaves unusually: list does not supports {@code add(int, value)} and removing
	 * an element from the list will not necessary change it's size - it's size constantly equals
	 * {@link #getRowCount()} of a table.
	 * <p>
	 * Removed elements will not shift subsequent elements and will be simply replaced by
	 * {@code null}.
	 * 
	 * @param colIdx
	 *            abscissa, column index
	 * @return a backed {@link List} representing a column
	 * @throws IndexOutOfBoundsException
	 *             if index is negative or out of subtable bounds
	 */
	public List <T> getCol(int colIdx);
	
	/**
	 * <i>ATTENTION: This method is subtable-unsafe. It is unavailable on subtables and will likely
	 * ruin the existing subtables.</i>
	 * <p>
	 * Removes the entire data row (horizontal line), shifting subsequent elements.
	 * <p>
	 * Returned list is unbound, it behaves normally and may be freely modified.
	 * 
	 * @param rowIdx
	 *            ordinate, row index
	 * @return a non-backed {@link List} representing a removed row or {@code null} if the index was
	 *         incorrent
	 * @throws IndexOutOfBoundsException
	 *             if index is negative
	 */
	public List <T> removeRow(int rowIdx);
	
	/**
	 * <i>ATTENTION: This method is subtable-unsafe. It is unavailable on subtables and will likely
	 * ruin the existing subtables.</i>
	 * <p>
	 * Removes the entire data column (vertical line), shifting subsequent elements.
	 * <p>
	 * Returned list is unbound, it behaves normally and may be freely modified.
	 * 
	 * @param colIdx
	 *            abscissa, column index
	 * @return a non-backed {@link List} representing a removed column or {@code null} if the index
	 *         was incorrent
	 * @throws IndexOutOfBoundsException
	 *             if index is negative
	 */
	public List <T> removeCol(int colIdx);
	
	/**
	 * <i>ATTENTION: This method is subtable-unsafe. It is unavailable on subtables and will likely
	 * ruin the existing subtables.</i>
	 * <p>
	 * Inserts a row in a target position, shifting current and subsequent rows (if any).
	 * <p>
	 * Returning list is represents an inserted row and is backed by the table
	 * 
	 * @param rowIdx
	 *            ordinate, row index
	 * @return a backed {@link List} representing a newly inserted row
	 * @throws IndexOutOfBoundsException
	 *             if index is negative
	 */
	public List <T> insertRow(int rowIdx);
	
	/**
	 * <i>ATTENTION: This method is subtable-unsafe. It is unavailable on subtables and will likely
	 * ruin the existing subtables.</i>
	 * <p>
	 * Inserts a column in a target position, shifting current and subsequent columns (if any).
	 * <p>
	 * Returning list is represents an inserted column and is backed by the table
	 * 
	 * @param colIdx
	 *            abscissa, column index
	 * @return a backed {@link List} representing a newly inserted column
	 * @throws IndexOutOfBoundsException
	 *             if index is negative
	 */
	public List <T> insertCol(int colIdx);
	
	/**
	 * Returns an iterator, which sequentialy returns values in the given row. Iterator's
	 * {@code next()} method will never return {@code null}.
	 * <p>
	 * Elements can be removed from the table by invoking {@code remove()} or a returned iterator.
	 * <p>
	 * If the table is changed during the iteration by any way other than via this iterator's
	 * {@code remove} method (concurent modification), iterator's behaviour is generally undefined.
	 * 
	 * @param rowIdx
	 *            row index
	 * @return iterator through values in row
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and index is out of bounds
	 * @see #valuesInCol(int)
	 * @see #rows()
	 * @see #columns()
	 */
	public KeyIterator <T, Integer> valuesInRow(int rowIdx);
	
	/**
	 * Returns an iterator, which sequentialy returns values in the given column. Iterator's
	 * {@code next()} method will never return {@code null}.
	 * <p>
	 * Elements can be removed from the table by invoking {@code remove()} or a returned iterator.
	 * <p>
	 * If the table is changed during the iteration by any way other than via this iterator's
	 * {@code remove} method (concurent modification), iterator's behaviour is generally undefined.
	 * 
	 * @param colIdx
	 *            column index
	 * @return iterator through values in column
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and index is out of bounds
	 * @see #valuesInRow(int)
	 * @see #rows()
	 * @see #columns()
	 */
	public KeyIterator <T, Integer> valuesInCol(int colIdx);
	
	/**
	 * Returns a list of table rows, one entry per Y-axis with a total of {@link #getRowCount()}.
	 * <p>
	 * Returned list and it's underlists is backed by the table, so you can change values via
	 * {@code add()} (not supported by upper-bounded tables), {@code set()}, {@code  remove()}
	 * methods or a list iterator. Positional {@code add(int, value)} methods are not supported.
	 * <p>
	 * If the list passed to {@code add()} or {@code set()} is too small, remaining element will be
	 * counted as {@code null}s.
	 * <p>
	 * Removed elements will not shift subsequent elements and will be simply replaced by
	 * {@code null}.
	 * <p>
	 * Underlist size is be exactly the same, as the table {@link #getColCount()}
	 * <p>
	 * If the table is changed by any way other than via this list's or underlist's {@code remove}
	 * method (concurrent modification), list is not guaranteed to remain stable.
	 * 
	 * @return backed list of all rows
	 * @see #columns()
	 * @see #valuesInRow(int)
	 * @see #valuesInCol(int)
	 */
	public List <List <T>> rows();
	
	/**
	 * Returns a list of table columns, one entry per X-axis with a total of {@link #getColCount()}.
	 * <p>
	 * Returned list and it's underlists is backed by the table, so you can change values via
	 * {@code add()} (not supported by upper-bounded tables), {@code set()}, {@code  remove()}
	 * methods or a list iterator. Positional {@code add(int, value)} methods are not supported.
	 * <p>
	 * If the list passed to {@code add()} or {@code set()} is too small, remaining element will be
	 * counted as {@code null}s
	 * <p>
	 * Removed elements will not shift subsequent elements and will be simply replaced by
	 * {@code null}.
	 * <p>
	 * Underlist size is be exactly the same, as the table {@link #getRowCount()}
	 * <p>
	 * If the table is changed by any way other than via this list's or underlist's {@code remove}
	 * method (concurrent modification), list is not guaranteed to remain stable.
	 * 
	 * @return backed list of all columns
	 * @see #rows()
	 * @see #valuesInRow(int)
	 * @see #valuesInCol(int)
	 */
	public List <List <T>> columns();
	
	/**
	 * Converts a table to a two-dimensional array of {@code Object}s. The array will be safe for
	 * modifications (changes will not be reflected on a table)
	 * 
	 * @return an array representing table content
	 * @see #toArray(Object[])
	 * @see List#toArray()
	 */
	public Object[][] toArray();
	
	/**
	 * Converts a table to a two-dimensional array of class {@code T}. The array will be safe for
	 * modifications (changes will not be reflected on a table)
	 * 
	 * @param instance
	 *            object array sample of any size, preferrably {@code new T[0]}
	 * @return an array representing table content
	 * @see #toArray()
	 * @see List#toArray(Object[])
	 */
	public T[][] toArray(T[] instance);
	
	/**
	 * Returns {@code true} if and only if this table contains at least one element {@code e} such
	 * that {@code (o == null ? e == null : o.equals(e))}
	 * <p>
	 * If table does not supports {@code null} values, you'll get a {@link NullPointerException}.
	 * 
	 * @param o
	 * @return true, if this table contains supplied data.
	 * @throws NullPointerException
	 *             if {@code data == null} and this table does not support {@code null}s
	 * @see Collection#contains(Object)
	 */
	public boolean contains(Object o);
	
	/**
	 * Returns the pair of indices of the first found object in the table. Search algorithm may
	 * differ between table implementations, so if the table has multiple such elements, either one
	 * can be returned.
	 * <p>
	 * If no changes are made to the table, this method must constantly return the same result.
	 * 
	 * @param o
	 * @return {@link Pair} of {@code (X,Y)}, or {@code null} if object not found
	 */
	public Pair <Integer, Integer> indexOf(Object o);
	
	/**
	 * Returns the {@link List} containing indices of all instances of given data within a table.
	 * 
	 * @param data
	 * @return {@link List} of {@link Pair} of {@code (X,Y)}, never {@code null}
	 */
	public List <Pair <Integer, Integer>> indicesOf(Object data);
	
	/**
	 * Swaps positions between two table cells
	 * 
	 * @param row1
	 * @param col1
	 * @param row2
	 * @param col2
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and index is out of bounds
	 */
	public void swap(int row1, int col1, int row2, int col2);
	
	/**
	 * Swaps two table columns. If working with subtable, rest of a table will remain unchanged.
	 * 
	 * @param column1
	 * @param column2
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and index is out of bounds
	 */
	public void swapCols(int column1, int column2);
	
	/**
	 * Swaps two table rows. If working with subtable, rest of a table will remain unchanged.
	 * 
	 * @param row1
	 * @param row2
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and index is out of bounds
	 */
	public void swapRows(int row1, int row2);
	
	/**
	 * Pretty-printing method. Returns something like:
	 * 
	 * <pre>
	 * +------+-------------+---------+
	 * |Rotti |Still working|176895E17|
	 * +------+-------------+---------+
	 * |Nathan|Fired        |         |
	 * +------+-------------+---------+
	 * </pre>
	 * 
	 * Empty lines must be collapsed:
	 * 
	 * <pre>
	 * ++----+---+--+
	 * ++----+---+--+
	 * ||abcd|efg|hi|
	 * ++----+---+--+
	 * </pre>
	 * 
	 * <blockquote> <i>Implementation hint:</i> Rectangular table drawing usually requires two
	 * passes - first to determine maximum width for every column and second to draw and fill the
	 * table.</blockquote>
	 */
	@Override
	public String toString();
	
	/** Removes all records from the table. If called on a subtable, will wipe out bounded rect. */
	public void clear();
	
	/**
	 * Returns a view of a portion of a table. All changes made upon it will be reflected on a main
	 * table. E.g. {@code subtable(a,b,a,b)} will make a zero-sized subtable, that will negate all
	 * {@code put} and/or {@code get} operations.
	 * <p>
	 * Subtable operations on indices, that are out of subtable bounds, will throw a
	 * {@link IndexOutOfBoundsException}. Subtable may, but not required to, also throw this
	 * exception upon {@code get()}, they may also return {@code null}.
	 * <p>
	 * Any bound can be set to 0 to remove limit.
	 * 
	 * @param fromRow
	 *            lower Y bound (inclusive)
	 * @param fromCol
	 *            lower X bound (inclusive)
	 * @param toRow
	 *            upper Y bound (exclusive)
	 * @param toCol
	 *            upper X bound (exclusive)
	 * @return a subtable, backed up by this table
	 * @throws IndexOutOfBoundsException
	 *             if this is bounded table and indices are out of bounds
	 */
	public Table <T> subtable(int fromRow, int fromCol, int toRow, int toCol);
	
	/**
	 * Two tables are equal, if they both are have the same size and all their elements are equals.
	 * 
	 * @param obj
	 *            argument being tested for equality
	 * @return <code>true</code> if and only if the {@code obj} is a same {@code Table}
	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * The table hash code is defined as follows:
	 * 
	 * <pre>
	 * int hashCode = 0;
	 * int rowCount = getRowCount();
	 * int colCount = getColCount();
	 * for (int row = 0; row &lt; rowCount; ++row) {
	 * 	for (int col = 0; col &lt; colCount; ++col) {
	 * 		Object next = get(row, col);
	 * 		hashCode = 17 * hashCode &circ; (next == null ? 0 : next.hashCode());
	 * 	}
	 * }
	 * return hashCode;
	 * </pre>
	 * 
	 * @return table hash code
	 */
	@Override
	public int hashCode();
	
	public static interface TableEntry <T> {
		
		public int getRow();
		
		public int getCol();
		
		public T getValue();
		
		public T setValue(T newValue);
		
		/**
		 * Returns {@code true}, if both table entries represents the same cell. This is true, if
		 * they both point at the cell with the same coordinates, which have the same value.
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
		 * return (getRow() * 29) &circ; (getCol() * 47) + (value == null ? 0 : value.hashCode());
		 * </pre>
		 * 
		 * @return table entry hash code
		 */
		@Override
		public int hashCode();
	}
}
