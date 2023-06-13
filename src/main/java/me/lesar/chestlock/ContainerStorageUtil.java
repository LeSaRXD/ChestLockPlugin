package me.lesar.chestlock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

public class ContainerStorageUtil {

	private static HashMap<String, ContainerState> containerStates = new HashMap<>();

	public static void saveData(@NotNull ChestLockPlugin plugin) throws IOException {

		File dataFile = new File(plugin.getDataFolder().getAbsolutePath() + "/containers.json");
		dataFile.getParentFile().mkdir();
		dataFile.createNewFile();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Writer writer = new FileWriter(dataFile, false);
		gson.toJson(containerStates, writer);
		writer.flush();
		writer.close();

		plugin.getLogger().info("Saved " + dataFile);

	}

	public static String dataToString() {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(containerStates);

	}

	public static void loadData(@NotNull ChestLockPlugin plugin) throws IOException, JsonIOException {

		File dataFile = new File(plugin.getDataFolder().getAbsolutePath() + "/containers.json");
		Reader reader = new FileReader(dataFile);
		Gson gson = new Gson();
		containerStates = gson.fromJson(reader, new TypeToken<HashMap<String, ContainerState>>(){}.getType());
		plugin.getLogger().info("Loaded existing data");

	}

	public static void createNewData(@NotNull ChestLockPlugin plugin) {

		containerStates = new HashMap<>();
		plugin.getLogger().info("Created new data");

	}



	private static @Nullable String locationToString(@NotNull Location location) {

		if(location.getX() != location.getBlockX() || location.getY() != location.getBlockY() || location.getZ() != location.getBlockZ()) return null;
		return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();

	}



	public static boolean changeContainerState(@NotNull Location location, @NotNull ChestLockPlugin.Interaction interaction, Player player) {

		return switch(interaction.type()) {
			case LOCK -> lockContainer(location, player.getName(), null);
			case UNLOCK -> unlockContainer(location, player);
			case SHARE -> shareContainer(location, player, interaction.other());
			case UNSHARE -> unshareContainer(location, player, interaction.other());
			default -> false;
		};

	}

	public static boolean removeContainerState(@NotNull Location location) {

		ContainerState state = getContainerState(location);
		if(state == null) return false;

		String key = locationToString(location);
		if(key == null) return false;
		return containerStates.remove(key) != null;

	}

	private static @Nullable ContainerState getContainerState(@NotNull Location location) {

		String key = locationToString(location);
		if(key == null) return null;
		return containerStates.get(key);

	}

	public static boolean isContainerLocked(@NotNull Location location) {

		return getContainerState(location) != null;

	}

	public static boolean canOpenContainer(@NotNull Location location, @NotNull Player player) {

		return getContainerOwner(location).equals(player.getName()) || getContainerShared(location).contains(player.getName()) || player.hasPermission("chestlock.admin.bypass");

	}

	public static @Nullable String getContainerOwner(Location location) {

		ContainerState state = getContainerState(location);
		if(state != null) return state.ownerName;
		return null;

	}

	public static @NotNull String getContainerInfo(Location location) {

		ContainerState state = getContainerState(location);
		if(state == null) return "This container isn't locked";
		return "Container owner: " + state.ownerName + "\nContainer shared with: " + String.join(", ", state.sharedNames);

	}

	public static @Nullable HashSet<String> getContainerShared(Location location) {

		ContainerState state = getContainerState(location);
		if(state != null) return state.sharedNames;
		return null;

	}



	private static boolean lockContainer(@NotNull Location location, @NotNull String ownerName, @Nullable HashSet<String> sharedNames) {

		if(isContainerLocked(location)) return false;

		ContainerState state = new ContainerState(ownerName, sharedNames);

		String key = locationToString(location);
		if(key == null) return false;
		containerStates.put(key, state);

		return true;

	}

	private static boolean unlockContainer(@NotNull Location location, @NotNull Player unlocker) {

		if(isContainerLocked(location)) return false;
		if(!getContainerOwner(location).equals(unlocker.getName()) && !unlocker.hasPermission("chectlock.admin.unlock")) return false;

		String key = locationToString(location);
		if(key == null) return false;
		return containerStates.remove(key) != null;

	}

	private static boolean shareContainer(Location location, Player owner, String shared) {

		if(!isContainerLocked(location)) return false;
		if(!getContainerOwner(location).equals(owner.getName())) return false;

		getContainerShared(location).add(shared);
		return true;

	}

	private static boolean unshareContainer(Location location, Player owner, String shared) {

		if(!isContainerLocked(location)) return false;
		if(!getContainerOwner(location).equals(owner.getName())) return false;

		return getContainerShared(location).remove(shared);

	}

	public static boolean copyContainerState(Location from, Location to) {

		if(!isContainerLocked(from)) return false;

		return lockContainer(to, getContainerOwner(from), (HashSet<String>) getContainerShared(from).clone());

	}

}
