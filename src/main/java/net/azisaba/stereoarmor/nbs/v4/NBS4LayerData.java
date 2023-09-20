package net.azisaba.stereoarmor.nbs.v4;

import org.jetbrains.annotations.NotNull;
import net.azisaba.stereoarmor.nbs.NBSLayerData;

public class NBS4LayerData implements NBSLayerData {
    @NotNull
    protected String name;
    protected byte lock;
    protected byte volume;
    protected byte panning;

    public NBS4LayerData(@NotNull String name, byte lock, byte volume, byte panning) {
        this.name = name;
        this.lock = lock;
        this.volume = volume;
        this.panning = panning;
    }

    @NotNull
    @Override
    public String getName() { return name; }

    @Override
    public byte getLock() { return lock; }

    @Override
    public byte getVolume() { return volume; }

    @Override
    public byte getPanning() { return panning; }

    @Override
    public String toString() {
        return "NBS4LayerData{" +
                "name='" + name + '\'' +
                ", lock=" + lock +
                ", volume=" + volume +
                ", panning=" + panning +
                '}';
    }
}
