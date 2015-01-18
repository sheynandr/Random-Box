package saharnooby.randombox.box;

import org.bukkit.configuration.ConfigurationSection;

/**
 *  ласс, представл€ющий команду дл€ выполнени€ при выдаче вещи.
 * @author saharNooby
 * @since 09.11.2014
 */
public class RewardCommand {
	public static RewardCommand fromSection(ConfigurationSection section) {		
		if (section == null)
			return null;
		
		RewardCommand command = new RewardCommand();
		
		command.line = section.getString("line");
		
		if (command.line == null)
			return null;		
		
		command.fromConsole = section.getBoolean("fromConsole", false);	
		
		return command;
	}
	
	private String line;
	
	private Boolean fromConsole;

	/**
	 * @return —трока, состо€ща€ из названи€ команды и аргументов
	 */
	public String getLine() {
		return line;
	}
	
	/**
	 * @return ¬ыполн€ть ли команду от имени консоли?
	 */
	public Boolean getFromConsole() {
		return fromConsole;
	}
}
