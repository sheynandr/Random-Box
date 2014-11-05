package saharnooby.randombox.box;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import saharnooby.randombox.RandomBox;
import saharnooby.randombox.Utils;

public class RewardItem {	
	public static RewardItem fromSection(ConfigurationSection section) {
		if (section == null)
			return null;
		
		RewardItem rewardItem = new RewardItem();
		
		ItemStack item = Utils.section2item(section);
		if (item == null)
			return null;
		else
			rewardItem.item = item;
		
		int probability = section.getInt("chance", 0);
		if (probability < 1)
			return null;
		else
			rewardItem.probability = probability;
		
		return rewardItem;		
	}
	
	private ItemStack item;
	
	private int probability;
	
	public ItemStack getItem() {
		return item;
	}
	
	public int getProbability() {
		return probability;
	}
}
