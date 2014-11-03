package saharnooby.randombox.box;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import saharnooby.randombox.RandomBox;
import saharnooby.randombox.Utils;

public class Box {
	public static Box fromItemStack(ItemStack item) {
		if (item == null) return null;
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;
		
		List<String> lore = meta.getLore();
		if (lore == null) return null;
		
		if (lore.isEmpty()) return null;
		
		String id = lore.get(0).substring(2);
		
		return fromSection(RandomBox.getConfiguration().getConfigurationSection("boxes." + id));
	}
	
	public static Box fromSection(ConfigurationSection section) {
		if (section == null)
			return null;
		
		Box box = new Box();
		
		box.id = section.getName();
		
		ItemStack item = Utils.section2box(section);
		if (item == null)
			return null;
		else
			box.item = item;
		
		int itemsToSelectCount = section.getInt("itemsToSelectCount", 0);
		if (itemsToSelectCount < 1)
			return null;
		else
			box.itemsToSelectCount = itemsToSelectCount;
		
		box.checkPermission = section.getBoolean("checkPermission", false);
		
		box.openWhenClicked = section.getBoolean("openWhenClicked", false);
		
		ConfigurationSection itemsSection = section.getConfigurationSection("items");
		if (itemsSection != null) {
			Set<String> keys = itemsSection.getKeys(false);
			
			List<RewardItem> items = new ArrayList<RewardItem>();
			
			for (String key : keys) {
				RandomBox.debugInfo("Key = " + key);
				
				RewardItem rewardItem = RewardItem.fromSection(itemsSection.getConfigurationSection(key));
				if (rewardItem == null)
					return null;
				else
					items.add(rewardItem);			
			}
			
			box.items = items;
		} else
			return null;
		
		Utils.fillBoxLore(box);
		
		return box;
	}
	
	private String id;
	
	private ItemStack item;
	
	private int itemsToSelectCount;
	
	private Boolean checkPermission;
	
	private Boolean openWhenClicked;
	
	private List<RewardItem> items;

	public String getId() {
		return id;
	}

	public ItemStack getItem() {
		return item;
	}

	public int getItemsToSelectCount() {
		return itemsToSelectCount;
	}

	public Boolean getCheckPermission() {
		return checkPermission;
	}

	public Boolean getOpenWhenClicked() {
		return openWhenClicked;
	}

	public List<RewardItem> getItems() {
		return items;
	}
	
	public List<RewardItem> getRandomItems() {
		int total = items.size();
		int count = itemsToSelectCount;

		if ((count < 1) || (total < 1) || (total < count)) {
			RandomBox.debugInfo("getRandomItems: returning null");
			RandomBox.debugInfo("total: " + Integer.toString(total));
			RandomBox.debugInfo("count: " + Integer.toString(count));
			return null;
		}
		
		List<RewardItem> items = new ArrayList<RewardItem>(this.items);		
		List<RewardItem> resultItems = new ArrayList<RewardItem>();
		
		// ���� ������� �����, ������� ����� �����, ������ ����, ������
		// ���� �������� ����
		while (count > 0) {
			RandomBox.debugInfo("----- ITERATION -----");
			
			// ����� ����� ���� ������������
			float chanceSum = 0;
			for (RewardItem item : items) {
				chanceSum += item.getProbability();
			}
			RandomBox.debugInfo("Sum: " + Float.toString(chanceSum));
			
			// ������ ������������ ��������� ������ ����, ���������� � ���������
			List<Float> percents = new ArrayList<Float>();
			
			// ��� ������ ���� ������������ ���� � ����� ����� (� ���������)
			for (RewardItem item : items) {
				// ����������� % = (���� ���� / ����� ������)  * 100
				percents.add( (float) (item.getProbability() / chanceSum * 100) );
				RandomBox.debugInfo("Percent: " + Float.toString(percents.get(percents.size() - 1)));
			}
			
			RandomBox.debugInfo("Size: " + Integer.toString(items.size()));
			
			// ��������� ����� �� ��������
			Collections.sort(percents);
			RewardItem.sortByChance(items);
			
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
			while (i < percents.size() - 1) {				
				float percent = percents.get(i);
				float prevPercent = (i > 0) ? percents.get(i - 1) : 0; 
				
				RandomBox.debugInfo("Checking: ( " + Float.toString(prevPercent) + " ; " + Float.toString(percent) + " ]");
				
				if ((randInt > prevPercent) && (randInt <= percent)) break;
				
				i++;
			}
			
			RandomBox.debugInfo("i: " + Integer.toString(i));
			
			// ��������� �������� ���� �� ������� ������ � ���������
			resultItems.add(items.get(i));
			
			// ������ ������� �������, ������� �����, ������, ������� ��� ��
			// ������� �����
			items.remove(i);
			
			// ��������� ������� ���������� �����, ������� �������� �����
			count--;
			
			total--;
		}
		
		return resultItems;
	}
}
