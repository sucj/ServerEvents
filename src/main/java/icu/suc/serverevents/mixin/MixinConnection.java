/*
 * MIT License
 *
 * Copyright (c) 2025 sucj
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package icu.suc.serverevents.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import icu.suc.serverevents.ServerEvents;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class MixinConnection {
    @Shadow @Nullable private volatile PacketListener packetListener;

    @ModifyVariable(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketListener;shouldHandleMessage(Lnet/minecraft/network/protocol/Packet;)Z"), argsOnly = true)
    private @NotNull Packet<?> Connection$Receive$MODIFY(Packet<?> packet, @Local PacketListener packetListener) {
        return ServerEvents.Connection.Receive.MODIFY.invoker().modifyReceive(packetListener, packet);
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketListener;shouldHandleMessage(Lnet/minecraft/network/protocol/Packet;)Z"), cancellable = true)
    private void Connection$Receive$ALLOW(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci, @Local PacketListener packetListener) {
        if (ServerEvents.Connection.Receive.ALLOW.invoker().allowReceive(packetListener, packet)) {
            return;
        }
        ci.cancel();
    }

    @ModifyVariable(method = "sendPacket", at = @At("HEAD"), argsOnly = true)
    private @NotNull Packet<?> Connection$Send$MODIFY(Packet<?> packet) {
        return ServerEvents.Connection.Send.MODIFY.invoker().modifySend(this.packetListener, packet);
    }

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void Connection$Send$ALLOW(Packet<?> packet, PacketSendListener packetSendListener, boolean bl, CallbackInfo ci) {
        if (ServerEvents.Connection.Send.ALLOW.invoker().allowSend(this.packetListener, packet)) {
            return;
        }
        ci.cancel();
    }
}
