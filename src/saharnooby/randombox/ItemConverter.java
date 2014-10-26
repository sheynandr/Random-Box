package saharnooby.randombox;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemConverter {
	private static Enchantment getEnchantment(String enchId) {
		switch (enchId) {
			case "power": enchId = "ARROW_DAMAGE"; break;
			case "flame": enchId = "ARROW_FIRE"; break;
			case "infinity": enchId = "ARROW_INFINITE"; break;
			case "punch": enchId = "ARROW_KNOCKBACK"; break;
			case "sharpness": enchId = "DAMAGE_ALL"; break;
			case "bane": case "baneofarthropods": case "bane_of_arthropods": enchId = "DAMAGE_ARTHROPODS"; break;
			case "smite": enchId = "DAMAGE_UNDEAD"; break;
			case "efficiency": enchId = "DIG_SPEED"; break;
			case "unbreaking": enchId = "DURABILITY"; break;
			case "fire": case "fireaspect": case "fire_aspect": enchId = "FIRE_ASPECT"; break;
			case "knockback": enchId = "KNOCKBACK"; break;
			case "fortune": enchId = "LOOT_BONUS_BLOCKS"; break;
			case "looting": enchId = "LOOT_BONUS_MOBS"; break;
			case "luck": case "luckofthesea": case "luck_of_the_sea": enchId = "LUCK"; break;
			case "lure": enchId = "LURE"; break;
			case "respiration": enchId = "OXYGEN"; break;
			case "protection": enchId = "PROTECTION_ENVIRONMENTAL"; break;
			case "blast": case "blastprotection": case "blast_protection": enchId = "PROTECTION_EXPLOSIONS"; break;
			case "feather": case "featherfalling": case "feather_falling": enchId = "PROTECTION_FALL"; break;
			case "fireprotection": case "fire_protection": enchId = "PROTECTION_FIRE";
			case "projectile": case "projectileprotection": case "projectile_protection": enchId = "PROTECTION_PROJECTILE"; break;
			case "silk": case "silktouch": case "silk_touch": enchId = "SILK_TOUCH"; break;
			case "thorns": enchId = "THORNS"; break;
			case "aqua": case "aquaaffinity": case "aqua_affinity": enchId = "WATER_WORKER";
		}
		
		return Enchantment.getByName(enchId);
	}
	
	
	public static ItemStack section2item(ConfigurationSection section) {
		// Числовой id
		int itemMatInt = section.getInt("item", -1); 		
		
		// Строковый id
		String itemMatString = section.getString("item");
		
		Material mat;
		try {
			// Если цифровой id правильный, то узнаём материал по нему
			if (itemMatInt > -1) {
				mat = Material.getMaterial(itemMatInt);
				if (mat == null) return null;
			// Если нет, но правильный строковый id, то узнаём по нему
			} else if (itemMatString != null) {
				mat = Enum.valueOf(Material.class, itemMatString);
			// Если оба неверны, то выходим
			} else return null;
		} catch (Exception e) {
			return null;
		}
		
		int amount = section.getInt("amount", 1);
		if (amount < 1) return null;
		
		ItemStack item = new ItemStack(mat, amount);
		
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(mat);
		
		String name = section.getString("name");
		if (name != null) meta.setDisplayName(name);
		
		List<String> lore = section.getStringList("lore");
		if (lore != null) if (!lore.isEmpty()) meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		short data = (short) section.getInt("data", -1);
		if (data < 0) data = 0;
		
		item.setDurability(data);
		
		ConfigurationSection enchs = section.getConfigurationSection("enchants");
		if (enchs != null) {
			Set<String> keys = enchs.getKeys(false);
			if (keys != null) if (keys.size() > 0) {
				for (String key : keys) {
					Enchantment ench = getEnchantment(key);
					if (ench == null) continue;
					int level = enchs.getInt(key, -1);
					if (level < 1) continue;
					item.addUnsafeEnchantment(ench, level);
				}	
			}
		}	
		
		return item;		
	}
}
