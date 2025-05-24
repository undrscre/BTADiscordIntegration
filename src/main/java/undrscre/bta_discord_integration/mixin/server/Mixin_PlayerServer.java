package undrscre.bta_discord_integration.mixin.server;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import undrscre.bta_discord_integration.server.DiscordChatRelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mob.class, remap = false)
public abstract class Mixin_PlayerServer {
    @Shadow public abstract String getDeathMessage(Entity entity);
    @Inject(
            method = "onDeath",
            at = @At("RETURN")
    )
    void processDeathMessage(Entity entity, CallbackInfo ci) {
        if((Mob)((Object)this) instanceof Player) {
            String message = getDeathMessage(entity).replaceAll("§.", "");
            DiscordChatRelay.sendDeathMessage(message);
        }
    }
}
