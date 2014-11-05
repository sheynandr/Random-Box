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
		
		int intId = item.getDurability();
		
		// �������� ��������� id ������� �� ���-id ����
		String id = String.valueOf(intId);
		
		ConfigurationSection section;
		section = RandomBox.getConfiguration().getConfigurationSection("boxes." + id);
		
		// ���� ������ � ����� id �� �������, �������...
		if (section == null)
			return null;
		
		// ������ �������, ������� ���������� �� ������ (����� ��������)
		ItemStack box = Utils.section2box(section);
		if (box == null)
			return null;
		
		// ���� ��������� ��������� � ������ ������� �� ���������, �� �������
		if (item.getType() != box.getType())
			return null;
		
		return fromSection(section);
	}
	
	public static Box fromSection(ConfigurationSection section) {
		if (section == null)
			return null;
		
		Box box = new Box();
		
		try {
			box.id = Short.valueOf(section.getName());
		} catch (Exception e) {
			return null;
		};
		
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
		
		box.enchant = section.getBoolean("enchant", true);
		
		box.unstackable = section.getBoolean("unstackable", true);
		
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
		
		box.item.setDurability(box.id);
		
		Utils.fillBoxLore(box);
		
		return box;
	}
	
	private short id;
	
	private ItemStack item;
	
	private int itemsToSelectCount;
	
	private Boolean checkPermission;
	
	private Boolean openWhenClicked;
	
	private Boolean enchant;
	
	private Boolean unstackable;
	
	private List<RewardItem> items;

	public short getId() {
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
	
	public Boolean getEnchant() {
		return enchant;
	}
	
	public Boolean getUnstackable() {
		return unstackable;
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
			RandomBox.debugInfo("----- ITERATION ------");
			
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
			
			Random random = new Random();
			// ���������� ��������� ����� ����� �� 0 �� ����� ������ + 1 ������������
			float rand = random.nextInt(100) + random.nextFloat(); // from 0 to 100 inclusive.
			
			RandomBox.debugInfo("Random number: " + rand);
			
			// ����� �������� �������� ������� % ������������
			int i = 0;
			
			float from = 0;

			while (i < percents.size() - 1) {				
				float percent = percents.get(i);
				from = (i > 0) ? from + percents.get(i - 1) : 0;  

				//float to = useNewSelector ? from + percent : percent;	
				float to = from + percent;	

				RandomBox.debugInfo("Checking: ( " + from + " ; " + to + " ]");
				
				if ((rand > from) && (rand <= to)) {
					RandomBox.debugInfo(rand + " is in ( " + from + " ; " + to + " ], i = " + i);
					break;
				}
				
				i++;
			}
			
			// ��������� �������� ���� �� ������� ������ � ���������
			resultItems.add(items.get(i));
			
			// ������ ������� �������, ������� �����, ������, ������� ��� ��
			// ������� �����
			items.remove(i);
			
			// ��������� ������� ���������� �����, ������� �������� �����
			count--;
			
			total--;
			
			RandomBox.debugInfo("-- END OF ITERATION --");
		}
		
		return resultItems;
	}
}
