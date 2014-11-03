package saharnooby.randombox;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import saharnooby.randombox.box.Box;
import saharnooby.randombox.box.RewardItem;

public class Utils {
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
	
	private static Material getMaterial(ConfigurationSection section, String path) {
		int materialId = section.getInt(path, -1); 		
		String materialName = section.getString(path);		
		
		Material material;
		
		try {
			if (materialId > -1) {
				material = Material.getMaterial(materialId);
				
				if (material == null)
					return null;
			} else if (materialName != null) {
				material = Enum.valueOf(Material.class, materialName);
			} else return null;
		} catch (Exception e) {
			return null;
		}
		
		return material;
	}
	
	public static ItemStack section2item(ConfigurationSection section) {
		Material material = getMaterial(section, "item");
		if (material == null)
			return null;
		
		int amount = section.getInt("amount", 1);
		if (amount < 1)
			return null;
		
		ItemStack item = new ItemStack(material, amount);
		
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
		
		String name = section.getString("name");
		if (name != null) meta.setDisplayName(name.replace("&", "§").replace("§§", "&"));
		
		List<String> lore = section.getStringList("lore");
		if (lore != null) if (!lore.isEmpty()) {
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, lore.get(i).replace("&", "§").replace("§§", "&"));
			}
			meta.setLore(lore);
		}
		
		item.setItemMeta(meta);
		
		short data = (short) section.getInt("data", -1);
		if (data < 0) data = 0;
		
		item.setDurability(data);
		
		ConfigurationSection enchants = section.getConfigurationSection("enchants");
		if (enchants != null) {
			Set<String> keys = enchants.getKeys(false);
			if (keys != null) if (keys.size() > 0) for (String key : keys) {
				Enchantment ench = getEnchantment(key);
				if (ench == null) continue;
				
				int level = enchants.getInt(key, -1);
				if (level < 1) continue;
				
				item.addUnsafeEnchantment(ench, level);
			}	
		}	
		
		return item;		
	}

	public static ItemStack section2box(ConfigurationSection section) {
		Material material = getMaterial(section, "boxItem");
		if (material == null)
			return null;		
		
		ItemStack item = new ItemStack(material, 1);
		
		String name = section.getString("boxName");
		if (name != null) {
			ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
			meta.setDisplayName("§r" + name.replace("&", "§").replace("§§", "&"));
			item.setItemMeta(meta);
		}		
		
		item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		
		return item;
	}
	
	public static void fillBoxLore(Box box) {
		ItemMeta boxMeta = box.getItem().getItemMeta();
		if (boxMeta == null)
			boxMeta = Bukkit.getItemFactory().getItemMeta(box.getItem().getType());
		
		List<String> lore = new ArrayList<String>();
		lore.add("§8" + box.getId());
		lore.add("§r" + RandomBox.getString("itemsToDrop"));
		
		for (RewardItem item : box.getItems()) {
			ItemMeta meta = item.getItem().getItemMeta();
			if (meta != null) {
				String name = meta.getDisplayName();
				if (name != null) {
					int amount = item.getItem().getAmount();
					if (amount > 0) {
						lore.add(String.format("§7%d.§e x%d %s", lore.size() - 1, amount, name));
					}
				}
			}
		}
		
		boxMeta.setLore(lore);
		box.getItem().setItemMeta(boxMeta);
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
