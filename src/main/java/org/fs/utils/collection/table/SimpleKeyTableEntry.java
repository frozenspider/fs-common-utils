package org.fs.utils.collection.table;

import static org.fs.utils.ObjectUtils.eq;

import org.fs.utils.collection.table.KeyTable.KeyTableEntry;

public class SimpleKeyTableEntry<Rt,Ct,T> implements KeyTableEntry <Rt, Ct, T> {

	private final Rt					rowKey;
	private final Ct					colKey;
	private final KeyTable <Rt, Ct, T>	table;

	protected SimpleKeyTableEntry(final Rt rowKey, final Ct colKey, final KeyTable <Rt, Ct, T> table){
		this.rowKey = rowKey;
		this.colKey = colKey;
		this.table = table;
	}

	@Override
	public Rt getRowKey(){
		return rowKey;
	}

	@Override
	public Ct getColKey(){
		return colKey;
	}

	@Override
	public T getValue(){
		return table.get(rowKey, colKey);
	}

	@Override
	public T setValue(final T newValue){
		return table.put(rowKey, colKey, newValue);
	}

	@Override
	public int hashCode(){
		final T value = getValue();
		return getRowKey().hashCode() * 29 ^ getColKey().hashCode() * 47 + (value == null ? 0 : value.hashCode());
	}

	@Override
	public boolean equals(final Object obj){
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof KeyTableEntry)) return false;
		@SuppressWarnings("unchecked")
		final KeyTableEntry <Rt, Ct, T> other = (KeyTableEntry <Rt, Ct, T>)obj;
		if (!eq(getRowKey(), other.getRowKey())) return false;
		if (!eq(getColKey(), other.getColKey())) return false;
		if (!eq(getValue(), other.getValue())) return false;
		return true;
	}

	@Override
	public String toString(){
		return "{" + rowKey + "," + colKey + ": " + getValue() + "}";
	}
}
