package instructions.players;

import commands.TAS;
import instructions.Actions;
import org.bukkit.entity.Player;
import plugin.TASUtils;

public class Archer extends Actions {
	public Archer(Player player) {
		super(player);
	}

	public void startPosition(String section) {
		switch(section) {
			case "all", "clear" -> startAt(-120.5, 71, -183.5, 0f, 0f);
			case "maxor", "boss" -> startAt(73.5, 221, 14.5, 0f, 0f);
			case "storm" -> startAt(65.087, 165, 69.329, 180f, -4f);
			case "goldor" -> startAt(63.811, 127, 35.940, -74.5f, -46f);
			case "necron" -> startAt(54.524, 64, 100.707, 180f, -6f);
			case "witherking" -> startAt(22.3, 6, 59.408, 65.6f, 29.3f);
		}
	}

	public void archerInstructions(String section) {
		int start = TAS.startDelay(section);
		switch(section) {
			case "all", "clear" -> {
				preClear();
				TASUtils.scheduleTask(() -> clear(section.equals("all")), start);
			}
			case "maxor", "boss" -> {
				swapItems(1, 28);
				swapItems(6, 33);
				swapItems(7, 34);
				swapItems(11, 36);
				setHotbarSlot(5);
				if(section.equals("maxor")) {
					TASUtils.scheduleTask(() -> maxor(false), start);
				} else {
					TASUtils.scheduleTask(() -> maxor(true), start);
				}
			}
			case "storm" -> {
				swapItems(1, 28);
				swapItems(6, 33);
				swapItems(7, 34);
				setHotbarSlot(4);
				TASUtils.scheduleTask(() -> storm(false), start);
			}
			case "goldor" -> {
				swapItems(1, 28);
				swapItems(5, 32);
				swapItems(6, 33);
				swapItems(7, 34);
				swapItems(12, 39);
				setHotbarSlot(5);
				at(1).dropItem(false);
				at(19).rightClick();
				at(26).stopRightClick();
				at(27).rightClick();
				at(34).stopRightClick();
				at(35).rightClick();
				at(42).stopRightClick();
				at(45).turnHead(-10.5f, -15.5f).setHotbarSlot(4);
				at(46).swapItems(18, 39).swapItems(19, 38).swapItems(20, 37).swapItems(21, 36);
				at(50).dropItem(true);
				at(51).turnHead(-3f, -4f);
				at(55).turnHead(-15f, -2.5f);
				at(56).rightClick();
				at(57).turnHead(-3f, 4f);
				// rapid fire #2 at 58
				at(59).turnHead(-14.5f, 5f);
				TASUtils.scheduleTask(() -> goldor(false), start);
			}
			case "necron" -> {
				swapItems(1, 28);
				swapItems(5, 32);
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

	private void preClear() {
		setHotbarSlot(1);
		move("N");
		rightClick(0f, 23f);
		at(1).rightClick();
	}

	private void clear(boolean doContinue) {
		/*
		 * ██████╗ ██╗      ██████╗  ██████╗ ██████╗     ██████╗ ██╗   ██╗███████╗██╗  ██╗
		 * ██╔══██╗██║     ██╔═══██╗██╔═══██╗██╔══██╗    ██╔══██╗██║   ██║██╔════╝██║  ██║
		 * ██████╔╝██║     ██║   ██║██║   ██║██║  ██║    ██████╔╝██║   ██║███████╗███████║
		 * ██╔══██╗██║     ██║   ██║██║   ██║██║  ██║    ██╔══██╗██║   ██║╚════██║██╔══██║
		 * ██████╔╝███████╗╚██████╔╝╚██████╔╝██████╔╝    ██║  ██║╚██████╔╝███████║██║  ██║
		 * ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝ ╚═════╝     ╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝
		 */
		at(15).rightClick(-2f, -1.5f); // etherwarp into fairy
		at(16).rightClick(7f, 17f); // etherwarp to wither door
		at(17).leftClick(); // open wither door
		at(27).move("JN");
		at(28).move("N");
		at(32).rightClick(0f, 12.85f); // go pick up key
		// Blood Rush: 32 ticks (-19)

		/*
		 * ███╗   ███╗██╗   ██╗███████╗███████╗██╗   ██╗███╗   ███╗
		 * ████╗ ████║██║   ██║██╔════╝██╔════╝██║   ██║████╗ ████║
		 * ██╔████╔██║██║   ██║███████╗█████╗  ██║   ██║██╔████╔██║
		 * ██║╚██╔╝██║██║   ██║╚════██║██╔══╝  ██║   ██║██║╚██╔╝██║
		 * ██║ ╚═╝ ██║╚██████╔╝███████║███████╗╚██████╔╝██║ ╚═╝ ██║
		 * ╚═╝     ╚═╝ ╚═════╝ ╚══════╝╚══════╝ ╚═════╝ ╚═╝     ╚═╝
		 */
		at(33).rightClick(97f, -6f); // etherwarp into museum
		at(34).rightClick(155f, 10f); // respoition for miniboss + secret
		at(35).setHotbarSlot(4).rightClick(0f, 13f); // kill miniboss
		at(36).setHotbarSlot(1).rightClick(180f, 1.4f); // etherwarp to secret
		at(37).setHotbarSlot(3).rightClick(); // blow up wall w/ tnt
		at(38).setHotbarSlot(1).rightClick(); // museum 1/5
		at(39).rightClick(45f, -43f); // etherwarp up
		at(40).rightClick(110f, -60f); // etherwarp to essence
		at(41).setHotbarSlot(5).rightClick(-147f, 15f); // musuem 2/5
		at(42).setHotbarSlot(1).rightClick(-26f, 64f); // etherwarp down
		at(43).rightClick(-10f, 2.5f); // etherwarp across
		at(44).setHotbarSlot(5).turnHead(1f, 15f).leftClick(); // stonk wall, you cannot etherwap through the gap
		at(45).leftClick();
		at(46).setHotbarSlot(1).rightClick(); // etherwarp in
		at(47).setHotbarSlot(5).rightClick(-90f, 0f); // museum 3/5
		at(48).turnHead(-90f, 90f).leftClick();
		at(49).leftClick();
		at(50).setHotbarSlot(1).rightClick(-90f, 79f);
		at(51).setHotbarSlot(4).rightClick(-45f, 0f); // museum 4/5
		at(52).rightClick(); // kill mimic
		at(53).setHotbarSlot(1).rightClick(80f, 0f);
		at(54).setHotbarSlot(5).turnHead(70f, 30f).leftClick(); // break wall
		at(55).setHotbarSlot(1).rightClick(); // etherwarp towards secret
		at(56).setHotbarSlot(3).rightClick(18f, 65f); // blow up floor
		at(57).setHotbarSlot(1).rightClick(); // etherwarp down
		at(58).rightClick(125f, 0f); // museum 5/5
		at(59).rightClick(180f, -68f); // etherwarp out
		at(60).rightClick(20f, 1.1f); // etherwarp into market
		// Museum: 28 ticks (-18 | -37)

		/*
		 * ███╗   ███╗ █████╗ ██████╗ ██╗  ██╗███████╗████████╗
		 * ████╗ ████║██╔══██╗██╔══██╗██║ ██╔╝██╔════╝╚══██╔══╝
		 * ██╔████╔██║███████║██████╔╝█████╔╝ █████╗     ██║
		 * ██║╚██╔╝██║██╔══██║██╔══██╗██╔═██╗ ██╔══╝     ██║
		 * ██║ ╚═╝ ██║██║  ██║██║  ██║██║  ██╗███████╗   ██║
		 * ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝   ╚═╝
		 */
//		at(98).turnHead(-10f, 0f).setHotbarSlot(7);
//		at(99).rightClick(); // throw pearl to reposition | lands in 9 ticks
//		at(100).turnHead(54.2f, 63.9f).setHotbarSlot(4);
//		at(101).rightClick(); // blow up floor
//		at(102).setHotbarSlot(1);
//		at(103).rightClick(); // etherwarp down
//		at(104).turnHead(-72f, 5f).setHotbarSlot(0);
//		at(105).rightClick();
//		at(106).turnHead(180f, 20f).setHotbarSlot(7);
//		at(107).leftClick();
//		at(108).turnHead(-12f, -3.5f);
//		at(109).rightClick(); // throw pearl | lands in 10 ticks
//		at(110).turnHead(53f, -78.5f).setHotbarSlot(1);
//		at(111).rightClick(); // etherwarp up
//		at(112).turnHead(90f, 35f).move("WP", 5).setHotbarSlot(5);
//		at(113).leftClick();
//		at(114).leftClick();
//		at(115).leftClick();
//		at(116).leftClick(); // break through wall
//		at(117).turnHead(124f, 16f).setHotbarSlot(7);
//		at(118).leftClick();
//		at(119).turnHead(15f, 6f); // pearl lands
//		at(120).rightClick(); // throw pearl to reposition | lands in 9 ticks
//		at(121).turnHead(-90f, 73f).setHotbarSlot(1);
//		at(122).rightClick(); // aotv down
//		at(123).turnHead(-90f, 90f).setHotbarSlot(5);
//		at(124).leftClick(); // break block
//		at(125).setHotbarSlot(1);
//		at(126).rightClick(); // aotv down
//		at(127).turnHead(66f, -8f);
//		at(128).leftClick();
//		at(129).turnHead(-45f, 0f).setHotbarSlot(3); // pearl lands
//		at(130).leftClick(); // kill miniboss
//		at(131).turnHead(-10f, 4f).setHotbarSlot(4);
//		at(132).rightClick(); // blow up crypt
//		at(133).setHotbarSlot(3);
//		at(134).leftClick();
//		at(135).turnHead(-45f, -70f).setHotbarSlot(1).move("N", 13);
//		at(136).rightClick(); // etherwarp up
//		at(137).turnHead(20f, -46f).setHotbarSlot(5);
//		at(138).leftClick(); // break block
//		at(139).setHotbarSlot(1).leftClick();
//		at(140).turnHead(-71.5f, 23f);
//		at(141).rightClick(); // etherwarp into hallway
//		// Market: 44 ticks
//
//		/*
//		 * ██╗  ██╗ █████╗ ██╗     ██╗     ██╗    ██╗ █████╗ ██╗   ██╗
//		 * ██║  ██║██╔══██╗██║     ██║     ██║    ██║██╔══██╗╚██╗ ██╔╝
//		 * ███████║███████║██║     ██║     ██║ █╗ ██║███████║ ╚████╔╝
//		 * ██╔══██║██╔══██║██║     ██║     ██║███╗██║██╔══██║  ╚██╔╝
//		 * ██║  ██║██║  ██║███████╗███████╗╚███╔███╔╝██║  ██║   ██║
//		 * ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚══════╝ ╚══╝╚══╝ ╚═╝  ╚═╝   ╚═╝
//		 */
//		at(142).turnHead(-97f, 0f);
//		at(143).rightClick(); // etherwarp towards miniboss
//		at(144).turnHead(-90f, 9f).setHotbarSlot(0);
//		at(145).leftClick();
//		at(146).turnHead(-99f, 8f).setHotbarSlot(1);
//		at(147).rightClick(); // etherwarp towards chest
//		at(148).turnHead(-57f, 6f).setHotbarSlot(7);
//		at(149).rightClick(); // throw pearl for reposiiton | lands in 6 ticks
//		at(150).turnHead(-180f, 40f).setHotbarSlot(5).move("WP", 4);
//		at(151).leftClick();
//		at(152).leftClick(); // stonk iron bars
//		at(153).setHotbarSlot(1); // move for 2 more ticks
//		at(154).turnHead(166f, 15f);
//		at(155).leftClick();
//		at(156).turnHead(-122.5f, 2f).move("N", 2); // pearl lands
//		at(157).rightClick(); // etherwarp to next secret
//		at(158).turnHead(-90f, 30f).setHotbarSlot(5);
//		at(159).leftClick().move("WP", 4);
//		at(160).leftClick();
//		at(161).leftClick();
//		at(162).leftClick();
//		at(163).turnHead(-150f, 43f).setHotbarSlot(1);
//		at(164).leftClick();
//		at(165).turnHead(-75f, -55f).move("N", 10);
//		at(166).rightClick(); // etherwarp up
//		at(167).turnHead(-104.5f, -5f);
//		at(168).rightClick(); // etherwarp to next secret
//		at(169).turnHead(-90f, -81f);
//		at(170).rightClick(); // etherwarp up
//		at(171).turnHead(-22.5f, 17f);
//		at(172).rightClick(); // etherwarp to secret
//		at(173).leftClick();
//		// Hallway: 32 ticks
//
//		at(619).swapItems(1, 28).swapItems(6, 33).swapItems(7, 34).swapItems(11, 36).setHotbarSlot(5);
//		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
	}

	public void maxor(boolean doContinue) {
		at(1).turnHead(13f, 0f);
		at(2).move("WP", 0);
		at(25).move("WN", 0); // spring boots to right crystal
		at(38).move("WP", 49);
		at(60).rightClick();
		at(61).turnHead(90f, 0f);
		at(63).turnHead(133f, 0f);
		at(88).turnHead(-52f, 0f); // land on pad
		at(161).move("WP", 0); // spring boots again to right crystal
		at(162).move("WN", 0);
		at(173).move("WP", 22);
		at(200).swapItems(11, 36);
		at(240).rightClick();
		at(241).turnHead(128f, 0f);
		at(242).move("WP", 24);
		at(243).setHotbarSlot(2);
		at(339).leap(fake("NoseThe"));
		at(340).turnHead(-76f, 0f).setHotbarSlot(4);
		at(341).move("WP", 9); // move to storm clear spot
		at(350).turnHead(-180f, 0f);
		if(doContinue) TASUtils.scheduleTask(() -> storm(true), TAS.MAXOR_TICKS);
	}

	public void storm(boolean doContinue) {
		for(int i = 1; i <= 79; i += 5) {
			at(i - 1).aimTerminatorAtNearestEnemy();
			at(i).loopRightClick();
		}
		at(79).turnHead(180f, -4f);
		at(80).move("DN", 60);
		for(int i = 80; i <= 140; i += 5) {
			at(i).rightClick();
		} // clear mid
		at(141).setHotbarSlot(2);
		at(142).leap(fake("cookiethebald")); // leap to help clear green pad
		at(143).turnHead(180f, 0f).setHotbarSlot(4);
		at(144).move("WP", 0);
		at(163).move("WN", 3);
		for(int i = 165; i <= 520; i += 5) {
			at(i - 1).aimTerminatorAtNearestEnemy();
			at(i).loopRightClick();
		}
		at(525).turnHead(-90f, 30f);
		at(526).move("D", 0);
		at(536).move("DN", 2);
		at(545).setHotbarSlot(5).swapItems(12, 39); // put on racing helmet -> speed auto-set to 650
		at(546).move("WP", 95); // go to goldor
		at(576).leftClick();
		at(577).leftClick();
		at(578).leftClick();
		at(579).leftClick();
		at(580).leftClick();
		at(581).leftClick();
		at(582).leftClick();
		at(583).leftClick();
		at(584).leftClick();
		at(585).leftClick();
		at(586).turnHead(-90f, 0f).setHotbarSlot(1);
		// tick 587: begin falling
		at(602).turnHead(-109f, 0f);
		at(603).rightClick(); // bonzo off opposite wall | lands in 34 ticks
		at(604).turnHead(71f, 0f).setHotbarSlot(5);
		at(638).turnHead(45f, 0f);
		at(640).turnHead(71f, 0f);
		at(641).leftClick();
		at(642).leftClick();
		at(643).leftClick();
		at(644).leftClick();
		at(645).turnHead(75f, 30f);
		at(646).leftClick();
		at(647).leftClick();
		at(648).leftClick();
		at(649).leftClick();
		at(650).turnHead(0f, 0f);
		at(684).turnHead(45f, 0f);
		at(685).move("WP", 0);
		at(687).turnHead(90f, 0f);
		at(689).leftClick();
		at(700).turnHead(125f, 80f).setHotbarSlot(1);
		at(702).rightClick(); // bonzo towards i4
		at(717).turnHead(86f, 0f);
		at(718).move("WPJ", 7);
		at(726).turnHead(-74.5f, -46f).swapItems(5, 32).setHotbarSlot(5);
		if(doContinue) {
			at(802).dropItem(false);
			at(820).rightClick();
			at(827).stopRightClick();
			at(828).rightClick();
			at(835).stopRightClick();
			at(836).rightClick();
			at(843).stopRightClick();
			at(844).turnHead(-10.5f, -15.5f).setHotbarSlot(4);
			at(845).swapItems(18, 39).swapItems(19, 38).swapItems(20, 37).swapItems(21, 36);
			at(850).dropItem(true);
			at(851).turnHead(-3f, -4f);
			at(855).turnHead(-15f, -2.5f);
			at(856).rightClick();
			at(857).turnHead(-3f, 4f);
			// rapid fire #2 at 858
			at(859).turnHead(-14.5f, 5f);
			TASUtils.scheduleTask(() -> goldor(true), TAS.STORM_TICKS);
		}
	}

	public void goldor(boolean doContinue) {
		/*
		 * ██╗██╗  ██╗
		 * ╚═╝██║  ██║
		 * ██╗███████║
		 * ██║╚════██║
		 * ██║     ██║
		 * ╚═╝     ╚═╝
		 */
		rightClick();
		at(1).swapItems(5, 32).swapItems(18, 39).swapItems(19, 38).swapItems(20, 37).swapItems(21, 36);

		/*
		 * ███████╗███████╗██████╗
		 * ██╔════╝██╔════╝╚════██╗
		 * █████╗  █████╗   █████╔╝
		 * ██╔══╝  ██╔══╝   ╚═══██╗
		 * ███████╗███████╗██████╔╝
		 * ╚══════╝╚══════╝╚═════╝
		 */
		at(2).turnHead(81f, 0f);
		// tick 5: device completes
		at(6).move("WP", 0);
		at(8).move("WPJ", 0);
		at(10).move("WP", 0);
		at(23).move("WPJ", 0);
		at(25).move("WP", 0);
		at(39).turnHead(81f, 80f).setHotbarSlot(1);
		at(40).rightClick();
		at(41).turnHead(81f, 0f).setHotbarSlot(5);
		at(60).swapItems(9, 39);
		// tick 60: death tick
		at(62).swapItems(9, 39);
		at(67).turnHead(0f, 30f);
		at(68).move("WP", 0);
		at(70).leftClick();
		at(71).leftClick().move("WAP", 0);
		at(72).leftClick().move("WP", 17);
		at(73).leftClick();
		at(74).leftClick();
		at(75).leftClick();
		at(77).turnHead(55f, 0f);
		at(91).turnHead(120f, 0f);
		// tick 110: tank leaps
		at(111).setHotbarSlot(1).turnHead(22.5f, 0f);
		at(112).move("WP", 15);
		at(114).turnHead(22.5f, 80f);
		at(115).rightClick();
		at(116).turnHead(22.5f, 0f);
		at(119).swapItems(10, 39);
		// tick 120: death tick
		at(121).swapItems(10, 39);

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 *  ╚═══██╗
		 * ██████╔╝
		 * ╚═════╝
		 */
		at(122).turnHead(0f, 0f);
		at(128).turnHead(-25f, 0f);
		at(129).move("WP", 26);
		at(132).turnHead(0f, -2f).setHotbarSlot(5);
		at(133).leftClick();
		at(134).leftClick();
		at(135).leftClick();
		at(137).turnHead(0f, 80f).setHotbarSlot(1);
		at(138).rightClick();
		at(139).turnHead(6f, 0f).setHotbarSlot(2);
		at(155).turnHead(55f, 2f);
		at(156).rightClick();
		// tick 157: terminal completes

		at(158).leap(fake("Beethoven"));

		/*
		 * ██╗  ██╗
		 * ██║  ██║
		 * ███████║
		 * ╚════██║
		 *      ██║
		 *      ╚═╝
		 */
		at(159).turnHead(130f, 0f);
		at(160).move("WP", 15).setHotbarSlot(5);
		at(174).turnHead(138f, 32f);
		at(175).rightClick(); // s4 1
		// tick 176: terminal completes

		at(177).turnHead(-90f, -4f);
		at(178).move("WP", 5);
		at(189).setHotbarSlot(2);
		at(190).rightClick(); // s4 2
		// tick 191: terminal completes

		at(192).leap(fake("Beethoven"));

		/*
		 * ███████╗██╗ ██████╗ ██╗  ██╗████████╗
		 * ██╔════╝██║██╔════╝ ██║  ██║╚══██╔══╝
		 * █████╗  ██║██║  ███╗███████║   ██║
		 * ██╔══╝  ██║██║   ██║██╔══██║   ██║
		 * ██║     ██║╚██████╔╝██║  ██║   ██║
		 * ╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═╝   ╚═╝
		 */
		at(193).move("WP", 35).setHotbarSlot(5);
		at(227).turnHead(0f, 42f);
		at(228).leftClick();
		at(229).turnHead(0f, 0f);
		at(240).turnHead(180f, -6f);
		// lands at tick 271
		at(272).turnHead(42f, 66f);
		at(273).leftClick();
		at(274).leftClick();
		at(275).turnHead(123f, 55f);
		at(276).leftClick();
		at(277).leftClick();
		at(278).turnHead(-160f, 52f);
		at(279).leftClick();
		at(281).leftClick();
		at(282).turnHead(-73f, 58f);
		at(283).leftClick();
		at(284).leftClick();
		at(285).turnHead(-180f, 90f);
		at(298).leftClick();
		at(299).setHotbarSlot(2);
		at(300).leap(fake("Beethoven"));
		at(301).swapItems(5, 32).swapItems(12, 39);
		if(doContinue) TASUtils.scheduleTask(() -> necron(true), TAS.GOLDOR_TICKS);
	}

	public void necron(boolean doContinue) {
		setHotbarSlot(5);
		at(1).rightClick();
		at(152).stopRightClick();
		at(153).swapItems(5, 32);
		at(154).turnHead(180f, 62f).move("WP", 0).setHotbarSlot(5);
		at(155).move("WPJ", 0);
		at(157).move("WP", 59);
		at(215).leftClick();
		at(216).leftClick();
		at(217).leftClick();
		at(218).turnHead(180f, 0f);
		at(219).swapItems(13, 39);
		at(503).turnHead(121.3f, 0f);
		at(504).move("WP", 0);
		at(522).move("WPJ", 0);
		at(524).move("WP", 9);
		at(533).turnHead(90f, 0f);
		if(doContinue) TASUtils.scheduleTask(this::witherKing, TAS.NECRON_TICKS);
	}

	public void witherKing() {
		rightClick();
		at(1).turnHead(-120f, -7f);
		at(2).move("WP", 22);
		at(24).rightClick();
		at(25).swapItems(5, 32).swapItems(13, 39);
		at(26).setHotbarSlot(2);
		at(100).leap(fake("cookiethebald"));
		at(101).setHotbarSlot(6).turnHead(180f, -90f);
		at(200).rightClick();
		at(261).setHotbarSlot(5);
		int lbFor = 8;
		for(int i = 290; i <= 386 - lbFor - 1; i += lbFor + 1) {
			at(i).rightClick();
			at(i + lbFor).stopRightClick();
		}
		at(394).setHotbarSlot(2);
		at(490).leap(fake("Beethoven"));
		at(566).rightClick();
		at(629).setHotbarSlot(5);
		lbFor = 10;
		for(int i = 630; i <= 726 - lbFor - 1; i += lbFor + 1) {
			at(i).rightClick();
			at(i + lbFor).stopRightClick();
		}
		at(729).turnHead(92.5f, -90f);
		at(730).move("WP", 0);
		at(780).move("WPJ", 2);
		for(int i = 792; i <= 826 - lbFor - 1; i += lbFor + 1) {
			at(i).rightClick();
			at(i + lbFor).stopRightClick();
		}
		at(829).turnHead(-3f, -90f);
		at(830).move("WP", 0);
		at(851).move("WPJ", 0);
		at(853).move("WP", 14);
		for(int i = 858; i <= 926 - lbFor - 1; i += lbFor + 1) {
			at(i).rightClick();
			at(i + lbFor).stopRightClick();
		}
	}

	private void quiz() {
		at(137).turnHead(0f, 5f).setHotbarSlot(5);
		at(296).rightClick();
		at(437).rightClick();
		at(578).rightClick();
	}
}
