package nps.id.publicapi.java.client.connection.storage;

import java.util.*;

public class SimpleCacheStorage {

    private static volatile SimpleCacheStorage instance;
    private static final Object mutex = new Object();

    private final Dictionary<String, List<Object>> _data = new Hashtable<>();

    private SimpleCacheStorage() {}

    public static SimpleCacheStorage getInstance() {
        var result = instance;

        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new SimpleCacheStorage();
            }
        }

        return result;
    }

    public <T> void setCache(String dataType, List<T> data, boolean overrideValues) {
        var dataByType = _data.get(dataType);
        if (dataByType == null) {
            _data.put(dataType, new ArrayList<>());
        }

        var entry = _data.get(dataType);
        if (overrideValues) {
            entry.clear();
        }
        entry.addAll(data);
    }

    public <T> List<T> getFromCache(String dataType) {
        var dataByType = _data.get(dataType);
        if (dataByType == null) {
            return Collections.emptyList();
        }

        return (List<T>)dataByType;
    }
}
