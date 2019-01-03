package info.tehnut.pluginloader;

import net.fabricmc.loader.launch.common.FabricLauncherBase;
import org.objectweb.asm.ClassReader;
import net.minecraft.util.ActionResult;

import java.io.IOException;

public interface ValidationStrategy {

    /**
     * Determines whether this plugin is valid. At this point, the plugin class has not yet been loaded.
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
    ActionResult validate(String pluginClass, PluginContainer container);

    default ValidationStrategy and(ValidationStrategy other) {
        return (pluginClass, container) -> {
            ActionResult first = validate(pluginClass, container);
            if (first == ActionResult.PASS || first == ActionResult.FAILURE)
                return first;

            return other.validate(pluginClass, container);
        };
    }

    static ValidationStrategy instanceOf(Class<?> clazz) {
        return instanceOf(clazz, false);
    }

    static ValidationStrategy instanceOf(Class<?> clazz, boolean hardFail) {
        return ((pluginClass, info) -> {
            if (clazz.getName().equals(pluginClass))
                return ActionResult.SUCCESS;

            String className = pluginClass.replace(".", "/") + ".class";
            try {
                ClassReader reader = new ClassReader(FabricLauncherBase.getLauncher().getResourceAsStream(className));
                boolean flag = false;

                for (String iface : reader.getInterfaces())
                    if (iface.equals(clazz.getName()))
                        flag = true;

                if (flag)
                    return ActionResult.SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return hardFail ? ActionResult.FAILURE : ActionResult.PASS;
        });
    }
}
