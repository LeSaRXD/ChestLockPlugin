package me.lesar.chestlock.completers;

import me.lesar.chestlock.ChestLockPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ChestCompleter implements TabCompleter {

	private final ChestLockPlugin plugin;
	private final List<String> firstArgs = new ArrayList<>() {{
		add("lock");
		add("unlock");
		add("share");
		add("unshare");
		add("info");
	}};

	public ChestCompleter(ChestLockPlugin plugin, String command) {

		this.plugin = plugin;
		plugin.getCommand(command).setTabCompleter(this);

	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(!(sender instanceof Player)) return new ArrayList<>();

		if(args.length == 1) return firstArgs;
		if(args.length == 2 && (args[0].equalsIgnoreCase("share") || args[0].equalsIgnoreCase("unshare"))) {

			Player player = (Player) sender;

			HashSet<String> otherPlayers = new HashSet<>();
			for(Player online : plugin.getServer().getOnlinePlayers()) otherPlayers.add(online.getName());
			for(OfflinePlayer offline : plugin.getServer().getOfflinePlayers()) otherPlayers.add(offline.getName());
			otherPlayers.remove(player.getName());

			return otherPlayers.stream().toList();

		}
		return new ArrayList<>();

	}

}
