package io.github.lilfroggy.bingohelper.util;

import java.util.ArrayList;

import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.events.ScoreboardUpdateEventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.scoreboard.Team;
import java.util.Collections;

public class Scoreboard {

    public static ArrayList<Text> TEXT_LINES = new ArrayList<>();
    public static ArrayList<String> STRING_LINES = new ArrayList<>();

    static {
        ClientTickEventBus.register(Scoreboard::onClientTick);
    }

    public static void init() {
        // Load
    }

    public static void onClientTick(int tick) {
        if(tick % 20 != 0) return;
        update();
    }

    public static void update() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        net.minecraft.scoreboard.Scoreboard scoreboard = mc.player.getScoreboard();
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
        
        ScoreboardUpdateEventBus.fire(new ArrayList<>(STRING_LINES));
    }
}