package io.github.lilfroggy.bingohelper.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.lilfroggy.bingohelper.events.Events;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @Inject(method = "setBlockState", at = @At("HEAD"))
    private void onBlockChange(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir) {
        BlockState old = this.getBlockState(pos);
        if (old != state) Events.BLOCK_UPDATE.invoke(listener -> listener.onBlockUpdate(pos, old, state));
    }
}