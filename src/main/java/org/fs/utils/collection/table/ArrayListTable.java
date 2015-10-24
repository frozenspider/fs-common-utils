package org.fs.utils.collection.table;

import static org.fs.utils.ObjectUtils.*;
import static org.fs.utils.character.CharUtils.*;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.fs.utils.collection.iter.KeyIterator;
import org.fs.utils.structure.wrap.Pair;

/**
 * A standard implementation of {@link Table}. Not thread-safe. Does not support {@code null}
 * elements.
 * 
 * @author FS
 * @param <T>
 *            data type
 */
public class ArrayListTable <T> implements Table <T>, Serializable, Cloneable {
	
	private static final long			serialVersionUID	= -8355756621475466755L;
	/** Array of rows */
	protected ArrayList <ArrayList <T>>	storage;
	
	/**
	 * Constructs a new empty table.
	 * 
	 * @param nullStorage
	 *            if {@code true}, storage will be initialized to {@code null}. May be used by
	 *            subclasses to save memory (ex. for delegates). Note, that {@link #storage} has
	 *            {@code protected} access modifier, so you may change your mind later.
	 */
	protected ArrayListTable(final boolean nullStorage) {
		storage = nullStorage ? null : new ArrayList <ArrayList <T>>();
	}
	
	/** Constructs a new empty table. */
	public ArrayListTable() {
		this(false);
	}
	
	/**
	 * Constructs a new table, copying the values from the provided table.
	 * 
	 * @param src
	 *            a source table
	 */
	public ArrayListTable(final Table <T> src) {
		if (src == null) throw new NullPointerException("Source table is null");
		final int rowNum = src.getRowCount();
		final int colNum = src.getColCount();
		storage = new ArrayList <ArrayList <T>>(rowNum);
		for (int rowIdx = 0; rowIdx < rowNum; rowIdx++) {
			final ArrayList <T> row = new ArrayList <T>(colNum);
			for (int colIdx = 0; colIdx < colNum; colIdx++) {
				row.add(src.get(rowIdx, colIdx));
			}
			while (row.size() > 0 && row.get(row.size() - 1) == null) {
				row.remove(row.size() - 1);
			}
			if (row.size() == 0) {
				storage.add(null);
			} else {
				storage.add(row);
				// removing trailing nulls
			}
		}
	}
	
	/**
	 * Constructs a new table, copying the values from the provided array.
	 * 
	 * @param src
	 *            a source array
	 * @param transverse
	 *            {@code true} if src coords are [col][row], {@code false} if [row][col]
	 */
	public ArrayListTable(final T[][] src, final boolean transverse) {
		if (src.length == 0) {
			storage = new ArrayList <ArrayList <T>>(0);
		} else if (transverse) { // src[x][y]
			int rowCount = 0;
			for (final T[] element : src) {
				rowCount = Math.max(rowCount, element.length);
			}
			storage = new ArrayList <ArrayList <T>>(rowCount);
			for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
				final ArrayList <T> tableRow = new ArrayList <T>();
				int colCount = 0;
				for (int colIdx = 0; colIdx < src.length; colIdx++) {
					if (src[colIdx].length > rowIdx) colCount = colIdx;
				}
				for (int colIdx = 0; colIdx <= colCount; colIdx++) {
					if (src[colIdx].length <= rowIdx) tableRow.add(null);
					else
						tableRow.add(src[colIdx][rowIdx]);
				}
				int trail;
				while ((trail = tableRow.size()) > 0 && tableRow.get(trail - 1) == null) {
					tableRow.remove(trail - 1);
				}
				if (tableRow.size() != 0) {
					storage.add(tableRow);
				} else {
					storage.add(null);
				}
			}
		} else { // src[y][x]
			storage = new ArrayList <ArrayList <T>>(src.length);
			for (final T[] element : src) {
				final ArrayList <T> tableRow = new ArrayList <T>();
				final int colCount = element.length;
				for (int colIdx = 0; colIdx < colCount; colIdx++) {
					tableRow.add(element[colIdx]);
				}
				int trail;
				while ((trail = tableRow.size()) > 0 && tableRow.get(trail - 1) == null) {
					tableRow.remove(trail - 1);
				}
				if (tableRow.size() != 0) {
					storage.add(tableRow);
				} else {
					storage.add(null);
				}
			}
		}
	}
	
	@Override
	public T put(final int rowIdx, final int colIdx, final T value) {
		if (rowIdx < 0) throw new IndexOutOfBoundsException("rowIdx = " + rowIdx);
		if (colIdx < 0) throw new IndexOutOfBoundsException("colIdx = " + colIdx);
		if (value == null) return remove(rowIdx, colIdx);
		ArrayList <T> tableRow;
		int total = storage.size();
		if (rowIdx == total) {
			tableRow = new ArrayList <T>();
			storage.add(tableRow);
		} else if (rowIdx < total) {
			tableRow = storage.get(rowIdx);
			if (tableRow == null) {
				tableRow = new ArrayList <T>();
				storage.set(rowIdx, tableRow);
			}
		} else {
			int dif = rowIdx - total;
			while (dif-- > 0)
				storage.add(null);
			tableRow = new ArrayList <T>();
			storage.add(tableRow);
		}
		total = tableRow.size();
		if (colIdx == total) {
			tableRow.add(value);
			return null;
		} else if (colIdx < total) {
			return tableRow.set(colIdx, value);
		} else {
			int dif = colIdx - total;
			while (dif-- > 0)
				tableRow.add(null);
			tableRow.add(value);
			return null;
		}
	}
	
	@Override
	public boolean putAll(final Table <? extends T> table) {
		final int rowCount = table.getRowCount();
		final int colCount = table.getColCount();
		boolean result = true;
		for (int row = 0; row < rowCount; ++row) {
			for (int col = 0; col < colCount; ++col) {
				try {
					put(row, col, table.get(row, col));
				} catch(final IndexOutOfBoundsException ex) {
					result = false;
				}
			}
		}
		return result;
	}
	
	@Override
	public T get(final int rowIdx, final int colIdx) {
		if (rowIdx < 0) throw new IndexOutOfBoundsException("rowIdx = " + rowIdx);
		if (colIdx < 0) throw new IndexOutOfBoundsException("colIdx = " + colIdx);
		if (rowIdx >= storage.size()) return null;
		final ArrayList <T> tableRow = storage.get(rowIdx);
		if (tableRow == null || colIdx >= tableRow.size()) return null;
		return tableRow.get(colIdx);
	}
	
	@Override
	public T get(final int rowIdx, final int colIdx, final T defaultValue) {
		final T value = get(rowIdx, colIdx);
		if (value != null) return value;
		put(rowIdx, colIdx, defaultValue);
		return defaultValue;
	}
	
	@Override
	public T remove(final int rowIdx, final int colIdx) {
		if (rowIdx < 0) throw new IndexOutOfBoundsException("rowIdx = " + rowIdx);
		if (colIdx < 0) throw new IndexOutOfBoundsException("colIdx = " + colIdx);
		if (rowIdx >= getRowCount()) return null;
		final ArrayList <T> tableRow = storage.get(rowIdx);
		if (tableRow == null || colIdx >= tableRow.size()) return null;
		final T removed = tableRow.get(colIdx);
		tableRow.set(colIdx, null);
		cleanupRemoveEmpty();
		return removed;
	}
	
	@Override
	public List <T> removeRow(final int rowIdx) {
		if (rowIdx < 0) throw new IndexOutOfBoundsException("rowIdx = " + rowIdx);
		if (rowIdx >= storage.size()) return null;
		final ArrayList <T> removed = storage.remove(rowIdx);
		cleanupRemoveEmpty();
		return removed;
	}
	
	@Override
	public List <T> removeCol(final int colIdx) {
		if (colIdx < 0) throw new IndexOutOfBoundsException("colIdx = " + colIdx);
		if (colIdx >= getColCount()) return null;
		final int numRows = storage.size();
		final List <T> result = new ArrayList <T>(numRows);
		for (int rowIdx = 0; rowIdx < numRows; ++rowIdx) {
			final List <T> row = storage.get(rowIdx);
			if (row == null || colIdx >= row.size()) {
				result.add(null);
			} else {
				result.add(row.remove(colIdx));
			}
		}
		cleanupRemoveEmpty();
		return result;
	}
	
	@Override
	public List <T> insertRow(final int rowIdx) {
		if (rowIdx < 0) throw new IndexOutOfBoundsException("rowIdx = " + rowIdx);
		if (rowIdx <= storage.size()) {
			storage.add(rowIdx, null);
		}
		return new BackedLineList(rowIdx, false);
	}
	
	@Override
	public List <T> insertCol(final int colIdx) {
		if (colIdx < 0) throw new IndexOutOfBoundsException("colIdx = " + colIdx);
		if (colIdx <= getColCount()) {
			for (final ArrayList <T> row : storage) {
				if (colIdx <= row.size()) {
					row.add(colIdx, null);
				}
			}
		}
		return new BackedLineList(colIdx, true);
	}
	
	@Override
	public List <T> getRow(final int rowIdx) {
		if (rowIdx < 0) throw new IndexOutOfBoundsException("rowIdx = " + rowIdx);
		return new BackedLineList(rowIdx, false);
	}
	
	@Override
	public List <T> getCol(final int colIdx) {
		if (colIdx < 0) throw new IndexOutOfBoundsException("colIdx = " + colIdx);
		return new BackedLineList(colIdx, true);
	}
	
	@Override
	public int getRowCount() {
		return storage.size();
	}
	
	@Override
	public int getColCount() {
		int maxLen = 0;
		for (final ArrayList <T> row : storage) {
			if (row == null) continue;
			maxLen = Math.max(maxLen, row.size());
		}
		return maxLen;
	}
	
	@Override
	public boolean isEmpty() {
		if (storage.isEmpty()) return true;
		for (final ArrayList <T> row : storage) {
			if (row == null) continue;
			if (!row.isEmpty()) return false;
		}
		return true;
	}
	
	@Override
	public boolean isEmptyRow(final int rowIdx) {
		if (rowIdx < 0) throw new IndexOutOfBoundsException("rowIdx = " + rowIdx);
		if (rowIdx >= getRowCount()) return true;
		final List <T> row = getRow(rowIdx);
		return Collections.frequency(row, null) == row.size();
	}
	
	@Override
	public boolean isEmptyCol(final int colIdx) {
		if (colIdx < 0) throw new IndexOutOfBoundsException("colIdx = " + colIdx);
		if (colIdx >= getColCount()) return true;
		final List <T> col = getCol(colIdx);
		return Collections.frequency(col, null) == col.size();
	}
	
	@Override
	public KeyIterator <T, Integer> valuesInCol(final int colIdx) {
		if (colIdx < 0) throw new IndexOutOfBoundsException("colIdx = " + colIdx);
		return new VerticalIterator(colIdx, -1, -1);
	}
	
	@Override
	public KeyIterator <T, Integer> valuesInRow(final int rowIdx) {
		if (rowIdx < 0) throw new IndexOutOfBoundsException("rowIdx = " + rowIdx);
		return new HorizontalIterator(rowIdx, -1, -1);
	}
	
	@Override
	public boolean contains(final Object o) {
		return indexOf(o) != null;
	}
	
	@Override
	public Pair <Integer, Integer> indexOf(final Object o) {
		if (o == null) throw new NullPointerException();
		final int rowCount = storage.size();
		for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
			final ArrayList <T> row = storage.get(rowIdx);
			if (row == null) continue;
			final int colCount = row.size();
			for (int colIdx = 0; colIdx < colCount; colIdx++) {
				if (o.equals(row.get(colIdx))) return new Pair <Integer, Integer>(rowIdx, colIdx);
			}
		}
		return null;
	}
	
	@Override
	public List <Pair <Integer, Integer>> indicesOf(final Object o) {
		if (o == null) throw new NullPointerException();
		final List <Pair <Integer, Integer>> result = new ArrayList <Pair <Integer, Integer>>();
		final int rowCount = storage.size();
		for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
			final ArrayList <T> tableRow = storage.get(rowIdx);
			if (tableRow == null) continue;
			final int colCount = tableRow.size();
			for (int colIdx = 0; colIdx < colCount; colIdx++) {
				if (o.equals(tableRow.get(colIdx)))
					result.add(new Pair <Integer, Integer>(rowIdx, colIdx));
			}
		}
		return result;
	}
	
	@Override
	public void swapRows(final int row1, final int row2) {
		if (row1 < 0) throw new IndexOutOfBoundsException("row1 = " + row1);
		if (row2 < 0) throw new IndexOutOfBoundsException("row2 = " + row2);
		final int maxRowIdx = Math.max(row1, row2);
		final int rowCount = storage.size();
		if (rowCount <= maxRowIdx) {
			for (int i = rowCount; i <= maxRowIdx; ++i) {
				storage.add(null);
			}
		}
		Collections.swap(storage, row1, row2);
		cleanupRemoveEmpty();
	}
	
	@Override
	public void swapCols(final int col1, final int col2) {
		if (col1 < 0) throw new IndexOutOfBoundsException("col1 = " + col1);
		if (col2 < 0) throw new IndexOutOfBoundsException("col2 = " + col2);
		final int maxColIdx = Math.max(col1, col2);
		for (int rowIdx = 0; rowIdx < getRowCount(); rowIdx++) {
			final ArrayList <T> row = storage.get(rowIdx);
			if (row == null) continue;
			final int colCount = row.size();
			if (colCount <= maxColIdx) {
				for (int colIdx = colCount; colIdx <= maxColIdx; ++colIdx) {
					row.add(null);
				}
			}
			Collections.swap(row, col1, col2);
		}
		cleanupRemoveEmpty();
	}
	
	@Override
	public void swap(final int row1, final int col1, final int row2, final int col2) {
		if (row1 < 0) throw new IndexOutOfBoundsException("row1 = " + row1);
		if (row2 < 0) throw new IndexOutOfBoundsException("row2 = " + row2);
		if (col1 < 0) throw new IndexOutOfBoundsException("col1 = " + col1);
		if (col2 < 0) throw new IndexOutOfBoundsException("col2 = " + col2);
		put(row1, col1, put(row2, col2, get(row1, col1)));
		cleanupRemoveEmpty();
	}
	
	@Override
	public void clear() {
		storage.clear();
	}
	
	@Override
	public List <List <T>> rows() {
		return new RowList(-1, -1, -1, -1);
	}
	
	@Override
	public List <List <T>> columns() {
		return new ColumnList(-1, -1, -1, -1);
	}
	
	@Override
	public Table <T> subtable(final int fromRow, final int fromCol, final int toRow, final int toCol) {
		if (fromRow < 0) throw new IndexOutOfBoundsException("fromRow = " + fromRow);
		if (fromCol < 0) throw new IndexOutOfBoundsException("fromCol = " + fromCol);
		if (toRow < 0) throw new IndexOutOfBoundsException("toRow = " + toRow);
		if (toCol < 0) throw new IndexOutOfBoundsException("toCol = " + toCol);
		return new ArrayListSubTable(fromRow, toRow, fromCol, toCol);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Table)) return false;
		final Table <?> cast = (Table <?>) obj;
		final int rowCount = getRowCount();
		final int colCount = getColCount();
		if (cast.getRowCount() != rowCount) return false;
		if (cast.getColCount() != colCount) return false;
		for (int row = 0; row < rowCount; ++row) {
			for (int col = 0; col < colCount; ++col) {
				final Object next1 = get(row, col);
				final Object next2 = cast.get(row, col);
				if (!eq(next1, next2)) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0;
		final int rowCount = getRowCount();
		final int colCount = getColCount();
		for (int row = 0; row < rowCount; ++row) {
			for (int col = 0; col < colCount; ++col) {
				final Object next = get(row, col);
				hashCode = 17 * hashCode ^ (next == null ? 0 : next.hashCode());
			}
		}
		return hashCode;
	}
	
	@Override
	public String toString() {
		final int rowCount = getRowCount();
		final int colCount = getColCount();
		final int[] maxColumnWidth = new int[colCount];
		{
			// Determining maximum cell widths
			for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
				final ArrayList <T> tableRow = storage.get(rowIdx);
				if (tableRow == null) continue;
				final int colSize = tableRow.size();
				for (int colIdx = 0; colIdx < colSize; colIdx++) {
					final String toStr = valueOr(tableRow.get(colIdx), "").toString();
					maxColumnWidth[colIdx] = Math.max(maxColumnWidth[colIdx], toStr.length());
				}
			}
		}
		final StringBuilder result = new StringBuilder();
		addTableStringRow(result, maxColumnWidth);
		for (final List <?> tableRow : storage) {
			if (tableRow == null) {
				addTableStringRow(result, maxColumnWidth);
				continue;
			}
			result.append('|');
			final int colSize = tableRow.size();
			for (int colIdx = 0; colIdx < colCount; colIdx++) {
				final String toStr;
				if (colIdx < colSize) {
					final Object get = tableRow.get(colIdx);
					toStr = valueOr(get, "").toString();
				} else {
					toStr = "";
				}
				result.append(toStr);
				result.append(nTimes(' ', maxColumnWidth[colIdx] - toStr.length()));
				result.append('|');
			}
			result.append('\n');
			addTableStringRow(result, maxColumnWidth);
		}
		return result.toString();
	}
	
	/** Unavailable on subtables. */
	@Override
	public Object clone() throws CloneNotSupportedException {
		ArrayListTable <T> clone;
		clone = (ArrayListTable <T>) super.clone();
		final ArrayList <ArrayList <T>> cloneStorage = (ArrayList <ArrayList <T>>) storage.clone();
		final int rowCount = clone.storage.size();
		for (int rowIdx = 0; rowIdx < rowCount; ++rowIdx) {
			final ArrayList <T> element = cloneStorage.get(rowIdx);
			cloneStorage.set(rowIdx, element == null ? null : (ArrayList <T>) element.clone());
		}
		clone.storage = cloneStorage;
		return clone;
	}
	
	@Override
	public Object[][] toArray() {
		final Object[][] result = new Object[getColCount()][getRowCount()];
		final int rowCount = storage.size();
		for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
			final ArrayList <T> tableRow = storage.get(rowIdx);
			if (tableRow == null) continue;
			final int colCount = tableRow.size();
			for (int colIdx = 0; colIdx < colCount; colIdx++) {
				result[colIdx][rowIdx] = tableRow.get(colIdx);
			}
		}
		return result;
	}
	
	@Override
	public T[][] toArray(final T[] instance) {
		final T[][] result = (T[][]) Array.newInstance(instance.getClass().getComponentType(),
				getColCount(), getRowCount());
		final int rowCount = storage.size();
		for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
			final ArrayList <T> tableRow = storage.get(rowIdx);
			final int colCount = tableRow.size();
			for (int colIdx = 0; colIdx < colCount; colIdx++) {
				result[colIdx][rowIdx] = tableRow.get(colIdx);
			}
		}
		return result;
	}
	
	/**
	 * Replaces empty rows with {@code null} and removes trailing {@code null}s in separate rows and
	 * row array. This state must be maintained constantly.
	 */
	protected void cleanupRemoveEmpty() {
		final ArrayList <ArrayList <T>> localStorage = getStorage();
		int rowCount = localStorage.size();
		for (int rowIdx = 0; rowIdx < rowCount; ++rowIdx) {
			// Remove trailing null columns
			final ArrayList <T> row = localStorage.get(rowIdx);
			if (row == null) continue;
			while (true) {
				final int rowCount2 = row.size();
				if (rowCount2 == 0 || row.get(rowCount2 - 1) != null) break;
				row.remove(rowCount2 - 1);
			}
			if (row.size() == 0) localStorage.set(rowIdx, null);
		}
		while (true) {
			// Remove trailing null rows
			rowCount = localStorage.size();
			if (rowCount == 0 || localStorage.get(rowCount - 1) != null) break;
			localStorage.remove(rowCount - 1);
		}
	}
	
	protected ArrayList <ArrayList <T>> getStorage() {
		return storage;
	}
	
	protected void addTableStringRow(final StringBuilder sb, final int[] maxCellLen) {
		sb.append('+');
		for (final int mcl : maxCellLen) {
			sb.append(nTimes('-', mcl));
			sb.append('+');
		}
		sb.append('\n');
	}
	
	protected class HorizontalIterator implements KeyIterator <T, Integer> {
		
		final int	rowIdx;
		int			colCurr;
		final int	colAfterLast;
		boolean		removed		= false;
		boolean		justStarted	= true;
		
		public HorizontalIterator(final int rowIdx, final int colBeforeFirst, final int colAfterLast) {
			this.rowIdx = rowIdx;
			this.colCurr = colBeforeFirst;
			this.colAfterLast = colAfterLast;
		}
		
		@Override
		public boolean hasNext() {
			if (colCurr + 1 == colAfterLast) return false;
			if (rowIdx >= storage.size()) return false;
			final ArrayList <T> tableRow = storage.get(rowIdx);
			if (tableRow == null) return false;
			final int colCount = colAfterLast == -1 ? tableRow.size() : Math.min(tableRow.size(),
					colAfterLast);
			if (colCurr + 1 >= colCount) return false;
			for (int i = colCurr + 1; i < colCount; i++) {
				if (tableRow.get(i) != null) return true;
			}
			return false;
		}
		
		@Override
		public T next() {
			if (colCurr + 1 == colAfterLast) throw new NoSuchElementException();
			final ArrayList <T> tableRow = storage.get(rowIdx);
			removed = false;
			T result;
			while ((result = tableRow.get(++colCurr)) == null) {
				if (colCurr + 1 == colAfterLast) throw new NoSuchElementException();
			}
			this.justStarted = false;
			return result;
		}
		
		@Override
		public void remove() {
			if (removed) throw new IllegalStateException();
			final ArrayList <T> tableRow = storage.get(rowIdx);
			tableRow.remove(colCurr);
			removed = true;
		}
		
		@Override
		public Integer getKey() {
			if (justStarted || removed) throw new IllegalStateException();
			return colCurr;
		}
	}
	
	protected class VerticalIterator implements KeyIterator <T, Integer> {
		
		final int	colIdx;
		int			rowCurr;
		final int	rowAfterLast;
		boolean		removed		= false;
		boolean		justStarted	= true;
		
		public VerticalIterator(final int colIdx, final int rowBeforeFirst, final int rowAfterLast) {
			this.colIdx = colIdx;
			this.rowCurr = rowBeforeFirst;
			this.rowAfterLast = rowAfterLast;
		}
		
		@Override
		public boolean hasNext() {
			if (rowCurr + 1 == rowAfterLast) return false;
			final int rowCount = rowAfterLast == -1 ? getRowCount() : Math.min(getRowCount(),
					rowAfterLast);
			for (int rowIdx = rowCurr + 1; rowIdx < rowCount; rowIdx++) {
				final ArrayList <T> tableRow = storage.get(rowIdx);
				if (tableRow != null && tableRow.size() > colIdx && tableRow.get(colIdx) != null)
					return true;
			}
			return false;
		}
		
		@Override
		public T next() {
			removed = false;
			while (true) {
				if (rowCurr + 1 == rowAfterLast) throw new NoSuchElementException();
				++rowCurr;
				final ArrayList <T> tableRow = storage.get(rowCurr);
				if (tableRow == null) continue;
				if (tableRow.size() <= colIdx) continue;
				final T result = tableRow.get(colIdx);
				if (result == null) continue;
				justStarted = false;
				return result;
			}
		}
		
		@Override
		public void remove() {
			if (removed) throw new IllegalStateException();
			final ArrayList <T> tableRow = storage.get(rowCurr);
			tableRow.remove(colIdx);
			removed = true;
		}
		
		@Override
		public Integer getKey() {
			if (justStarted || removed) throw new IllegalStateException();
			return rowCurr;
		}
	}
	
	protected class RowList extends AbstractList <List <T>> {
		
		private final boolean	upperBounded;
		private final int		rowZeroIdx;
		private final int		rowLast;
		/** Inclusive */
		private final int		colFrom;
		/** Exclusive */
		private final int		colTo;
		
		public RowList(
				final int rowBeforeFirst,
				final int rowAfterLast,
				final int colBeforeFirst,
				final int colAfterLast) {
			if (colAfterLast == -1) colTo = getColCount();
			else
				colTo = Math.min(getColCount(), colAfterLast);
			int tyLast;
			if (rowAfterLast == -1) {
				upperBounded = false;
				tyLast = getRowCount() - 1;
			} else {
				upperBounded = true;
				tyLast = Math.min(getRowCount(), rowAfterLast) - 1;
			}
			rowZeroIdx = rowBeforeFirst + 1;
			rowLast = tyLast == -1 ? -10 : tyLast;
			colFrom = colBeforeFirst + 1;
		}
		
		@Override
		public List <T> get(final int index) {
			if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
			return new BackedLineList(rowZeroIdx + index, false);
		}
		
		@Override
		public int size() {
			return rowLast + 1;
		}
		
		@Override
		public List <T> remove(final int index) {
			if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
			final List <T> result = new ArrayList <T>();
			for (int i = colFrom; i < colTo; ++i) {
				result.add(ArrayListTable.this.remove(rowZeroIdx + index, i));
			}
			return result;
		}
		
		@Override
		public List <T> set(final int index, final List <T> list) {
			if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
			final List <T> result = new ArrayList <T>();
			final int listSize = list.size();
			for (int i = colFrom; i < colTo; ++i) {
				final int listIdx = i - colFrom;
				if (listIdx < listSize) result.add(ArrayListTable.this.put(rowZeroIdx + index, i,
						list.get(listIdx)));
				else
					result.add(ArrayListTable.this.put(rowZeroIdx + index, i, null));
			}
			return result;
		}
		
		@Override
		public boolean add(final List <T> list) {
			if (upperBounded)
				throw new UnsupportedOperationException("Cannot perform this on a bounded subtable");
			final int listSize = list.size();
			final int insertRowIdx = rowZeroIdx + size();
			for (int colIdx = colFrom; colIdx < colTo; ++colIdx) {
				final int listIdx = colIdx - colFrom;
				if (listIdx >= listSize) break;
				ArrayListTable.this.put(insertRowIdx, colIdx, list.get(listIdx));
			}
			return true;
		}
	}
	
	protected class ColumnList extends AbstractList <List <T>> {
		
		private final boolean	upperBounded;
		private final int		colZeroIdx;
		private final int		colLast;
		/** Inclusive */
		private final int		rowFrom;
		/** Exclusive */
		private final int		rowTo;
		
		public ColumnList(
				final int rowBeforeFirst,
				final int rowAfterLast,
				final int colBeforeFirst,
				final int colAfterLast) {
			if (colAfterLast == -1) rowTo = getRowCount();
			else
				rowTo = Math.min(getRowCount(), rowAfterLast);
			int txLast;
			if (colAfterLast == -1) {
				upperBounded = false;
				txLast = getColCount() - 1;
			} else {
				upperBounded = true;
				txLast = Math.min(getColCount(), colAfterLast) - 1;
			}
			colZeroIdx = colBeforeFirst + 1;
			colLast = txLast == -1 ? -10 : txLast;
			rowFrom = rowBeforeFirst + 1;
		}
		
		@Override
		public List <T> get(final int index) {
			if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
			return new BackedLineList(colZeroIdx + index, true);
		}
		
		@Override
		public int size() {
			return colLast + 1;
		}
		
		@Override
		public List <T> remove(final int index) {
			if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
			final List <T> result = new ArrayList <T>();
			for (int i = rowFrom; i < rowTo; ++i) {
				result.add(ArrayListTable.this.remove(i, colZeroIdx + index));
			}
			return result;
		}
		
		@Override
		public List <T> set(final int index, final List <T> list) {
			if (index >= size()) throw new IndexOutOfBoundsException("index >= size()");
			final List <T> result = new ArrayList <T>();
			final int listSize = list.size();
			for (int i = rowFrom; i < rowTo; ++i) {
				final int listIdx = i - rowFrom;
				if (listIdx < listSize) result.add(ArrayListTable.this.put(i, colZeroIdx + index,
						list.get(listIdx)));
				else
					result.add(ArrayListTable.this.put(i, colZeroIdx + index, null));
			}
			return result;
		}
		
		@Override
		public boolean add(final List <T> list) {
			if (upperBounded)
				throw new UnsupportedOperationException("Cannot perform this on a bounded subtable");
			final int listSize = list.size();
			final int insertPos = colZeroIdx + size();
			for (int i = rowFrom; i < rowTo; ++i) {
				final int listIdx = i - rowFrom;
				if (listIdx >= listSize) break;
				ArrayListTable.this.put(i, insertPos, list.get(listIdx));
			}
			return true;
		}
	}
	
	protected class BackedLineList extends AbstractList <T> {
		
		private final int		fixedValue;
		private final boolean	vertical;
		
		public BackedLineList(final int fixedValue, final boolean vertical) {
			this.fixedValue = fixedValue;
			this.vertical = vertical;
		}
		
		@Override
		public boolean add(final T element) {
			final int size = size();
			if (vertical) return put(size, fixedValue, element) == null;
			return put(fixedValue, size, element) == null;
		}
		
		@Override
		public T remove(final int index) {
			if (vertical) return ArrayListTable.this.remove(index, fixedValue);
			return ArrayListTable.this.remove(fixedValue, index);
		}
		
		@Override
		public T get(final int index) {
			if (vertical) return ArrayListTable.this.get(index, fixedValue);
			return ArrayListTable.this.get(fixedValue, index);
		}
		
		@Override
		public T set(final int index, final T value) {
			if (vertical) return ArrayListTable.this.put(index, fixedValue, value);
			return ArrayListTable.this.put(fixedValue, index, value);
		}
		
		@Override
		public int size() {
			if (vertical) return getRowCount();
			return getColCount();
		}
	}
	
	protected static class SimpleTableEntry <T> implements TableEntry <T> {
		
		final int		row;
		final int		col;
		final Table <T>	t;
		
		public SimpleTableEntry(final int row, final int col, final Table <T> t) {
			this.row = row;
			this.col = col;
			this.t = t;
		}
		
		@Override
		public int getRow() {
			return row;
		}
		
		@Override
		public int getCol() {
			return col;
		}
		
		@Override
		public T getValue() {
			return t.get(row, col);
		}
		
		@Override
		public T setValue(final T newValue) {
			return t.put(row, col, newValue);
		}
		
		@Override
		public String toString() {
			return "{" + row + "," + col + ":" + getValue() + "}";
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof TableEntry)) return false;
			final TableEntry <?> cast = (TableEntry <?>) obj;
			return cast.getRow() == row && cast.getCol() == col && eq(getValue(), cast.getValue());
		}
		
		@Override
		public int hashCode() {
			final T value = getValue();
			return row * 29 ^ col * 47 + (value == null ? 0 : value.hashCode());
		}
	}
	
	protected class ArrayListSubTable extends ArrayListTable <T> {
		
		final int	shiftCol;
		final int	maxCol;
		final int	shiftRow;
		final int	maxRow;
		
		public ArrayListSubTable(
				final int fromRow,
				final int toRow,
				final int fromCol,
				final int toCol) {
			super(true); // Not waste memory for parent's storage
			this.shiftRow = fromRow;
			this.maxRow = toRow;
			this.shiftCol = fromCol;
			this.maxCol = toCol;
		}
		
		@Override
		public List <T> removeRow(final int y) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public List <T> removeCol(final int x) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public List <T> insertRow(final int y) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public List <T> insertCol(final int x) {
			throw new UnsupportedOperationException();
		}
		
		protected void checkIndices(final int row, final int col) throws IndexOutOfBoundsException {
			if (row < 0 || maxRow != 0 && row + shiftRow >= maxRow)
				throw new IndexOutOfBoundsException("row = " + row);
			if (col < 0 || maxCol != 0 && col + shiftCol >= maxCol)
				throw new IndexOutOfBoundsException("col = " + col);
		}
		
		@Override
		public T put(final int row, final int col, final T data) {
			checkIndices(row, col);
			return ArrayListTable.this.put(shiftRow + row, shiftCol + col, data);
		}
		
		@Override
		public T get(final int row, final int col) {
			checkIndices(row, col);
			return ArrayListTable.this.get(shiftRow + row, shiftCol + col);
		}
		
		@Override
		public T remove(final int row, final int col) {
			checkIndices(row, col);
			return ArrayListTable.this.remove(shiftRow + row, shiftCol + col);
		}
		
		@Override
		public int getColCount() {
			if (maxCol == 0) {
				return ArrayListTable.this.getColCount() - shiftCol;
			}
			return maxCol - shiftCol;
		}
		
		@Override
		public int getRowCount() {
			if (maxRow == 0) {
				return ArrayListTable.this.getRowCount() - shiftRow;
			}
			return Math.min(ArrayListTable.this.getRowCount() - shiftRow, maxRow - shiftRow);
		}
		
		@Override
		public boolean putAll(final Table <? extends T> table) {
			return super.putAll(table); // put() is overriden
		}
		
		@Override
		public boolean isEmpty() {
			if (shiftCol == 0 && shiftRow == 0 && maxCol == 0 && maxRow == 0)
				return ArrayListTable.this.isEmpty();
			if (ArrayListTable.this.storage.isEmpty()) return true;
			final int origRowCount = ArrayListTable.this.storage.size();
			if (origRowCount <= shiftRow) return true;
			final int lastRowIdx = maxRow == 0 ? origRowCount : Math.min(maxRow, origRowCount);
			int origColCount;
			int lastColIdx;
			for (int i = shiftRow; i < lastRowIdx; i++) {
				final ArrayList <T> row = ArrayListTable.this.storage.get(i);
				if (row == null) continue;
				origColCount = row.size();
				if (origColCount > shiftCol) {
					lastColIdx = maxCol == 0 ? origColCount : Math.min(maxCol, origColCount);
					for (int colIdx = shiftCol; colIdx < lastColIdx; colIdx++) {
						if (row.get(colIdx) != null) return false;
					}
				}
			}
			return true;
		}
		
		@Override
		public boolean contains(final Object o) {
			return super.contains(o); // indexOf() is overriden
		}
		
		@Override
		public void swap(final int row1, final int col1, final int row2, final int col2) {
			super.swap(row1, col1, row2, col2); // put() is overriden
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException {
			throw new CloneNotSupportedException();
		}
		
		@Override
		public KeyIterator <T, Integer> valuesInRow(final int rowIdx) {
			checkIndices(rowIdx, 0);
			return ArrayListTable.this.new HorizontalIterator(shiftRow + rowIdx, shiftCol - 1,
					maxCol == 0 ? -1 : maxCol);
		}
		
		@Override
		public KeyIterator <T, Integer> valuesInCol(final int colIdx) {
			checkIndices(0, colIdx);
			return ArrayListTable.this.new VerticalIterator(shiftCol + colIdx, shiftRow - 1,
					maxRow == 0 ? -1 : maxRow);
		}
		
		@Override
		public Pair <Integer, Integer> indexOf(final Object data) {
			if (data == null) throw new NullPointerException();
			final int rowTo = Math.min(ArrayListTable.this.storage.size(), maxRow);
			for (int rowIdx = shiftRow; rowIdx < rowTo; rowIdx++) {
				final ArrayList <T> tableRow = ArrayListTable.this.storage.get(rowIdx);
				if (tableRow == null) continue;
				final int colTo = Math.min(tableRow.size(), maxCol);
				for (int colIdx = shiftCol; colIdx < colTo; colIdx++) {
					if (data.equals(tableRow.get(colIdx)))
						return new Pair <Integer, Integer>(rowIdx, colIdx);
				}
			}
			return null;
		}
		
		@Override
		public List <Pair <Integer, Integer>> indicesOf(final Object data) {
			if (data == null) throw new NullPointerException();
			final List <Pair <Integer, Integer>> result = new ArrayList <Pair <Integer, Integer>>();
			final int rowTo = Math.min(ArrayListTable.this.storage.size(), maxRow);
			for (int rowIdx = shiftRow; rowIdx < rowTo; rowIdx++) {
				final ArrayList <T> tableRow = ArrayListTable.this.storage.get(rowIdx);
				if (tableRow == null) continue;
				final int colTo = Math.min(tableRow.size(), maxCol);
				for (int colIdx = shiftCol; colIdx < colTo; colIdx++) {
					if (data.equals(tableRow.get(colIdx)))
						result.add(new Pair <Integer, Integer>(rowIdx, colIdx));
				}
			}
			return result;
		}
		
		@Override
		public Table <T> subtable(
				final int fromRow,
				final int fromCol,
				final int toRow,
				final int toCol) {
			checkIndices(fromRow, fromCol);
			checkIndices(toRow == 0 ? 0 : toRow - 1, toCol == 0 ? 0 : toCol - 1);
			return ArrayListTable.this.subtable(shiftRow + fromRow, shiftCol + fromCol,
					toRow == 0 ? maxRow : shiftRow + toRow, toCol == 0 ? maxCol : shiftCol + toCol);
		}
		
		@Override
		public String toString() {
			final int rowCount = getRowCount();
			final int colCount = getColCount();
			final int[] maxCellLen = new int[colCount];
			for (int rowIdx = shiftRow; rowIdx < rowCount; rowIdx++) {
				final ArrayList <T> tableRow = ArrayListTable.this.storage.get(rowIdx);
				if (tableRow == null) continue;
				final int xs = maxCol == 0 ? tableRow.size() : Math.min(tableRow.size(), maxCol);
				for (int colIdx = shiftCol; colIdx < xs; colIdx++) {
					final T val = tableRow.get(colIdx);
					if (val == null) continue;
					maxCellLen[colIdx - shiftCol] = Math.max(maxCellLen[colIdx - shiftCol],
							val.toString().length());
				}
			}
			final StringBuilder result = new StringBuilder();
			String toStr;
			addTableStringRow(result, maxCellLen);
			final ListIterator <ArrayList <T>> iter = ArrayListTable.this.storage.listIterator(shiftRow);
			int rowIdx = 0;
			while (iter.hasNext() && (maxRow == 0 || ++rowIdx <= maxRow - shiftRow)) {
				final List <T> tableRow = iter.next();
				if (tableRow != null && !tableRow.isEmpty()) {
					result.append('|');
					final int colTo = maxCol == 0 ? tableRow.size() : Math.min(tableRow.size(),
							maxCol);
					for (int x = 0; x < colCount; x++) {
						if (x + shiftCol < colTo) {
							final Object get = tableRow.get(x + shiftCol);
							toStr = get == null ? "" : get.toString();
						} else {
							toStr = "";
						}
						result.append(toStr);
						for (int j = toStr.length(); j < maxCellLen[x]; j++) {
							result.append(' ');
						}
						result.append('|');
					}
					result.append('\n');
				}
				addTableStringRow(result, maxCellLen);
			}
			if (maxRow != 0) for (int i = rowCount + 1; i < maxRow - shiftRow; i++) {
				addTableStringRow(result, maxCellLen);
			}
			return result.toString();
		}
		
		@Override
		public Object[][] toArray() {
			final Object[][] result = new Object[getColCount()][getRowCount()];
			final int rowCount = maxRow == 0 ? ArrayListTable.this.storage.size() : Math.min(
					maxRow, ArrayListTable.this.storage.size());
			for (int rowIdx = shiftRow; rowIdx < rowCount; rowIdx++) {
				final ArrayList <T> tableRow = ArrayListTable.this.storage.get(rowIdx);
				if (tableRow == null) continue;
				final int colCount = maxRow == 0 ? tableRow.size() : Math.min(maxCol,
						tableRow.size());
				for (int colIdx = shiftCol; colIdx < colCount; colIdx++) {
					result[colIdx - shiftCol][rowIdx - shiftRow] = tableRow.get(colIdx);
				}
			}
			return result;
		}
		
		@Override
		public T[][] toArray(final T[] instance) {
			final T[][] result = (T[][]) Array.newInstance(instance.getClass().getComponentType(),
					getColCount(), getRowCount());
			final int rowCount = maxRow == 0 ? ArrayListTable.this.storage.size() : Math.min(
					maxRow, ArrayListTable.this.storage.size());
			for (int rowIdx = shiftRow; rowIdx < rowCount; rowIdx++) {
				final ArrayList <T> tableRow = ArrayListTable.this.storage.get(rowIdx);
				final int colCount = maxRow == 0 ? tableRow.size() : Math.min(maxCol,
						tableRow.size());
				for (int colIdx = shiftCol; colIdx < colCount; colIdx++) {
					result[colIdx - shiftCol][rowIdx - shiftRow] = tableRow.get(colIdx);
				}
			}
			return result;
		}
		
		// ++ Unimplementing Serializable
		private void writeObject(@SuppressWarnings("unused") final java.io.ObjectOutputStream out)
				throws java.io.IOException {
			throw new java.io.NotSerializableException("You cannot serialize a subtable");
		}
		
		private void readObject(@SuppressWarnings("unused") final java.io.ObjectInputStream in)
				throws java.io.IOException {
			throw new java.io.NotSerializableException("You cannot serialize a subtable");
		}
		
		// -- Unimplementing Serializable
		@Override
		public void swapCols(final int col1, final int col2) {
			if (maxCol == 0) {
				ArrayListTable.this.swapCols(col1, col2);
			} else {
				checkIndices(0, col1);
				checkIndices(0, col2);
				final int rowCount = getRowCount();
				for (int rowIdx = 0; rowIdx < rowCount; ++rowIdx) {
					put(rowIdx, col1, put(rowIdx, col2, get(rowIdx, col1)));
				}
				cleanupRemoveEmpty();
			}
		}
		
		@Override
		public void swapRows(final int row1, final int row2) {
			if (maxCol == 0) {
				ArrayListTable.this.swapRows(row1, row2);
			} else {
				checkIndices(row1, 0);
				checkIndices(row2, 0);
				final int colCount = getColCount();
				for (int colIdx = 0; colIdx < colCount; ++colIdx) {
					put(row1, colIdx, put(row2, colIdx, get(row1, colIdx)));
				}
				cleanupRemoveEmpty();
			}
		}
		
		@Override
		public void clear() {
			final int rowCount = getRowCount();
			final int colCount = getColCount();
			for (int colIdx = 0; colIdx < colCount; ++colIdx) {
				for (int rowIdx = 0; rowIdx < rowCount; ++rowIdx) {
					remove(rowIdx, colIdx);
				}
			}
		}
		
		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}
		
		@Override
		public List <List <T>> rows() {
			final int rowAfterLast = maxRow == 0 ? -1 : maxRow - shiftRow;
			final int colAfterLast = maxCol == 0 ? -1 : maxCol - shiftCol;
			return new RowList(-1, rowAfterLast, -1, colAfterLast);
		}
		
		@Override
		public List <List <T>> columns() {
			final int rowAfterLast = maxRow == 0 ? -1 : maxRow - shiftRow;
			final int colAfterLast = maxCol == 0 ? -1 : maxCol - shiftCol;
			return new ColumnList(-1, rowAfterLast, -1, colAfterLast);
		}
		
		@Override
		public List <T> getRow(final int rowIdx) {
			checkIndices(rowIdx, 0);
			return super.getRow(rowIdx);
		}
		
		@Override
		public List <T> getCol(final int colIdx) {
			checkIndices(0, colIdx);
			return super.getCol(colIdx);
		}
		
		@Override
		protected ArrayList <ArrayList <T>> getStorage() {
			return ArrayListTable.this.storage;
		}
	}
}
