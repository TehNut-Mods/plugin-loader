package info.tehnut.pluginloader;

import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.ModContainer;
import net.minecraft.util.ActionResult;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class PluginLoaderBuilder {

    //<editor-fold desc="Default Behaviors">
    private static final BiConsumer<ModContainer, Map<String, List<PluginContainer>>> DEFAULT_DISCOVERER = (loaderMod, plugins) -> {
        FabricLoader.INSTANCE.getModContainers().forEach(modContainer -> {
            List<PluginContainer> loaders = LoaderUtil.getPluginsForMod(loaderMod.getInfo().getId(), modContainer);
            if (loaders != null)
                plugins.put(modContainer.getInfo().getId(), loaders);
        });
    };
    private static final ValidationStrategy DEFAULT_VALIDATOR = (p, i) -> ActionResult.SUCCESS;
    private static final BiConsumer<Class<?>, PluginContainer> DEFAULT_INITIALIZER = (aClass, pluginContainer) -> {};
    private static final Runnable DEFAULT_POST = () -> {};
    //</editor-fold>

    private final ModContainer owner;
    private boolean invalid;
    private BiConsumer<ModContainer, Map<String, List<PluginContainer>>> discoverer = DEFAULT_DISCOVERER;
    private ValidationStrategy validationStrategy = DEFAULT_VALIDATOR;
    private BiConsumer<Class<?>, PluginContainer> initializer = DEFAULT_INITIALIZER;
    private Runnable postCall = DEFAULT_POST;

    /**
     * Creates a new PluginLoaderBuilder instance to customize the behavior of this loader. The passed modid is used by
     * the default {@link #discoverer} to discover plugins to be loaded. It must be an actively loaded modid. These plugins should be <code>$modid.plugin.json</code>
     * and located in a <code>/plugins/</code> directory at the root of the jar.
     *
     * @param modid The mod that owns this loader
     */
    public PluginLoaderBuilder(String modid) {
        this.owner = FabricLoader.INSTANCE.getModContainers()
                .stream()
                .filter(m -> m.getInfo().getId().equals(modid))
                .findFirst()
                .orElse(null);

        if (owner == null)
            this.invalid = true;
    }

    /**
     * Plugin discovery behavior. This handler is responsible for looking through mod jars and finding potential plugins
     * to be loaded by this loader.
     *
     * Discovery takes place before {@link net.fabricmc.api.ModInitializer#onInitialize()} has been sent out.
     *
     * In the majority of cases, this can be left as {@link #DEFAULT_DISCOVERER}.
     *
     * @param discoverer The discoverer behavior
     * @return The builder instance for chaining
     */
    public PluginLoaderBuilder withDiscoverer(BiConsumer<ModContainer, Map<String, List<PluginContainer>>> discoverer) {
        this.discoverer = discoverer;
        return this;
    }

    /**
     * The strategy to use when deciding if a discovered plugin is valid or not. At this point, the plugin class has not
     * been loaded yet.
     *
     * The most common use-cases for this would be making sure the class inherits from an API class or making sure a mod
     * requested by the plugin via {@link PluginInfo#getData()} is loaded.
     *
     * Validation takes place before {@link net.fabricmc.api.ModInitializer#onInitialize()} has been sent out.
     *
     * In the majority of cases, this should be changed from {@link #DEFAULT_VALIDATOR} as that considers all potential
     * plugins as valid.
     *
     * @param validator The validation behavior
     * @return The builder instance for chaining
     */
    public PluginLoaderBuilder withValidator(ValidationStrategy validator) {
        this.validationStrategy = validator;
        return this;
    }

    /**
     * Plugin initialization behavior. Behavior is dependent upon the API implementation.
     *
     * Initialization takes place after {@link net.fabricmc.api.ModInitializer#onInitialize()} has been sent out.
     *
     * This should always be changed from {@link #DEFAULT_INITIALIZER} as that is just a dummy handler.
     *
     * @param initializer The initialization behavior
     * @return The builder instance for chaining
     */
    public PluginLoaderBuilder withInitializer(BiConsumer<Class<?>, PluginContainer> initializer) {
        this.initializer = initializer;
        return this;
    }

    /**
     * Behavior that should be performed after plugin initialization has taken place.
     *
     * This is called once at the very end of plugin initialization.
     *
     * @param runnable The post initialization behavior
     * @return The builder instance for chaining
     */
    public PluginLoaderBuilder withPostCall(Runnable runnable) {
        this.postCall = runnable;
        return this;
    }

    public PluginLoader build() {
        if (invalid)
            throw new RuntimeException(new PluginException("Could not locate owning mod container"));

        return new PluginLoader(owner, discoverer, validationStrategy, initializer, postCall);
    }
}
