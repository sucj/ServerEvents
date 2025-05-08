package icu.suc.mc.serverevents.mixin;

import icu.suc.mc.serverevents.ServerEvents;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerList {

    @Unique
    private static final ThreadLocal<ServerPlayer> currentServerPlayer = new ThreadLocal<>();

    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    private void callPlayerJoinEvent(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        currentServerPlayer.set(serverPlayer);
    }

    @ModifyArg(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"), index = 0)
    private Component callPlayerJoinEvent(Component component) {
        var player = currentServerPlayer.get();

        if (player == null) {
            return component;
        }

        var message = new MutableObject<>(component);

        ServerEvents.Player.JOIN.invoker().onJoin(player, message);

        currentServerPlayer.remove();

        return message.getValue();
    }
}
