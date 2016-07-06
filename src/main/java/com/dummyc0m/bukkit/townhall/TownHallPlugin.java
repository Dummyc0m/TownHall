package com.dummyc0m.bukkit.townhall;

import com.dummyc0m.bukkit.townhall.core.chatinput.ChatListener;
import com.dummyc0m.bukkit.townhall.core.chatinput.CoreChatTrigger;
import com.dummyc0m.bukkit.townhall.core.config.ACConfig;
import com.dummyc0m.bukkit.townhall.core.inventory.InventoryListener;
import com.dummyc0m.bukkit.townhall.core.item.CoreItem;
import com.dummyc0m.bukkit.townhall.core.item.ItemListener;
import com.dummyc0m.bukkit.townhall.core.util.StringTranslator;
import com.dummyc0m.bukkit.townhall.item.AdminStaff;
import com.dummyc0m.bukkit.townhall.item.MessageStaff;
import com.dummyc0m.bukkit.townhall.item.TownStaff;
import com.dummyc0m.bukkit.townhall.region.RegionListener;
import com.dummyc0m.bukkit.townhall.region.RegionManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by Dummyc0m on 3/7/16.
 */
public class TownHallPlugin extends JavaPlugin {
    public static final String version = "0.1-SNAPSHOT for 1.9";
    private static TownHallPlugin plugin;
    private static Logger logger;
    private CoreItem coreItem;
    private CoreChatTrigger coreChatTrigger;
    private RegionManager regionManager;
    private BukkitTask saveTask;
    private ACConfig configFile;

    public static TownHallPlugin getInstance() {
        return plugin;
    }

    public static Logger logger() {
        return logger;
    }

    public CoreItem getCoreItem() {
        return coreItem;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public CoreChatTrigger getCoreChatTrigger() {
        return coreChatTrigger;
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();
        configFile = new ACConfig(getDataFolder(), "townhall.json", Settings.class);
        Settings settings = ((Settings) configFile.getSettings());
        assert settings != null;
        settings.setRegionValues();
        Settings.setSettings(settings);
        if (!settings.verify()) {
            getServer().getPluginManager().disablePlugin(this);
            logger.severe("There is an error in the config file, plugin disabled");
            return;
        }

        logger.info("Registering Locale");
        StringTranslator.getTranslator().addLangResource("townhall", "en_US");
        if (!settings.getLocale().equals("en_US")) {
            StringTranslator.getTranslator().addLangFile(new File(getDataFolder().getAbsolutePath(), settings.getLocale() + ".lang"));
        }

        logger.info("Loading Cores");
        coreItem = new CoreItem();
        coreChatTrigger = new CoreChatTrigger();

        try {
            regionManager = new RegionManager(settings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        saveTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                regionManager.saveConcurrently();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 6000, 36000);

        logger.info("Registering Listeners");
        this.getServer().getPluginManager().registerEvents(new ItemListener(this, coreItem), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        this.getServer().getPluginManager().registerEvents(new RegionListener(regionManager), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(coreChatTrigger), this);

        logger.info("Registering Items");
        AdminStaff adminStaff = new AdminStaff(regionManager, coreChatTrigger);
        TownStaff townStaff = new TownStaff(this, regionManager, coreChatTrigger);
        MessageStaff messageStaff = new MessageStaff(regionManager, coreChatTrigger);
        coreItem.registerItem(adminStaff);
        coreItem.registerItem(townStaff);
        coreItem.registerItem(messageStaff);

        logger.info("Adding Crafting Recipes");
        ShapelessRecipe townStaffRecipe = new ShapelessRecipe(townStaff.getItemStack());
        townStaffRecipe.addIngredient(9, Material.EMERALD_BLOCK);
        getServer().addRecipe(townStaffRecipe);

        ShapelessRecipe messageStaffRecipe = new ShapelessRecipe(messageStaff.getItemStack());
        messageStaffRecipe.addIngredient(1, Material.PAPER);
        messageStaffRecipe.addIngredient(1, Material.INK_SACK);
        messageStaffRecipe.addIngredient(1, Material.FEATHER);
        getServer().addRecipe(messageStaffRecipe);

        logger.info("Enabled");
    }

    @Override
    public void onDisable() {
        logger.info("Saving Configurations");
        configFile.save();
        saveTask.cancel();
        try {
            regionManager.onStop();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        logger.info("Removing Listeners");
        HandlerList.unregisterAll(this);

        plugin = null;
        coreItem = null;
        regionManager = null;
        logger.info("Disabled");
        logger = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("townhall".equalsIgnoreCase(command.getName()) && sender.hasPermission("townhall.command.townhall")) {
            sender.sendMessage("§bRunning TownHall Ver. " + version);
            if (args != null && args.length > 0 && args[0].equals("update")) {
                Settings.getSettings().setVersion(0);
            } else if (sender instanceof Player) {
                ((Player) sender).getInventory().addItem(coreItem.getItem("townhall:admin_staff").getItemStack(),
                        coreItem.getItem("townhall:town_staff").getItemStack(),
                        coreItem.getItem("townhall:message_staff").getItemStack());
            }
            return true;
        }

        return false;
    }
}
