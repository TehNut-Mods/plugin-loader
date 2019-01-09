package info.tehnut.pluginloader.mixin.client;

import info.tehnut.pluginloader.StateHandler;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public final class MixinMinecraftClient {

    private MixinMinecraftClient() {}

    @Inject(method = "init", at = @At("HEAD"))
    public void handlePlugins(CallbackInfo info) {
        StateHandler.pluginDiscovery();
        StateHandler.pluginInitialization();
    }
}
