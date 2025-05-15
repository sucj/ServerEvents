package icu.suc.serverevents.mixin;

import icu.suc.serverevents.ServerEvents;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {
    @Unique
    private ServerPlayer serverPlayer;

    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    private void Player$MODIFY_JOIN_MESSAGE(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        this.serverPlayer = serverPlayer;
    }

    @ModifyArg(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"), index = 0)
    private Component Player$MODIFY_JOIN_MESSAGE(Component component) {
        try {
            return ServerEvents.Player.MODIFY_JOIN_MESSAGE.invoker().modifyJoinMessage(serverPlayer, component);
        } finally {
            serverPlayer = null;
        }
    }
}
