package saharnooby.randombox;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import saharnooby.randombox.box.Box;
import saharnooby.randombox.box.RewardItem;

public class CommandHandler {
	public static CommandResult openBox(CommandSender sender) {
		CommandResult result = new CommandResult(); 	
		
		Player player;		
		if (sender instanceof Player) {
			player = (Player) sender; 	
		} else {
			return result.fail(true, "notAPlayer");
		}
		
		Box box = Box.fromItemStack(player.getItemInHand());
		if (box == null)
			return result.fail(true, "itemIsNotABox");
		
		if (
				box.getCheckPermission() &&
				!(
						player.hasPermission("randombox.open." + box.getId()) ||
						player.hasPermission("randombox.open.*")
				)
			)
			return result.fail(true, "noPermissionsToOpen");
		
		PlayerInventory inventory = player.getInventory();
		
		int freeCount = 0;
		
		for(ItemStack i : inventory.getContents()) {
		    if (i == null) {
		    	freeCount++;
		    	continue;
		    }
			if (i.getType() == Material.AIR) {
				freeCount++;
		    }
		}

		if (freeCount < box.getItemsToSelectCount())
			return result.fail(true, "notEnoughSpace");
		
		// Удаляем коробочку!		
		if (!sender.hasPermission("randombox.infinitebox")) {
			ItemStack item = player.getItemInHand();
			
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else
				player.setItemInHand(new ItemStack(Material.AIR, 0));
		}
		
		List<RewardItem> rewardItems = box.getRandomItems();
		
		for (RewardItem item : rewardItems) {
			inventory.addItem(item.getItem());
		}
		
		return result.successful(false, Utils.getFlatItemsList(rewardItems));
	}
	
	public static CommandResult giveBox(CommandSender sender, String[] args) {
		CommandResult result = new CommandResult(); 
		
		Player player = Bukkit.getPlayer(args[0]);
		
		if (player == null)
			return result.fail(true, "playerNotFound");	
		else if (!player.isOnline())
			return result.fail(true, "playerNotFound");	

		ConfigurationSection section = RandomBox.getConfiguration().getConfigurationSection("boxes." + args[1]);
		if (section == null)
			return result.fail(true, "noSuchBox");	
		
		Box box = Box.fromSection(section);
		if (box == null)
			return result.fail(true, "boxParsingError");	

		int slot = player.getInventory().firstEmpty();
		if (slot == -1)
			return result.fail(true, "notEnoughSpace");	
		
		player.getInventory().addItem(box.getItem());
		
		return result.successful(true, "boxGiven");
	}
}
