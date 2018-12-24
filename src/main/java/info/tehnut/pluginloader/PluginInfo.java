package info.tehnut.pluginloader;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public final class PluginInfo {

    private final String id;
    private final String initializer;
    private final JsonElement data;

    public PluginInfo(String id, String initializer, JsonElement data) {
        this.id = id;
        this.initializer = initializer;
        this.data = data;
    }

    public <T> T dataAs() {
        return data == null ? null : LoaderUtil.GSON.fromJson(data, new TypeToken<T>(){}.getType());
    }

    public String getId() {
        return id;
    }

    public String getInitializer() {
        return initializer;
    }

    public JsonElement getData() {
        return data;
    }
}
