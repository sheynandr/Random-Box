package saharnooby.randombox.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import saharnooby.randombox.CommandHandler;
import saharnooby.randombox.CommandResult;
import saharnooby.randombox.RandomBox;

public class GiveBox implements CommandExecutor {
	private RandomBox plugin;
	
	public GiveBox(RandomBox plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("randombox.givebox")) {
			plugin.chatInfo(sender, plugin.getString("noPermissions"));
			return true;
		}
		
		if (args.length == 2) {
			CommandResult result = CommandHandler.giveBox(sender, args);
			
			if (result.isSuccessful())
				plugin.chatInfo(sender, result.getMessage());
			else
				plugin.chatInfo(sender, plugin.getString("boxGiveError") + ": " + result.getMessage());

			return true;
		};
		
		return false;
	}
}
