package info.tehnut.pluginloader.mixin;

import info.tehnut.pluginloader.StateHandler;
import net.fabricmc.loader.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FabricLoader.class)
public final class MixinFabricLoader {

    private MixinFabricLoader() {}

    @Inject(method = "freeze", at = @At("TAIL"), remap = false)
    public void loaderDiscovery(CallbackInfo callbackInfo) {
        StateHandler.loaderDiscovery();
    }
}
