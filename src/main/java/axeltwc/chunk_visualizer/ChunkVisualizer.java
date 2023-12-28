package axeltwc.chunk_visualizer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class ChunkVisualizer extends JavaPlugin implements Listener {

    private final Set<Location> borderParticles = new HashSet<>();

    @Override
    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Reset particles when the server stops
        resetBorderParticles();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check if the player is holding the chunk tool (e.g., using an item)
        if (player.getInventory().getItemInMainHand().getType() == Material.GOLDEN_SHOVEL && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§6Kit Claim")) {
            visualizeChunkBorders(player);
        } else {
            resetBorderParticles();
        }
    }

    private void visualizeChunkBorders(Player player) {
        int chunkX = player.getLocation().getBlockX() >> 4; // Divide by 16 to get chunk coordinates
        int chunkZ = player.getLocation().getBlockZ() >> 4;

        // Iterate through the blocks along the chunk borders
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (x == 0 || x == 15 || z == 0 || z == 15) {
                    Location particleLocation = new Location(
                            player.getWorld(),
                            (chunkX << 4) + x + 0.5, // Add 0.5 to center particles on the block
                            player.getLocation().getBlockY() + 0.5,
                            (chunkZ << 4) + z + 0.5
                    );
                    visualizeParticle(particleLocation);
                }
            }
        }
    }

    private void visualizeParticle(Location location) {
        // Spawn particle at the specified location
        location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, location, 1);
        borderParticles.add(location);
    }

    private void resetBorderParticles() {
        // Remove previously spawned particles
        for (Location particleLocation : borderParticles) {
            particleLocation.getWorld().spawnParticle(Particle.BLOCK_DUST, particleLocation, 1, Material.AIR.createBlockData());
        }

        // Clear the set after resetting particles
        borderParticles.clear();
    }
}
