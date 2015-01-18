package saharnooby.randombox.box;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import saharnooby.randombox.Utils;
import saharnooby.randombox.box.result.RewardItemConstructorResult;

public class RewardItem {	
	public static RewardItemConstructorResult fromSection(ConfigurationSection section) {		
		RewardItemConstructorResult result = new RewardItemConstructorResult();
		
		if (section == null)
			return result.fail("sectionIsNULL");
		
		RewardItem rewardItem = new RewardItem();

		rewardItem.name = "§r" + section.getString("name").replace("&", "§").replace("§§", "&");		
		
		rewardItem.item = Utils.section2item(section);
		
		int probability = section.getInt("chance", 0);
		
		if (probability < 1)
			return result.fail("chanceLessThanOne");
		else
			rewardItem.probability = probability;
		
		// Список команд для выполнения при выдаче этой вещи
		List<RewardCommand> commandsList = new ArrayList<RewardCommand>();
		
		// Секция, содержащая команды
		ConfigurationSection commandsSection = section.getConfigurationSection("commands");		
		
		if (commandsSection != null) {
			Set<String> keys = commandsSection.getKeys(false);
			// Конвертируем секцию каждой команды в объект и добавляем в список
			for (String key : keys) {				
				RewardCommand command = RewardCommand.fromSection(commandsSection.getConfigurationSection(key));
				if (command != null)
					commandsList.add(command);
				else
					return result.fail("invalidCommandSection");
			}
		}
		
		rewardItem.commands = commandsList.isEmpty() ? null : commandsList;
		
		return result.success(null, rewardItem);		
	}
	
	private String name;
	
	private ItemStack item;
	
	private int probability;
	
	private List<RewardCommand> commands;
	
	/**
	 * Не получайте имя через getItem(), результат может быть null!
	 * Пользуйтесь этим.
	 * @return Имя вещи
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return Вещь как ItemStack
	 */
	public ItemStack getItem() {
		return item;
	}
	
	/**
	 * @return Вероятность выпадения
	 */
	public int getProbability() {
		return probability;
	}
	
	/**
	 * @return Команды, выполняемые при выдаче вещи
	 */
	public List<RewardCommand> getCommands() {
		return commands;
	}
}
