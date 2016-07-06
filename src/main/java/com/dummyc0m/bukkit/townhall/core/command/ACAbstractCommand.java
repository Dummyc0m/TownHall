package com.dummyc0m.bukkit.townhall.core.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Dummyc0m on 7/30/15.
 */
public abstract class ACAbstractCommand {
    private String label;
    private String permission;
    private boolean playerRequired;

    public ACAbstractCommand(String label, String permission, boolean playerRequired) {
        this.label = label;
        this.permission = permission;
        this.playerRequired = playerRequired;
    }

    public abstract void execute(CommandSender sender, String[] args);

    protected void processCommand(CommandSender sender, String[] args) {
        if (checkPermission(sender) && checkPlayer(sender)) {
            execute(sender, args);
        }
    }

    public String getLabel() {
        return label;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isPlayerRequired() {
        return playerRequired;
    }

    protected boolean checkPermission(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
    }

    protected boolean checkPlayer(CommandSender sender) {
        return !playerRequired || sender instanceof Player;
    }
}
