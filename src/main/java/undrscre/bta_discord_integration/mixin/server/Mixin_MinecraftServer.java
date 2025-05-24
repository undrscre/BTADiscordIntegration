package undrscre.bta_discord_integration.mixin.server;

import net.minecraft.server.MinecraftServer;
import undrscre.bta_discord_integration.server.DiscordChatRelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, remap = false)
public class Mixin_MinecraftServer {

    @Inject(
            method = "initiateShutdown",
            at = @At("HEAD")
    )
    public void sendStopMessage(CallbackInfo ci) {
        DiscordChatRelay.sendServerStoppedMessage();
    }

}
