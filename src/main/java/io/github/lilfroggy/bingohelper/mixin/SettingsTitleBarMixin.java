package io.github.lilfroggy.bingohelper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIImage;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.Window;
import gg.essential.elementa.constraints.AdditiveConstraint;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.gui.Searchbar;
import gg.essential.vigilance.gui.SettingsGui;
import gg.essential.vigilance.gui.SettingsTitleBar;
import gg.essential.vigilance.gui.VigilancePalette;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.update.UpdateButtons;
import io.github.lilfroggy.bingohelper.update.UpdateManager;
import io.github.lilfroggy.bingohelper.update.UpdateState;
import io.github.lilfroggy.bingohelper.util.Logger;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.function.Consumer;

@Mixin(value = SettingsTitleBar.class, remap = false)
public class SettingsTitleBarMixin {
    private static final float DIVIDER_GAP = 10f;
    private static final float BUTTON_GAP = 5f;
    private static final float BUTTON_SIZE = 16f;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(SettingsGui gui, Vigilant config, Window window, CallbackInfo ci) {
        if (!(config instanceof Config)) return;
        try {

            // Make searchBar align left

            Field searchDelegateField = this.getClass().getDeclaredField("searchBar$delegate");
            searchDelegateField.setAccessible(true);
            Object searchDelegate = searchDelegateField.get(this);
            Field searchValueField = searchDelegate.getClass().getSuperclass().getDeclaredField("value");
            searchValueField.setAccessible(true);
            Searchbar searchbar = (Searchbar) searchValueField.get(searchDelegate);
            AdditiveConstraint searchBarLeft = new AdditiveConstraint(new RelativeConstraint(0.25f), new PixelConstraint(SettingsGui.dividerWidth + 12f));
            searchbar.setX(searchBarLeft);

            // Get contentContainer

            Field containerDelegateField = this.getClass().getDeclaredField("contentContainer$delegate");
            containerDelegateField.setAccessible(true);
            Object containerDelegate = containerDelegateField.get(this);
            Field valueField = containerDelegate.getClass().getSuperclass().getDeclaredField("value");
            valueField.setAccessible(true);
            UIBlock contentContainer = (UIBlock) valueField.get(containerDelegate);

            // Social buttons

            UIImage discordBtn = UpdateButtons.button("/assets/bingohelper/textures/config/discord.png", () -> {
                Config.openLink("https://discord.gg/rChEGmzXxa");
            });
            discordBtn.setX(new AdditiveConstraint(new RelativeConstraint(0.25f), new PixelConstraint(SettingsGui.dividerWidth - DIVIDER_GAP - (BUTTON_SIZE * 1) - (BUTTON_GAP * 0))));
            discordBtn.setY(new CenterConstraint());
            discordBtn.setChildOf(contentContainer);

            UIImage githubBtn = UpdateButtons.button("/assets/bingohelper/textures/config/github.png", () -> {
                Config.openLink("https://github.com/LilFroggy/BingoHelper");
            });
            githubBtn.setX(new AdditiveConstraint(new RelativeConstraint(0.25f), new PixelConstraint(SettingsGui.dividerWidth - DIVIDER_GAP - (BUTTON_SIZE * 2) - (BUTTON_GAP * 1))));
            githubBtn.setY(new CenterConstraint());
            githubBtn.setChildOf(contentContainer);

            // Update buttons

            UIBlock updateBlock = new UIBlock();
            updateBlock.setX(new PixelConstraint(DIVIDER_GAP, true));
            updateBlock.setY(new CenterConstraint());
            updateBlock.setColor(VigilancePalette.INSTANCE.getComponentBackground());
            updateBlock.setWidth(new PixelConstraint(BUTTON_SIZE));
            updateBlock.setHeight(new PixelConstraint(BUTTON_SIZE));
            updateBlock.setChildOf(contentContainer);

            UIText updateTxt = new UIText("", true);
            updateTxt.setX(new PixelConstraint(16f + updateBlock.getWidth(), true));
            updateTxt.setY(new CenterConstraint());
            updateTxt.setColor(new Color(0, 80, 0));
            updateTxt.setChildOf(contentContainer);

            Consumer<UpdateState> refreshUpdateStatus = state -> {
                updateBlock.clearChildren();
                updateTxt.setText("");
                if (!state.isDisplayable()) return;
                state.BUTTON.setChildOf(updateBlock);
                updateTxt.setText(state.TEXT);
                updateTxt.setColor(state.COLOR);
            };

            refreshUpdateStatus.accept(UpdateManager.getState()); // Initialize

            UpdateManager.onStateChange(state -> {
                refreshUpdateStatus.accept(state);
            });

        } catch (Exception e) {
            Logger.error("Error mixing into settingsTitleBar", e);
        }
    }
}