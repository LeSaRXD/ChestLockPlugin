package me.lesar.chestlock.listeners;

import me.lesar.chestlock.ChestLockPlugin;
import me.lesar.chestlock.ContainerStorageUtil;
import me.lesar.chestlock.InteractionType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractListener implements Listener {

	private final ChestLockPlugin plugin;

	public InteractListener(ChestLockPlugin plugin) {

		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {

		Player player = e.getPlayer();
		if(!plugin.interactingPlayers.containsKey(player)) return;

		if(e.getAction() != Action.LEFT_CLICK_BLOCK) return;

		Block block = e.getClickedBlock();
		Material material = block.getType();

		ChestLockPlugin.Interaction interaction = plugin.interactingPlayers.get(player);

		if(interaction.type() == InteractionType.INFO) player.sendMessage(ChatColor.BLUE + ContainerStorageUtil.getContainerInfo(block.getLocation()));
		else if(material == Material.CHEST || material == Material.TRAPPED_CHEST) { // changing a chest

			Inventory inventory = ((Chest) block.getState()).getInventory();
			// double chest
			if (inventory instanceof DoubleChestInventory doubleInventory) {

				if(
						ContainerStorageUtil.changeContainerState(doubleInventory.getLeftSide().getLocation(), interaction, player) &&
						ContainerStorageUtil.changeContainerState(doubleInventory.getRightSide().getLocation(), interaction, player)
				) player.sendMessage(getInteractionMessage(interaction, true, material == Material.CHEST ? "double chest" : "double trapped chest"));
				else player.sendMessage(getInteractionMessage(interaction, false, material == Material.CHEST ? "double chest" : "double trapped chest"));

			} else { // single chest

				player.sendMessage(getInteractionMessage(
						interaction,
						ContainerStorageUtil.changeContainerState(block.getLocation(), interaction, player),
						material == Material.CHEST ? "chest" : "trapped chest"
				));

			}

		} else if(plugin.shulkerBoxMaterials.contains(material)) { // changing a shulker box

			player.sendMessage(getInteractionMessage(
					interaction,
					ContainerStorageUtil.changeContainerState(block.getLocation(), interaction, player),
					"shulker box"
			));

		} else return;

		e.setCancelled(true);

	}



	private @Nullable String getInteractionMessage(@NotNull ChestLockPlugin.Interaction interaction, @NotNull boolean successful, @NotNull String containerName) {

		if(successful) return switch (interaction.type()) {
			case LOCK -> ChatColor.GREEN + "Successfully locked " + containerName;
			case UNLOCK -> ChatColor.GREEN + "Successfully unlocked " + containerName;
			case SHARE -> ChatColor.GREEN + "Successfully shared " + containerName + " with " + interaction.other();
			case UNSHARE -> ChatColor.GREEN + "Successfully stopped sharing " + containerName + " with " + interaction.other();
			case INFO -> null;
		};

		return switch(interaction.type()) {
			case LOCK -> ChatColor.RED + "Could not lock " + containerName;
			case UNLOCK -> ChatColor.RED + "Could not unlock " + containerName;
			case SHARE -> ChatColor.RED + "Could not share " + containerName + " with " + interaction.other();
			case UNSHARE -> ChatColor.RED + "Could not stop sharing " + containerName + " with " + interaction.other();
			case INFO -> null;
		};

	}

}
