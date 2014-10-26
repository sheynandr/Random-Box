package saharnooby.randombox.box;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import saharnooby.randombox.Errors;

/**
 * Методы, запускаемые при выполнении
 * команд givebox и openbox.
 * @author saharNooby
 *
 */
public class BoxCommands {		
	public static Boolean openBox(CommandSender sender) {
		Player player;
		
		if (sender instanceof Player) player = (Player) sender;
		else {
			Errors.setLastErrorId(sender, "notAPlayer");
			return false;		
		}
		
		ItemStack item = player.getInventory().getItemInHand();
		
		String id = Utils.getBoxId(item);
		if (id == null) {
			Errors.setLastErrorId(sender, "itemIsNotABox");
			return false;		
		}
	
		ConfigurationSection section = Utils.getBoxSection(id);
		if (section == null) {
			Errors.setLastErrorId(sender, "noSuchBox");
			return false;		
		}
		
		Boolean checkPerm = section.getBoolean("checkPermission", false);
		if (checkPerm && !sender.hasPermission("randombox.open." + id) && !sender.hasPermission("randombox.open.*")) {
			Errors.setLastErrorId(sender, "noPermissionsToOpen");
			return false;	
		};		
		
		List<RewardItem> items = Utils.getRewardItems(section.getConfigurationSection("items"));
		if (items == null) {
			Errors.setLastErrorId(sender, "itemsParsingError");
			return false;		
		}
		
		int count = section.getInt("itemsToSelectCount", 0);
		if (count < 1) {
			Errors.setLastErrorId(sender, "itemsCountMissing");
			return false;		
		}
		
		if (Utils.getFreeSlotsCount(player.getInventory()) < count) {
			Errors.setLastErrorId(sender, "notEnoughSpace");
			return false;		
		}
		
		// Удаляем коробочку!
		if (!player.hasPermission("randombox.infinitebox")) {
			ItemStack handItem = player.getItemInHand();
			if (handItem.getAmount() > 1)
				handItem.setAmount(handItem.getAmount() - 1);
			else
				player.getInventory().setItemInHand(new ItemStack(Material.AIR, 0));
		}
		
		List<RewardItem> rewardItems = Utils.getRandomItems(count, items);
		
		// Это не ошибка, а список вещей
		Errors.setLastError(sender, Utils.getFlatItemsList(rewardItems));
		
		Utils.giveRewardItems(player, rewardItems);
		
		return true;	
	}

	public static Boolean giveBox(CommandSender sender, String nick, String boxId) {
		Player player = Bukkit.getPlayer(nick);
		
		if (player == null) {
			Errors.setLastErrorId(sender, "playerNotFound");
			return false;	
		}

		ConfigurationSection section = Utils.getBoxSection(boxId);
		if (section == null) {
			Errors.setLastErrorId(sender, "noSuchBox");
			return false;		
		}
		
		ItemStack box = Utils.getBoxItem(section);
		if (box == null) {
			Errors.setLastErrorId(sender, "boxParsingError");
			return false;		
		}
		
		if (!Utils.giveBox(player, box)) {
			Errors.setLastErrorId(sender, "notEnoughSpace");
			return false;				
		}
		
		return true;
	}
}
