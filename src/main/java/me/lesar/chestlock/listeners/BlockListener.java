package me.lesar.chestlock.listeners;

import me.lesar.chestlock.ChestLockPlugin;
import me.lesar.chestlock.ContainerStorageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.DoubleChestInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BlockListener implements Listener {

	private final ChestLockPlugin plugin;

	public BlockListener(ChestLockPlugin plugin) {

		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {

		Block block = e.getBlockPlaced();
		Material material = block.getType();

		if(material != Material.CHEST && material != Material.TRAPPED_CHEST) return;

		Player player = e.getPlayer();

		Location[] adjacentLocations = {
				block.getLocation().clone().add(1, 0, 0),
				block.getLocation().clone().add(-1, 0, 0),
				block.getLocation().clone().add(0, 0, 1),
				block.getLocation().clone().add(0, 0, -1)
		};

		boolean foundAdjacent = false;
		for(Location adjacentLocation : adjacentLocations) {

			String owner = ContainerStorageUtil.getContainerOwner(adjacentLocation);
			if(owner == null) continue;

			if(owner.equals(e.getPlayer().getName())) foundAdjacent = true;
			else {

				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "The adjacent chest is owned by " + owner);
				return;

			}

		}
		if(!foundAdjacent) return;

		// have to add a 0 tick delay because minecraft is dumb and doesn't immediately make a chest a double chest when placed (WTF)
		plugin.getServer().getScheduler().runTaskLater(plugin, () -> {

			Chest chest = (Chest) block.getState();

			if (!(chest.getInventory() instanceof DoubleChestInventory doubleChest)) return;

			Location otherLocation = doubleChest.getLeftSide().getLocation().equals(block.getLocation())
					? doubleChest.getRightSide().getLocation()
					: doubleChest.getLeftSide().getLocation();

			ContainerStorageUtil.copyContainerState(otherLocation, block.getLocation());
			player.sendMessage(ContainerStorageUtil.getContainerOwner(block.getLocation()));
			player.sendMessage(ChatColor.GREEN + "Successfully extended locked chest");

		}, 0L);

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {

		Location location = e.getBlock().getLocation();
		if(!ContainerStorageUtil.isContainerLocked(location)) return;

		Player player = e.getPlayer();
		if(ContainerStorageUtil.getContainerOwner(location).equals(player.getName())) {

			ContainerStorageUtil.removeContainerState(location);
			return;

		}

		e.setCancelled(true);
		player.sendMessage(ChatColor.RED + "You cannot break this container, it belongs to " + ContainerStorageUtil.getContainerOwner(location));

	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {

		for(Block block : e.blockList()) {

			if(!plugin.containerMaterials.contains(block.getType())) continue;
			if(ContainerStorageUtil.isContainerLocked(block.getLocation())) e.blockList().remove(block);

		}

	}

}
