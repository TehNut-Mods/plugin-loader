package info.tehnut.pluginloader;

import net.minecraft.util.ActionResult;

public interface ValidationStrategy {

    /**
     * Determines whether this plugin is valid.
     *
     * If the plugin is valid, return {@link ActionResult#SUCCESS}.
     *
     * If the plugin is not valid, you may either return {@link ActionResult#PASS} to just skip over the plugin or
     * {@link ActionResult#FAILURE} to throw an {@link PluginException}.
     *
     * @param pluginClass The plugin class
     * @param container The plugin container
     *
     * @return whether this plugin is valid or not.
     */
    ActionResult validate(Class<?> pluginClass, PluginContainer container);

    static ValidationStrategy instanceOf(Class<?> clazz) {
        return instanceOf(clazz, false);
    }

    static ValidationStrategy instanceOf(Class<?> clazz, boolean hardFail) {
        return ((pluginClass, info) -> {
            return clazz.isAssignableFrom(pluginClass) ? ActionResult.SUCCESS : hardFail ? ActionResult.FAILURE : ActionResult.PASS;
        });
    }
}
