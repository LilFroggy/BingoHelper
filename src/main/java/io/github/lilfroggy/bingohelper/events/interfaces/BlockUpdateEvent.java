package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockUpdateEvent {
    void onBlockUpdate(BlockPos pos, BlockState old, BlockState updated);
}