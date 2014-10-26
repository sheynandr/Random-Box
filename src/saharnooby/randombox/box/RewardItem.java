package saharnooby.randombox.box;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import saharnooby.randombox.ItemConverter;
import saharnooby.randombox.RandomBox;

public class RewardItem {
	private ItemStack item = null;
	
	private int chance = 0;
	
	public RewardItem(ConfigurationSection section) {
		if (section == null) return;
		
		int _chance = section.getInt("chance", 0);
		if (_chance < 1) return; else chance = _chance;
	
		ItemStack _item = ItemConverter.section2item(section);
		if (_item == null) return; else item = _item;
	}
	
	public ItemStack getItem() {
		return item;	
	}
	
	public int getChance() {
		return chance;
	}
	
	public Boolean isValid() {
		return (item != null) && (chance > 0);
	}
	
	private static void swapItems(List<RewardItem> list, int i, int j) {
		RewardItem item = list.get(i);
		list.set(i, list.get(j));
		list.set(j, item);
	}
	
	public static void sortByChance(List<RewardItem> itemsList){
	    for (int i = itemsList.size() - 1; i >= 0; i--) {
	        for (int j = 0; j < i; j++) {
	            if(itemsList.get(i).getChance() < itemsList.get(j).getChance())
	            	swapItems(itemsList, j, j + 1);
	        }
	    }
	}
}
