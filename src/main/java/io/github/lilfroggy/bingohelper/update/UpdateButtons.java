package io.github.lilfroggy.bingohelper.update;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import gg.essential.elementa.components.UIImage;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.vigilance.gui.ExpandingClickEffect;
import io.github.lilfroggy.bingohelper.config.Config;

public class UpdateButtons {
    private static final Minecraft CLIENT = Minecraft.getInstance();
    private static final Color CLICK_EFFECT_COLOR = new Color(41, 151, 255, 127);
    private static final Color BUTTON_HOVER_COLOR = new Color(0xC8C8C8);
    private static final Color BUTTON_CLICK_COLOR = new Color(0x6E6E6E);
    private static final Color BUTTON_COLOR = new Color(0xFFFFFF);
    private static final float BUTTON_SIZE = 16f;

    public static final UIImage DOWNLOAD = button("/assets/bingohelper/textures/config/download.png", () -> {
        UpdateManager.download();
    });

    // Possibly change to RESTART if I can figure out how to relaunch the instance
    public static final UIImage SHUTDOWN = button("/assets/bingohelper/textures/config/shutdown.png", () -> {
        CLIENT.stop();
    });

    public static final UIImage MANUAL_UPDATE_INFO = button("/assets/bingohelper/textures/config/info-gold.png", () -> {
        Config.openLink(UpdateManager.MAINTENANCE_URL);
    });

    public static final UIImage LATEST_RELEASE = button("/assets/bingohelper/textures/config/external-link.png", () -> {
        Config.openLink(UpdateManager.LATEST_RELEASE_URL);
    });

    public static UIImage button(String imagePath, Runnable callback) {
        UIImage img = UIImage.ofResource(imagePath);
        img.enableEffect(new ExpandingClickEffect(CLICK_EFFECT_COLOR));
        img.setWidth(new PixelConstraint(BUTTON_SIZE));
        img.setHeight(new PixelConstraint(BUTTON_SIZE));
        img.onMouseEnterRunnable(() -> {
            if (img.getColor() == BUTTON_CLICK_COLOR) return;
            img.setColor(BUTTON_HOVER_COLOR);
        });
        img.onMouseLeaveRunnable(() -> {
            if (img.getColor() == BUTTON_CLICK_COLOR) return;
            img.setColor(BUTTON_COLOR);
        });
        img.onMouseClick((event, component) -> {
            img.setColor(BUTTON_CLICK_COLOR);
            return null;
        });
        img.onMouseReleaseRunnable(() -> {
            if (img.isHovered()) callback.run();
            img.setColor(BUTTON_COLOR);
        });
        return img;
    }
}