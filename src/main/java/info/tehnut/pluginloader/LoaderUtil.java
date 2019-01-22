package info.tehnut.pluginloader;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.fabricmc.loader.ModContainer;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

final class LoaderUtil {

    static final JsonParser PARSER = new JsonParser();
    static final Gson GSON = new Gson();

    private LoaderUtil() {}

    public static List<PluginContainer> getLoadersForMod(ModContainer modContainer) {
        try {
            if (modContainer.getOriginFile().isDirectory()) {
                File loaderJson = new File(modContainer.getOriginFile(), "pluginloader.json");
                if (!loaderJson.exists())
                    return null;

                try (FileReader reader = new FileReader(loaderJson)) {
                    return parseContainers(reader, modContainer);
                }
            } else {
                JarFile jarFile = new JarFile(modContainer.getOriginFile());
                ZipEntry entry = jarFile.getEntry("pluginloader.json");
                return parseZipEntry(jarFile, entry, modContainer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<PluginContainer> getPluginsForMod(String pluginOwner, ModContainer modContainer) {
        try {
            if (modContainer.getOriginFile().isDirectory()) {
                File pluginFile = new File(modContainer.getOriginFile(), "plugins/" + pluginOwner + ".plugin.json");
                if (!pluginFile.exists())
                    return null;

                try (FileReader reader = new FileReader(pluginFile)) {
                    return parseContainers(reader, modContainer);
                }
            } else {
                JarFile jarFile = new JarFile(modContainer.getOriginFile());
                ZipEntry entry = jarFile.getEntry("plugins/" + pluginOwner + ".plugin.json");
                return parseZipEntry(jarFile, entry, modContainer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<PluginContainer> parseContainers(Reader reader, ModContainer modContainer) {
        List<PluginInfo> plugins;
        JsonElement element = PARSER.parse(reader);
        if (element.isJsonArray()) {
            plugins = Lists.newArrayList();
            element.getAsJsonArray().forEach(e -> plugins.add(parsePluginInfo(e)));
        } else {
            plugins = Collections.singletonList(parsePluginInfo(element));
        }

        return plugins.stream().map(i -> new PluginContainer(modContainer, i)).collect(Collectors.toList());
    }

    // FIXME - Wait for GSON to update so we can use a type adapter
    private static PluginInfo parsePluginInfo(JsonElement element) {
        if (!element.isJsonObject())
            throw new JsonParseException("PluginInfo must be an object");

        JsonObject json = element.getAsJsonObject();

        String id = json.getAsJsonPrimitive("id").getAsString();
        String initializer = json.getAsJsonPrimitive("initializer").getAsString();
        JsonElement data = json.has("data") ? json.get("data") : JsonNull.INSTANCE;
        return new PluginInfo(id, initializer, data);
    }

    private static List<PluginContainer> parseZipEntry(JarFile jarFile, ZipEntry entry, ModContainer container) throws IOException {
        if (entry == null)
            return null;

        InputStream inputStream = jarFile.getInputStream(entry);
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            return parseContainers(reader, container);
        }
    }
}
