package nps.id.publicapi.java.client.connection.storage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCacheStorage {


    private final Map<String, List<Object>> data = new ConcurrentHashMap<>();

    public SimpleCacheStorage() {}

    public <T> void setCache(String dataType, List<T> data, boolean overrideValues) {
        this.data.computeIfAbsent(dataType, k -> new ArrayList<>());

        var entry = this.data.get(dataType);
        if (overrideValues) {
            entry.clear();
        }
        entry.addAll(data);
    }

    public <T> List<T> getFromCache(String dataType) {
        var dataByType = data.get(dataType);
        if (dataByType == null) {
            return Collections.emptyList();
        }

        return (List<T>) dataByType;
    }
}
