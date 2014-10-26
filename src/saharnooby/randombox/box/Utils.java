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
 * Методы для работы с коробками удачи.
 * @author saharNooby
 * @since October 20, 2014
 */
public class Utils {
	/**
	 * Возвращает id коробки из её описания (lore) или null, если:
	 * предмет равен null;
	 * метаданные предмета равны null;
	 * описание равно null или пусто.
	 * @param item Предмет (мнимая коробка).
	 * @return Строка - id коробки.
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
	 * Возвращает секцию конфигурации, в которой хранятся данные о коробке.
	 * Путь: boxes.<id коробки>.
	 * @param id id коробки.
	 * @return Объект ConfigurationSection или null, если секции не существует.
	 */
	public static ConfigurationSection getBoxSection(String id) {
		FileConfiguration config = RandomBox.config.get();
		if (config == null) return null;
		return config.getConfigurationSection("boxes." + id);
	}
	
	/**
	 * Возвращает список вещей (ItemStack + вероятность выпадения),
	 * распарсенный из массива предметов section.
	 * @param section Секция-массив предметов.
	 * @return Список RewardItem или null, если не удалось распарсить некоторые вещи.
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
	 * Из данного списка вещей выбирает count вещей, согласно их шансам в классе
	 * RewardItem. Больше комментариев в коде.
	 * @param count Сколько вещей нужно выбрать из списка
	 * @param itemsList Список вещей
	 * @return Список выпавших вещей
	 */
	public static List<RewardItem> getRandomItems(int count, List<RewardItem> itemsList) {
		// Всего вещей
		int total = itemsList.size();
		
		// count - сколько должно выпасть,
		// То есть, берём count вещей из total
		
		// Всё не меньше 1, total >= count
		if ((count < 1) || (total < 1) || (total < count)) return null;
		
		List<RewardItem> resultList = new ArrayList<RewardItem>();
		
		// Пока счётчик вещей, которых нужно взять, больше нуля, делаем
		// этот огромный блок
		while (count > 0) {
			RandomBox.debugInfo("----- ITERATION -----");
			
			// Узнаём сумму всех вероятностей
			float chanceSum = 0;
			for (RewardItem item : itemsList) {
				chanceSum += item.getChance();
			}
			RandomBox.debugInfo("Sum: " + Float.toString(chanceSum));
			
			// Список вероятностей выпадения каждой вещи, выраженных в процентах
			List<Float> percentList = new ArrayList<Float>();
			
			// Для каждой вещи рассчитываем долю в общей сумме (в процентах)
			for (RewardItem item : itemsList) {
				// Вероятность % = (Шанс вещи / Сумма шансов)  * 100
				percentList.add((float) (item.getChance() / chanceSum * 100));
				RandomBox.debugInfo("Percent: " + Float.toString(percentList.get(percentList.size() - 1)));
			}
			
			RandomBox.debugInfo("Size: " + Integer.toString(itemsList.size()));
			
			// Сортируем шансы по убыванию
			Collections.sort(percentList);
			RewardItem.sortByChance(itemsList);
			
			Random random = new Random();
			// Определяем случайное целое число от 0 до суммы шансов + 1 включительно
			float randInt = random.nextInt(100) + random.nextFloat(); // from 0 to 100 inclusive.
			
			RandomBox.debugInfo("Random number: " + Float.toString(randInt));
			
			// Номер текущего элемента массива % вероятностей
			int i = 0;
			// Перебираем все проценты, пока не найдём такой, который будет
			// попадать в промежуток от предыдущего процента до текущего.
			// Например, если есть 2 шанса 60% и 40%, то больше вероятности,
			// что случайное число будет в промежутке от 0 до 60.
			while (i < percentList.size() - 1) {				
				float percent = percentList.get(i);
				float prevPercent = (i > 0) ? percentList.get(i - 1) : 0; 
				
				RandomBox.debugInfo("Checking: ( " + Float.toString(prevPercent) + " ; " + Float.toString(percent) + " ]");
				
				if ((randInt > prevPercent) && (randInt <= percent)) break;
				
				i++;
			}
			
			RandomBox.debugInfo("i: " + Integer.toString(i));
			
			// Добавляем выпавшую вещь из данного списка в результат
			resultList.add(itemsList.get(i));
			
			// Узнали элемент массива, который выпал, значит, удаляем его из
			// массива вещей
			itemsList.remove(i);
			
			// Уменьшаем счётчик количества вещей, которых осталось взять
			count--;
			
			total--;
		}
		
		return resultList;
	}
	
	/**
	 * Сколько свободных слотов в инвентаре?
	 * @param inv Инвентарь игрока.
	 * @return Целое число свободных слотов.
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
	 * Выдать вещи из списка list игроку player.
	 * Проверка на наличие свободного места не проводится.
	 * @param player Игрок, которому надо выдать вещи.
	 * @param list Список вещей, которые надо выдать.
	 */
	public static void giveRewardItems(Player player, List<RewardItem> list) {
		PlayerInventory inv = player.getInventory();
		for (RewardItem item : list) {
			inv.addItem(item.getItem());
		}
	}
	
	/**
	 * Даёт описание для коробки, состоящее из имён предметов, которые могут выпасть.
	 * Формат: 1. Вещь 1 <новая строка> 2. Вещь 2
	 * @param items Секция-массив вещей
	 * @return Список или null, если нет вещей или нет имени ни у одной вещи.
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
			list.add("§7" + Integer.toString(list.size() + 1) + ".§e x" + Integer.toString(amount) + " " + name);
		}
				
		if (list.isEmpty()) return null; else return list;
	}
	
	/**
	 * Возвращает ItemStack коробки, распарсенной из секции section.
	 * @param section Секция коробки.
	 * @return ItemStack коробки или null, если секция коробки неверна.
	 */
	public static ItemStack getBoxItem(ConfigurationSection section) {
		// Числовой id коробки
		int boxMatInt = section.getInt("boxItem", -1); 		
		
		// Строковый id коробки (типа STORAGE_MINECART)
		String boxMatString = section.getString("boxItem");
		
		Material boxMat;
		try {
			// Если цифровой id правильный, то узнаём материал по нему
			if (boxMatInt > -1) {
				boxMat = Material.getMaterial(boxMatInt);
				if (boxMat == null) return null;
			// Если нет, но правильный строковый id, то узнаём по нему
			} else if (boxMatString != null) {
				boxMat = Enum.valueOf(Material.class, boxMatString);
			// Если оба неверны, то выходим
			} else return null;
		} catch (Exception e) {
			return null;
		}
		
		ItemStack box = new ItemStack(boxMat, 1);
		
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(boxMat);

		String name = section.getString("boxName");
		if (name != null) meta.setDisplayName("§r" + name);
		
		List<String> lore = new ArrayList<String>();
		lore.add("§8" + section.getName());
		
		List<String> list = getContentList(section.getConfigurationSection("items"));
		if (list != null) {
			lore.add("§6" + RandomBox.config.getStringById("itemsToDrop"));
			lore.addAll(list);		
		}
		
		meta.setLore(lore);
		
		box.setItemMeta(meta);
		
		box.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);	
		
		return box;
	}
	
	/**
	 * Даёт коробку игроку.
	 * @param player Игрок, которому выдать коробку.
	 * @param item Уже распарсенная коробка в виде ItemStack.
	 * @return True, если успешно добавлено, False если нет места.
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
							result += name + " §rx" + Integer.toString(amount);
						else
							result += ", " + name + " §rx" + Integer.toString(amount);
					}
				}
			}
						
		}
		
		return result;
	}
}
