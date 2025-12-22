package com.ablestrategies.web;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * MapWithDuplicates

 * Implementation of a Map that permits duplicates and null values.
 * Notice that this can lead to some ambiguities which are handled
 * in a predictable manner by this class. For instance, if you remove
 * a key, then all values with that key will be removed. So to remove
 * only a specific value with that key, call removeValue(). You may
 * have to cast it to a MapWithDuplicates in order to allow this.

 * In most situations you should prefer to call getAll() instead of
 * get() because it will return all values that have the specified
 * key, as duplicates are allowed. You may have to cast it to a
 * MapWithDuplicates in order to allow this.

 * Values are stored in a List that is the value V in the Map(K, V).
 * So you cannot retrieve the EntrySet directly because that would
 * violate the guarantee of the Map interface. Instead it returns a
 * null value. To get the actual EntrySet, call getKeyValueList.

 * @param <K> The key - any type
 * @param <V> The value - any type
 */
public class MapWithDuplicates<K, V> implements Map<K, V> {

    /**
     * Here's the underlying collections.
     */
    private final HashMap<K, List<V>> map = new HashMap<>();

    /**
     * Are null values permitted?
     */
    private boolean allowNulls = false;

    /**
     * Allow null values in the collection?
     *
     * @param allowNulls true to allow nulls. default is false
     */
    public void setAllowNulls(boolean allowNulls) {
        this.allowNulls = allowNulls;
    }

    /**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of key-value mappings in this map (duplicates are
     * counted once, no matter how many there are.) For example, if there are
     * 20 values but 10 are indexed by one key and 10 are indexed by another
     * key, then there are only two keys, so this would return 2.
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * @return {@code true} if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified
     * key.  More formally, returns {@code true} if and only if
     * this map contains a mapping for a key {@code k} such that
     * {@code Objects.equals(key, k)}.  (There can be
     * at most one such mapping.)
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified
     * key
     * @throws ClassCastException   if the key is of an inappropriate type for
     *                              this map ({@linkplain Collection##optional-restrictions optional})
     * @throws NullPointerException if the specified key is null and this map
     *                              does not permit null keys ({@linkplain Collection##optional-restrictions optional})
     */
    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the
     * specified value.  More formally, returns {@code true} if and only if
     * this map contains at least one mapping to a value {@code v} such that
     * {@code Objects.equals(value, v)}.  This operation
     * will probably require time linear in the map size for most
     * implementations of the {@code Map} interface.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the
     * specified value
     * @throws ClassCastException   if the value is of an inappropriate type for
     *                              this map ({@linkplain Collection##optional-restrictions optional})
     * @throws NullPointerException if the specified value is null and this
     *                              map does not permit null values ({@linkplain Collection##optional-restrictions optional})
     */
    @Override
    @SuppressWarnings("all")
    public boolean containsValue(Object value) {
        for(K key : map.keySet()) {
            if(map.get(key).contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the first value to which the specified key is mapped,
     * of the first matching value if there are duplicates,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that
     * {@code Objects.equals(key, k)},
     * then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>If this map permits null values, then a return value of
     * {@code null} does not <i>necessarily</i> indicate that the map
     * contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to {@code null}.  The {@link #containsKey
     * containsKey} operation may be used to distinguish these two cases.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped,
     * or the first occurrence if there are duplicates
     * or {@code null} if this map contains no mapping for the key
     * @throws ClassCastException   if the key is of an inappropriate type for
     *                              this map ({@linkplain Collection##optional-restrictions optional})
     * @throws NullPointerException if the specified key is null and this map
     *                              does not permit null keys ({@linkplain Collection##optional-restrictions optional})
     */
    @Override
    public V get(Object key) {
        List<V> list = map.get(key);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Get all values selected by this key.
     * @param key name/key to be found
     * @return a list of all matching values, null if none
     */
    @SuppressWarnings("all")
    public List<V> getAll(Object key) {
        return map.get(key);
    }

    /**
     * Associates the specified value with the specified key in this map
     * (optional operation).  If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.  (A map
     * {@code m} is said to contain a mapping for a key {@code k} if and only
     * if {@link #containsKey(Object) m.containsKey(k)} would return
     * {@code true}.)
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key}.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with {@code key},
     * if the implementation supports {@code null} values.)
     * @throws UnsupportedOperationException if the {@code put} operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the class of the specified key or value
     *                                       prevents it from being stored in this map
     * @throws NullPointerException          if the specified key or value is null
     *                                       and this map does not permit null keys or values
     * @throws IllegalArgumentException      if some property of the specified key
     *                                       or value prevents it from being stored in this map
     */
    @Override
    @SuppressWarnings("all")
    public @Nullable Object put(Object key, Object value) {
        if(value == null && !allowNulls) {
            return null;
        }
        List<V> list = map.get(key);
        Object was = get(key);
        if(list == null) {
            list = new LinkedList<>();
        }
        list.add((V) value);
        map.put((K)key, list);
        return was;
    }

    /**
     * Removes the mapping for a key from this map if it is present
     * (optional operation).   More formally, if this map contains a mapping
     * from key {@code k} to value {@code v} such that
     * {@code Objects.equals(key, k)}, that mapping
     * is removed.  (If there are duplicates, then all of them will be removed.)
     *
     * <p>Returns the value to which this map previously associated the key,
     * or the first match if there were duplciates
     * or {@code null} if the map contained no mapping for the key.
     *
     * <p>If this map permits null values, then a return value of
     * {@code null} does not <i>necessarily</i> indicate that the map
     * contained no mapping for the key; it's also possible that the map
     * explicitly mapped the key to {@code null}.
     *
     * <p>The map will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key}.
     * @throws UnsupportedOperationException if the {@code remove} operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the key is of an inappropriate type for
     *                                       this map ({@linkplain Collection##optional-restrictions optional})
     * @throws NullPointerException          if the specified key is null and this
     *                                       map does not permit null keys ({@linkplain Collection##optional-restrictions optional})
     */
    @Override
    @SuppressWarnings("all")
    public V remove(Object key) {
        Object was = get(key);
        map.remove(key);
        return (V)was;
    }

    /**
     * This removes a specific value from the map. This is useful when
     * there are duplicates and you wish to remove only one of them or
     * certain ones of them. If there is more than one duplicate of the
     * key that also has a cuplicate value, they will be removed as well.
     *
     * @param key the key to look up
     * @param value the value to remove
     * @return number of values deleted
     */
    @SuppressWarnings("all")
    public int removeValue(K key, V value) {
        int count = 0;
        List<V> list = map.get(key);
        if(list == null) {
            return 0;
        }
        for(V v : list) {
            if(v.equals(value)) {
                list.remove(v);
                count++;
            }
        }
        if(list.isEmpty()) {
            map.remove(key);
        }
        return count;
    }

    /**
     * Copies all of the mappings from the specified map to this map
     * (optional operation).  The effect of this call is equivalent to that
     * of calling {@link #put(Object, Object) put(k, v)} on this map once
     * for each mapping from key {@code k} to value {@code v} in the
     * specified map.  The behavior of this operation is undefined if the specified map
     * is modified while the operation is in progress. If the specified map has a defined
     * <a href="SequencedCollection.html#encounter">encounter order</a>,
     * processing of its mappings generally occurs in that order.
     *
     * @param m mappings to be stored in this map
     * @throws UnsupportedOperationException if the {@code putAll} operation
     *                                       is not supported by this map
     * @throws ClassCastException            if the class of a key or value in the
     *                                       specified map prevents it from being stored in this map
     * @throws NullPointerException          if the specified map is null, or if
     *                                       this map does not permit null keys or values, and the
     *                                       specified map contains null keys or values
     * @throws IllegalArgumentException      if some property of a key or value in
     *                                       the specified map prevents it from being stored in this map
     */
    @Override
    @SuppressWarnings("all")
    public void putAll(@NotNull Map m) {
        if(m instanceof MapWithDuplicates) {
            for (Object k : m.keySet()) {
                List<V> list = ((MapWithDuplicates)m).getAll(k);
                for(V v : list) {
                    put(k, v);
                }
            }
        } else {
            for (Object k : m.keySet()) {
                put(k, m.get(k));
            }
        }
    }

    /**
     * Removes all of the mappings from this map (optional operation).
     * The map will be empty after this call returns.
     *
     * @throws UnsupportedOperationException if the {@code clear} operation
     *                                       is not supported by this map
     */
    @Override
    public void clear() {
        map.clear();
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear}
     * operations.  It does not support the {@code add} or {@code addAll}
     * operations.
     *
     * @return a set view of the keys contained in this map
     */
    @Override
    @SuppressWarnings("all")
    public @NotNull Set keySet() {
        return map.keySet();
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own {@code remove} operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Collection.remove}, {@code removeAll},
     * {@code retainAll} and {@code clear} operations.  It does not
     * support the {@code add} or {@code addAll} operations.
     *
     * @return a collection view of the values contained in this map
     */
    @Override
    @SuppressWarnings("all")
    public @NotNull Collection values() {
        List<V> list = new LinkedList<>();
        for(List<V> list2 : map.values()) {
            list.addAll(list2);
        }
        return list;
    }

    /**
     * entrySet() is not supported for this class.
     *
     * @throws UnsupportedOperationException if the {@code clear} operation
     *                                       is not supported by this map
     */
    @Override
    @SuppressWarnings("all")
    public Set<Entry<K, V>> entrySet() {
        throw(new UnsupportedOperationException("MapWithDuplicates.entrySet() not supported"));
    }

    /**
     * Use this instead of entrySet() to retrieve the internal representation.
     *
     * @return a Map of K<List<V>>
     */
    public Map<K, List<V>> getKeyValueList() {
        return map;
    }

    /**
     * Compares the specified object with this map for equality.  Returns
     * {@code true} if the given object is also a map and the two maps
     * represent the same mappings.  More formally, two maps {@code m1} and
     * {@code m2} represent the same mappings if
     * {@code m1.entrySet().equals(m2.entrySet())}.  This ensures that the
     * {@code equals} method works properly across different implementations
     * of the {@code Map} interface.
     *
     * @param o object to be compared for equality with this map
     * @return {@code true} if the specified object is equal to this map
     */
    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    /**
     * Returns the hash code value for this map.  The hash code of a map is
     * defined to be the sum of the hash codes of each entry in the map's
     * {@code entrySet()} view.  This ensures that {@code m1.equals(m2)}
     * implies that {@code m1.hashCode()==m2.hashCode()} for any two maps
     * {@code m1} and {@code m2}, as required by the general contract of
     * {@link Object#hashCode}.
     *
     * @return the hash code value for this map
     * @see Entry#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
