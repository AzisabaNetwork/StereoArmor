package net.azisaba.stereoarmor.util;

import net.azisaba.stereoarmor.nbs.*;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NBSBukkitHelper {
    public static @Nullable Sound getBukkitSound(@NotNull String sound) {
        try {
            return Sounds.tryResolveSound(sound.replaceAll("^(.*)\\.ogg$", "$1").replaceAll("\\.", "_"));
        } catch (IllegalArgumentException ignored) {}
        return null;
    }

    public static @Nullable Sound getBukkitSound(List<NBSInstrument> customInstruments, byte instrument) {
        if (instrument == 0) return Sounds.BLOCK_NOTE_HARP;
        if (instrument == 1) return Sounds.BLOCK_NOTE_BASS;
        if (instrument == 2) return Sounds.BLOCK_NOTE_BASEDRUM;
        if (instrument == 3) return Sounds.BLOCK_NOTE_SNARE_DRUM;
        if (instrument == 4) return Sounds.BLOCK_NOTE_STICKS;
        if (instrument == 5) return Sounds.BLOCK_NOTE_BASS_GUITAR;
        if (instrument == 6) return Sounds.BLOCK_NOTE_FLUTE;
        if (instrument == 7) return Sounds.BLOCK_NOTE_BELL;
        if (instrument == 8) return Sounds.BLOCK_NOTE_CHIME;
        if (instrument == 9) return Sounds.BLOCK_NOTE_XYLOPHONE;
        if (instrument == 10) return Sounds.BLOCK_NOTE_IRON_XYLOPHONE;
        if (instrument == 11) return Sounds.BLOCK_NOTE_COW_BELL;
        if (instrument == 12) return Sounds.BLOCK_NOTE_DIDGERIDOO;
        if (instrument == 13) return Sounds.BLOCK_NOTE_BIT;
        if (instrument == 14) return Sounds.BLOCK_NOTE_BANJO;
        if (instrument == 15) return Sounds.BLOCK_NOTE_PLING;
        try {
            NBSInstrument customInstrument = customInstruments.get(instrument - 16);
            Sound sound = getBukkitSound(customInstrument.getSound());
            if (sound != null) return sound;
            return getBukkitSound(customInstrument.getName());
        } catch (IndexOutOfBoundsException | NullPointerException ex) {
            throw new IndexOutOfBoundsException("Instrument is out of range, unsupported version?");
        }
    }

    @Contract(pure = true)
    public static float getSoundPitch(byte key) {
        if (key == 33) return 0.5F;      // F#
        if (key == 34) return 0.529732F; // G
        if (key == 35) return 0.561231F; // G#
        if (key == 36) return 0.594604F; // A
        if (key == 37) return 0.629961F; // A#
        if (key == 38) return 0.667420F; // B
        if (key == 39) return 0.707107F; // C
        if (key == 40) return 0.749154F; // C#
        if (key == 41) return 0.793701F; // D
        if (key == 42) return 0.840896F; // D#
        if (key == 43) return 0.890899F; // E
        if (key == 44) return 0.943874F; // F
        if (key == 45) return 1.0F;      // F#
        if (key == 46) return 1.059463F; // G
        if (key == 47) return 1.122462F; // G#
        if (key == 48) return 1.189207F; // A
        if (key == 49) return 1.259921F; // A#
        if (key == 50) return 1.334840F; // B
        if (key == 51) return 1.414214F; // C
        if (key == 52) return 1.498307F; // C#
        if (key == 53) return 1.587401F; // D
        if (key == 54) return 1.681793F; // D#
        if (key == 55) return 1.781797F; // E
        if (key == 56) return 1.887749F; // F
        if (key == 57) return 2.0F;      // F#
        return 0.0F;                     // out of range
    }

    public static void play(@NotNull NBSFile file, @NotNull NBSLayerData layer, @NotNull NBSNote note, @Nullable Player player, @NotNull Location origin, float volumeMulti, boolean world) {
        Sound sound = getBukkitSound(file.getCustomInstruments(), note.getInstrument());
        if ((layer.getVolume() > 0 || note.getVolume() > 0) && sound != null) {
            float volume = note.getVolume();
            if (layer.getVolume() != 100) {
                volume += layer.getVolume();
                volume /= 2;
            }
            volume *= volumeMulti;
            volume /= 100;
            if (world) {
                origin.getWorld().playSound(origin, sound, SoundCategory.RECORDS, volume, getSoundPitch(note.getKey()));
            } else if (player != null) {
                player.playSound(origin, sound, SoundCategory.RECORDS, volume, getSoundPitch(note.getKey()));
            }
        }
    }

    public static void play(@NotNull NBSFile file, @NotNull NBSTick tick, @Nullable Player player, @NotNull Location origin, float volumeMulti, boolean world) {
        for (int layerIndex = 0; layerIndex < tick.getLayers().size(); layerIndex++) {
            NBSNote note = tick.getLayers().get(layerIndex);
            if (note == null) continue;
            NBSLayerData layer = file.getLayers().get(layerIndex);
            play(file, layer, note, player, origin, volumeMulti, world);
        }
    }
}
