package hide92795.bukkit.plugin.vanishplayer;

import hide92795.bukkit.plugin.corelib.Localizable;

public enum Type implements Localizable {
	VANISH("Vanish"), ALREADY_VANISHED("AlreadyVanished"), UNVANISH("Unvanish"), ALREADY_UNVANISHED("AlreadyUnvanished"), NO_VANISHED_PLAYER(
			"NoVanishedPlayer"), VANISHED_PLAYER("VanishedPlayer"), RELOADED_SETTING("ReloadedSetting"), ERROR_RELOAD_SETTING(
			"ErrorReloadSetting");
	private final String type;

	private Type(String type) {
		this.type = type;
	}

	@Override
	public String getName() {
		return type;
	}
}
