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

		if(args.length == 1) {

			if(args[0].length() == 0) return firstArgs;

			List<String> currArgs = new ArrayList<>();
			for(String arg : firstArgs) if(arg.toLowerCase().startsWith(args[0].toLowerCase())) currArgs.add(arg);
			return currArgs;

		}
		if(args.length == 2 && args[0].length() > 0 && (args[0].equalsIgnoreCase("share") || args[0].equalsIgnoreCase("unshare"))) {

			Player player = (Player) sender;

			HashSet<String> otherPlayers = new HashSet<>();
			for(Player online : plugin.getServer().getOnlinePlayers()) otherPlayers.add(online.getName());
			for(OfflinePlayer offline : plugin.getServer().getOfflinePlayers()) otherPlayers.add(offline.getName());
			if(player.getName() != "") otherPlayers.remove(player.getName());
			if(player.getDisplayName() != "") otherPlayers.remove(player.getDisplayName());

			return otherPlayers.stream().toList();

		}
		return new ArrayList<>();

	}

}
