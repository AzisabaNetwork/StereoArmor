package net.azisaba.stereoarmor.nbs.v4;

import org.jetbrains.annotations.NotNull;
import net.azisaba.stereoarmor.nbs.NBSNote;
import net.azisaba.stereoarmor.nbs.NBSTick;

import java.util.List;

public class NBS4Tick implements NBSTick {
    protected int startingTick;
    protected @NotNull List<NBSNote> layers;

    public NBS4Tick(int startingTick, @NotNull List<NBSNote> layers){
        this.startingTick = startingTick;
        this.layers = layers;
    }

    @Override
    public int getStartingTick() {
        return startingTick;
    }

    @NotNull
    @Override
    public List<NBSNote> getLayers() {
        return layers;
    }

    @Override
    public String toString() {
        return "NBS4Tick{tick=" + startingTick + ", notes=" + layers + "}";
    }
}
