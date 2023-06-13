package me.lesar.chestlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class ContainerState {

	public String ownerName;
	public HashSet<String> sharedNames = new HashSet<>();

	public ContainerState(@NotNull String ownerName, @Nullable HashSet<String> sharedNames) {

		this.ownerName = ownerName;
		if(sharedNames != null) this.sharedNames = sharedNames;

	}

}
