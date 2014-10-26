package saharnooby.randombox;

import java.util.Hashtable;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * ����� ��� ��������� ������ � �������.
 * �������� �� �������� ������ Windows.
 * ��������������� ������ ����� setLastError,
 * � ���������� �� ������ ����� �����
 * getLastError.
 * @author saharNooby
 *
 */
public class Errors {
	private static Map<CommandSender, String> errors = new Hashtable<CommandSender, String>();
	
	public static void setLastError(final CommandSender sender, final String error) {
		errors.put(sender, error);
	}
	
	public static void setLastErrorId(final CommandSender sender, final String id) {
		setLastError(sender, RandomBox.config.getStringById(id));
	}
	
	public static String getLastError(final CommandSender sender) {
		return errors.get(sender);
	}
	
	public static void newError(final CommandSender sender) {
		errors.put(sender, "");
	}
	
	public static void clearError(final CommandSender sender) {
		errors.remove(sender);
	}
}
