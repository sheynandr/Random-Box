package saharnooby.randombox;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Методы для отправки сообщений от плагина.
 * @author saharNooby
 *
 */
public class Chat {
	/**
	 * Пишет в чат sender'у строку с указанным id и префиксом.
	 * Строка берётся из конфига, из секции strings.
	 * @param sender Отправитель команды.
	 * @param id ID сообщения в конфиге.
	 */
	public static void sendMsgId(CommandSender sender, String id) {
		String msg = RandomBox.config.getStringById(id);
		sendMsg(sender, msg);
	}
	
	/**
	 * Пишет в чат sender'у указанную строку с префиксом.
	 * @param sender Отправитель команды.
	 * @param msg Строка для вывода.
	 */
	public static void sendMsg(CommandSender sender, String msg) {
		String prefix = RandomBox.config.getStringById("prefix");				
		sender.sendMessage(prefix + msg);
	}
	
	/**
	 * Пишет в чат sender'у информацию о плагине:
	 * описание, версию и автора.
	 * @param sender Отправитель команды.
	 */
	public static void ShowInfo(CommandSender sender) {
		PluginDescriptionFile desc = JavaPlugin.getPlugin(RandomBox.class).getDescription();
		
		sender.sendMessage("§3----------- [RandomBox] -----------");
		sender.sendMessage("§b " + desc.getDescription() );
		sender.sendMessage("§b Версия: " + desc.getVersion() );
		sender.sendMessage("§b Автор: saharNooby");
		sender.sendMessage("§3----------------------------------");		
	}
	
}
