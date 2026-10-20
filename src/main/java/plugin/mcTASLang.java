package plugin;

import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import commands.KickAllFakes;
import commands.Simulate;
import commands.Spectate;
import commands.TAS;
import listeners.SpectatorListener;

import java.util.List;
import java.util.Map;

public class mcTASLang extends JavaPlugin {
	private static Plugin plugin;

	@Override
	public void onEnable() {
		plugin = this;

		for(String cmd : List.of("spawnfakes", "setup", "kickallfakes", "spectate", "unspectate", "tas", "simulate")) {
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
				case "setup" -> command.setExecutor((sender, c, label, args) -> {
					if(!(sender instanceof Player p)) {
						sender.sendMessage(TASUtils.msg("Only players can run this"));
						return true;
					}
					p.performCommand("m7:setup");
					FakePlayerManager.spawnAllFakes(p.getWorld());
					return true;
				});
				case "kickallfakes" -> command.setExecutor(new KickAllFakes());
				case "spectate", "unspectate" -> command.setExecutor(new Spectate());
				case "tas" -> command.setExecutor(new TAS());
				case "simulate" -> command.setExecutor(new Simulate());
			}
		}
		// Load order decides who gets a shared bare label, so take /setup from M7 once every plugin is enabled.
		getServer().getScheduler().runTask(this, this::claimSetupLabel);
		// MONITOR: last of everything at tick end: inputs, then movement (TASUtils.runDue, FakePlayerManager.tickFakes).
		getServer().getPluginManager().registerEvent(com.destroystokyo.paper.event.server.ServerTickEndEvent.class,
				new org.bukkit.event.Listener() {}, org.bukkit.event.EventPriority.MONITOR, (listener, event) -> {
					TASUtils.runDue();
					FakePlayerManager.tickFakes();
				}, this);
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

	private void claimSetupLabel() {
		PluginCommand ours = getCommand("setup");
		if(ours == null || !(getServer().getCommandMap() instanceof SimpleCommandMap map)) return;
		Map<String, Command> known = map.getKnownCommands();
		if(known.get("setup") == ours) return;
		known.put("setup", ours);
		ours.register(map);
	}

	public static Plugin getInstance() {
		return plugin;
	}
}
