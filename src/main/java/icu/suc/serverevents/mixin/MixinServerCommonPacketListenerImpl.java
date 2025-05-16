package icu.suc.serverevents.mixin;

import icu.suc.serverevents.ServerEvents;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class MixinServerCommonPacketListenerImpl {
    @Shadow @Final protected MinecraftServer server;

    @ModifyArg(method = "disconnect(Lnet/minecraft/network/chat/Component;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/DisconnectionDetails;<init>(Lnet/minecraft/network/chat/Component;)V"), index = 0)
    private Component Player$Kick$MODIFY_REASON(Component component) {
        if ((Object) this instanceof ServerGamePacketListenerImpl serverGamePacketListener) {
            return ServerEvents.Player.Kick.MODIFY_REASON.invoker().modifyKickReason(serverGamePacketListener.player, component);
        }
        return component;
    }

    @Inject(method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At("HEAD"), cancellable = true)
    private void Player$Kick$ALLOW(DisconnectionDetails disconnectionDetails, CallbackInfo ci) {
        if ((Object) this instanceof ServerGamePacketListenerImpl serverGamePacketListener) {
            if (ServerEvents.Player.Kick.ALLOW.invoker().allowKick(serverGamePacketListener.player, disconnectionDetails.reason())) {
                return;
            }
            ci.cancel();
        }
    }
}
