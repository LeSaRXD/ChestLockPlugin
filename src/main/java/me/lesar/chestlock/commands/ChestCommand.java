package me.lesar.chestlock.commands;

import me.lesar.chestlock.ChestLockPlugin;
import me.lesar.chestlock.InteractionType;
import me.lesar.chestlock.completers.ChestCompleter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChestCommand implements CommandExecutor {

	private final ChestLockPlugin plugin;

	public ChestCommand(ChestLockPlugin plugin) {

		this.plugin = plugin;
		plugin.getCommand("chest").setExecutor(this);
		new ChestCompleter(plugin, "chest");

	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(!(sender instanceof Player)) {

			sender.sendMessage(ChatColor.RED + "You are not a player!");
			return true;

		}

		ChestLockPlugin.Interaction interaction = parseArgs(args);
		if(interaction == null) return false;

		Player player = (Player) sender;

		switch(interaction.type()) {
			case LOCK:
				if(player.hasPermission("chestlock.lock")) player.sendMessage(ChatColor.GREEN + "Left click chests you want to lock. Sneak to stop");
				else player.sendMessage(ChatColor.RED + "You do not have the permission to lock chests");
				break;
			case UNLOCK:
				if(player.hasPermission("chestlock.unlock")) player.sendMessage(ChatColor.GREEN + "Left click chests you want to unlock. Sneak to stop");
				else player.sendMessage(ChatColor.RED + "You do not have the permission to unlock chests");
				break;
			case SHARE:
				if(player.hasPermission("chestlock.share")) player.sendMessage(ChatColor.GREEN + "Left click chests you want to share with " + interaction.other() + ". Sneak to stop");
				else player.sendMessage(ChatColor.RED + "You do not have the permission to share chests");
				break;
			case UNSHARE:
				if(player.hasPermission("chestlock.unshare")) player.sendMessage(ChatColor.GREEN + "Left click chests you want to stop sharing with " + interaction.other() + ". Sneak to stop");
				else player.sendMessage(ChatColor.RED + "You do not have the permission to stop sharing chests");
				break;
			case INFO:
				if(player.hasPermission("chestlock.admin.info")) player.sendMessage(ChatColor.GREEN + "Left click chests to view their info. Sneak to stop");
				else player.sendMessage(ChatColor.RED + "You do not have the permission to view info about chests");
				break;
		}
		plugin.interactingPlayers.put(player, interaction);

		return true;

	}

	private @Nullable ChestLockPlugin.Interaction parseArgs(String[] args) {

		if(args.length == 0) return null;
		if(args[0].equalsIgnoreCase("lock")) return new ChestLockPlugin.Interaction(InteractionType.LOCK, null);
		if(args[0].equalsIgnoreCase("unlock")) return new ChestLockPlugin.Interaction(InteractionType.UNLOCK, null);
		if(args[0].equalsIgnoreCase("info")) return new ChestLockPlugin.Interaction(InteractionType.INFO, null);

		if(args.length < 2) return null;
		if(args[0].equalsIgnoreCase("share")) return new ChestLockPlugin.Interaction(InteractionType.SHARE, args[1]);
		if(args[0].equalsIgnoreCase("unshare")) return new ChestLockPlugin.Interaction(InteractionType.UNSHARE, args[1]);
		return null;

	}

}
