package info.tehnut.pluginloader;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public final class PluginInfo {

    private final String id;
    private final String initializer;
    private final JsonElement data;

    PluginInfo(String id, String initializer, JsonElement data) {
        this.id = id;
        this.initializer = initializer;
        this.data = data;
    }

    public <T> T dataAs() throws JsonSyntaxException {
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
