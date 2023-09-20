package net.azisaba.stereoarmor.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ByteUtil {
    public static @NotNull List<Byte> listOf(byte @NotNull ... bytes) {
        List<Byte> list = new ArrayList<>(bytes.length);
        for (byte b : bytes) {
            list.add(b);
        }
        return list;
    }
}
