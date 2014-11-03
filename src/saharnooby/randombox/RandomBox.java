package saharnooby.randombox;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import saharnooby.randombox.box.Box;

public class RandomBox extends JavaPlugin implements Listener {
	
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public static void info(final String msg) {
		log.info("[RandomBox] " + msg);
	}
	
	public static void debugInfo(final String msg) {
		if (config.getBoolean("debug", false)) info(msg);
	}
	
	private static FileConfiguration config;
	
	public static FileConfiguration getConfiguration() {
		return config;
	}
	
	private void load() {
		info("Loading config...");
		
		File configFile = new File(getDataFolder(), "config.yml");		
		
		if (!configFile.exists()) {
			saveDefaultConfig();
			RandomBox.info("Created default config.");
		}
		
		config = this.getConfig();
		
		info("Configuration loaded.");		
	}
	
	public static String getString(String id) {
		String s = config.getString("strings." + id);
		if (s != null) s = s.replace("&", "§").replace("§§", "&");
		return s;
	}
	
	public static void chatInfo(CommandSender sender, String msg) {
		String prefix = getString("prefix");
		
		sender.sendMessage(prefix + msg);
	}
	
	public static void chatInfo(Player player, String msg) {
		chatInfo((CommandSender) player, msg);
	}
	
	@Override
	public void onEnable() { 
		Bukkit.getPluginManager().registerEvents(this, this);
		
		load();
		
		info("RandomBox enabled.");
    }
     
	@Override
	public void onDisable() { 
		info("RandomBox disabled.");
    }	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmd = command.getName().toLowerCase();
		switch (cmd) {
		case "randombox": {			
			if (!sender.hasPermission("randombox.main")) {
				chatInfo(sender, getString("noPermissions"));
				return true;
			}
			
			if (args.length == 0) {
				PluginDescriptionFile dsc = getDescription();
				
				sender.sendMessage("§b" + dsc.getName() + " " + dsc.getVersion() + " §3by §bsaharNooby");
				
				return true;
			} else if (args.length == 1) if (args[0].equalsIgnoreCase("reload")) {
				if (!sender.hasPermission("randombox.main.reload")) {
					chatInfo(sender, getString("noPermissions"));
					return true;
				}
				
				this.reloadConfig();
				config = getConfig();
				
				chatInfo(sender, getString("configReloaded"));
				
				return true;
			}
			
			break;
		}
		case "openbox": {
			if (!sender.hasPermission("randombox.openbox")) {
				chatInfo(sender, getString("noPermissions"));
				return true;
			}
			
			CommandResult result = CommandHandler.openBox(sender);
			if (result.isSuccessful())
				chatInfo(sender, getString("itemsDropped") + result.getMessage());
			else
				chatInfo(sender, getString("boxOpenError") + ": " + result.getMessage());

			return true;
		}
		case "givebox": {
			if (!sender.hasPermission("randombox.givebox")) {
				chatInfo(sender, getString("noPermissions"));
				return true;
			}
			
			if (args.length == 2) {
				CommandResult result = CommandHandler.giveBox(sender, args);
				if (result.isSuccessful())
					chatInfo(sender, result.getMessage());
				else
					chatInfo(sender, getString("boxGiveError") + ": " + result.getMessage());

				return true;
			};
			break;
		}
		}

		return false;
	}	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if ( (action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK) ) {
			Box box = Box.fromItemStack(event.getItem());
			if (box == null)
				return;
			else {
				event.setCancelled(true);
				
				if (box.getOpenWhenClicked()) {
					Player player = event.getPlayer();
					
					CommandResult result = CommandHandler.openBox((CommandSender) player);
					
					player.updateInventory();
					
					if (result.isSuccessful())
						chatInfo(player, getString("itemsDropped") + result.getMessage());
					else
						chatInfo(player, getString("boxOpenError") + ": " + result.getMessage());
				}
			}
		}
	}
}