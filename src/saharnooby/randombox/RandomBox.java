package saharnooby.randombox;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import saharnooby.randombox.commands.GiveBox;
import saharnooby.randombox.commands.Main;
import saharnooby.randombox.commands.OpenBox;
import saharnooby.randombox.metrics.MetricsLite;

public class RandomBox extends JavaPlugin {
	
	// Log
	
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public static void info(final String msg) {
		log.info("[RandomBox] " + msg);
	}
	
	public static void debugInfo(final String msg) {
		if (config.getBoolean("debug", false)) info(msg);
	}
	
	// Configuration
	
	private static FileConfiguration config;
	
	public static FileConfiguration getConfiguration() {
		return config;
	}
	
	public void load() {
		info("Loading config...");
		
		File configFile = new File(getDataFolder(), "config.yml");		
		
		if (!configFile.exists()) {
			saveDefaultConfig();
			RandomBox.info("Created default config.");
		}
		
		config = this.getConfig();
		
		info("Configuration loaded.");		
	}
	
	// Chat
	
	public static void chatInfo(CommandSender sender, String msg) {
		String prefix = getString("prefix");
		
		sender.sendMessage(prefix + msg);
	}
	
	public static void chatInfo(Player player, String msg) {
		chatInfo((CommandSender) player, msg);
	}
	
	public static String getString(String id) {
		if (id == null)
			return null;
		
		String locale = config.getString("locale");
		if (locale == null)
			return null;
		
		String s = config.getString("strings." + locale + "." + id);		
		if (s != null)
			s = s.replace("&", "§").replace("§§", "&");
		
		return s;
	}
	
	// Events
	
	@Override
	public void onEnable() { 		
		Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
		
		getCommand("randombox").setExecutor(new Main(this));
		getCommand("openbox").setExecutor(new OpenBox(this));
		getCommand("givebox").setExecutor(new GiveBox(this));
		
		load();
		
		// Metrics
		
		if (config.getBoolean("metrics", false))
		    try {
		        MetricsLite metrics = new MetricsLite(this);
		        metrics.start();
		    } catch (IOException e) {
		        // Failed to submit the stats :-(
		    }
    }
	
}