package instructions.players;

import commands.TAS;
import instructions.Actions;
import org.bukkit.*;
import org.bukkit.entity.Player;
import plugin.TASUtils;

public class Archer extends Actions {
	public Archer(Player player) {
		super(player);
	}

	public void archerInstructions(String section) {
		int start = TAS.startDelay(section);
		switch(section) {
			case "all", "clear" -> {
				startAt(-120.5, 71, -183.5, 0f, 0f);
				preClear();
				TASUtils.scheduleTask(() -> clear(section.equals("all")), start);
			}
			case "maxor", "boss" -> {
				startAt(73.5, 221, 14.5, 0f, 0f);
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
				startAt(65.087, 165, 69.329, 180f, -4f);
				swapItems(1, 28);
				swapItems(6, 33);
				swapItems(7, 34);
				setHotbarSlot(4);
				TASUtils.scheduleTask(() -> storm(false), start);
			}
			case "goldor" -> {
				startAt(63.811, 127, 35.940, -74.5f, -46f);
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
				startAt(54.524, 64, 100.707, 180f, -6f);
				swapItems(1, 28);
				swapItems(5, 32);
				swapItems(6, 33);
				swapItems(7, 34);
				TASUtils.scheduleTask(() -> necron(false), start);
			}
			case "witherking" -> {
				startAt(22.3, 6, 59.408, 65.6f, 29.3f);
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
		move("WPJ", 0);
		at(13).move("WP", 15);
		at(31).turnHead(-1f, -3.4f);
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
		at(20).move("N", 4);
		at(21).rightClick(); // etherwarp into fairy
		at(22).turnHead(4f, 24f);
		at(23).rightClick(); // etherwarp to wither door
		// Left-click the wither door to open it.  This requires the Wither Key from killing archaeologist I.
		at(25).leftClick(); // opens tick 45
		at(46).turnHead(10f, 6.5f).move("N", 0).setHotbarSlot(1); // mage leaps
		at(47).rightClick(); // etherwarp into room
		at(48).turnHead(-57.15f, 1.75f);
		at(49).rightClick(); // etherwarp into Gravel
		// Blood Rush: 49 ticks

		/*
		 *  ██████╗ ██████╗  █████╗ ██╗   ██╗███████╗██╗
		 * ██╔════╝ ██╔══██╗██╔══██╗██║   ██║██╔════╝██║
		 * ██║  ███╗██████╔╝███████║██║   ██║█████╗  ██║
		 * ██║   ██║██╔══██╗██╔══██║╚██╗ ██╔╝██╔══╝  ██║
		 * ╚██████╔╝██║  ██║██║  ██║ ╚████╔╝ ███████╗███████╗
		 *  ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝  ╚═══╝  ╚══════╝╚══════╝
		 */
		at(50).turnHead(0f, 0f).move("P", 0);
		at(51).dropItem(true); // blow up crypt | arrows land in 4 ticks
		at(52).turnHead(45.1f, 4.2f).move("N", 21);
		at(53).rightClick(); // etherwarp into room
		at(54).turnHead(-44f, 8.5f);
		at(55).rightClick(); // reposition for secret
		at(56).turnHead(-106f, 3.3f);
		at(57).rightClick(); // etherwarp to secret
		at(58).turnHead(-90f, 7f).setHotbarSlot(5);
		at(59).leftClick();
		at(60).setHotbarSlot(1).leftClick();
		at(61).turnHead(28.7f, -56f);
		at(62).rightClick(); // etherwarp up
		at(63).turnHead(-99.7f, -53f).setHotbarSlot(0);
		at(64).rightClick(); // wither impact, kill bat
		at(65).turnHead(74f, 57.5f).setHotbarSlot(1);
		at(66).rightClick(); // etherwarp down
		at(67).turnHead(-86.5f, 2.7f);
		at(68).rightClick(); // etherwarp to miniboss
		at(69).turnHead(0f, 0f).setHotbarSlot(4);
		at(70).rightClick(); // shoot and kill miniboss | arrows kill in 1 tick
		at(71).turnHead(-85f, 2.25f).setHotbarSlot(1);
		at(72).rightClick(); // etherwarp towards quiz
		at(73).turnHead(0f, 0f);
		at(74).rightClick(); // aotv into quiz
		at(75).turnHead(180f, -12f).setHotbarSlot(7);
		at(76).rightClick(); // pearl to dino | lands in 25 ticks
		at(77).turnHead(180f, 2.2f).setHotbarSlot(1).move("N", 2);
		at(78).rightClick(); // etherwarp towards secrets
		at(79).turnHead(-90f, -38.5f);
		at(80).rightClick(); // etherwarp into area
		at(81).turnHead(0f, 58f);
		at(82).rightClick(); // etherwarp to item
		at(83).turnHead(-141f, 10f);
		at(84).rightClick(); // etherwarp to wither essence
		at(85).turnHead(180f, 55f);
		at(86).leftClick();
		at(87).turnHead(90f, -23.5f);
		at(88).rightClick(); // aotv out
		at(89).turnHead(52f, -25f).move("N", 2);
		at(90).rightClick(); // etherwarp to final secrets
		at(91).turnHead(42f, 50f);
		at(92).rightClick(); // reposition
		at(93).turnHead(90f, -90f);
		at(94).move("WP", 1);
		at(95).rightClick(); // aotv up
		at(96).turnHead(90f, 75f);
		at(97).rightClick(); // aotv down
		at(98).turnHead(90f, 36f);
		at(99).leftClick();
		at(100).turnHead(180f, -5.5f).move("N", 2); // pearl lands
		at(101).rightClick(); // etherwarp into dino
		// Gravel: 52 ticks

		/*
		 * ██████╗ ██╗███╗   ██╗ ██████╗     ██████╗ ██╗ ██████╗     ███████╗██╗████████╗███████╗
		 * ██╔══██╗██║████╗  ██║██╔═══██╗    ██╔══██╗██║██╔════╝     ██╔════╝██║╚══██╔══╝██╔════╝
		 * ██║  ██║██║██╔██╗ ██║██║   ██║    ██║  ██║██║██║  ███╗    ███████╗██║   ██║   █████╗
		 * ██║  ██║██║██║╚██╗██║██║   ██║    ██║  ██║██║██║   ██║    ╚════██║██║   ██║   ██╔══╝
		 * ██████╔╝██║██║ ╚████║╚██████╔╝    ██████╔╝██║╚██████╔╝    ███████║██║   ██║   ███████╗
		 * ╚═════╝ ╚═╝╚═╝  ╚═══╝ ╚═════╝     ╚═════╝ ╚═╝ ╚═════╝     ╚══════╝╚═╝   ╚═╝   ╚══════╝
		 */
		at(102).turnHead(101f, 32f).setHotbarSlot(4);
		at(103).rightClick(); // shoot at miniboss | arrows land in 5 ticks
		at(104).turnHead(46f, -50f).setHotbarSlot(1).move("N", 2);
		at(105).rightClick(); // etherwarp to secret
		at(106).turnHead(-163f, 49f).setHotbarSlot(7);
		at(107).rightClick(); // throw pearl | lands in 19 ticks
		at(108).turnHead(13f, 9f).setHotbarSlot(3);
		at(109).rightClick(); // blow up door
		at(110).setHotbarSlot(1);
		at(111).rightClick(); // aotv to chest
		at(112).turnHead(0f, 13f);
		at(113).leftClick();
		at(114).turnHead(-180f, 24.8f);
		at(115).rightClick(); // aotv out of secret
		at(116).turnHead(96f, 59f).move("N", 2);
		at(117).rightClick(); // etherwarp down
		at(118).turnHead(18f, 2f);
		at(119).rightClick(); // aotv to item
		at(120).turnHead(135.1f, 30.7f).move("N", 2);
		at(121).rightClick();
		at(122).turnHead(-155f, -16f).setHotbarSlot(5);
		at(123).leftClick();
		at(124).setHotbarSlot(1).leftClick();
		at(125).turnHead(-157f, 8f).setHotbarSlot(7);
		// tick 126: pearl lands
		at(127).rightClick(); // throw pearl | lands in 6 ticks
		at(128).turnHead(-55f, 62f).setHotbarSlot(1).move("N", 2);
		at(129).rightClick(); // etherwarp towards bat
		at(130).turnHead(-8.2f, 7.3f).setHotbarSlot(0);
		at(131).rightClick(); // kill bat
		at(132).turnHead(0f, 1.8f).setHotbarSlot(1).move("N", 5);
		// tick 133: pearl lands
		at(134).rightClick(); // etherwarp back to gravel
		at(135).turnHead(0f, 1.45f);
		at(136).rightClick(); // etherwarp to quiz
		// Dino Dig Site: 35 ticks

		/*
		 *  ██████╗ ██╗   ██╗██╗███████╗
		 * ██╔═══██╗██║   ██║██║╚══███╔╝
		 * ██║   ██║██║   ██║██║  ███╔╝
		 * ██║▄▄ ██║██║   ██║██║ ███╔╝
		 * ╚██████╔╝╚██████╔╝██║███████╗
		 *  ╚══▀▀═╝  ╚═════╝ ╚═╝╚══════╝
		 */
		quiz();
		// Quiz: 482 ticks | 543 ticks from open | puzzle solved 503 ticks from open

		at(619).swapItems(1, 28).swapItems(6, 33).swapItems(7, 34).swapItems(11, 36).setHotbarSlot(5);
		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
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

		at(158).leap(fake("Beethoven_"));

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

		at(192).leap(fake("Beethoven_"));

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
		at(300).leap(fake("Beethoven_"));
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
		at(490).leap(fake("Beethoven_"));
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
