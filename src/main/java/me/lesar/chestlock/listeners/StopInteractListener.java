package me.lesar.chestlock.listeners;

import me.lesar.chestlock.ChestLockPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class StopInteractListener implements Listener {

	private final ChestLockPlugin plugin;

	public StopInteractListener(ChestLockPlugin plugin) {

		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {

		if(e.isSneaking() == false) return;

		Player player = e.getPlayer();
		ChestLockPlugin.Interaction interaction = plugin.interactingPlayers.remove(player);
		if(interaction != null) switch(interaction.type()) {
			case LOCK -> player.sendMessage(ChatColor.YELLOW + "Stopped locking containers");
			case UNLOCK -> player.sendMessage(ChatColor.YELLOW + "Stopped unlocking containers");
			case SHARE -> player.sendMessage(ChatColor.YELLOW + "Stopped sharing containers");
			case UNSHARE -> player.sendMessage(ChatColor.YELLOW + "Stopped unsharing containers");
			case INFO -> player.sendMessage(ChatColor.YELLOW + "Stopped viewing containers info");
		}

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {

		plugin.interactingPlayers.remove(e.getPlayer());

	}

}
