package icu.suc.serverevents.mixin;

import icu.suc.mc.serverevents.ServerEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {
    @Shadow public ServerPlayer player;

    @ModifyArg(method = "removePlayerFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"), index = 0)
    private Component Player$MODIFY_LEAVE_MESSAGE(Component component) {
        return ServerEvents.Player.MODIFY_LEAVE_MESSAGE.invoker().modifyLeaveMessage(player, component);
    }
}
