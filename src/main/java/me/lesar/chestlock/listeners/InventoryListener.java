package me.lesar.chestlock.listeners;

import me.lesar.chestlock.ChestLockPlugin;
import me.lesar.chestlock.ContainerStorageUtil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.PlayerInventory;

public class InventoryListener implements Listener {

	private final ChestLockPlugin plugin;

	public InventoryListener(ChestLockPlugin plugin) {

		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e) {

		plugin.getLogger().info("moved item(s)!");

		Location sourceLocation = e.getSource().getLocation();
		if(sourceLocation != null && ContainerStorageUtil.isContainerLocked(sourceLocation)) {

			if(e.getDestination() instanceof PlayerInventory playerInventory)
				if(ContainerStorageUtil.getContainerOwner(sourceLocation).equals(playerInventory.getHolder().getName())) return;

			e.setCancelled(true);
			return;

		}

		Location destinationLocation = e.getDestination().getLocation();
		if(ContainerStorageUtil.isContainerLocked(destinationLocation)) {

			if(e.getSource() instanceof PlayerInventory playerInventory)
				if(ContainerStorageUtil.getContainerOwner(destinationLocation).equals(playerInventory.getHolder().getName())) return;

			e.setCancelled(true);
			return;

		}

	}

}
