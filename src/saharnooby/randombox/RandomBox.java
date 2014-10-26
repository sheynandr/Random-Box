package saharnooby.randombox;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import saharnooby.randombox.box.Utils;

/**
 * Главный класс плагина RandomBox.
 * @author saharNooby
 *
 */
public class RandomBox extends JavaPlugin implements Listener {
	
	public static ConfigurationManager config; 
	
	private static final Logger log = Logger.getLogger("Minecraft");
	
	/**
	 * Пишет сообщение в лог (консоль) в формате
	 * "[RandomBox] сообщение".
	 * @param msg Сообщение.
	 */
	public static void toLog(final String msg) {
		log.info("[RandomBox] " + msg);
	}
	
	/**
	 * Пишет сообщение в лог, только если
	 * включен режим debug (параметр debug в конфиге).
	 * @param msg Сообщение.
	 */
	public static void debugInfo(final String msg) {
		if (config.get().getBoolean("debug", false)) toLog(msg);
	}
	
	@Override
	public void onEnable() { 
    	toLog("Enabling RandomBox...");
    	
    	Bukkit.getPluginManager().registerEvents(this, this);
    	
    	config = new ConfigurationManager(this);
    	config.load();
    	
    	toLog("RandomBox enabled.");
    }
     
	@Override
	public void onDisable() { 
		toLog("RandomBox disabled.");
    }	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmdName = command.getName().toLowerCase();
		
		// Создаём поле для записи ошибок для текущего sender
		Errors.newError(sender);
		
		if (cmdName.equals("randombox") || cmdName.equals("rb")) {
			return Commands.randombox(sender, args);
	    } else if (cmdName.equals("givebox")) {
	    	return Commands.givebox(sender, args);    	
	    } else if (cmdName.equals("openbox")) {
	    	return Commands.openbox(sender);
	    };
	    
	    // Удаляем поле
	    Errors.clearError(sender);
	    
	    return false;
	}	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if ((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) {
			ItemStack handItem = event.getPlayer().getInventory().getItemInHand();
			String id = Utils.getBoxId(handItem);
			ConfigurationSection section = config.get().getConfigurationSection("boxes." + id);
			if (section == null) return;
			event.setCancelled(true);
		}
	}
}
