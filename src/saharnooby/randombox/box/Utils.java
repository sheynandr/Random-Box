package saharnooby.randombox.box;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import saharnooby.randombox.RandomBox;

/**
 * ������ ��� ������ � ��������� �����.
 * @author saharNooby
 * @since October 20, 2014
 */
public class Utils {
	/**
	 * ���������� id ������� �� � �������� (lore) ��� null, ����:
	 * ������� ����� null;
	 * ���������� �������� ����� null;
	 * �������� ����� null ��� �����.
	 * @param item ������� (������ �������).
	 * @return ������ - id �������.
	 */
	public static String getBoxId(ItemStack item) {
		if (item == null) return null;
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;
		
		List<String> lore = meta.getLore();
		if (lore == null) return null;
		
		if (lore.isEmpty()) return null;
		
		return lore.get(0).substring(2);		
	}
	
	/**
	 * ���������� ������ ������������, � ������� �������� ������ � �������.
	 * ����: boxes.<id �������>.
	 * @param id id �������.
	 * @return ������ ConfigurationSection ��� null, ���� ������ �� ����������.
	 */
	public static ConfigurationSection getBoxSection(String id) {
		FileConfiguration config = RandomBox.config.get();
		if (config == null) return null;
		return config.getConfigurationSection("boxes." + id);
	}
	
	/**
	 * ���������� ������ ����� (ItemStack + ����������� ���������),
	 * ������������ �� ������� ��������� section.
	 * @param section ������-������ ���������.
	 * @return ������ RewardItem ��� null, ���� �� ������� ���������� ��������� ����.
	 */
	public static List<RewardItem> getRewardItems(ConfigurationSection section) {
		if (section == null) return null;
		
		List<RewardItem> list = new ArrayList<RewardItem>();
		
		Set<String> keys = section.getKeys(false);
		for (String key : keys) {
			ConfigurationSection itemSection = section.getConfigurationSection(key);
			
			RewardItem rewardItem = new RewardItem(itemSection);
			if (!rewardItem.isValid()) return null; else list.add(rewardItem);
		}
		
		return list;
	}
	
	/**
	 * �� ������� ������ ����� �������� count �����, �������� �� ������ � ������
	 * RewardItem. ������ ������������ � ����.
	 * @param count ������� ����� ����� ������� �� ������
	 * @param itemsList ������ �����
	 * @return ������ �������� �����
	 */
	public static List<RewardItem> getRandomItems(int count, List<RewardItem> itemsList) {
		// ����� �����
		int total = itemsList.size();
		
		// count - ������� ������ �������,
		// �� ����, ���� count ����� �� total
		
		// �� �� ������ 1, total >= count
		if ((count < 1) || (total < 1) || (total < count)) return null;
		
		List<RewardItem> resultList = new ArrayList<RewardItem>();
		
		// ���� ������� �����, ������� ����� �����, ������ ����, ������
		// ���� �������� ����
		while (count > 0) {
			RandomBox.debugInfo("----- ITERATION -----");
			
			// ����� ����� ���� ������������
			float chanceSum = 0;
			for (RewardItem item : itemsList) {
				chanceSum += item.getChance();
			}
			RandomBox.debugInfo("Sum: " + Float.toString(chanceSum));
			
			// ������ ������������ ��������� ������ ����, ���������� � ���������
			List<Float> percentList = new ArrayList<Float>();
			
			// ��� ������ ���� ������������ ���� � ����� ����� (� ���������)
			for (RewardItem item : itemsList) {
				// ����������� % = (���� ���� / ����� ������)  * 100
				percentList.add((float) (item.getChance() / chanceSum * 100));
				RandomBox.debugInfo("Percent: " + Float.toString(percentList.get(percentList.size() - 1)));
			}
			
			RandomBox.debugInfo("Size: " + Integer.toString(itemsList.size()));
			
			// ��������� ����� �� ��������
			Collections.sort(percentList);
			RewardItem.sortByChance(itemsList);
			
			Random random = new Random();
			// ���������� ��������� ����� ����� �� 0 �� ����� ������ + 1 ������������
			float randInt = random.nextInt(100) + random.nextFloat(); // from 0 to 100 inclusive.
			
			RandomBox.debugInfo("Random number: " + Float.toString(randInt));
			
			// ����� �������� �������� ������� % ������������
			int i = 0;
			// ���������� ��� ��������, ���� �� ����� �����, ������� �����
			// �������� � ���������� �� ����������� �������� �� ��������.
			// ��������, ���� ���� 2 ����� 60% � 40%, �� ������ �����������,
			// ��� ��������� ����� ����� � ���������� �� 0 �� 60.
			while (i < percentList.size() - 1) {				
				float percent = percentList.get(i);
				float prevPercent = (i > 0) ? percentList.get(i - 1) : 0; 
				
				RandomBox.debugInfo("Checking: ( " + Float.toString(prevPercent) + " ; " + Float.toString(percent) + " ]");
				
				if ((randInt > prevPercent) && (randInt <= percent)) break;
				
				i++;
			}
			
			RandomBox.debugInfo("i: " + Integer.toString(i));
			
			// ��������� �������� ���� �� ������� ������ � ���������
			resultList.add(itemsList.get(i));
			
			// ������ ������� �������, ������� �����, ������, ������� ��� ��
			// ������� �����
			itemsList.remove(i);
			
			// ��������� ������� ���������� �����, ������� �������� �����
			count--;
			
			total--;
		}
		
		return resultList;
	}
	
	/**
	 * ������� ��������� ������ � ���������?
	 * @param inv ��������� ������.
	 * @return ����� ����� ��������� ������.
	 */
	public static int getFreeSlotsCount(PlayerInventory inv) {
		int count = 0;
		for(ItemStack i : inv.getContents()) {
		    if (i == null) {
		    	count++;
		    	continue;
		    }
			if (i.getType() == Material.AIR) {
		        count++;
		    }
		}
		return count;
	}
	
	/**
	 * ������ ���� �� ������ list ������ player.
	 * �������� �� ������� ���������� ����� �� ����������.
	 * @param player �����, �������� ���� ������ ����.
	 * @param list ������ �����, ������� ���� ������.
	 */
	public static void giveRewardItems(Player player, List<RewardItem> list) {
		PlayerInventory inv = player.getInventory();
		for (RewardItem item : list) {
			inv.addItem(item.getItem());
		}
	}
	
	/**
	 * ��� �������� ��� �������, ��������� �� ��� ���������, ������� ����� �������.
	 * ������: 1. ���� 1 <����� ������> 2. ���� 2
	 * @param items ������-������ �����
	 * @return ������ ��� null, ���� ��� ����� ��� ��� ����� �� � ����� ����.
	 */
	private static List<String> getContentList(ConfigurationSection items) {
		if (items == null) return null;
		
		Set<String> keys = items.getKeys(false);
		if (keys == null) return null;
		
		List<String> list = new ArrayList<String>();
		
		for (String key : keys) {
			String name = items.getString(key + ".name", null);
			if (name == null) continue;
			int amount = items.getInt(key + ".amount", 0);
			if (amount < 1) continue;
			list.add("�7" + Integer.toString(list.size() + 1) + ".�e x" + Integer.toString(amount) + " " + name);
		}
				
		if (list.isEmpty()) return null; else return list;
	}
	
	/**
	 * ���������� ItemStack �������, ������������ �� ������ section.
	 * @param section ������ �������.
	 * @return ItemStack ������� ��� null, ���� ������ ������� �������.
	 */
	public static ItemStack getBoxItem(ConfigurationSection section) {
		// �������� id �������
		int boxMatInt = section.getInt("boxItem", -1); 		
		
		// ��������� id ������� (���� STORAGE_MINECART)
		String boxMatString = section.getString("boxItem");
		
		Material boxMat;
		try {
			// ���� �������� id ����������, �� ����� �������� �� ����
			if (boxMatInt > -1) {
				boxMat = Material.getMaterial(boxMatInt);
				if (boxMat == null) return null;
			// ���� ���, �� ���������� ��������� id, �� ����� �� ����
			} else if (boxMatString != null) {
				boxMat = Enum.valueOf(Material.class, boxMatString);
			// ���� ��� �������, �� �������
			} else return null;
		} catch (Exception e) {
			return null;
		}
		
		ItemStack box = new ItemStack(boxMat, 1);
		
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(boxMat);

		String name = section.getString("boxName");
		if (name != null) meta.setDisplayName("�r" + name);
		
		List<String> lore = new ArrayList<String>();
		lore.add("�8" + section.getName());
		
		List<String> list = getContentList(section.getConfigurationSection("items"));
		if (list != null) {
			lore.add("�6" + RandomBox.config.getStringById("itemsToDrop"));
			lore.addAll(list);		
		}
		
		meta.setLore(lore);
		
		box.setItemMeta(meta);
		
		box.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);	
		
		return box;
	}
	
	/**
	 * ��� ������� ������.
	 * @param player �����, �������� ������ �������.
	 * @param item ��� ������������ ������� � ���� ItemStack.
	 * @return True, ���� ������� ���������, False ���� ��� �����.
	 */
	public static Boolean giveBox(Player player, ItemStack item) {
		PlayerInventory inv = player.getInventory();
		
		int firstEmpty = inv.firstEmpty();
		if (firstEmpty < 0) return false;
		
		inv.addItem(item);
		
		return true;		
	}
	
	public static String getFlatItemsList(List<RewardItem> list) {
		String result = "";
		
		for (RewardItem item : list) {
			ItemMeta meta = item.getItem().getItemMeta();
			if (meta != null) {
				String name = meta.getDisplayName();
				if (name != null) {
					int amount = item.getItem().getAmount();
					if (amount > 0) {
						if (result.isEmpty())
							result += name + " �rx" + Integer.toString(amount);
						else
							result += ", " + name + " �rx" + Integer.toString(amount);
					}
				}
			}
						
		}
		
		return result;
	}
}
