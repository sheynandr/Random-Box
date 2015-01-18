package saharnooby.randombox.box;

import org.bukkit.configuration.ConfigurationSection;

/**
 * �����, �������������� ������� ��� ���������� ��� ������ ����.
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
	 * @return ������, ��������� �� �������� ������� � ����������
	 */
	public String getLine() {
		return line;
	}
	
	/**
	 * @return ��������� �� ������� �� ����� �������?
	 */
	public Boolean getFromConsole() {
		return fromConsole;
	}
}
