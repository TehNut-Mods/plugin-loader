package info.tehnut.pluginloader.mixin.server;

import info.tehnut.pluginloader.StateHandler;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public final class MixinMinecraftDedicatedServer {

    private MixinMinecraftDedicatedServer() {}

    @Inject(method = "setupServer", at = @At("HEAD"))
    public void setupServer(CallbackInfoReturnable<Boolean> info) {
        StateHandler.pluginDiscovery();
        StateHandler.pluginInitialization();
    }
}
