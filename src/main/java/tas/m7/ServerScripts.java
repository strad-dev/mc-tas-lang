package tas.m7;

import instructions.Server;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import plugin.Utils;
import tas.Actions;
import tas.mcTASLang;

import java.util.HashSet;
import java.util.Set;

public class ServerScripts {
	public static class Quiz {
		private static final String ORUO = "<dark_red>[STATUE] Oruo the Omniscient<white>: ";

		private static void spawnQuestion(int questionNum, String questionText, String[] answers, TextDisplay[] options, Player player, World world) {
			Server.Quiz.animateQuestion(world, player, questionNum, questionText, answers, options);
			Utils.scheduleTask(() -> {
				answeredCorrectly(questionNum, options);
				Actions.rightClick(player);
			}, 61);
		}

		private static void answeredCorrectly(int questionNum, TextDisplay[] options) {
			Bukkit.broadcast(Utils.msg(ORUO + "<gold>akc0303 <green>answered <gold>Question #" + questionNum + "<green> correctly!"));
			Utils.playGlobalSound(Sound.ENTITY_GUARDIAN_HURT, 2.0f, 0.5f);
			Utils.playGlobalSound(Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 0.75f);
			options[0].remove();
			options[1].remove();
			options[2].remove();
		}

		public static void run(Player player, World world) {
			Utils.scheduleTask(() -> {
				Actions.turnHead(player, 0f, 5f);
				Actions.setHotbarSlot(player, 5);
			}, 137);
			Utils.scheduleTask(() -> Server.Quiz.oruoMessage("Prove your knowledge by answering 3 questions and I shall reward you in ways that transcend time!"), 155);
			Utils.scheduleTask(() -> Server.Quiz.oruoMessage("Answer incorrectly, and your moment of ineptitude will live on for generations."), 195);
			final TextDisplay[] options = new TextDisplay[3];
			Utils.scheduleTask(() -> spawnQuestion(1, "How is the run going so far?", new String[]{"Alright", "Trash", "Literally tick-perfect"}, options, player, world), 235);
			Utils.scheduleTask(() -> Server.Quiz.oruoMessage("2 question left... then you will have proven your worth to me!"), 336);
			Utils.scheduleTask(() -> spawnQuestion(2, "Did you know that you can sub scribe to Stradivarius Violin to see more content like this?!", new String[]{"Oh wow, I should sub scribe!", "Oh wow, I should sub scribe!!", "Oh wow, I should sub scribe!!!"}, options, player, world), 376);
			Utils.scheduleTask(() -> Server.Quiz.oruoMessage("One more question!"), 477);
			Utils.scheduleTask(() -> spawnQuestion(3, "Is akc0303 bald?", new String[]{"No", "Yes", "Decline to Answer"}, options, player, world), 517);
			Utils.scheduleTask(() -> {
				Bukkit.broadcast(Utils.msg("<dark_green>Archer: Quiz Cleared"));
				Bukkit.broadcast(Utils.msg("<dark_green>Archer: Clear Finished in 578 Ticks (28.90 seconds)"));
			}, 578);
			Utils.scheduleTask(() -> Server.Quiz.oruoMessage("I bestow upon you all the power of a hundred years!"), 598);
			Utils.scheduleTask(() -> Utils.broadcastBlessing(player, Utils.BlessingType.TIME, 5), 618);
		}
	}

	public static class IceFill {
		private static BukkitTask iceFillTask;
		private static final Set<Block> frozenBlocks = new HashSet<>();

		private static void startIceFillTask(Player player) {
			if(iceFillTask != null) {
				iceFillTask.cancel();
			}

			frozenBlocks.clear();

			iceFillTask = new BukkitRunnable() {
				@Override
				public void run() {
					Block below = player.getLocation().subtract(0, 1, 0).getBlock();
					if(below.getType() == Material.ICE) {
						below.setType(Material.PACKED_ICE, false);
						frozenBlocks.add(below);
						Utils.playGlobalSound(Sound.BLOCK_SNOW_BREAK, 2.0f, 1.0f);
					}
				}
			}.runTaskTimer(mcTASLang.getInstance(), 0L, 1L);
		}

		public static void stopIceFillTask() {
			if(iceFillTask != null) {
				iceFillTask.cancel();
				iceFillTask = null;
			}

			for(Block block : frozenBlocks) {
				if(block.getType() == Material.PACKED_ICE) {
					block.setType(Material.ICE, false);
				}
			}
			frozenBlocks.clear();
		}

		public static void run(Player player, World world) {
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 121);
			Utils.scheduleTask(() -> {
				startIceFillTask(player);
				Actions.rightClick(player);
			}, 122);
			Utils.scheduleTask(() -> Actions.rightClick(player), 123);
			Utils.scheduleTask(() -> Actions.rightClick(player), 124);
			Utils.scheduleTask(() -> Actions.turnHead(player, 0f, 60f), 125);
			Utils.scheduleTask(() -> Actions.rightClick(player), 126);
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 127);
			Utils.scheduleTask(() -> Actions.rightClick(player), 128);
			Utils.scheduleTask(() -> Actions.turnHead(player, 180f, 60f), 129);
			Utils.scheduleTask(() -> Actions.rightClick(player), 130);
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 131);
			Utils.scheduleTask(() -> {
				Actions.rightClick(player);
				Server.IceFill.playIceFillSounds(1, player);
			}, 132);
			Utils.scheduleTask(() -> Actions.rightClick(player), 133);
			Utils.scheduleTask(() -> Actions.rightClick(player), 134);
			Utils.scheduleTask(() -> Actions.turnHead(player, 0f, 60f), 135);
			Utils.scheduleTask(() -> Actions.rightClick(player), 136);
			Utils.scheduleTask(() -> Actions.rightClick(player), 137);
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 138);
			Utils.scheduleTask(() -> Actions.rightClick(player), 139);
			Utils.scheduleTask(() -> Actions.rightClick(player), 140);
			Utils.scheduleTask(() -> Actions.rightClick(player), 141);
			Utils.scheduleTask(() -> Actions.rightClick(player), 142);
			Utils.scheduleTask(() -> Actions.turnHead(player, 180f, 60f), 143);
			Utils.scheduleTask(() -> Actions.rightClick(player), 144);
			Utils.scheduleTask(() -> Actions.turnHead(player, -90f, 60f), 145);
			Utils.scheduleTask(() -> Actions.rightClick(player), 146);
			Utils.scheduleTask(() -> Actions.rightClick(player), 147);
			Utils.scheduleTask(() -> Actions.turnHead(player, 180f, 60f), 148);
			Utils.scheduleTask(() -> Actions.rightClick(player), 149);
			Utils.scheduleTask(() -> Actions.rightClick(player), 150);
			Utils.scheduleTask(() -> Actions.turnHead(player, -90f, 60f), 151);
			Utils.scheduleTask(() -> Actions.rightClick(player), 152);
			Utils.scheduleTask(() -> Actions.rightClick(player), 153);
			Utils.scheduleTask(() -> Actions.turnHead(player, 180f, 60f), 154);
			Utils.scheduleTask(() -> Actions.rightClick(player), 155);
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 156);
			Utils.scheduleTask(() -> Actions.rightClick(player), 157);
			Utils.scheduleTask(() -> Actions.rightClick(player), 158);
			Utils.scheduleTask(() -> Actions.rightClick(player), 159);
			Utils.scheduleTask(() -> Actions.rightClick(player), 160);
			Utils.scheduleTask(() -> Actions.turnHead(player, 0f, 60f), 161);
			Utils.scheduleTask(() -> Actions.rightClick(player), 162);
			Utils.scheduleTask(() -> Actions.rightClick(player), 163);
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 164);
			Utils.scheduleTask(() -> {
				Actions.rightClick(player);
				Server.IceFill.playIceFillSounds(2, player);
			}, 165);
			Utils.scheduleTask(() -> Actions.rightClick(player), 166);
			Utils.scheduleTask(() -> Actions.rightClick(player), 167);
			Utils.scheduleTask(() -> Actions.turnHead(player, 0f, 60f), 168);
			Utils.scheduleTask(() -> Actions.rightClick(player), 169);
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 170);
			Utils.scheduleTask(() -> Actions.rightClick(player), 171);
			Utils.scheduleTask(() -> Actions.rightClick(player), 172);
			Utils.scheduleTask(() -> Actions.turnHead(player, 0f, 60f), 173);
			Utils.scheduleTask(() -> Actions.rightClick(player), 174);
			Utils.scheduleTask(() -> Actions.turnHead(player, -90f, 60f), 175);
			Utils.scheduleTask(() -> Actions.rightClick(player), 176);
			Utils.scheduleTask(() -> Actions.rightClick(player), 177);
			Utils.scheduleTask(() -> Actions.turnHead(player, 0f, 60f), 178);
			Utils.scheduleTask(() -> Actions.rightClick(player), 179);
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 180);
			Utils.scheduleTask(() -> Actions.rightClick(player), 181);
			Utils.scheduleTask(() -> Actions.rightClick(player), 182);
			Utils.scheduleTask(() -> Actions.rightClick(player), 183);
			Utils.scheduleTask(() -> Actions.rightClick(player), 184);
			Utils.scheduleTask(() -> Actions.rightClick(player), 185);
			Utils.scheduleTask(() -> Actions.rightClick(player), 186);
			Utils.scheduleTask(() -> Actions.turnHead(player, 180f, 60f), 187);
			Utils.scheduleTask(() -> Actions.rightClick(player), 188);
			Utils.scheduleTask(() -> Actions.turnHead(player, -90f, 60f), 189);
			Utils.scheduleTask(() -> Actions.rightClick(player), 190);
			Utils.scheduleTask(() -> Actions.rightClick(player), 191);
			Utils.scheduleTask(() -> Actions.turnHead(player, 180f, 60f), 192);
			Utils.scheduleTask(() -> Actions.rightClick(player), 193);
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 194);
			Utils.scheduleTask(() -> Actions.rightClick(player), 195);
			Utils.scheduleTask(() -> Actions.turnHead(player, 180f, 60f), 196);
			Utils.scheduleTask(() -> Actions.rightClick(player), 197);
			Utils.scheduleTask(() -> Actions.rightClick(player), 198);
			Utils.scheduleTask(() -> Actions.turnHead(player, -90f, 60f), 199);
			Utils.scheduleTask(() -> Actions.rightClick(player), 200);
			Utils.scheduleTask(() -> Actions.turnHead(player, 180f, 60f), 201);
			Utils.scheduleTask(() -> Actions.rightClick(player), 202);
			Utils.scheduleTask(() -> Actions.turnHead(player, -90f, 60f), 203);
			Utils.scheduleTask(() -> Actions.rightClick(player), 204);
			Utils.scheduleTask(() -> Actions.rightClick(player), 205);
			Utils.scheduleTask(() -> Actions.turnHead(player, 0f, 60f), 206);
			Utils.scheduleTask(() -> Actions.rightClick(player), 207);
			Utils.scheduleTask(() -> Actions.turnHead(player, -90f, 60f), 208);
			Utils.scheduleTask(() -> Actions.rightClick(player), 209);
			Utils.scheduleTask(() -> Actions.rightClick(player), 210);
			Utils.scheduleTask(() -> Actions.turnHead(player, 180f, 60f), 211);
			Utils.scheduleTask(() -> Actions.rightClick(player), 212);
			Utils.scheduleTask(() -> Actions.rightClick(player), 213);
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 214);
			Utils.scheduleTask(() -> Actions.rightClick(player), 215);
			Utils.scheduleTask(() -> Actions.rightClick(player), 216);
			Utils.scheduleTask(() -> Actions.rightClick(player), 217);
			Utils.scheduleTask(() -> Actions.rightClick(player), 218);
			Utils.scheduleTask(() -> Actions.rightClick(player), 219);
			Utils.scheduleTask(() -> Actions.turnHead(player, 0f, 60f), 220);
			Utils.scheduleTask(() -> Actions.rightClick(player), 221);
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 222);
			Utils.scheduleTask(() -> Actions.rightClick(player), 223);
			Utils.scheduleTask(() -> Actions.turnHead(player, 0f, 60f), 224);
			Utils.scheduleTask(() -> Actions.rightClick(player), 225);
			Utils.scheduleTask(() -> Actions.rightClick(player), 226);
			Utils.scheduleTask(() -> Actions.turnHead(player, 90f, 60f), 227);
			Utils.scheduleTask(() -> {
				Actions.rightClick(player);
				Server.IceFill.playIceFillSounds(3, player);
				Server.openIceFillRewards();
				Bukkit.broadcast(Utils.msg("<red>Berserk: Ice Fill Cleared"));
			}, 228);
			Utils.scheduleTask(() -> Actions.move(player, "WP", 1), 229);
			Utils.scheduleTask(() -> Actions.turnHead(player, 68f, -33f), 230);
			Utils.scheduleTask(() -> {
				Actions.leftClick(player);
				Utils.broadcastBlessing(player, Utils.BlessingType.POWER, 5);
				Utils.playSecretFoundSound(player, Utils.SecretType.BLESSING_CHEST);
			}, 248);
			Utils.scheduleTask(() -> Actions.turnHead(player, 112f, -33f), 249);
			Utils.scheduleTask(() -> {
				Actions.leftClick(player);
				Utils.broadcastBlessing(player, Utils.BlessingType.POWER, 5);
				Utils.playSecretFoundSound(player, Utils.SecretType.BLESSING_CHEST);
				Bukkit.broadcast(Utils.msg("<red>Berserk: Clear Finished in 250 Ticks (12.50 seconds)"));
			}, 250);
		}
	}
}
