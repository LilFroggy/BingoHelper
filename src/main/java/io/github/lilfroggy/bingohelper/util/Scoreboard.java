package io.github.lilfroggy.bingohelper.util;

import java.util.ArrayList;

import io.github.lilfroggy.bingohelper.events.Events;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.scoreboard.Team;
import java.util.Collections;

// Shoutout Skyblocker

public class Scoreboard {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static ArrayList<Text> TEXT_LINES = new ArrayList<>();
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
        if (!(CLIENT.world instanceof ClientWorld world)) return;

        net.minecraft.scoreboard.Scoreboard scoreboard = world.getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        TEXT_LINES.clear();
        STRING_LINES.clear();

        for (ScoreHolder scoreHolder : scoreboard.getKnownScoreHolders()) {
            // Limit to just objectives displayed in the scoreboard (specifically sidebar objective)
            if (scoreboard.getScoreHolderObjectives(scoreHolder).containsKey(objective)) {
                Team team = scoreboard.getScoreHolderTeam(scoreHolder.getNameForScoreboard());

                if (team != null) {
                    Text textLine = Text.empty().append(team.getPrefix().copy()).append(team.getSuffix().copy());
                    String strLine = team.getPrefix().getString() + team.getSuffix().getString();

                    if (!strLine.trim().isEmpty()) {
                        String formatted = Formatting.strip(strLine);
                        TEXT_LINES.add(textLine);
                        STRING_LINES.add(formatted);
                    }
                }
            }
        }

        if (objective != null) {
            STRING_LINES.add(objective.getDisplayName().getString());
            TEXT_LINES.add(Text.empty().append(objective.getDisplayName().copy()));

            Collections.reverse(STRING_LINES);
            Collections.reverse(TEXT_LINES);
        }
        
        Events.SCOREBOARD_UPDATE.invoke(listener -> listener.onScoreboardUpdate(new ArrayList<>(STRING_LINES)));
    }
}