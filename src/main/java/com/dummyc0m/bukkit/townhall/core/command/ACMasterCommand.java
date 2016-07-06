package com.dummyc0m.bukkit.townhall.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Created by Dummyc0m on 7/30/15.
 */
public class ACMasterCommand extends ACAbstractCommand implements CommandExecutor {
    private Map<String, ACAbstractCommand> subCommands = new LinkedHashMap<>();

    public ACMasterCommand(String label, String permission, boolean playerRequired) {
        super(label, permission, playerRequired);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        processCommand(sender, args);
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args != null) {
            ACAbstractCommand subCommand = searchCommand(args[0]);
            if (subCommand != null) {
                subCommand.processCommand(sender, shiftArgs(args));
            }
        }
    }

    public List<ACAbstractCommand> getSubCommands() {
        return Collections.unmodifiableList(new ArrayList<>(subCommands.values()));
    }

    public void addSubCommand(ACAbstractCommand subCommand) {
        subCommands.put(subCommand.getLabel(), subCommand);
    }

    public ACAbstractCommand removeSubCommand(String label) {
        return subCommands.remove(label);
    }

    private ACAbstractCommand searchCommand(String string) {
        ACAbstractCommand subCommand = subCommands.get(string);
        if (subCommand == null) {
            subCommand = subCommands.get("help");
        }
        return subCommand;
    }

    private String[] shiftArgs(String[] args) {
        return args.length > 1 ? Arrays.copyOfRange(args, 1, args.length - 1) : null;
    }
}
