package me.lesar.chestlock.listeners;

import me.lesar.chestlock.ChestLockPlugin;
import me.lesar.chestlock.ContainerStorageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OpenContainerListener implements Listener {

	private final ChestLockPlugin plugin;

	public OpenContainerListener(ChestLockPlugin plugin) {

		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {

		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if(!plugin.containerMaterials.contains(e.getClickedBlock().getType())) return;

		Location location = e.getClickedBlock().getLocation();
		if(!ContainerStorageUtil.isContainerLocked(location)) return;

		Player player = e.getPlayer();
		if(ContainerStorageUtil.canOpenContainer(location, player)) return;

		e.setCancelled(true);
		player.sendMessage(ChatColor.RED + "You cannot open this container, it belongs to " + ContainerStorageUtil.getContainerOwner(location));

	}

}
