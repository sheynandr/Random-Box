package saharnooby.randombox.box;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import saharnooby.randombox.RandomBox;
import saharnooby.randombox.Utils;
import saharnooby.randombox.box.result.BoxConstructorResult;
import saharnooby.randombox.box.result.RewardItemConstructorResult;

public class Box {
	public static BoxConstructorResult fromItemStack(ItemStack item) {
		BoxConstructorResult result = new BoxConstructorResult();
		
		if (item == null)
			return result.fail("itemIsNULL");
		
		int intId = -1;
		
		if (item.hasItemMeta())
			if (item.getItemMeta().hasLore())
				intId = Utils.getIdFromLoreLine(item.getItemMeta().getLore().get(0));
		
		if (intId == -1)
			intId = item.getDurability();
		
		// Получаем строковый id коробки из под-id вещи
		String id = String.valueOf(intId);
		
		ConfigurationSection section;
		section = RandomBox.getConfiguration().getConfigurationSection("boxes." + id);
		
		// Если секция с таким id не найдена, выходим...
		if (section == null)
			return result.fail("noSuchBox");
		
		// Создаём коробку, которая распарсена из секции (будет эталоном)
		ItemStack box = Utils.section2box(section);
		if (box == null)
			return result.fail("canNotParseBox");
		
		// Если материалы эталонной и данной коробки не совпадают, то выходим
		if (item.getType() != box.getType())
			return result.fail("notABox");
		
		return fromSection(section);
	}
	
	public static BoxConstructorResult fromSection(ConfigurationSection section) {
		BoxConstructorResult result = new BoxConstructorResult();
		
		if (section == null)
			return result.fail("sectionIsNULL");
		
		Box box = new Box();
		
		try {
			box.id = Short.valueOf(section.getName());
		} catch (Exception e) {
			return result.fail("convertError");
		};
		
		ItemStack item = Utils.section2box(section);
		if (item == null)
			return result.fail("canNotParseBox");
		else
			box.item = item;
		
		int itemsToSelectCount = section.getInt("itemsToSelectCount", 0);
		if (itemsToSelectCount < 1)
			return result.fail("selectCntLessThanOne");
		else
			box.itemsToSelectCount = itemsToSelectCount;
		
		box.checkPermission = section.getBoolean("checkPermission", false);
		
		box.openWhenClicked = section.getBoolean("openWhenClicked", false);
		
		box.enchant = section.getBoolean("enchant", true);
		
		box.unstackable = section.getBoolean("unstackable", true);
		
		ConfigurationSection itemsSection = section.getConfigurationSection("items");
		if (itemsSection != null) {
			Set<String> keys = itemsSection.getKeys(false);
			if (keys.isEmpty())
				return result.fail("noItemsSection");
			
			List<RewardItem> items = new ArrayList<RewardItem>();
			
			for (String key : keys) {
				RandomBox.debugInfo("Key = " + key);
				
				RewardItemConstructorResult itemResult = RewardItem.fromSection(itemsSection.getConfigurationSection(key));
				
				if (!itemResult.getSuccessful())
					return result.fail(itemResult.getMessage());
				else
					items.add(itemResult.getResult());
			}
			
			box.items = items;
		} else
			return result.fail("noItemsSection");
		
		Utils.fillBoxLore(box);
		
		return result.success(null, box);
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
		
		// Пока счётчик вещей, которых нужно взять, больше нуля, делаем
		// этот огромный блок
		while (count > 0) {
			RandomBox.debugInfo("----- ITERATION ------");
			
			// Узнаём сумму всех вероятностей
			float chanceSum = 0;
			for (RewardItem item : items) {
				chanceSum += item.getProbability();
			}
			RandomBox.debugInfo("Sum: " + Float.toString(chanceSum));
			
			// Список вероятностей выпадения каждой вещи, выраженных в процентах
			List<Float> percents = new ArrayList<Float>();
			
			// Для каждой вещи рассчитываем долю в общей сумме (в процентах)
			for (RewardItem item : items) {
				// Вероятность % = (Шанс вещи / Сумма шансов)  * 100
				percents.add( (float) (item.getProbability() / chanceSum * 100) );
				RandomBox.debugInfo("Percent: " + Float.toString(percents.get(percents.size() - 1)));
			}
			
			RandomBox.debugInfo("Size: " + Integer.toString(items.size()));
			
			Random random = new Random();
			// Определяем случайное целое число от 0 до суммы шансов + 1 включительно
			float rand = random.nextInt(100) + random.nextFloat(); // from 0 to 100 inclusive.
			
			RandomBox.debugInfo("Random number: " + rand);
			
			// Номер текущего элемента массива % вероятностей
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
			
			// Добавляем выпавшую вещь из данного списка в результат
			resultItems.add(items.get(i));
			
			// Узнали элемент массива, который выпал, значит, удаляем его из
			// массива вещей
			items.remove(i);
			
			// Уменьшаем счётчик количества вещей, которых осталось взять
			count--;
			
			total--;
			
			RandomBox.debugInfo("-- END OF ITERATION --");
		}
		
		return resultItems;
	}
}
