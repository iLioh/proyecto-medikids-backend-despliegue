package com.medikids.medikids.utils.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache<K, V> {
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public SimpleCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return null;
        if (System.currentTimeMillis() > entry.expiry) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis));
    }

    public void invalidate(K key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    private record CacheEntry<V>(V value, long expiry) {}
}
