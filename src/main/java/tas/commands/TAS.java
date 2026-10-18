package tas.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import plugin.Utils;

/*
 * TAS
 * 1. Gives all NPCs the appropriate inventory
 * 2. Re-teleports all NPCs to their initial locations
 * 3. Re-spawns all mobs
 * 4. Runs the TAS script
 */
public class TAS implements CommandExecutor {

	public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
		if(!(sender instanceof Player p)) {
			sender.sendMessage(Utils.msg("Only players can run this"));
			return true;
		}

		p.sendMessage(Utils.msg("<red>The TAS is disabled until TAS v3."));
		return true;
	}

	/* Disabled until TAS v3: needs the player routines in tas.m7 and M7 hooks for the boss handoffs. Last working at M7 c6b2625.
	public static void runTAS(World world, String section) {
		Map<String, Player> fakePlayers = FakePlayerManager.getFakePlayers();
		if(fakePlayers.isEmpty()) {
			Bukkit.broadcastMessage(ChatColor.RED + "Could not run TAS!  There are no actors.");
			return;
		}

		// A prior /m7practice may have left practice-mode aggro on, so turn it back off for a real TAS run.
		WitherActions.setPracticeMode(false);
		// Clear any section splits recorded by a previous run (used by the Wither-King practice scoreboard).
		WitherActions.clearSplits();
		// Clear game-mode-change tracking (the practice scoreboard's golden-name anti-cheat).
		WitherActions.clearGameModeChanges();
		// Reset Berserk's per-mob damage-ramp counters.
		listeners.CustomItems.resetBerserkDamage();
		// Reset terminator firing cooldown state.
		listeners.CustomItems.resetTerminatorCooldowns();
		// Reset class-ability (drop) cooldowns.
		listeners.CustomItems.resetAbilityCooldowns();
		// Clear any one-shot choreography still queued from a previous run before this one schedules its own.
		Utils.cancelAllScheduled();

		// Reset the verbose phase-tick counter immediately so carryover tasks from the previous phase
		// count from 0; the run proper re-marks it again when it actually starts (see Server "Run started").
		Utils.markPhaseStart();

		MovementAudit.cancelAll();
		Actions.cancelAllMovement();
		FakeKits.setInventories();
		Server.serverSetup(world);

		fakePlayers.values().forEach(p -> Utils.setSpeed(p, 400));

		// Start the boss and world instructions BEFORE the player routines.  Both schedule their work for the same
		// tick (+60), and submitting this first makes the boss phase activate before the players' tick-0 device
		// interactions fire, e.g. Tank right-clicking the S3 arrow-align frame, which would otherwise hit an
		// inactive phase and be dropped.
		Server.serverInstructions(world, section);

		Archer.archerInstructions(fakePlayers.get("Archer"), section);
		Berserk.berserkInstructions(fakePlayers.get("Mage3"), section);
		Healer.healerInstructions(fakePlayers.get("Mage4"), section);
		Mage.mageInstructions(fakePlayers.get("Mage1"), section);
		Tank.tankInstructions(fakePlayers.get("Mage2"), section);

		// Arm the Watcher with the run's continuation intent + the full Maxor handoff. The handoff fires when a fake
		// steps into the Blood Room's nether portal: it teleports every actor to the boss spawn, spawns Maxor, and
		// kicks off each player's maxor() routine (replacing the old hardcoded tick-742 teleport+maxor in each script).
		if(section.equals("all") || section.equals("clear")) {
			// The Watcher teleports the actors to the boss spawn the tick a fake enters the portal, then runs this
			// handoff the next tick, so Maxor and every player's maxor() routine start together.
			Runnable maxorHandoff = () -> {
				Maxor.maxorInstructions(world, true);
				Archer.maxor(true);
				Berserk.maxor(true);
				Healer.maxor(true);
				Mage.maxor(true);
				Tank.maxor(true);
			};
			Watcher.INSTANCE.arm(world, section.equals("all"), maxorHandoff);
		}

		// Arm the boss-to-boss player handoffs: when Maxor or Storm die and spawn the next boss (chainNext), each
		// player's storm() or goldor() routine starts that same tick.  This replaces the old hardcoded transition
		// ticks (storm(true)@496, goldor(true)@881) in the player scripts.
		if(section.equals("all") || section.equals("boss")) {
			Maxor.INSTANCE.armPlayerHandoff(() -> {
				Archer.storm(true);
				Berserk.storm(true);
				Healer.storm(true);
				Mage.storm(true);
				Tank.storm(true);
			});
			Storm.INSTANCE.armPlayerHandoff(() -> {
				Archer.goldor(true);
				Berserk.goldor(true);
				Healer.goldor(true);
				Mage.goldor(true);
				Tank.goldor(true);
			});
			Goldor.INSTANCE.armPlayerHandoff(() -> {
				Archer.necron(true);
				Berserk.necron(true);
				Healer.necron(true);
				Mage.necron(true);
				Tank.necron(true);
			});
			Necron.INSTANCE.armPlayerHandoff(() -> {
				Archer.witherKing();
				Berserk.witherKing();
				Healer.witherKing();
				Mage.witherKing();
				Tank.witherKing();
			});
		}

		// Restart spectator sync so it runs AFTER all instruction tasks in each tick
		Spectate.stopSpectatorSync();
		Spectate.startSpectatorSync();
	}
	*/
}
