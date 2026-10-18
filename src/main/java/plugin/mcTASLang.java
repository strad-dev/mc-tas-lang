package plugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class mcTASLang extends JavaPlugin {
	private static Plugin plugin;

	public void onEnable() {
		plugin = this;
	}

	public static Plugin getInstance() {
		return plugin;
	}
}
