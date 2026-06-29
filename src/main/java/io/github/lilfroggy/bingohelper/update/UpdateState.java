package io.github.lilfroggy.bingohelper.update;

import java.awt.Color;

import gg.essential.elementa.components.UIImage;

public enum UpdateState {
    NONE(null, null, null),
    CHECKING(null, null, null),
    AVAILABLE(UpdateButtons.DOWNLOAD, "Update Available", Color.GREEN),
    DOWNLOADING(UpdateButtons.DOWNLOAD, "Downloading...", Color.GRAY),
    DOWNLOADED(UpdateButtons.SHUTDOWN, "Download successful. Restart to apply changes.", Color.GREEN),
    INCOMPATIBLE(UpdateButtons.MANUAL_UPDATE_INFO, "Manual Update Required", new Color(255, 170, 0)),
    PARSE_ERROR(UpdateButtons.LATEST_RELEASE, "Update check failed. Manually check GitHub or submit a report.", Color.RED),
    DOWNLOAD_ERROR(UpdateButtons.DOWNLOAD, "Error downloading update. Try again later or submit a report.", Color.RED);

    public final UIImage BUTTON;
    public final String TEXT;
    public final Color COLOR;

    UpdateState(UIImage button, String text, Color color) {
        this.BUTTON = button;
        this.TEXT = text;
        this.COLOR = color;
    }

    public boolean isDisplayable() {
        return this.BUTTON != null && this.TEXT != null && this.COLOR != null;
    }

    public static UpdateState fromString(String updateState) {
        try {
            return UpdateState.valueOf(updateState);
        } catch (Exception e) {
            return UpdateState.NONE;
        }
    }
}