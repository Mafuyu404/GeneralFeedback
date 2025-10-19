package com.sighs.generalfeedback.loader;

import com.sighs.generalfeedback.init.Entry;

import java.util.HashMap;

public class EntryCache {
    public static final HashMap<String, Entry> UnitMapCache = new HashMap<>();

    public static void putEntry(Entry unit) {
        UnitMapCache.put(unit.id, unit);
    }

    public static void clearCache() {
        UnitMapCache.clear();
    }

    public static void loadAllRule() {
        clearCache();
        EntryLoader.loadAll().forEach(EntryCache::putEntry);
    }
}