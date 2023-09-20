package net.azisaba.stereoarmor.nbs.v4;

import org.jetbrains.annotations.NotNull;
import net.azisaba.stereoarmor.nbs.NBSFile;
import net.azisaba.stereoarmor.nbs.NBSInstrument;
import net.azisaba.stereoarmor.nbs.NBSLayerData;
import net.azisaba.stereoarmor.nbs.NBSTick;
import net.azisaba.stereoarmor.nbs.NBSHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBS4File implements NBSFile {

    @NotNull
    protected final NBSHeader header;

    @NotNull
    protected final List<NBSTick> ticks;

    @NotNull
    protected final List<NBSLayerData> layers;

    @NotNull
    protected final List<NBSInstrument> customInstruments;

    @NotNull
    protected final Map<Integer, List<NBSTick>> tickMap = new HashMap<>();

    public NBS4File(@NotNull NBSHeader header, @NotNull List<NBSTick> ticks, @NotNull List<NBSLayerData> layers, @NotNull List<NBSInstrument> customInstruments) {
        this.header = header;
        this.ticks = ticks;
        this.layers = layers;
        this.customInstruments = customInstruments;
        for (NBSTick tick : ticks) {
            tickMap.computeIfAbsent((int) (tick.getStartingTick() / (header.getTempo() / 2000F)), k -> new ArrayList<>()).add(tick);
        }
    }

    @Override
    public @NotNull NBSHeader getHeader() {
        return header;
    }

    @Override
    public @NotNull List<NBSTick> getTicks() {
        return ticks;
    }

    @Override
    public @NotNull List<NBSLayerData> getLayers() {
        return layers;
    }

    @Override
    public @NotNull List<NBSInstrument> getCustomInstruments() {
        return customInstruments;
    }

    @NotNull
    @Override
    public Map<Integer, List<NBSTick>> getTickMap() {
        return tickMap;
    }
}
