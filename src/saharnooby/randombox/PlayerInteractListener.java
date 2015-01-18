package saharnooby.randombox;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import saharnooby.randombox.box.Box;

public class PlayerInteractListener implements Listener {
	
	private RandomBox plugin;

	public PlayerInteractListener(RandomBox plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {		
		Action action = event.getAction();
		if ( (action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK) ) {
			Box box = Box.fromItemStack(event.getItem()).getResult();
			if (box == null)
				return;
			else {
				event.setCancelled(true);
				
				if (box.getOpenWhenClicked()) {
					Player player = event.getPlayer();
					
					CommandResult result = CommandHandler.openBox((CommandSender) player);
					
					player.updateInventory();
					
					if (result.isSuccessful())
						plugin.chatInfo(player, plugin.getString("itemsDropped") + result.getMessage());
					else
						plugin.chatInfo(player, plugin.getString("boxOpenError") + ": " + result.getMessage());
				}
			}
		}
	}
	
}
