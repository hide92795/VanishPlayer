package hide92795.bukkit.plugin.vanishplayer;

import hide92795.bukkit.plugin.corelib.Localize;
import hide92795.bukkit.plugin.vanishplayer.listener.PlayerLoginListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

public class VanishPlayer extends JavaPlugin {
	public List<String> invisible;
	private boolean enable_dynmap;
	private DynmapAPI dynmap;
	private Logger logger;
	private Localize localize;

	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		reloadConfig();
		logger = getLogger();
		invisible = new ArrayList<String>();
		localize = new Localize(this);
		try {
			reload();
		} catch (Exception e1) {
			logger.severe("Error has occured on loading config.");
		}

		getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);

		// Hook into dynmap
		if (getServer().getPluginManager().isPluginEnabled("dynmap")) {
			dynmap = (DynmapAPI) getServer().getPluginManager().getPlugin("dynmap");
			getLogger().info("Hook into dynmap");
			enable_dynmap = true;
		}
	}

	@Override
	public void onDisable() {
		if (enable_dynmap) {
			for (String player : invisible) {
				dynmap.setPlayerVisiblity(player, true);
			}
		}
	}

	private void reload() throws Exception {
		reloadConfig();
		try {
			localize.reload(getConfig().getString("Language"));
		} catch (Exception e1) {
			logger.severe("Can't load language file.");
			try {
				localize.reload("jp");
				logger.severe("Loaded default language file.");
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public void vanish(Player player) {
		for (Player other : getServer().getOnlinePlayers()) {
			if (!other.hasPermission("vanishplayer.showvanished")) {
				other.hidePlayer(player);
			}
		}
		if (enable_dynmap) {
			dynmap.setPlayerVisiblity(player, false);
		}
		player.sendMessage(localize.getString(Type.VANISH));
	}

	public void unvanish(Player player) {
		if (invisible.remove(player.getName())) {
			for (Player other : getServer().getOnlinePlayers()) {
				other.showPlayer(player);
			}
			if (enable_dynmap) {
				dynmap.setPlayerVisiblity(player, true);
			}
			player.sendMessage(localize.getString(Type.UNVANISH));
		} else {
			player.sendMessage(localize.getString(Type.ALREADY_UNVANISHED));
		}
	}

	public void showVanishList(CommandSender sender) {
		String result = "";
		boolean first = true;
		for (String hidden : invisible) {
			if (getServer().getPlayerExact(hidden) == null)
				continue;

			if (first) {
				result += hidden;
				first = false;
				continue;
			}

			result += ", " + hidden;
		}
		if (result.length() == 0) {
			sender.sendMessage(localize.getString(Type.NO_VANISHED_PLAYER));
		} else {
			sender.sendMessage(String.format(localize.getString(Type.VANISHED_PLAYER), result));
		}
	}



	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		if (command.getName().equalsIgnoreCase("vanish")) {
			if (sender instanceof Player) {
				// Player
				Player player = (Player) sender;
				if (invisible.contains(player.getName())) {
					player.sendMessage(localize.getString(Type.ALREADY_VANISHED));
				} else {
					invisible.add(player.getName());
					vanish(player);
				}
			} else {
				// Console
				sender.sendMessage("This command is only for player.");
			}
		} else if (command.getName().equalsIgnoreCase("unvanish")) {
			if (sender instanceof Player) {
				// Player
				unvanish((Player) sender);
			} else {
				// Console
				sender.sendMessage("This command is only for player.");
			}
		} else if (command.getName().equalsIgnoreCase("vanish-reload")) {
			try {
				reload();
				sender.sendMessage(localize.getString(Type.RELOADED_SETTING));
				logger.info("Reloaded successfully.");
			} catch (Exception e) {
				sender.sendMessage(localize.getString(Type.ERROR_RELOAD_SETTING));
				logger.info("An error has occured on reloading config.");
			}
		} else if (command.getName().equalsIgnoreCase("vanish-list")) {
			showVanishList(sender);
		}
		return true;
	}
}
