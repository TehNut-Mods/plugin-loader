package info.tehnut.pluginloader;

import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.ModContainer;
import net.fabricmc.loader.language.LanguageAdapter;
import net.minecraft.util.ActionResult;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class PluginLoaderBuilder {

    private static final BiConsumer<ModContainer, Map<String, List<PluginContainer>>> DEFAULT_DISCOVERER = (loaderMod, plugins) -> {
        FabricLoader.INSTANCE.getMods().forEach(modContainer -> {
            List<PluginContainer> loaders = LoaderUtil.getPluginsForMod(loaderMod.getInfo().getId(), modContainer);
            if (loaders != null)
                plugins.put(modContainer.getInfo().getId(), loaders);
        });
    };
    private static final ValidationStrategy DEFAULT_VALIDATOR = (p, i) -> ActionResult.SUCCESS;
    private static final BiConsumer<Class<?>, PluginContainer> DEFAULT_INITIALIZER = (aClass, pluginContainer) -> {
        try {
            pluginContainer.getOwner().getAdapter().createInstance(aClass, new LanguageAdapter.Options());
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
    private static final Runnable DEFAULT_POST = () -> {};

    private final ModContainer owner;
    private boolean invalid;
    private BiConsumer<ModContainer, Map<String, List<PluginContainer>>> discoverer = DEFAULT_DISCOVERER;
    private ValidationStrategy validationStrategy = DEFAULT_VALIDATOR;
    private BiConsumer<Class<?>, PluginContainer> initializer = DEFAULT_INITIALIZER;
    private Runnable postCall = DEFAULT_POST;

    public PluginLoaderBuilder(String modid) {
        this.owner = FabricLoader.INSTANCE.getMods().stream().filter(m -> m.getInfo().getId().equals(modid)).findFirst().orElse(null);

        if (owner == null)
            this.invalid = true;
    }

    public PluginLoaderBuilder withDiscoverer(BiConsumer<ModContainer, Map<String, List<PluginContainer>>> discoverer) {
        this.discoverer = discoverer;
        return this;
    }

    public PluginLoaderBuilder withValidator(ValidationStrategy validator) {
        this.validationStrategy = validator;
        return this;
    }

    public PluginLoaderBuilder withInitializer(BiConsumer<Class<?>, PluginContainer> initializer) {
        this.initializer = initializer;
        return this;
    }

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
