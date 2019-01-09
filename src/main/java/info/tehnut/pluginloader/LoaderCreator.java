package info.tehnut.pluginloader;

/**
 * The interface used to mark a class as a plugin-loader initializer. These initializers are used to allow implementers
 * to create and customize their {@link PluginLoader} instances before Fabric loads it's own mod initializers ({@link net.fabricmc.api.ModInitializer},
 * {@link net.fabricmc.api.DedicatedServerModInitializer}, and {@link net.fabricmc.api.ClientModInitializer}).
 */
public interface LoaderCreator {

    /**
     * Called just before Fabric begins it's normal mod initialization cycle. Simply create a new {@link PluginLoaderBuilder}
     * instance, customize it's behavior, and finally call {@link PluginLoaderBuilder#build()} to register it.
     */
    void createLoaders();
}
