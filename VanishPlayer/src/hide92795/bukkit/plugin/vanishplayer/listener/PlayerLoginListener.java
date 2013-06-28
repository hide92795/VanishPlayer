package hide92795.bukkit.plugin.vanishplayer.listener;

import hide92795.bukkit.plugin.vanishplayer.VanishPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerLoginListener implements Listener {
	public VanishPlayer plugin;

	public PlayerLoginListener(VanishPlayer plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// 権限持ち以外のプレイヤーには現在透明化しているプレイヤーを透明化する。
		Player player = event.getPlayer();
		if (player.hasPermission("vanishplayer.showvanished") || player.isOp())
			return;

		for (String hidden : plugin.invisible) {
			Player hiddenPlayer = plugin.getServer().getPlayerExact(hidden);
			if (hiddenPlayer != null) {
				player.hidePlayer(hiddenPlayer);
			}
		}
	}
}
