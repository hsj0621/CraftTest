package org.craft.test;

import me.rukon0621.rkutils.bukkit.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeedCalculator {

    private final Map<UUID, Location> lastLocations = new HashMap<>();

    public SpeedCalculator() {
        Bukkit.getScheduler().runTaskTimer(CraftTest.getInst(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Location cnt = player.getLocation();
                cnt.setY(0); // Ignore Y axis
                Location last = lastLocations.getOrDefault(player.getUniqueId(), cnt);
                lastLocations.put(player.getUniqueId(), cnt);
                double distance = last.distance(cnt);
                double kmh = distance * 4 * 3600 / 1000; // km/h
                double mps = distance * 4; // m/s


                int base = 180;
                int redValue = (int) (Math.clamp(kmh, 0, 250) / 250 * (255 - base));

                String color = Msg.getColor(redValue + base, base - redValue, base - redValue);

                player.sendActionBar(Msg.mm(String.format("<gray>Speed: %s%.1f km/h | %.1f m/s", color, kmh, mps)));
            }

        }, 0, 5L);
    }
}
