package io.github.lilfroggy.bingohelper.events;

import io.github.lilfroggy.bingohelper.events.interfaces.*;

public class Events {

    // Minecraft

    public static final EventHandler<ServerTickEvent> SERVER_TICK = new EventHandler<>();
    public static final EventHandler<ClientTickStartEvent> CLIENT_TICK_START = new EventHandler<>();
    public static final EventHandler<ClientTickEndEvent> CLIENT_TICK_END = new EventHandler<>();
    public static final EventHandler<WorldChangeEvent> CHANGE_WORLD = new EventHandler<>();

    public static final EventHandler<MessageEvent> MESSAGE = new EventHandler<>();
    public static final EventHandler<ActionBarMessageEvent> ACTION_BAR_MESSAGE = new EventHandler<>();

    public static final EventHandler<ClickSlotEvent> CLICK_SLOT = new EventHandler<>();
    public static final EventHandler<ScoreboardUpdateEvent> SCOREBOARD_UPDATE = new EventHandler<>();
    public static final EventHandler<ScoreboardTitleUpdateEvent> SCOREBOARD_TITLE_UPDATE = new EventHandler<>();
    public static final EventHandler<TablistUpdateEvent> TABLIST_UPDATE = new EventHandler<>();
    public static final EventHandler<CloseScreenEvent> CLOSE_SCREEN = new EventHandler<>();

    public static final EventHandler<EntityStateUpdateEvent> ENTITY_STATE_UPDATE = new EventHandler<>();
    public static final EventHandler<ParticleSpawnEvent> PARTICLE_SPAWN = new EventHandler<>();
    public static final EventHandler<PacketReceivedEvent> PACKET_RECEIVED = new EventHandler<>();
    public static final EventHandler<BlockUpdateEvent> BLOCK_UPDATE = new EventHandler<>();

    // Bingo

    public static final EventHandler<CreateBingoProfileEvent> CREATE_BINGO_PROFILE = new EventHandler<>();
    public static final EventHandler<JoinBingoEvent> JOIN_BINGO = new EventHandler<>();
    public static final EventHandler<LeaveBingoEvent> LEAVE_BINGO = new EventHandler<>();

    // Hypixel

    public static final EventHandler<JoinHypixelEvent> JOIN_HYPIXEL = new EventHandler<>();

    // Render

    public static final EventHandler<RenderHudEvent> RENDER_HUD = new EventHandler<>();
    public static final EventHandler<RenderWorldEvent> RENDER_WORLD = new EventHandler<>();
    public static final EventHandler<RenderSlotEvent> RENDER_SLOT = new EventHandler<>();
    public static final EventHandler<RenderScreenEvent> RENDER_SCREEN = new EventHandler<>();

    // Skyblock

    public static final EventHandler<UnlockMobTypeEvent> UNLOCK_MOB_TYPE = new EventHandler<>();
    public static final EventHandler<LevelCollectionEvent> LEVEL_COLLECTION = new EventHandler<>();
    public static final EventHandler<LevelSkillEvent> LEVEL_SKILL = new EventHandler<>();
    public static final EventHandler<SkyblockLevelChangeEvent> SKYBLOCK_LEVEL_CHANGE = new EventHandler<>();

    // this is based off tablist (not hypixelmodapi packets)
    public static final EventHandler<IslandChangeEvent> CHANGE_ISLAND = new EventHandler<>();

    public static final EventHandler<AreaChangeEvent> CHANGE_AREA = new EventHandler<>();
    public static final EventHandler<SubAreaChangeEvent> CHANGE_SUB_AREA = new EventHandler<>();
}