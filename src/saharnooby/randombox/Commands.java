package saharnooby.randombox;

import org.bukkit.command.CommandSender;

import saharnooby.randombox.box.BoxCommands;

public class Commands {
	private static Boolean checkPerm(CommandSender sender, String permission) {
		Boolean hasPerm = sender.hasPermission(permission);
		// Пишем в чат, если нет прав
		if (!hasPerm) {
			Chat.sendMsgId(sender, "noPermissions");
		}
		return hasPerm;
	}
	
	public static Boolean randombox(CommandSender sender, String[] args) {
		// Игрок должен иметь разрешение на выполнение /randombox без агрументов
		if (!checkPerm(sender, "randombox.main")) return true;		
		
		if (args.length == 1) {			
			String arg = args[0].toLowerCase();
			
        	// Команда перезагрузки настроек
			if (arg.equals("reload")) {
        		if (!checkPerm(sender, "randombox.main.reload")) return true;        	
        		RandomBox.config.reload();
        		Chat.sendMsgId(sender, "configReloaded");	
        		return true;   
        	}	
		} else {
			Chat.ShowInfo(sender);
			return true;
		}
		return false;
	}
	
	public static Boolean givebox(CommandSender sender, String[] args) {
		// Игрок должен иметь разрешение на выдачу коробки
		if (!checkPerm(sender, "randombox.givebox")) return true; 
		
		if (args.length == 2) {    		
    		if (BoxCommands.giveBox(sender, args[0], args[1])) {
    			Chat.sendMsgId(sender, "boxGiven");
    		} else {
    			String msg = RandomBox.config.getStringById("boxGiveError");
    			Chat.sendMsg(sender, msg + " " + Errors.getLastError(sender));	
    		}
	    	return true;
    	}	
		
		return false;
	}
	
	public static Boolean openbox(CommandSender sender) {
		if (!checkPerm(sender, "randombox.openbox")) return true; 
		
		if (BoxCommands.openBox(sender)) {
			// При успешном выполнении lastError содержит список
			// выпавших вещей.
			Chat.sendMsg(sender, RandomBox.config.getStringById("itemsDropped") +
					Errors.getLastError(sender));	
		} else {
			String msg = RandomBox.config.getStringById("boxOpenError");
			Chat.sendMsg(sender, msg + " " + Errors.getLastError(sender));	
		}
		
		return true;
	}
}
