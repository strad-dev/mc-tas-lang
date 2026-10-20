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

//		at(26).turnHead(-110f, 11f).setHotbarSlot(7);
//		at(27).rightClick(); // throw pearl into spot | lands in 5 ticks
//		at(28).turnHead(180f, 35f).setHotbarSlot(5);
//		// tick 32: pearl lands
//		at(33).move("WP", 6).leftClick();
//		at(34).leftClick();
//		at(35).leftClick();
//		at(36).leftClick();
//		at(37).leftClick();
//		at(38).leftClick();
//		at(39).turnHead(180f, 35f);
//		at(39).turnHead(175f, 9.5f).setHotbarSlot(7);
//		at(40).rightClick(); // throw pearl across | lands in 12 ticks
//		at(41).turnHead(-160f, 55f);
//		at(42).leftClick();
//		at(43).turnHead(109.5f, -68f);
//		// Tick 52: pearl lands
//		at(53).rightClick(); // throw pearl up | lands in 21 ticks
//		at(54).turnHead(32f, 16f);
//		// Tick 74: pearl lands
//		at(75).rightClick(); // throw pearl towards bat | lands in 4 ticks
//		at(76).turnHead(0f, 0f);
//		// Tick 79: pearl lands
//		at(80).move("AWP", 0);
//		at(82).move("WP", 3);
//		at(84).turnHead(60f, 6f);
//		at(86).rightClick(); // pearl to dig spot | lands in 3 ticks
//		at(87).turnHead(78f, -22f).setHotbarSlot(0);
//		at(88).leftClick();
//		at(89).turnHead(90f, 35f).setHotbarSlot(5); // pearl lands
//		at(90).leftClick().move("WP", 9);
//		at(91).leftClick();
//		at(92).leftClick();
//		at(93).leftClick();
//		at(94).leftClick();
//		at(95).leftClick();
//		at(96).leftClick();
//		at(97).leftClick();
//		at(99).turnHead(0f, 20f).setHotbarSlot(7);
//		at(100).rightClick(); // throw pearl towards chest | lands in 4 ticks
//		at(101).turnHead(0f, 50f);
//		// Tick 104: pearl lands
//		at(105).leftClick();
//		at(106).turnHead(0f, 81.5f).setHotbarSlot(5);
//		at(107).leftClick(); // stonk block
//		at(108).setHotbarSlot(7);
//		at(109).rightClick(); // throw pearl down | lands in 8 ticks
//		at(110).turnHead(0f, 30f).setHotbarSlot(5);
//		// Tick 117: pearl lands
//		at(118).move("WP", 0).leftClick();
//		at(119).leftClick();
//		at(120).leftClick();
//		at(121).leftClick();
//		at(122).leftClick();
//		at(123).leftClick();
//		at(124).leftClick(); // stonk outwards
//		at(126).turnHead(-58.5f, 38f).move("N", 2).setHotbarSlot(7);
//		at(127).rightClick(); // throw pearl down | lands in 10 ticks
//		at(128).turnHead(-47f, 20f);
//		// Tick 137: pearl lands
//		at(138).rightClick(); // throw pearl out of trap | lands in 3 ticks
//		at(139).turnHead(16f, 2.9f).move("N", 4).setHotbarSlot(1);
//		// Tick 141: pearl lands
//		at(142).rightClick(); // etherwarp to red blue
//		// Trap: 142 ticks | Time from Enter: 117 ticks
//
//		/*
//		 * ██████╗ ███████╗██████╗     ██████╗ ██╗     ██╗   ██╗███████╗
//		 * ██╔══██╗██╔════╝██╔══██╗    ██╔══██╗██║     ██║   ██║██╔════╝
//		 * ██████╔╝█████╗  ██║  ██║    ██████╔╝██║     ██║   ██║█████╗
//		 * ██╔══██╗██╔══╝  ██║  ██║    ██╔══██╗██║     ██║   ██║██╔══╝
//		 * ██║  ██║███████╗██████╔╝    ██████╔╝███████╗╚██████╔╝███████╗
//		 * ╚═╝  ╚═╝╚══════╝╚═════╝     ╚═════╝ ╚══════╝ ╚═════╝ ╚══════╝
//		 */
//		at(143).turnHead(65f, 0f).setHotbarSlot(5);
//		at(144).leftClick();
//		at(145).setHotbarSlot(1).leftClick();
//		at(146).turnHead(90f, -90f).move("N", 30);
//		at(147).rightClick(); // etherwarp to prince
//		at(148).turnHead(90f, 0f).swapItems(5, 31).setHotbarSlot(5); // Stonk -> 31, Infinityboom TNT -> 5
//		at(149).rightClick(); // blow up crypt
//		at(150).swapItems(5, 31).setHotbarSlot(3); // restore: Stonk -> 5, Infinityboom TNT -> 31; Claymore
//		at(151).leftClick(); // kill prince | claymore swing
//		at(152).turnHead(180f, -5f).setHotbarSlot(1);
//		at(153).rightClick(); // etherwarp across
//		at(154).turnHead(100f, 10f);
//		at(155).rightClick(); // etherwarp to item
//		at(156).turnHead(-97f, -4f);
//		at(157).rightClick(); // etherwarp to chest
//		at(158).turnHead(-115f, 0f).setHotbarSlot(5);
//		at(159).leftClick();
//		at(160).leftClick(); // stonk towards chest
//		at(161).setHotbarSlot(1).leftClick();
//		at(162).turnHead(57f, 39f);
//		at(163).rightClick(); // etherwarp down
//		at(164).turnHead(118f, -39f);
//		at(165).rightClick(); // etherwarp to wither essence
//		at(166).turnHead(23f, 30f);
//		at(167).leftClick();
//		at(168).turnHead(176f, 44.4f);
//		at(169).rightClick(); // etherwarp towards yellow
//		// Red Blue: 26 ticks
//
//		/*
//		 * ██╗   ██╗███████╗██╗     ██╗      ██████╗ ██╗    ██╗
//		 * ╚██╗ ██╔╝██╔════╝██║     ██║     ██╔═══██╗██║    ██║
//		 *  ╚████╔╝ █████╗  ██║     ██║     ██║   ██║██║ █╗ ██║
//		 *   ╚██╔╝  ██╔══╝  ██║     ██║     ██║   ██║██║███╗██║
//		 *    ██║   ███████╗███████╗███████╗╚██████╔╝╚███╔███╔╝
//		 *    ╚═╝   ╚══════╝╚══════╝╚══════╝ ╚═════╝  ╚══╝╚══╝
//		 */
//		at(170).turnHead(180f, 5.5f);
//		at(171).rightClick(); // etherwarp up to miniboss
//		at(172).setHotbarSlot(3);
//		at(173).leftClick();
//		// Yellow: 10 ticks
//
//		at(175).swapItems(1, 28).swapItems(3, 30).swapItems(6, 33).swapItems(7, 34).swapItems(12, 39).setHotbarSlot(5);
//		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
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
