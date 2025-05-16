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

import icu.suc.serverevents.ServerEvents;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Iterator;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Shadow protected abstract void onEffectsRemoved(Collection<MobEffectInstance> collection);

    @Unique private Entity entity;
    @Unique private MobEffectInstance effect;

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z", shift = At.Shift.AFTER), cancellable = true)
    private void LivingEntity$Effect$ADD(MobEffectInstance mobEffectInstance, Entity entity, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (ServerEvents.LivingEntity.Effect.ADD.invoker().addEffect((LivingEntity) (Object) this, mobEffectInstance, entity)) {
            this.entity = entity;
            return;
        }
        cir.setReturnValue(false);
    }

    @SuppressWarnings({"DataFlowIssue"})
    @Redirect(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;update(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean LivingEntity$Effect$OVERRIDE(@NotNull MobEffectInstance instance, MobEffectInstance mobEffectInstance) {
        try {
            return ServerEvents.LivingEntity.Effect.OVERRIDE.invoker().overrideEffect((LivingEntity) (Object) this, instance, mobEffectInstance, entity, instance.update(mobEffectInstance));
        } finally {
            entity = null;
        }
    }

    @Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;tickServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/lang/Runnable;)Z"))
    private boolean LivingEntity$Effect$REMOVE(@NotNull MobEffectInstance instance, ServerLevel serverLevel, LivingEntity livingEntity, Runnable runnable) {
        if (instance.tickServer(serverLevel, livingEntity, runnable)) {
            return true;
        }
        effect = instance;
        return false;
    }

    @Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V"))
    private void LivingEntity$Effect$REMOVE(Iterator<Holder<MobEffectInstance>> instance) {
        if (effect != null) {
            ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect((LivingEntity) (Object) this, effect);
        }
        instance.remove();
    }

    @Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectsRemoved(Ljava/util/Collection;)V"))
    private void LivingEntity$Effect$REMOVE(LivingEntity instance, Collection<MobEffectInstance> collection) {
        if (effect == null) {
            onEffectsRemoved(collection);
            return;
        }
        try {
            if (ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect(instance, effect)) {
                onEffectsRemoved(collection);
            }
        } finally {
            effect = null;
        }
    }
}
