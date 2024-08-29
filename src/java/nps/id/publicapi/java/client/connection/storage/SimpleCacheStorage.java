package nps.id.publicapi.java.client.connection.storage;

import nps.id.publicapi.java.client.connection.enums.WebSocketClientTarget;

import java.util.*;

public class SimpleCacheStorage {

    private static volatile SimpleCacheStorage instance;
    private static final Object mutex = new Object();

    private final Dictionary<WebSocketClientTarget, Dictionary<String, List<Object>>> _data = new Hashtable<>();

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

    public <T> void setCache(WebSocketClientTarget clientTarget, String dataType, List<T> data, boolean overrideValues) {
        var dataByClient = _data.get(clientTarget);
        if (dataByClient == null) {
            _data.put(clientTarget, new Hashtable<>());
        }

        var dataByType = _data.get(clientTarget).get(dataType);
        if (dataByType == null) {
            _data.get(clientTarget).put(dataType, new ArrayList<>());
        }

        var entry = _data.get(clientTarget).get(dataType);
        if (overrideValues) {
            entry.clear();
        }
        entry.addAll(data);
    }

    public <T> List<T> getFromCache(WebSocketClientTarget clientTarget, String dataType) {
        var dataByClient = _data.get(clientTarget);
        if (dataByClient == null) {
            return Collections.emptyList();
        }

        var dataByType = _data.get(clientTarget).get(dataType);
        if (dataByType == null) {
            return Collections.emptyList();
        }

        return (List<T>)dataByType;
    }
}
