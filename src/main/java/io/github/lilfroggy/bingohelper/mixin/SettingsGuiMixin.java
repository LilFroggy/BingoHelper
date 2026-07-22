package io.github.lilfroggy.bingohelper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIContainer;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.AdditiveConstraint;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.gui.ExpandingClickEffect;
import gg.essential.vigilance.gui.SettingsGui;
import gg.essential.vigilance.gui.VigilancePalette;
import gg.essential.vigilance.gui.elementa.GuiScaleOffsetConstraint;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.hud.HudManager;
import io.github.lilfroggy.bingohelper.util.Logger;

import java.awt.Color;

@Mixin(value = SettingsGui.class, remap = false)
public class SettingsGuiMixin {
    private static final float dividerWidth = SettingsGui.dividerWidth;
    private static final float sidebarWidth = 0.25f;

    private static final Color BUTTON_CLICK_EFFECT_COLOR = new Color(255, 255, 255, 20);
    private static final Color TEXT_HOVERED_COLOR = new Color(0xFFFFFF);
    private static final Color TEXT_UNHOVERED_COLOR = new Color(0xBBBBBB);
    private static final String BUTTON_TEXT = "Edit Hud Layout";
    private static final float BUTTON_HEIGHT = 30f;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(Vigilant config, CallbackInfo ci) {
        if (!(config instanceof Config)) return;
        try {

            // Make room for button under sidebar

            UIContainer sidebar = Config.getField(this, "sidebar$delegate");
            sidebar.setHeight(new AdditiveConstraint(new RelativeConstraint(1f), new PixelConstraint(-BUTTON_HEIGHT)));

            // Make sidebarVerticalScrollbar stay above button

            UIBlock middleDivider = Config.getField(this, "middleDivider$delegate");
            middleDivider.setHeight(new AdditiveConstraint(new RelativeConstraint(1f), new PixelConstraint(-BUTTON_HEIGHT + dividerWidth)));

            // Get UIBlock that button goes on

            UIContainer bottomContainer = Config.getField(this, "bottomContainer$delegate");

            // Edit hud button

            UIBlock editHudBlock = new UIBlock();

            editHudBlock.enableEffect(new ExpandingClickEffect(BUTTON_CLICK_EFFECT_COLOR));
            editHudBlock.setColor(VigilancePalette.INSTANCE.getComponentBackground());
            editHudBlock.setX(new PixelConstraint(0f));
            editHudBlock.setWidth(new AdditiveConstraint(new RelativeConstraint(sidebarWidth), new PixelConstraint(dividerWidth * 2))); // Match sidebar width
            editHudBlock.setHeight(new PixelConstraint(BUTTON_HEIGHT));
            editHudBlock.setY(new AdditiveConstraint(new RelativeConstraint(1.0f), new PixelConstraint(-BUTTON_HEIGHT + dividerWidth))); // Shift up from bottom
            editHudBlock.setChildOf(bottomContainer);

            UIText editHudText = new UIText(BUTTON_TEXT, true);

            editHudText.setColor(TEXT_UNHOVERED_COLOR);
            editHudText.setTextScale(new GuiScaleOffsetConstraint(1f));
            editHudText.setX(new CenterConstraint());
            editHudText.setY(new CenterConstraint());
            editHudText.setChildOf(editHudBlock);

            editHudBlock.onMouseEnterRunnable(() -> {
                editHudText.setColor(TEXT_HOVERED_COLOR);
            });

            editHudBlock.onMouseLeaveRunnable(() -> {
                editHudText.setColor(TEXT_UNHOVERED_COLOR);
            });

            editHudBlock.onMouseReleaseRunnable(() -> {
                if (editHudBlock.isHovered()) HudManager.open();
                editHudText.setColor(TEXT_UNHOVERED_COLOR);
            });

        } catch (Exception e) {
            Logger.error("Error mixing into settingsGui", e);
        }
    }
}