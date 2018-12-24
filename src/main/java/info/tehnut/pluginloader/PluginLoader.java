package info.tehnut.pluginloader;

import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.ModContainer;
import net.fabricmc.loader.language.LanguageAdapter;
import net.fabricmc.loader.language.LanguageAdapterException;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiConsumer;

public final class PluginLoader {

    static final List<PluginLoader> LOADERS = new ArrayList<>();
    static final Logger LOGGER = LogManager.getLogger("PluginLoader");
    static final PluginLoader PRIMARY_LOADER = new PluginLoaderBuilder("pluginloader")
            .withDiscoverer((owningMod, plugins) -> FabricLoader.INSTANCE.getMods().forEach(modContainer -> {
                List<PluginContainer> loaders = LoaderUtil.getLoadersForMod(modContainer);
                if (loaders != null)
                    plugins.put(modContainer.getInfo().getId(), loaders);
            }))
            .withValidator(ValidationStrategy.instanceOf(LoaderCreator.class))
            .withInitializer((pluginClass, container) -> {
                try {
                    LoaderCreator loaderCreator = (LoaderCreator) container.getOwner().getAdapter().createInstance(pluginClass, new LanguageAdapter.Options());
                    loaderCreator.createLoaders();
                } catch (LanguageAdapterException e) {
                    e.printStackTrace();
                }
            })
            .build();
    static {
        LOADERS.remove(PRIMARY_LOADER);
    }

    private final ModContainer owner;
    private final ValidationStrategy validationStrategy;
    private final BiConsumer<ModContainer, Map<String, List<PluginContainer>>> discoverer;
    private final BiConsumer<Class<?>, PluginContainer> initializer;
    private final Runnable postCall;
    private final Map<String, List<PluginContainer>> plugins;

    PluginLoader(ModContainer owner, BiConsumer<ModContainer, Map<String, List<PluginContainer>>> discoverer, ValidationStrategy validationStrategy, BiConsumer<Class<?>, PluginContainer> initializer, Runnable postCall) {
        this.owner = owner;
        this.validationStrategy = validationStrategy;
        this.discoverer = discoverer;
        this.initializer = initializer;
        this.postCall = postCall;
        this.plugins = new HashMap<>();

        LOADERS.add(this);
    }

    public void discover() {
        discoverer.accept(owner, plugins);
    }

    public void initialize() {
        plugins.forEach((modId, containers) -> {
            containers.forEach(container -> {
                try {
                    Class<?> pluginClass = Class.forName(container.getInfo().getInitializer());
                    ActionResult result = validationStrategy.validate(pluginClass, container);
                    if (result == ActionResult.PASS)
                        return;

                    if (result == ActionResult.FAILURE)
                        return;

                    initializer.accept(pluginClass, container);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        });

        postCall.run();
    }

    public ModContainer getOwner() {
        return owner;
    }

    public List<PluginContainer> getPlugins(String modId) {
        return plugins.getOrDefault(modId, Collections.emptyList());
    }

    public PluginContainer getPlugin(String modId, String pluginId) {
        return getPlugins(modId).stream().filter(p -> p.getInfo().getId().equals(pluginId)).findFirst().orElse(null);
    }
}
