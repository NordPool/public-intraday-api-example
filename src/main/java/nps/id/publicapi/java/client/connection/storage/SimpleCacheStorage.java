package nps.id.publicapi.java.client.connection.storage;

import java.util.*;

public class SimpleCacheStorage {


    private final HashMap<String, List<Object>> data = new HashMap<>();

    public SimpleCacheStorage() {}

    public <T> void setCache(String dataType, List<T> data, boolean overrideValues) {
        var dataByType = this.data.get(dataType);
        if (dataByType == null) {
            this.data.put(dataType, new ArrayList<>());
        }

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

        return (List<T>)dataByType;
    }
}
