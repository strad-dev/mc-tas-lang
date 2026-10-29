package instructions.players;

import commands.TAS;
import instructions.Actions;
import org.bukkit.entity.Player;
import plugin.TASUtils;

public class Healer extends Actions {
	private static final int LEAP_ARCH = 0;
	private static final int LEAP_BERS = 44;
	private static final int LEAP_MAGE = 8;
	private static final int LEAP_TANK = 36;

	public Healer(Player player) {
		super(player);
	}

	@SuppressWarnings("FieldCanBeLocal")
	// Healer --> NoseThe
	public void startPosition(String section) {
		switch(section) {
			case "all", "clear" -> startAt(-120.5, 71, -183.5, 0f, 0f);
			case "maxor", "boss" -> startAt(73.5, 221, 14.5, 0f, 0f);
			case "storm" -> startAt(32.702, 170, 95.151, -155f, 0f);
			case "goldor" -> startAt(107.622, 120, 93.618, -123f, 1f);
			case "necron" -> startAt(54.524, 64, 100.707, 180f, -6f);
			case "witherking" -> startAt(56.326, 8, 130.7, -16.2f, 18.8f);
		}
	}

	public void healerInstructions(String section) {
		int start = TAS.startDelay(section);
		switch(section) {
			case "all", "clear" -> {
				preClear();
				TASUtils.scheduleTask(() -> clear(section.equals("all")), start);
			}
			case "maxor", "boss" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				useSpeedLoadout();
				setHotbarSlot(4);
				if(section.equals("maxor")) {
					TASUtils.scheduleTask(() -> maxor(false), start);
				} else {
					TASUtils.scheduleTask(() -> maxor(true), start);
				}
			}
			case "storm" -> {
				startAt(36.3, 170, 105.7, 2.5f, 45f);
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				useSpeedLoadout();
				setHotbarSlot(5);
				TASUtils.scheduleTask(() -> storm(false), start);
			}
			case "goldor" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				swapItems(12, 39);
				setHotbarSlot(5);
				TASUtils.scheduleTask(() -> goldor(false), start);
			}
			case "necron" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				TASUtils.scheduleTask(() -> necron(false), start);
			}
			case "witherking" -> {
				swapItems(1, 28);
				swapItems(6, 33);
				swapItems(7, 34);
				swapItems(13, 39);
				setHotbarSlot(5);
				TASUtils.scheduleTask(this::witherKing, start);
			}
		}
	}

	public void preClear() {
		setHotbarSlot(1);
		move("N");
		at(1).rightClick(0f, 23f);
		at(2).rightClick();
	}

	public void clear(boolean doContinue) {
		/*
		 * ████████╗██████╗  █████╗ ██████╗
		 * ╚══██╔══╝██╔══██╗██╔══██╗██╔══██╗
		 *    ██║   ██████╔╝███████║██████╔╝
		 *    ██║   ██╔══██╗██╔══██║██╔═══╝
		 *    ██║   ██║  ██║██║  ██║██║
		 *    ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝
		 */
		at(15).rightClick(-9f, 2.9f); // etherwarp into red blue
		at(16).rightClick(109.5f, 1.75f); // etherwarp towards trap
		at(17).rightClick(-159f, 4.5f); // etherwarp into trap
		at(18).setHotbarSlot(7).rightClick(-110f, 8f); // throw reposition pearl, lands in 5 ticks
		at(19).turnHead(180f, 35f).setHotbarSlot(5);
		at(23).move("WP").leftClick();
		at(24).leftClick();
		at(25).leftClick();
		at(26).leftClick();
		at(27).leftClick();
		at(28).leftClick();
		at(29).move("").setHotbarSlot(7).rightClick(175f, 9.5f); // throw pearl, lands in 12 ticks
		at(30).rightClick(-160f, 55f); // trap 1/3
		at(31).turnHead(105f, -68f);
		at(41).rightClick(); // throw pearl up, lands in 27 ticks
		at(42).turnHead(-7f, 10f);
		at(68).rightClick(); // throw reposition pearl, lands in 5 ticks
		at(69).turnHead(60f, 6f);
		at(73).rightClick(); // throw reposition pearl, lands in 3 ticks
		at(74).setHotbarSlot(3).turnHead(72f, -28f).leftClick(); // trap 2/3
		at(75).setHotbarSlot(5).turnHead(90f, 35f);
		at(76).move("WP").leftClick();
		at(77).leftClick();
		at(78).leftClick();
		at(79).leftClick();
		at(80).leftClick();
		at(81).leftClick();
		at(82).leftClick();
		at(83).leftClick();
		// wait 1 tick for better positioning
		at(85).move("").setHotbarSlot(7).rightClick(0f, 20f); // throw pearl towards chest, lands in 4 ticks
		at(89).setHotbarSlot(5).turnHead(0f, 85f).leftClick();
		at(90).setHotbarSlot(7).rightClick(); // throw pearl down, lands in 9 ticks
		at(91).rightClick(0f, 50f); // trap 3/3
		at(92).turnHead(0f, 20f).setHotbarSlot(5);
		at(99).move("WP").leftClick();
		at(100).leftClick();
		at(101).leftClick();
		at(102).leftClick();
		at(103).leftClick();
		at(104).leftClick();
		at(105).leftClick();
		at(106).move("N").setHotbarSlot(7).turnHead(-56.5f, 38f);
		at(107).rightClick(); // throw pearl down, lands in 10 ticks, must wait 1 tick for sneak to actually activate
		at(108).turnHead(-45.1f, 21f).move("");
		at(117).rightClick(); // throw reposition pearl, lands in 3 ticks
		at(118).move("N").setHotbarSlot(1).turnHead(16f, 2.9f);
		at(120).rightClick().move(""); // etherwarp to red blue
		// Trap: 120 ticks (-22 | -22) | Time from Enter: 103 ticks (-14)

		/*
		 * ██████╗ ███████╗██████╗     ██████╗ ██╗     ██╗   ██╗███████╗
		 * ██╔══██╗██╔════╝██╔══██╗    ██╔══██╗██║     ██║   ██║██╔════╝
		 * ██████╔╝█████╗  ██║  ██║    ██████╔╝██║     ██║   ██║█████╗
		 * ██╔══██╗██╔══╝  ██║  ██║    ██╔══██╗██║     ██║   ██║██╔══╝
		 * ██║  ██║███████╗██████╔╝    ██████╔╝███████╗╚██████╔╝███████╗
		 * ╚═╝  ╚═╝╚══════╝╚═════╝     ╚═════╝ ╚══════╝ ╚═════╝ ╚══════╝
		 */
		at(121).turnHead(65f, 0f).setHotbarSlot(5).leftClick();
		at(122).rightClick().setHotbarSlot(1).move("N"); // red blue 1/4, pre-sneak
		at(123).rightClick(90f, -90f); // etherwarp to prince
		at(124).swapItems(4, 31).setHotbarSlot(4).rightClick(90f, 0f); // blow up crypt
		at(125).swapItems(4, 31).setHotbarSlot(3).leftClick(); // kill prince, crypt 5/5
		at(126).setHotbarSlot(1).rightClick(180f, -5f); // etherwarp across
		at(127).rightClick(100f, 10f); // etherwarp to item, red blue 2/4
		at(128).rightClick(-97f, -4f); // etherwarp to chest
		at(129).rightClick(); // etherwarp to chest
		at(130).turnHead(-115f, 0f).setHotbarSlot(5).leftClick();
		at(131).leftClick(); // stonk towards chest
		at(132).setHotbarSlot(1).rightClick(); // red blue 3/4
		at(133).rightClick(57f, 39f); // etherwarp down
		at(134).rightClick(118f, -39f); // etherwarp to wither essence
		at(135).setHotbarSlot(5).rightClick(23f, 30f); // red blue 4/4, must swap off aotv to avoid item use
		at(136).setHotbarSlot(1).rightClick(176f, 44.4f); // etherwarp towards yellow
		// Red Blue: 16 ticks (-11 | -33)

		/*
		 * ██╗   ██╗███████╗██╗     ██╗      ██████╗ ██╗    ██╗
		 * ╚██╗ ██╔╝██╔════╝██║     ██║     ██╔═══██╗██║    ██║
		 *  ╚████╔╝ █████╗  ██║     ██║     ██║   ██║██║ █╗ ██║
		 *   ╚██╔╝  ██╔══╝  ██║     ██║     ██║   ██║██║███╗██║
		 *    ██║   ███████╗███████╗███████╗╚██████╔╝╚███╔███╔╝
		 *    ╚═╝   ╚══════╝╚══════╝╚══════╝ ╚═════╝  ╚══╝╚══╝
		 */
		at(137).rightClick(180f, 5.5f); // etherwap to miniboss
		at(138).setHotbarSlot(3).leftClick(); // kill miniboss
		// Yellow: 2 ticks (-3 | -36)

		at(139).swapItems(1, 28).swapItems(3, 30).swapItems(6, 33).swapItems(7, 34).useSpeedLoadout().setHotbarSlot(4); // swap out boss items
		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
	}

	public void maxor(boolean doContinue) {
		turnHead(20f, 0f);
		move("WP");
		at(12).move("WPJ");
		at(15).turnHead(90f, 0f).move("WP");
		at(20).move("WPJ");
		at(26).turnHead(88.5f, 0f);
		at(31).move("WP");
		at(40).move("WN");
		at(41).turnHead(-50f, 0f).move("");
		at(179).rightClick();
		at(180).clickGui(LEAP_BERS);
		at(181).turnHead(40f, 0f).move("WP").setHotbarSlot(1);
		at(245).turnHead(20f, 75f).rightClick(); // bonzo to pad
		at(246).setHotbarSlot(5);
		// there is no good place to place the player before the transition tick, so storm start will place the player at dig spot, and maxor will handle the loose ends
		// this is done in a way where it is still deterministic
		at(258).turnHead(2.5f, 45f); // storm tick 8
		// tick 268: stopped by block, can dig on tick 269 / storm tick 19
		if(doContinue) TASUtils.scheduleTask(() -> storm(true), TAS.MAXOR_TICKS);
	}

	public void storm(boolean doContinue) {
		at(19).turnHead(0f, 45f).move("WP").leftClick(); // start moving again (case of storm-only), mine to chest room
		at(20).leftClick(); // 18
		at(21).leftClick(); // 17
		at(22).leftClick(); // 16
		at(23).leftClick(); // 15
		at(24).leftClick(); // 14
		at(25).leftClick(); // 13
		at(26).leftClick(); // 12
		at(27).leftClick(); // 11
		at(28).leftClick(); // 10
		at(29).leftClick(); // 10
		at(30).leftClick(); // 9
		at(31).leftClick(); // 8
		at(32).leftClick(); // 7
		at(33).leftClick(); // 6
		at(34).leftClick(); // 5
		at(35).leftClick(); // 4
		at(36).leftClick(); // 3
		at(37).leftClick(); // 2
		// 39: 3
		// 49: 4
		at(55).move("WPJ");
		at(56).move("WP");
		// 59: 5
		at(62).turnHead(-90f, 45f);
		// 69: 6
		at(70).leftClick(); // 5
		at(71).leftClick(); // 4
		at(72).leftClick(); // 3
		at(73).leftClick(); // 2
		at(74).leftClick(); // 1
		at(75).leftClick(); // 0
		at(79).turnHead(-90f, 90f).leftClick().move("WDP");
		at(89).leftClick();
		at(99).leftClick();
		at(109).leftClick();
		at(119).leftClick();
		at(129).leftClick();
		at(139).leftClick();
		at(149).leftClick();
		at(159).leftClick();
		at(169).leftClick();
		at(179).leftClick();
		at(189).leftClick();
		at(199).leftClick();
		at(209).leftClick();
		at(219).leftClick();
		at(229).leftClick();
		at(239).leftClick(); // mine down to lights, charges fully regenerate by tick 439
		at(248).move("WP"); // land on pipe
		at(252).move("WAP");
		at(253).move("WDP");
		at(265).move("WP");
		at(269).move("WPJ");
		at(270).move("WDP");
		at(279).move("").rightClick(8f, -42f);
		at(280).rightClick(16f, 2f);
		at(281).rightClick(-18f, -32f);
		at(282).rightClick(-16f, -17f);
		at(283).rightClick(-44f, 2f).move("WPJ");
		at(284).rightClick(-44f, -41f);
		at(285).setHotbarSlot(4).rightClick(180f, 0f);
		at(286).clickGui(LEAP_TANK);
		at(287).turnHead(77.5f, 0f).move("WP").setHotbarSlot(3);
		at(296).move("WPJ");
		at(297).move("WP");
		at(314).move("");
		for(int i = 315; i <= 495; i += 5) {
			at(i).snapHeadToNearestEnemy().leftClick();
		}
		at(496).setHotbarSlot(5).turnHead(90f, 45f).move("WP");
		at(497).leftClick();
		at(498).leftClick();
		at(499).leftClick();
		at(500).leftClick();
		at(501).leftClick();
		at(502).leftClick();
		at(503).leftClick();
		at(504).leftClick();
		at(505).leftClick();
		at(506).leftClick();
		at(507).leftClick();
		at(508).leftClick();
		at(509).leftClick();
		at(510).leftClick();
		at(511).leftClick();
		at(512).leftClick();
		at(513).setHotbarSlot(4).rightClick();
		at(514).clickGui(LEAP_TANK);
		at(515).useDpsLoadout();
		at(526).turnHead(-93f, 0f).move("WP");
		at(537).move("N");
		at(547).move("");
		at(548).setHotbarSlot(6).rightClick(-95.5f, -20f);
		at(645).stopRightClick();
		at(660).setHotbarSlot(3).snapHeadAtNearestNamed("Storm").leftClick();
		at(661).useSpeedLoadout();
		at(663).turnHead(42.5f, 75f).setHotbarSlot(1).move("WP");
		at(668).move("WPJ");
		at(671).rightClick();
		at(672).move("WP");
		at(691).move("");
		at(700).setHotbarSlot(4).rightClick();
		at(701).clickGui(LEAP_BERS);
		at(702).setHotbarSlot(1).turnHead(33f, 75f).move("S");
		at(703).rightClick();
		at(704).turnHead(-147f, 0f).move("WP");
		at(732).move("");

		// TODO move into goldor
		at(741).setHotbarSlot(5).turnHead(180f, 0f).leftClick();
		at(742).leftClick();
		at(743).leftClick();
		at(744).setHotbarSlot(5).turnHead(180f, 26f).leftClick();
		at(745).leftClick();
		at(746).leftClick();
		at(747).turnHead(26f, 0f).move("WPJ");
		at(750).move("").turnHead(-129f, 2f);

		if(doContinue) {
			TASUtils.scheduleTask(() -> goldor(true), TAS.STORM_TICKS);
		}
	}

	public void goldor(boolean doContinue) {
		/*
		 *  ██╗
		 * ███║
		 * ╚██║
		 *  ██║
		 *  ██║
		 *  ╚═╝
		 */
		rightClick();
		at(1).rightClick();
		// tick 2: body-blocked by tank
		// tick 3: body-blocked by tank
		at(4).rightClick();
		at(5).rightClick();
		at(6).rightClick();
		at(7).rightClick(); // s1 dev

		at(8).turnHead(0f, 80f).setHotbarSlot(1);
		at(9).move("WP", 0);
		at(10).move("WPJ", 0);
		at(12).move("WP", 12);
		at(14).rightClick();
		at(15).turnHead(0f, 0f).setHotbarSlot(5);
		at(24).turnHead(18f, 1f);
		at(25).rightClick(); // s1 left

		at(26).turnHead(75f, 90f).setHotbarSlot(1);
		at(27).move("J", 0);
		at(29).move("WP", 10);
		at(31).rightClick();
		at(40).turnHead(83f, 35f).setHotbarSlot(5);
		at(41).rightClick(); //s1 right

		at(42).setHotbarSlot(4);
		at(51).leap(fake("RedFish2471")); // delay for better positioning

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 * ██╔═══╝
		 * ███████╗
		 * ╚══════╝
		 */
		at(52).turnHead(0f, 90f).setHotbarSlot(7);
		at(53).move("WP", 44);
		at(59).rightClick();
		at(60).turnHead(0f, -10f).setHotbarSlot(5);
		at(99).rightClick(); // s2 dev

		at(100).turnHead(90f, 80f).setHotbarSlot(1);
		at(101).move("W", 0);
		at(102).move("WP", 27);
		at(108).rightClick();
		at(109).turnHead(90f, 0f).setHotbarSlot(4);
		at(123).turnHead(96f, 0f);
		at(128).turnHead(90f, 6f);
		at(129).rightClick(); // s2 top

		at(130).leap(fake("akc0303"));

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 *  ╚═══██╗
		 * ██████╔╝
		 * ╚═════╝
		 */
		at(131).turnHead(90f, 5f).setHotbarSlot(5);
		at(132).rightClick(); // s3 4
		// tick 133: terminal completes

		at(134).turnHead(-8f, 0f);
		at(135).move("WP", 10);
		at(142).turnHead(55f, 0f);
		at(157).turnHead(53f, -8f).setHotbarSlot(4);
		at(158).rightClick(); // s3 2
		// tick 159: terminal completes

		at(160).leap(fake("Beethoven"));

		/*
		 * ██╗  ██╗
		 * ██║  ██║
		 * ███████║
		 * ╚════██║
		 *      ██║
		 *      ╚═╝
		 */
		at(161).move("WP", 0).setHotbarSlot(1);
		at(170).move("WPJ", 12);
		at(172).turnHead(-100f, 80f);
		at(173).rightClick();
		at(174).turnHead(-100f, 0f).setHotbarSlot(4);
		at(180).turnHead(-151f, -22f);
		at(181).rightClick(); // s4 bottom

		at(191).leap(fake("Beethoven"));

		/*
		 * ███████╗██╗ ██████╗ ██╗  ██╗████████╗
		 * ██╔════╝██║██╔════╝ ██║  ██║╚══██╔══╝
		 * █████╗  ██║██║  ███╗███████║   ██║
		 * ██╔══╝  ██║██║   ██║██╔══██║   ██║
		 * ██║     ██║╚██████╔╝██║  ██║   ██║
		 * ╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═╝   ╚═╝
		 */
		at(200).turnHead(180f, 0f).setHotbarSlot(3);
		at(219).move("WP", 5);
		at(223).turnHead(-111f, -2f).swapItems(12, 39);
		at(224).leftClick();
		at(225).setHotbarSlot(4);
		at(245).leap(fake("akc0303"));
		at(246).move("WP", 0);
		at(287).move("WN", 0); 
		at(297).move("N", 10);
		if(doContinue) TASUtils.scheduleTask(() -> necron(true), TAS.GOLDOR_TICKS);
	}

	public void necron(boolean doContinue) {
		setHotbarSlot(6);
		at(1).rightClick();
		at(152).stopRightClick();
		at(153).setHotbarSlot(3);
		at(160).leftClick();
		at(161).setHotbarSlot(6);
		at(162).rightClick();
		at(183).stopRightClick();
		at(184).setHotbarSlot(3);
		for(int i = 185; i <= 360; i += 5) {
			at(i).leftClick();
		}
		at(361).setHotbarSlot(6);
		at(362).rightClick();
		at(383).stopRightClick();
		at(384).setHotbarSlot(3);
		for(int i = 385; i <= 500; i += 5) {
			at(i).leftClick();
		}
		at(501).setHotbarSlot(4);
		at(502).leap(fake("akc0303"));
		at(503).swapItems(13, 39);
		at(504).turnHead(-2f, 0f).setHotbarSlot(5);
		at(505).move("WP", 0);
		at(515).move("WPJ", 0);
		at(517).move("WP", 0);
		at(527).move("WPJ", 0);
		at(529).move("WP", 0);
		at(539).move("WPJ", 0);
		at(541).move("WP", 10);
		at(551).turnHead(0f, 0f);
		at(552).swapItems(3, 30);
		if(doContinue) TASUtils.scheduleTask(this::witherKing, TAS.NECRON_TICKS);
	}

	public void witherKing() {
		rightClick();
		at(1).setHotbarSlot(4);
		at(25).leap(fake("RedFish2471"));
		at(26).turnHead(90f, 0f).move("WDP", 0).setHotbarSlot(8);
		at(27).move("WP", 1);
		at(28).rightClick();
		at(29).swapItems(5, 32).swapItems(13, 39);
		at(30).setHotbarSlot(4);
		at(100).leap(fake("Beethoven"));
		at(101).setHotbarSlot(5).turnHead(180f, -90f);
		at(200).rightClick();
		at(261).setHotbarSlot(6);
		int lbFor = 10;
		for(int i = 290; i <= 386 - lbFor - 1; i += lbFor + 1) {
			at(i).rightClick();
			at(i + lbFor).stopRightClick();
		}
		at(387).setHotbarSlot(3);
		at(388).leftClick();
		at(393).leftClick();
		at(394).turnHead(-177.5f, -90f).setHotbarSlot(5);
		at(395).move("WP", 0);
		at(411).move("WPJ", 0);
		at(413).move("WP", 21);
		at(529).rightClick();
		at(590).setHotbarSlot(6);
		for(int i = 630; i <= 726 - lbFor - 1; i += lbFor + 1) {
			at(i).rightClick();
			at(i + lbFor).stopRightClick();
		}
		at(727).setHotbarSlot(3);
		at(728).leftClick();
		at(729).turnHead(92.5f, -90f).setHotbarSlot(5);
		at(730).rightClick().move("WP", 0);
		at(780).move("WPJ", 2);
		at(791).setHotbarSlot(6);
		for(int i = 792; i <= 826 - lbFor - 1; i += lbFor + 1) {
			at(i).rightClick();
			at(i + lbFor).stopRightClick();
		}
		at(827).setHotbarSlot(3);
		at(828).leftClick();
		at(829).turnHead(-3f, -90f).setHotbarSlot(6);
		at(830).move("WP", 0);
		at(851).move("WPJ", 0);
		at(853).move("WP", 14);
		for(int i = 858; i <= 926 - lbFor - 1; i += lbFor + 1) {
			at(i).rightClick();
			at(i + lbFor).stopRightClick();
		}
		at(927).setHotbarSlot(3);
		at(928).leftClick();
	}
}
