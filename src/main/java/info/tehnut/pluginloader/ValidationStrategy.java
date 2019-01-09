package info.tehnut.pluginloader;

import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.minecraft.util.ActionResult;
import org.objectweb.asm.ClassReader;

import java.io.IOException;

public interface ValidationStrategy {

    /**
     * Determines whether this plugin is valid. At this point, the plugin class has not yet been loaded.
     *
     * If the plugin is valid, return {@link ActionResult#SUCCESS}.
     *
     * If the plugin is not valid, you may either return {@link ActionResult#PASS} to just skip over the plugin or
     * {@link ActionResult#FAILURE} to throw a {@link PluginException}.
     *
     * @param pluginClass The plugin class
     * @param container   The plugin container
     * @return whether this plugin is valid or not.
     */
    ActionResult validate(String pluginClass, PluginContainer container);

    /**
     * Appends another condition to the validation tree. The original is always checked first this tree will short circuit
     * on the first {@link ActionResult#PASS} or {@link ActionResult#FAILURE}.
     *
     * @param other The next condition for the validation tree
     * @return A validator that wraps both the original as well as the new condition
     */
    default ValidationStrategy and(ValidationStrategy other) {
        return (pluginClass, container) -> {
            ActionResult first = validate(pluginClass, container);
            if (first == ActionResult.PASS || first == ActionResult.FAILURE)
                return first;

            return other.validate(pluginClass, container);
        };
    }

    /**
     * @see #hasInterface(Class, boolean)
     *
     * @param clazz The interface class
     * @return A validator that checks if the plugin class implements a given interface.
     */
    static ValidationStrategy hasInterface(Class<?> clazz) {
        return hasInterface(clazz, false);
    }

    /**
     * Checks if the plugin class implements an interface without loading the plugin class.
     *
     * @param clazz    The interface class
     * @param hardFail If this validator should throw a {@link PluginException} if the interface is not implemented
     * @return A validator that checks if the plugin class implements a given interface.
     */
    static ValidationStrategy hasInterface(Class<?> clazz, boolean hardFail) {
        return ((pluginClass, info) -> {
            if (clazz.getName().equals(pluginClass))
                return ActionResult.SUCCESS;

            String className = pluginClass.replace(".", "/") + ".class";
            try {
                ClassReader reader = new ClassReader(FabricLauncherBase.getLauncher().getResourceAsStream(className));
                boolean flag = false;

                for (String iface : reader.getInterfaces())
                    if (iface.equals(clazz.getName().replace(".", "/")))
                        flag = true;

                if (flag)
                    return ActionResult.SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return hardFail ? ActionResult.FAILURE : ActionResult.PASS;
        });
    }

    /**
     * @deprecated Use {@link #hasInterface(Class)}.
     */
    @Deprecated
    static ValidationStrategy instanceOf(Class<?> clazz) {
        return hasInterface(clazz);
    }

    /**
     * @deprecated Use {@link #hasInterface(Class, boolean)}.
     */
    @Deprecated
    static ValidationStrategy instanceOf(Class<?> clazz, boolean hardFail) {
        return hasInterface(clazz, hardFail);
    }
}
