package undrscre.bta_discord_integration.mixin.server;

import net.minecraft.core.net.packet.PacketLogin;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import undrscre.bta_discord_integration.server.DiscordChatRelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = PacketHandlerLogin.class, remap = false)
public class Mixin_NetLoginHandler {
    @Inject(
            method = "doLogin",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/net/PlayerList;sendPacketToAllPlayers(Lnet/minecraft/core/net/packet/Packet;)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    void sendJoinMessage(PacketLogin packet1login, CallbackInfo ci, PlayerServer player) {
        String username = player.username;
        DiscordChatRelay.sendJoinLeaveMessage(username, true);
    }
}
