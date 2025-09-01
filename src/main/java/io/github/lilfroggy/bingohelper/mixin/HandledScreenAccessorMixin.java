package io.github.lilfroggy.bingohelper.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessorMixin {
    @Accessor("x")
    int getScreenX();

    @Accessor("y")
    int getScreenY();
    
    @Accessor("backgroundWidth")
    int getBackgroundWidth();
    
    @Accessor("backgroundHeight")
    int getBackgroundHeight();
}