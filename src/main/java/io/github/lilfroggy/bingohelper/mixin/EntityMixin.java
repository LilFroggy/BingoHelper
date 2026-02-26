package io.github.lilfroggy.bingohelper.mixin;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.lilfroggy.bingohelper.util.render.GlowingEntities;

@Mixin(Entity.class)
public class EntityMixin {
    
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (GlowingEntities.contains(entity)) cir.setReturnValue(true);
    }
}