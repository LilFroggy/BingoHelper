package io.github.lilfroggy.bingohelper.util;

import java.util.List;

import io.github.lilfroggy.bingohelper.events.Events;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;

import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreHolder;

// Shoutout Skyblocker and SkyHanni

public class Scoreboard {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static List<Component> TEXT_LINES = new ArrayList<>();
    public static List<String> STRING_LINES = new ArrayList<>();
    public static boolean dirty = false;

    static {
        Events.CLIENT_TICK_END.register(Scoreboard::onClientTickEnd);
        Events.PACKET_RECEIVED.register(Scoreboard::onPacketReceived);
    }

    public static void init() {
        // Load
    }

    public static void onClientTickEnd(int tick) {
        if (!dirty) return;
        dirty = false;
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
        
        Events.SCOREBOARD_UPDATE.invoke(listener -> listener.onScoreboardUpdate(STRING_LINES));
    }

    public static void onPacketReceived(Packet<?> packet) {
        switch (packet) {
            case ClientboundSetScorePacket p: {
                if (p.objectiveName() == "update") {
                    dirty = true;
                }
                break;
            }
            case ClientboundSetPlayerTeamPacket p: {
                if (p.getName().startsWith("team_")) {
                    dirty = true;
                }
                break;
            }
            case ClientboundSetObjectivePacket p: {
                var type = p.getRenderType();
                if (type != ObjectiveCriteria.RenderType.INTEGER) return;
                String objectiveName = p.getObjectiveName();
                if (objectiveName.equals("health")) return;
                var objectiveValue = p.getDisplayName().getString().strip();
                Events.SCOREBOARD_TITLE_UPDATE.invoke(listener -> listener.onScoreboardTitleUpdate(objectiveValue, objectiveName));
                break;
            }
            default: return;
        }
    }
}