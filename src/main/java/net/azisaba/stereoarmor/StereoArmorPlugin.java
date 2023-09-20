package net.azisaba.stereoarmor;

import net.azisaba.stereoarmor.commands.GiveStereoArmorCommand;
import net.azisaba.stereoarmor.nbs.NBSFile;
import net.azisaba.stereoarmor.nbs.NBSTick;
import net.azisaba.stereoarmor.nbs.v4.NBS4Reader;
import net.azisaba.stereoarmor.util.NBSBukkitHelper;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class StereoArmorPlugin extends JavaPlugin {
    public final NamespacedKey fileKey =  new NamespacedKey(this, "file");
    private final Map<String, NBSFile> nbs = new HashMap<>();
    private final AtomicInteger playerTick = new AtomicInteger();
    private final AtomicInteger worldTick = new AtomicInteger();
    private final Timer playerTimer = new Timer("StereoArmor(Player)");
    private final Timer worldTimer = new Timer("StereoArmor(World)");

    @Override
    public void onEnable() {
        getLogger().info("Loading nbs files...");
        Path path = getDataFolder().toPath().resolve("songs");
        try {
            NBS4Reader reader4 = new NBS4Reader();
            try (Stream<Path> stream = Files.list(path)) {
                stream.forEach(file -> {
                    if (Files.isDirectory(file)) return;
                    if (!file.getFileName().toString().endsWith(".nbs")) return;
                    try {
                        String name = file.getFileName().toString().replaceAll("[^a-zA-Z0-9.\\-_]", "_");
                        NBSFile nbsFile = reader4.read(file.toFile());
                        if (nbsFile.getLastTick() == null) {
                            getSLF4JLogger().warn("Skipping {} because this song does not have any tick", name);
                            return;
                        }
                        nbs.put(name, nbsFile);
                        getSLF4JLogger().info("Loaded {}", name);
                    } catch (Exception e) {
                        getSLF4JLogger().error("Failed to read {}", path.getFileName(), e);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getSLF4JLogger().info("Loaded {} nbs files", nbs.size());
        playerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int current = playerTick.getAndIncrement();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getGameMode() == GameMode.SPECTATOR) continue;
                    ItemStack item = player.getInventory().getLeggings();
                    if (item == null || !item.hasItemMeta()) continue;
                    ItemMeta meta = item.getItemMeta();
                    String identifier = meta.getPersistentDataContainer().get(fileKey, PersistentDataType.STRING);
                    if (identifier == null) continue;
                    NBSFile file = nbs.get(identifier);
                    if (file == null) {
                        player.sendActionBar(ChatColor.RED + "このNBSファイルは読み込まれていません。");
                        continue;
                    }
                    int lastTick = (int) (Objects.requireNonNull(file.getLastTick()).getStartingTick() / (file.getHeader().getTempo() / 2000F));
                    for (NBSTick tick : file.getTickMap().getOrDefault(current % lastTick, Collections.emptyList())) {
                        tick.getLayers().stream().filter(Objects::nonNull).findAny().ifPresent(note ->
                                player.getWorld().spawnParticle(Particle.NOTE, player.getLocation().clone().add(0, 2.4, 0), 1, 0, 0, 0, note.getInstrument())
                        );
                        NBSBukkitHelper.play(file, tick, player, player.getLocation(), 1, true);
                    }
                }
            }
        }, 50, 50);
        worldTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int current = worldTick.getAndIncrement();
                for (World world : Bukkit.getWorlds()) {
                    for (ItemFrame itemFrame : world.getEntitiesByClass(ItemFrame.class)) {
                        ItemStack item = itemFrame.getItem();
                        if (!item.hasItemMeta() || item.getType() == Material.FILLED_MAP) continue;
                        ItemMeta meta = item.getItemMeta();
                        String identifier = meta.getPersistentDataContainer().get(fileKey, PersistentDataType.STRING);
                        if (identifier == null) continue;
                        NBSFile file = nbs.get(identifier);
                        if (file == null) continue;
                        int lastTick = (int) (Objects.requireNonNull(file.getLastTick()).getStartingTick() / (file.getHeader().getTempo() / 2000F));
                        for (NBSTick tick : file.getTickMap().getOrDefault(current % lastTick, Collections.emptyList())) {
                            tick.getLayers().stream().filter(Objects::nonNull).findAny().ifPresent(note ->
                                    itemFrame.getWorld().spawnParticle(Particle.NOTE, itemFrame.getLocation().clone().add(0, 0.4, 0), 1, 0, 0, 0, note.getInstrument())
                            );
                            NBSBukkitHelper.play(file, tick, null, itemFrame.getLocation(), 1, true);
                        }
                    }
                }
            }
        }, 50, 50);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
        }, 1, 1);
        Objects.requireNonNull(Bukkit.getPluginCommand("givestereoarmor")).setExecutor(new GiveStereoArmorCommand(this));
    }

    @Override
    public void onDisable() {
        playerTimer.cancel();
        worldTimer.cancel();
    }

    public Map<String, NBSFile> getNbs() {
        return nbs;
    }
}
