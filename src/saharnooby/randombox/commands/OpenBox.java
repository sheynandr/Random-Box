package saharnooby.randombox.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import saharnooby.randombox.CommandHandler;
import saharnooby.randombox.CommandResult;
import saharnooby.randombox.RandomBox;

public class OpenBox implements CommandExecutor {
	private RandomBox plugin;
	
	public OpenBox(RandomBox plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("randombox.openbox")) {
			plugin.chatInfo(sender, plugin.getString("noPermissions"));
			return true;
		}
		
		CommandResult result = CommandHandler.openBox(sender);
		if (result.isSuccessful())
			plugin.chatInfo(sender, plugin.getString("itemsDropped") + result.getMessage());
		else
			plugin.chatInfo(sender, plugin.getString("boxOpenError") + ": " + result.getMessage());

		return true;
	}
}
