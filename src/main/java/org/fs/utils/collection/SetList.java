package org.fs.utils.collection;

import java.util.List;
import java.util.Set;

import org.fs.utils.collection.set.IndexedSet;

/**
 * Implements both {@link Set} and {@link List} interfaces. As such, it violates the contract of
 * {@code equals} and {@code hashCode}: it is mutually equal with appropirate {@code List}'s and
 * {@code Set}'s, and uses {@code hashCode()} for {@link Set#hashCode() Set}.
 *
 * @author FS
 * @param <E>
 *            the type of elements in this collection
 */
public interface SetList<E> extends IndexedSet <E>, List <E> {}

