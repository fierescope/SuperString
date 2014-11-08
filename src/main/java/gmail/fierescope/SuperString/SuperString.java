package gmail.fierescope.SuperString;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SuperString extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("SuperString has been enabled.");
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("SuperString has been disabled!");
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block;
        if (player.hasPermission("superstring.grapple") && player.getItemInHand().getType() == Material.STRING && player.getItemInHand().getAmount() >= 1) {
            Location playerStart = player.getLocation();
            if (this.getConfig().getBoolean("plugin.use-string")) {
                block = player.getTargetBlock(null, player.getItemInHand().getAmount());
            } else {
                block = player.getTargetBlock(null, getServer().getViewDistance()*16);
            }
            if (block.getType() != Material.AIR && ((int)Math.floor(block.getLocation().distance(playerStart)) < player.getItemInHand().getAmount() || !this.getConfig().getBoolean("plugin.use-string"))) {
                if (player.teleport(block.getLocation())) {
                    player.sendMessage("Grappled successfully.");
                    if (this.getConfig().getBoolean("plugin.use-string")) {
                        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - (int)Math.floor(block.getLocation().distance(playerStart)));
                        score(playerStart, player);
                    }
                } else {
                    player.sendMessage("Block does not exist.");
                }
            } else {
                    player.sendMessage("That's too far to grapple on.");
              }
        }
    }
    public void score(Location playerStart, Player player) {
        int distance = (int)Math.floor(player.getLocation().distance(playerStart));
        this.getConfig().createSection("db." + player.getUniqueId().toString());
        this.getConfig().set("db." + player.getUniqueId().toString() + "name", player.getName());
        this.getConfig().set("db." + player.getUniqueId().toString() + "score", distance);
        this.saveConfig();
        log(distance, player);
    }

    private void log(int distance, Player player) {
        List<String> scoreCodes = Arrays.asList("q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p");
        int scoreOpCode;
        String scoreCode = "";
        if (distance > 25) {
            for (int i = 26; (distance - i) >= 0 && (distance - i) < 26; i += 26) {
                scoreOpCode = distance - i;
                if (scoreOpCode == 0) {
                    scoreCode = scoreCodes.get(scoreOpCode + 25) + scoreCodes.get(scoreOpCode);
                } else {
                    scoreCode = scoreCodes.get(scoreOpCode - 1) + scoreCodes.get(scoreOpCode);
                }
            }
        } else {
            scoreOpCode = distance;
            scoreCode = scoreCodes.get(scoreOpCode - 1) + scoreCodes.get(scoreOpCode);
        }
        getServer().dispatchCommand(getServer().getConsoleSender(), scoreCode + " " + player.getName());
    }
}
