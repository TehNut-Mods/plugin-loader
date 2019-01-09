package info.tehnut.pluginloader;

public final class StateHandler {

    private StateHandler() {}

    public static void loaderDiscovery() {
        PluginLoader.LOGGER.info("Discovering plugin loaders");
        PluginLoader.PRIMARY_LOADER.discover();
        PluginLoader.LOGGER.info("Discovered {} plugin loaders", PluginLoader.LOADERS.size());
        PluginLoader.LOGGER.info("Initializing plugin loaders");
        PluginLoader.PRIMARY_LOADER.initialize();
    }

    public static void pluginDiscovery() {
        PluginLoader.LOGGER.info("Discovering plugins");
        PluginLoader.LOADERS.forEach(PluginLoader::discover);
    }

    public static void pluginInitialization() {
        PluginLoader.LOGGER.info("Initializing plugins");
        PluginLoader.LOADERS.forEach(PluginLoader::initialize);
    }
}
