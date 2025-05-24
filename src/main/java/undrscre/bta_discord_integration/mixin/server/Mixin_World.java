package undrscre.bta_discord_integration.mixin.server;

import net.minecraft.core.world.World;
import undrscre.bta_discord_integration.server.DiscordChatRelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = World.class, remap = false)
public class Mixin_World {
    @Inject(
            method = "wakeUpAllPlayers",
            at = @At("RETURN")
    )
    public void sendServerSleepMessage(CallbackInfo ci) {
        DiscordChatRelay.sendServerSleepMessage();
    }
}