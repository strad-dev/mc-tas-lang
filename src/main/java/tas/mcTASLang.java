package tas;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.Utils;
import tas.commands.KickAllFakes;
import tas.commands.Simulate;
import tas.commands.Spectate;
import tas.commands.TAS;
import tas.listeners.SpectatorListener;
import tas.m7.FakeKits;

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
						sender.sendMessage(Utils.msg("Only players can run this"));
						return true;
					}
					FakePlayerManager.spawnAllFakes(p.getWorld());
					FakeKits.setInventories();
					return true;
				});
				case "kickallfakes" -> command.setExecutor(new KickAllFakes());
				case "spectate", "unspectate" -> command.setExecutor(new Spectate());
				case "tas" -> command.setExecutor(new TAS());
				case "simulate" -> command.setExecutor(new Simulate());
			}
		}
		getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
		getServer().getPluginManager().registerEvents(new tas.listeners.FakeJoinListener(), this);
		getServer().getPluginManager().registerEvents(new tas.listeners.FakeListener(), this);

		SpectatorSync.startInventorySync();
		Spectate.startSpectatorSync();
	}

	@Override
	public void onDisable() {
		SpectatorSync.stopInventorySync();
		Spectate.stopSpectatorSync();
		FakePlayerManager.stopCustomConnection();
		FakePlayerManager.kickAllFakes();
		SpectatorSync.clearAll();
	}

	public static Plugin getInstance() {
		return plugin;
	}
}
