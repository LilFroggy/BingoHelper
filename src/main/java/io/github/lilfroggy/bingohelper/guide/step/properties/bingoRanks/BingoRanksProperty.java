package io.github.lilfroggy.bingohelper.guide.step.properties.bingoRanks;

import java.util.List;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.Bingo;
import io.github.lilfroggy.bingohelper.util.ChatLib;

public class BingoRanksProperty implements ClientTickEndEvent {
    
    public Step step;
    public List<Integer> ranks;

    public void register(Step step) {
        this.step = step;
        if (ranks != null) Events.CLIENT_TICK_END.register(this);
    }

    public void unregister() {
        if (ranks != null) Events.CLIENT_TICK_END.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        int rank = Bingo.rank();
        if (rank == -1) {
            ChatLib.showTitle("§cUnknown bingo rank", "§e/bhskip unless you are one of these ranks: " + ranks.toString(), 0, 3, 0);
            return;
        }
        if (!ranks.contains(rank)) step.complete();
    }
}