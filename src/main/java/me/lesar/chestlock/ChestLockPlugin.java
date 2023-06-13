package me.lesar.chestlock;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import me.lesar.chestlock.commands.*;
import me.lesar.chestlock.listeners.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public final class ChestLockPlugin extends JavaPlugin {

	public record Interaction(InteractionType type, @Nullable String other){}

	public HashMap<Player, Interaction> interactingPlayers = new HashMap<>();

	public final HashSet<Material> shulkerBoxMaterials = new HashSet<>() {{
		add(Material.SHULKER_BOX);
		add(Material.CYAN_SHULKER_BOX);
		add(Material.BROWN_SHULKER_BOX);
		add(Material.GREEN_SHULKER_BOX);
		add(Material.GRAY_SHULKER_BOX);
		add(Material.BLUE_SHULKER_BOX);
		add(Material.BLACK_SHULKER_BOX);
		add(Material.LIGHT_BLUE_SHULKER_BOX);
		add(Material.LIGHT_GRAY_SHULKER_BOX);
		add(Material.LIME_SHULKER_BOX);
		add(Material.MAGENTA_SHULKER_BOX);
		add(Material.ORANGE_SHULKER_BOX);
		add(Material.PINK_SHULKER_BOX);
		add(Material.PURPLE_SHULKER_BOX);
		add(Material.RED_SHULKER_BOX);
		add(Material.WHITE_SHULKER_BOX);
		add(Material.YELLOW_SHULKER_BOX);
	}};
	public final HashSet<Material> containerMaterials = new HashSet<>() {{
		add(Material.CHEST);
		add(Material.TRAPPED_CHEST);
		addAll(shulkerBoxMaterials);
	}};

	@Override
	public void onEnable() {

		registerCommands();
		registerListeners();

		initStorageUtil();

	}

	@Override
	public void onDisable() {

		shutdownStorageUtil();

	}



	private void registerCommands() {

		new ChestCommand(this);

	}

	private void registerListeners() {

		new InteractListener(this);
		new StopInteractListener(this);
		new OpenContainerListener(this);
		new BlockListener(this);
		new InventoryListener(this);

	}

	private void initStorageUtil() {

		try {

			ContainerStorageUtil.loadData(this);


		} catch (JsonIOException e) {

			getLogger().severe("Could not parse json, your data may be corrupted! Creating new data instead");
			ContainerStorageUtil.createNewData(this);

		} catch (IOException e) {

			getLogger().warning("Could not open file! Does it exist? Creating new data instead");
			ContainerStorageUtil.createNewData(this);

		}

	}

	private void shutdownStorageUtil() {

		try {

			ContainerStorageUtil.saveData(this);

		} catch (IOException e) {

			getLogger().severe("Could not save data! Dumping here instead");
			getLogger().info(ContainerStorageUtil.dataToString());

		}

	}

}
