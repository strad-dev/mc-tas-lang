package instructions.players;

import commands.TAS;
import instructions.Actions;
import org.bukkit.entity.Player;
import plugin.TASUtils;

public class Healer extends Actions {
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
				swapItems(12, 39);
				setHotbarSlot(5);
				if(section.equals("maxor")) {
					TASUtils.scheduleTask(() -> maxor(false), start);
				} else {
					TASUtils.scheduleTask(() -> maxor(true), start);
				}
			}
			case "storm" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				setHotbarSlot(3);
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
		rightClick(0f, 23f);
		at(1).rightClick();
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
		at(18).setHotbarSlot(7).turnHead(-110f, 11f).move("WP");
		at(19).rightClick().move(""); // throw reposition pearl, lands in 5 ticks
		at(20).turnHead(180f, 35f).setHotbarSlot(5);
		at(24).move("WP").leftClick();
		at(25).leftClick();
		at(26).leftClick();
		at(27).leftClick();
		at(28).leftClick();
		at(29).leftClick();
		at(30).move("").setHotbarSlot(7).rightClick(175f, 9.5f); // throw pearl, lands in 12 ticks
		at(31).rightClick(-160f, 55f); // trap 1/3
		at(32).turnHead(105f, -68f);
		at(42).rightClick(); // throw pearl up, lands in 27 ticks
		at(43).turnHead(-7f, 10f);
		at(69).rightClick(); // throw reposition pearl, lands in 5 ticks
		at(70).turnHead(60f, 6f);
		at(74).rightClick(); // throw reposition pearl, lands in 3 ticks
		at(75).setHotbarSlot(3).turnHead(72f, -28f).leftClick(); // trap 2/3
		at(76).setHotbarSlot(5).turnHead(90f, 35f);
		at(77).move("WP").leftClick();
		at(78).leftClick();
		at(79).leftClick();
		at(80).leftClick();
		at(81).leftClick();
		at(82).leftClick();
		at(83).leftClick();
		at(84).leftClick();
		// wait 1 tick for better positioning
		at(86).move("").setHotbarSlot(7).rightClick(0f, 20f); // throw pearl towards chest, lands in 4 ticks
		at(90).setHotbarSlot(5).turnHead(0f, 85f).leftClick();
		at(91).setHotbarSlot(7).rightClick(); // throw pearl down, lands in 9 ticks
		at(92).rightClick(0f, 50f); // trap 3/3
		at(93).turnHead(0f, 20f).setHotbarSlot(5);
		at(100).move("WP").leftClick();
		at(101).leftClick();
		at(102).leftClick();
		at(103).leftClick();
		at(104).leftClick();
		at(105).leftClick();
		at(106).leftClick();
		at(107).move("N").setHotbarSlot(7).turnHead(-56.5f, 38f);
		at(108).rightClick(); // throw pearl down, lands in 10 ticks, must wait 1 tick for sneak to actually activate
		at(109).turnHead(-45.1f, 21f).move("");
		at(118).rightClick(); // throw reposition pearl, lands in 3 ticks
		at(119).move("N").setHotbarSlot(1).turnHead(16f, 2.9f);
		at(121).rightClick().move(""); // etherwarp to red blue
		// Trap: 121 ticks (-21 | -21) | Time from Enter: 104 ticks (-13)

		/*
		 * ██████╗ ███████╗██████╗     ██████╗ ██╗     ██╗   ██╗███████╗
		 * ██╔══██╗██╔════╝██╔══██╗    ██╔══██╗██║     ██║   ██║██╔════╝
		 * ██████╔╝█████╗  ██║  ██║    ██████╔╝██║     ██║   ██║█████╗
		 * ██╔══██╗██╔══╝  ██║  ██║    ██╔══██╗██║     ██║   ██║██╔══╝
		 * ██║  ██║███████╗██████╔╝    ██████╔╝███████╗╚██████╔╝███████╗
		 * ╚═╝  ╚═╝╚══════╝╚═════╝     ╚═════╝ ╚══════╝ ╚═════╝ ╚══════╝
		 */
		at(122).turnHead(65f, 0f).setHotbarSlot(5).leftClick();
		at(123).rightClick().setHotbarSlot(1); // red blue 1/4
		at(124).move("N").rightClick(90f, -90f); // etherwarp to prince
		at(125).swapItems(4, 31).setHotbarSlot(4).rightClick(90f, 0f); // blow up crypt
		at(126).swapItems(4, 31).setHotbarSlot(3).leftClick(); // kill prince
		at(127).setHotbarSlot(1).rightClick(180f, -5f); // etherwarp across
		at(128).rightClick(100f, 10f); // etherwarp to item, red blue 2/4
		at(129).rightClick(-97f, -4f); // etherwarp to chest
		at(130).rightClick(); // etherwarp to chest
		at(131).turnHead(-115f, 0f).setHotbarSlot(5).leftClick();
		at(132).leftClick(); // stonk towards chest
		at(133).setHotbarSlot(1).rightClick(); // red blue 3/4
		at(134).rightClick(57f, 39f); // etherwarp down
		at(135).rightClick(118f, -39f); // etherwarp to wither essence
		at(136).setHotbarSlot(5).rightClick(23f, 30f); // red blue 4/4, must swap off aotv to avoid item use
		at(137).setHotbarSlot(1).rightClick(176f, 44.4f); // etherwarp towards yellow
		// Red Blue: 16 ticks (-10 | -32)

		/*
		 * ██╗   ██╗███████╗██╗     ██╗      ██████╗ ██╗    ██╗
		 * ╚██╗ ██╔╝██╔════╝██║     ██║     ██╔═══██╗██║    ██║
		 *  ╚████╔╝ █████╗  ██║     ██║     ██║   ██║██║ █╗ ██║
		 *   ╚██╔╝  ██╔══╝  ██║     ██║     ██║   ██║██║███╗██║
		 *    ██║   ███████╗███████╗███████╗╚██████╔╝╚███╔███╔╝
		 *    ╚═╝   ╚══════╝╚══════╝╚══════╝ ╚═════╝  ╚══╝╚══╝
		 */
		at(138).rightClick(180f, 5.5f); // etherwap to miniboss
		at(139).setHotbarSlot(3).leftClick(); // kill miniboss
		// Yellow: 2 ticks (-3 | -35)

		at(140).swapItems(1, 28).swapItems(3, 30).swapItems(6, 33).swapItems(7, 34).swapItems(12, 39).runCommand("pet");
		at(141).clickGui(14);
		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
	}

	public void maxor(boolean doContinue) {
		at(1).turnHead(-14.5f, 37.5f);
		at(2).move("WP", 11);
		at(12).leftClick();
		at(13).leftClick(); // stonk maxor floor
		at(14).turnHead(-55f, 0f);
		at(16).move("WP", 0); // fall to storm floor
		at(62).turnHead(-90f, 30f);
		at(63).move("W", 11).leftClick(); // walk forward + fall to goldor floor
		at(64).leftClick();
		at(65).leftClick();
		at(66).leftClick();
		at(67).leftClick();
		at(68).leftClick();
		at(69).leftClick();
		at(70).leftClick();
		at(71).leftClick();
		at(72).leftClick(); // stonk into goldor
		at(74).turnHead(15f, 0f).setHotbarSlot(1); // adjust movement angle
		at(75).move("WP", 0);
		at(116).move("WPJ", 0);
		at(118).move("WP", 0);
		at(128).turnHead(0f, 0f);
		at(137).turnHead(-4f, 80f);
		at(138).rightClick(); // bonzo 1 of 3
		// tick 139: propelled forward for 10 ticks
		at(140).turnHead(0f, 80f);
		at(150).move("WPJ", 0); // jump to gain slightly more distance
		at(151).move("WP", 0);
		at(152).rightClick(); // bonzo 2 of 3
		// tick 154: propelled forward for 11 ticks
		at(166).rightClick(); // bonzo 3 of 3
		// tick 167: propelled forward for 10 ticks
		at(168).turnHead(0f, 45f).setHotbarSlot(5);
		// tick 177: walk forward and fall for 7 ticks
		at(184).leftClick();
		at(185).leftClick();
		at(186).leftClick();
		at(187).leftClick();
		at(188).leftClick();
		at(189).leftClick().move("W", 0);
		// tick 191: start falling for 13 ticks
		at(193).turnHead(75f, 90f);
		// tick 204: reach ground
		at(205).move("WP", 0);
		at(220).turnHead(90f, 90f).setHotbarSlot(7);
		at(222).move("WPJ", 0);
		at(224).move("WP", 49);
		at(237).rightClick(); // soul sand to lava jump higher
		at(238).turnHead(16f, 0f).setHotbarSlot(5);
		at(271).turnHead(85f, 0f);
		at(273).turnHead(-12f, -16f);
		at(274).rightClick();
		at(275).turnHead(-34f, -63f);
		at(276).rightClick();
		at(277).turnHead(34f, -30f);
		at(278).rightClick();
		at(279).turnHead(33f, -53f);
		at(280).rightClick();
		at(281).turnHead(66f, 3f);
		at(282).rightClick();
		at(283).turnHead(66f, -43f);
		at(284).rightClick();
		at(285).setHotbarSlot(4);
		at(286).leap(fake("RedFish2471")); // leap to bers
		at(403).setHotbarSlot(1).move("WP", 0);
		at(408).move("WPJ", 0).turnHead(34f, 80f);
		at(410).rightClick(); // bonzo to yellow pad
		at(411).turnHead(34f, 0f).move("WP", 19).setHotbarSlot(0);
		at(431).turnHead(-155f, 0f).swapItems(12, 39).setHotbarSlot(3); // rod swap off of black cat, remove racing helmet -> speed auto-set to 400
		if(doContinue) TASUtils.scheduleTask(() -> storm(true), TAS.MAXOR_TICKS);
	}

	public void storm(boolean doContinue) {
		for(int i = 0; i <= 80; i += 5) {
			at(i - 1).snapHeadToNearestEnemy();
			at(i).loopLeftClick();
		}
		at(81).setHotbarSlot(0);
		at(82).rightClick();
		at(85).rightClick();
		at(88).rightClick();
		at(91).rightClick();
		at(92).setHotbarSlot(3);
		for(int i = 95; i <= 170; i += 3) {
			at(i - 1).snapHeadToNearestEnemy();
			at(i).loopLeftClick();
		} // clear pad, including shadow assassin
		at(171).turnHead(-155f, 0f);
		at(172).move("WP", 32).setHotbarSlot(1); // move off the pad early enough
		at(179).turnHead(-155f, 80f);
		at(180).rightClick(); // bonzo back to pillar
		at(181).turnHead(-155f, 0f).setHotbarSlot(3);
		for(int i = 207; i <= 532; i += 5) {
			at(i - 1).snapHeadToNearestEnemy();
			at(i).loopLeftClick();
		} // kill outstanding wither skeletons
		at(533).turnHead(-85f, 0f).swapItems(5, 32).setHotbarSlot(6);
		at(546).move("WP", 0).rightClick(); // rag buff
		at(555).move("WN", 8); // go to checkpoint spot
		at(565).turnHead(-94.5f, -18.5f);
		at(607).setHotbarSlot(6).swapItems(5, 32);
		at(608).rightClick();
		at(665).stopRightClick();
		at(684).turnHead(90f, 0f);
		at(685).move("WP", 0);
		at(691).turnHead(36f, 0f);
		at(699).turnHead(36f, 80f).move("WPJ", 0).setHotbarSlot(1);
		at(702).rightClick();
		at(703).turnHead(36f, 0f).move("WP", 20).setHotbarSlot(3);
		at(723).turnHead(-144f, 0f);
		at(741).move("WP", 4);
		at(759).snapHeadAtNearestNamed("Storm");
		at(760).leftClick();
		at(761).setHotbarSlot(4).swapItems(12, 39); // put on racing helmet -> speed auto-set to 650
		at(782).leap(fake("RedFish2471"));
		at(783).setHotbarSlot(1).move("WP", 12);
		at(784).rightClick();
		at(785).turnHead(-47f, 0f).setHotbarSlot(5);
		at(800).turnHead(-141f, 1f);
		at(812).move("AN", 3);
		at(814).turnHead(-123f, 1f);
		if(doContinue) TASUtils.scheduleTask(() -> goldor(true), TAS.STORM_TICKS);
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
