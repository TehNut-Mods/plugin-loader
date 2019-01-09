package info.tehnut.pluginloader;

import net.fabricmc.loader.ModContainer;

public final class PluginContainer {

    private final ModContainer owner;
    private final PluginInfo info;

    PluginContainer(ModContainer owner, PluginInfo info) {
        this.owner = owner;
        this.info = info;
    }

    public ModContainer getOwner() {
        return owner;
    }

    public PluginInfo getInfo() {
        return info;
    }
}
