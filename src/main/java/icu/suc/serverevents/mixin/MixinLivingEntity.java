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
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Shadow protected abstract void onEffectsRemoved(Collection<MobEffectInstance> collection);

    @Shadow @Final private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z", shift = At.Shift.AFTER), cancellable = true)
    private void LivingEntity$Effect$ADD(MobEffectInstance mobEffectInstance, Entity entity, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (ServerEvents.LivingEntity.Effect.ADD.invoker().addEffect((LivingEntity) (Object) this, mobEffectInstance, entity)) {
            return;
        }
        cir.setReturnValue(false);
    }

    @Redirect(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;update(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean LivingEntity$Effect$OVERRIDE(@NotNull MobEffectInstance instance, MobEffectInstance mobEffectInstance, @Local(argsOnly = true) Entity entity) {
        return ServerEvents.LivingEntity.Effect.OVERRIDE.invoker().overrideEffect((LivingEntity) (Object) this, instance, mobEffectInstance, entity, instance.update(mobEffectInstance));
    }

    @Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V"))
    private void LivingEntity$Effect$REMOVE(@NotNull Iterator<Holder<MobEffectInstance>> instance, @Local MobEffectInstance mobEffectInstance) {
        ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect((LivingEntity) (Object) this, mobEffectInstance);
        instance.remove();
    }

    @Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectsRemoved(Ljava/util/Collection;)V"))
    private void LivingEntity$Effect$REMOVE(LivingEntity instance, Collection<MobEffectInstance> collection, @Local MobEffectInstance mobEffectInstance) {
        if (ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect(instance, mobEffectInstance)) {
            onEffectsRemoved(collection);
        }
    }

    @SuppressWarnings({"DataFlowIssue"})
    @Inject(method = "triggerOnDeathMobEffects", at = @At("HEAD"), cancellable = true)
    private void LivingEntity$Effect$REMOVE(ServerLevel serverLevel, Entity.RemovalReason removalReason, CallbackInfo ci) {
        var iterator = this.activeEffects.entrySet().iterator();
        while (iterator.hasNext()) {
            var effect = iterator.next().getValue();
            if (ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect((LivingEntity) (Object) this, effect)) {
                effect.onMobRemoved(serverLevel, (LivingEntity) (Object) this, removalReason);
                iterator.remove();
            }
        }
        ci.cancel();
    }

    @Inject(method = "removeAllEffects", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap(Ljava/util/Map;)Ljava/util/HashMap;"), cancellable = true)
    private void LivingEntity$Effect$REMOVE(CallbackInfoReturnable<Boolean> cir) {
        var iterator = this.activeEffects.entrySet().iterator();
        var toRemove = new LinkedList<MobEffectInstance>();
        while (iterator.hasNext()) {
            var effect = iterator.next().getValue();
            if (ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect((LivingEntity) (Object) this, effect)) {
                iterator.remove();
                toRemove.add(effect);
            }
        }
        this.onEffectsRemoved(toRemove);
        cir.setReturnValue(!toRemove.isEmpty());
    }

    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"), cancellable = true)
    private void LivingEntity$Effect$REMOVE(Holder<MobEffect> holder, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect((LivingEntity) (Object) this, this.activeEffects.get(holder))) {
            cir.setReturnValue(this.activeEffects.remove(holder));
            return;
        }
        cir.setReturnValue(null);
    }
}
