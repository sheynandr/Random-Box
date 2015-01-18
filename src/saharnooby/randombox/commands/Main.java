package saharnooby.randombox.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import saharnooby.randombox.RandomBox;

public class Main implements CommandExecutor {
	private RandomBox plugin;
	
	public Main(RandomBox plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("randombox.main")) {
			plugin.chatInfo(sender, plugin.getString("noPermissions"));
			return true;
		}
		
		if (args.length == 0) {
			PluginDescriptionFile dsc = plugin.getDescription();
			
			sender.sendMessage("§6" + dsc.getName() + " §b" + dsc.getVersion() + " §bby §6saharNooby");
			
			return true;
		} else if (args.length == 1) if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("randombox.main.reload")) {
				plugin.chatInfo(sender, plugin.getString("noPermissions"));
				return true;
			}
			
			plugin.reloadConfig();
			plugin.load();
			
			plugin.chatInfo(sender, plugin.getString("configReloaded"));
			
			return true;
		}
		
		return false;
	}
}
