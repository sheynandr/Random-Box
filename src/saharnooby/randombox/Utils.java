package saharnooby.randombox;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
	
	public static int getIdFromLoreLine(String line) {
		// &1&2&3&4&rSome string
		
		int rIndex = line.indexOf("§r");
		if (rIndex <= 0)
			return -1;
		
		line = line.substring(0, rIndex);
		
		// &1&2&3&4
		
		String[] digits = line.split("§");
		if (digits.length == 0)
			return -1;
		
		// {1, 2, 3, 4}
		
		String id = "";
		
		for (int i = 0; i < digits.length; i++) {
			id = id.concat(digits[i]);
		}
		
		// 1234
		
		try {
			return Integer.valueOf(id);
		} catch (Exception e) {
			return -1;
		}
	}
	
	private static String getIdFromIntId(int id) {
		// 1234
		String rawId = String.valueOf(id);
		String stringId = "";
		
		// {1, 2, 3, 4}
		for (char c : rawId.toCharArray()) {
			stringId = stringId.concat("§" + c);
		}
		
		// §1§2§3§4
		return stringId;
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
		
		if (material == Material.AIR)
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
			name = "§r" + name.replace("&", "§").replace("§§", "&");
			
			if (section.getBoolean("unstackable", true)) {
				String randomString = "";
				Random random = new Random();
				for (int i = 1; i <= 4; i++) 
					randomString += "§" + random.nextInt(10);
				name = randomString + name;
			}

			ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
			meta.setDisplayName(name);
			item.setItemMeta(meta);
		}
		
		Enchantment boxEnchantment = getEnchantment(RandomBox.getConfiguration().getString("boxEnchantment"));
		
		if (boxEnchantment != null && section.getBoolean("enchant", true))
			item.addUnsafeEnchantment(boxEnchantment, 1);
		
		return item;
	}
	
	public static void fillBoxLore(Box box) {
		ItemMeta boxMeta = box.getItem().getItemMeta();
		if (boxMeta == null)
			boxMeta = Bukkit.getItemFactory().getItemMeta(box.getItem().getType());
		
		List<String> lore = new ArrayList<String>();

		lore.add(Utils.getIdFromIntId(box.getId()) + "§r" + RandomBox.getString("itemsToDrop"));
		
		for (RewardItem item : box.getItems()) {
			String name = item.getName();
			String loreString;
			
			if (item.getItem() == null) {
				loreString = String.format("§7%d.§e %s", lore.size(), name);
			} else {
				int amount = item.getItem().getAmount();
				loreString = String.format("§7%d.§e x%d %s", lore.size(), amount, name);
			};
			
			lore.add(loreString);
		}
		
		boxMeta.setLore(lore);
		box.getItem().setItemMeta(boxMeta);
	}
	
	public static String getFlatItemsList(List<RewardItem> list) {
		String result = "";
		
		for (RewardItem item : list) {
			String name = item.getName();
			int amount = (item.getItem() == null) ? 0 : item.getItem().getAmount();
			
			if (amount == 0) 
				result += ", " + name + "§r";
			else
				result += ", " + name + " §rx" + Integer.toString(amount);						
		}
		
		return result.substring(2, result.length());
	}
}
