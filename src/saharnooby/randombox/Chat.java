package saharnooby.randombox;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * ������ ��� �������� ��������� �� �������.
 * @author saharNooby
 *
 */
public class Chat {
	/**
	 * ����� � ��� sender'� ������ � ��������� id � ���������.
	 * ������ ������ �� �������, �� ������ strings.
	 * @param sender ����������� �������.
	 * @param id ID ��������� � �������.
	 */
	public static void sendMsgId(CommandSender sender, String id) {
		String msg = RandomBox.config.getStringById(id);
		sendMsg(sender, msg);
	}
	
	/**
	 * ����� � ��� sender'� ��������� ������ � ���������.
	 * @param sender ����������� �������.
	 * @param msg ������ ��� ������.
	 */
	public static void sendMsg(CommandSender sender, String msg) {
		String prefix = RandomBox.config.getStringById("prefix");				
		sender.sendMessage(prefix + msg);
	}
	
	/**
	 * ����� � ��� sender'� ���������� � �������:
	 * ��������, ������ � ������.
	 * @param sender ����������� �������.
	 */
	public static void ShowInfo(CommandSender sender) {
		PluginDescriptionFile desc = JavaPlugin.getPlugin(RandomBox.class).getDescription();
		
		sender.sendMessage("�3----------- [RandomBox] -----------");
		sender.sendMessage("�b " + desc.getDescription() );
		sender.sendMessage("�b ������: " + desc.getVersion() );
		sender.sendMessage("�b �����: saharNooby");
		sender.sendMessage("�3----------------------------------");		
	}
	
}
