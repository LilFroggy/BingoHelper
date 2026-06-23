package io.github.lilfroggy.bingohelper.util;

import java.util.ArrayList;

import io.github.lilfroggy.bingohelper.events.Events;
import java.util.Collections;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreHolder;

// Shoutout Skyblocker

public class Scoreboard {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static ArrayList<Component> TEXT_LINES = new ArrayList<>();
    public static ArrayList<String> STRING_LINES = new ArrayList<>();

    static {
        Events.CLIENT_TICK_END.register(Scoreboard::onClientTickEnd);
    }

    public static void init() {
        // Load
    }

    public static void onClientTickEnd(int tick) {
        if(tick % 20 != 0) return;
        update();
    }

    public static void update() {
        if (!(CLIENT.level instanceof ClientLevel world)) return;

        net.minecraft.world.scores.Scoreboard scoreboard = world.getScoreboard();
        Objective objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        TEXT_LINES.clear();
        STRING_LINES.clear();

        for (ScoreHolder scoreHolder : scoreboard.getTrackedPlayers()) {
            // Limit to just objectives displayed in the scoreboard (specifically sidebar objective)
            if (scoreboard.listPlayerScores(scoreHolder).containsKey(objective)) {
                PlayerTeam team = scoreboard.getPlayersTeam(scoreHolder.getScoreboardName());

                if (team != null) {
                    Component textLine = Component.empty().append(team.getPlayerPrefix().copy()).append(team.getPlayerSuffix().copy());
                    String strLine = team.getPlayerPrefix().getString() + team.getPlayerSuffix().getString();

                    if (!strLine.trim().isEmpty()) {
                        String formatted = ChatFormatting.stripFormatting(strLine);
                        TEXT_LINES.add(textLine);
                        STRING_LINES.add(formatted);
                    }
                }
            }
        }

        if (objective != null) {
            STRING_LINES.add(objective.getDisplayName().getString());
            TEXT_LINES.add(Component.empty().append(objective.getDisplayName().copy()));

            Collections.reverse(STRING_LINES);
            Collections.reverse(TEXT_LINES);
        }
        
        Events.SCOREBOARD_UPDATE.invoke(listener -> listener.onScoreboardUpdate(new ArrayList<>(STRING_LINES)));
    }
}