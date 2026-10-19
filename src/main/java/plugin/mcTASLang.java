package plugin;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import commands.KickAllFakes;
import commands.Simulate;
import commands.Spectate;
import commands.TAS;
import listeners.SpectatorListener;

import java.util.List;

public class mcTASLang extends JavaPlugin {
	private static Plugin plugin;

	@Override
	public void onEnable() {
		plugin = this;

		for(String cmd : List.of("spawnfakes", "kickallfakes", "spectate", "unspectate", "tas", "simulate")) {
			PluginCommand command = getCommand(cmd);
			switch(cmd) {
				case "spawnfakes" -> command.setExecutor((sender, c, label, args) -> {
					if(!(sender instanceof Player p)) {
						sender.sendMessage(TASUtils.msg("Only players can run this"));
						return true;
					}
					FakePlayerManager.spawnAllFakes(p.getWorld());
					return true;
				});
				case "kickallfakes" -> command.setExecutor(new KickAllFakes());
				case "spectate", "unspectate" -> command.setExecutor(new Spectate());
				case "tas" -> command.setExecutor(new TAS());
				case "simulate" -> command.setExecutor(new Simulate());
			}
		}
		getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
		getServer().getPluginManager().registerEvents(new listeners.FakeJoinListener(), this);
		SpectatorSync.startInventorySync();
		Spectate.startSpectatorSync();
	}

	@Override
	public void onDisable() {
		// Also clears the M7 watching tag, which would otherwise stay on the player for good.
		ScriptWatchers.release();
		new java.util.ArrayList<>(Spectate.getSpectatorMap().keySet()).forEach(Spectate::removeSpectator);
		SpectatorSync.stopInventorySync();
		Spectate.stopSpectatorSync();
		FakePlayerManager.kickAllFakes();
		SpectatorSync.clearAll();
	}

	public static Plugin getInstance() {
		return plugin;
	}
}
