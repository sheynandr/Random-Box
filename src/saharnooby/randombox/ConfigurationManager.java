package saharnooby.randombox;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Методы для работы с конфигом плагина:
 * загрузка, перезагрузка, сброс
 * @author saharNooby
 *
 */
public class ConfigurationManager {
	private FileConfiguration config;
	
	private RandomBox plugin;
	
	public ConfigurationManager(final RandomBox plugin) {
		this.plugin = plugin;
	}
	
	public FileConfiguration get() {
		return config;
	}
	
	public void load() {
		RandomBox.toLog("Loading config...");
		File configFile = new File(plugin.getDataFolder(), "config.yml");		
		if (!configFile.exists()) {
			plugin.saveDefaultConfig();
			RandomBox.toLog("Created default config.");
		}
		config = plugin.getConfig();
		RandomBox.toLog("Configuration loaded.");
	}
	
	public void reload() {
		RandomBox.toLog("Reloading config...");
		plugin.reloadConfig();
		config = plugin.getConfig();
		RandomBox.toLog("Configuration reloaded.");
	}
	
	public String getStringById(String id) {
		return config.getString("strings." + id);
	}
}