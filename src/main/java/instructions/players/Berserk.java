package instructions.players;

import commands.TAS;
import instructions.Actions;
import org.bukkit.*;
import org.bukkit.entity.Player;
import plugin.TASUtils;

public class Berserk extends Actions {
	public Berserk(Player player) {
		super(player);
	}

	// Berserk --> RedFish2471
	public void startPosition(String section) {
		switch(section) {
			case "all", "clear" -> startAt(-120.5, 71, -183.5, 0f, 0f);
			case "maxor", "boss" -> startAt(73.5, 221, 14.5, 0f, 0f);
			case "storm" -> startAt(114.145, 170, 93.962, 154f, 0f);
			case "goldor" -> startAt(91.351, 115, 133.781, -145f, -5f);
			case "necron" -> startAt(54.524, 64, 100.707, 180f, -6f);
			case "witherking" -> startAt(90.7, 6, 56.581, -79.7f, 19.1f);
		}
	}

	public void berserkInstructions(String section) {
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
				setHotbarSlot(1);
				at(1).swapItems(7, 35);
				at(49).dropItem(true);
				TASUtils.scheduleTask(() -> goldor(false), start);
			}
			case "necron" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				at(1).swapItems(7, 35);
				TASUtils.scheduleTask(() -> necron(false), start);
			}
			case "witherking" -> {
				swapItems(1, 28);
				swapItems(6, 33);
				swapItems(7, 34);
				swapItems(13, 39);
				setHotbarSlot(5);
				at(1).swapItems(7, 35);
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
		 * ██╗    ██╗██╗███████╗ █████╗ ██████╗ ██████╗
		 * ██║    ██║██║╚══███╔╝██╔══██╗██╔══██╗██╔══██╗
		 * ██║ █╗ ██║██║  ███╔╝ ███████║██████╔╝██║  ██║
		 * ██║███╗██║██║ ███╔╝  ██╔══██║██╔══██╗██║  ██║
		 * ╚███╔███╔╝██║███████╗██║  ██║██║  ██║██████╔╝
		 * ╚══╝╚══╝ ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝
		 */
		at(15).rightClick(-2f, -1.5f);
		at(16).rightClick(-90f, 13f);
		at(17).setHotbarSlot(0).rightClick();
//		// tick 2: tac tp back, sneak
//		at(2).move("N", 2);
//		at(3).rightClick();
//		at(4).rightClick();
//		at(5).turnHead(110f, -72f).setHotbarSlot(7);
//		at(6).rightClick(); // throw pearl towards wizard crystal | lands in 8 ticks
//		at(7).turnHead(159.5f, -15.5f);
//		at(8).rightClick(); // throw pearl towards lower part | lands in 19 ticks
//		at(13).move("N", 1).setHotbarSlot(1);
//		at(14).rightClick(); // pearl lands, etherwarp into compartment with crystal
//		at(15).turnHead(28f, 7f);
//		at(16).rightClick(); // teleport to crystal
//		at(17).turnHead(45f, 45f);
//		at(18).leftClick(); // pick up crystal
//		at(19).turnHead(180f, -25f).move("N", 0);
//		at(20).rightClick(); // etherwarp up
//		at(21).turnHead(-161f, -72f);
//		at(22).rightClick(); // etherwarp to secret compartment
//		at(23).turnHead(0f, -32f);
//		at(24).rightClick(); // etherwarp to secret
//		at(25).turnHead(0f, 0f);
//		at(26).leftClick(); // obtain secret 1/4
//		at(27).turnHead(-139f, 0f); // pearl lands
//		at(28).rightClick(); // etherwarp towards bat
//		at(29).turnHead(-149.7f, 17.5f).setHotbarSlot(1);
//		at(30).rightClick(); // etherwarp into bat area
//		at(31).turnHead(145f, 6f).setHotbarSlot(0);
//		at(32).rightClick(); // kill bat
//		at(33).turnHead(-23f, -20f).setHotbarSlot(1);
//		at(34).rightClick(); // etherwarp out
//		at(35).turnHead(50f, -25f);
//		at(36).rightClick(); // etherwarp up
//		at(37).setHotbarSlot(7).turnHead(-180f, -15.5f).move("S", 1);
//		at(38).rightClick(); // pearl to correct spot for well | lands in 28 ticks
//		at(39).setHotbarSlot(1).turnHead(180f, 0.97f).move("N", 0);
//		at(40).rightClick(); // etherwarp to position
//		at(41).turnHead(91f, -68f).setHotbarSlot(7);
//		at(42).rightClick(); // pearl to dig spot | lands in 4 ticks
//		at(43).turnHead(90f, 90f);
//		// tick 46: pearl lands
//		at(47).rightClick(); // pearl to reposition | lands in 1 tick
//		at(48).turnHead(90f, 0f).setHotbarSlot(5); // pearl lands
//		at(49).leftClick().move("WP", 0);
//		at(50).leftClick(); // stonk through wall
//		at(51).turnHead(-90f, -90f).setHotbarSlot(1).move("N", 0);
//		at(52).rightClick(); // etherwarp up
//		at(53).turnHead(-76f, -21f);
//		at(54).rightClick(); // etherwarp to wizard
//		at(55).turnHead(180f, 0f);
//		at(56).move("WP", 1); // walk forward 1 tick to be in range
//		at(57).leftClick();
//		at(58).turnHead(102f, 1f).move("N", 24);
//		at(59).rightClick(); // etherwarp to wall
//		at(60).turnHead(41f, 1.21f).setHotbarSlot(4);
//		at(61).rightClick(); // blow up wall
//		at(62).setHotbarSlot(1);
//		at(63).rightClick(); // etherwarp to secret
//		at(64).turnHead(-10f, 13f);
//		at(65).leftClick();
//		at(66).turnHead(-90f, 1f); // pearl lands
//		at(67).rightClick(); // etherwarp into well
//		// Wizard: 67 ticks
//
//		/*
//		 * ██╗    ██╗███████╗██╗     ██╗
//		 * ██║    ██║██╔════╝██║     ██║
//		 * ██║ █╗ ██║█████╗  ██║     ██║
//		 * ██║███╗██║██╔══╝  ██║     ██║
//		 * ╚███╔███╔╝███████╗███████╗███████╗
//		 *  ╚══╝╚══╝ ╚══════╝╚══════╝╚══════╝
//		 */
//		at(68).turnHead(90f, -60.1f);
//		at(69).rightClick(); // etherwarp up to secret
//		at(70).turnHead(90f, -36f);
//		at(71).leftClick();
//		at(72).turnHead(-67f, -18f);
//		at(73).rightClick(); // reposition for next secret
//		at(74).turnHead(85f, 17.5f);
//		at(75).rightClick(); // etherwarp to secret
//		at(76).turnHead(-96.3f, 3.1f);
//		at(77).rightClick(); // etherwarp out
//		at(78).turnHead(-158f, 24f);
//		at(79).rightClick(); // etherwarp down
//		at(80).turnHead(-98f, 1.5f);
//		at(81).rightClick(); // etherwarp to secret
//		at(82).turnHead(-90f, 20f).setHotbarSlot(5);
//		at(83).leftClick();
//		at(84).setHotbarSlot(7).leftClick();
//		at(84).turnHead(30f, 0f).setHotbarSlot(7);
//		at(85).rightClick(); // pearl to upper secret | lands in 13 ticks
//		at(86).setHotbarSlot(1).move("N", 2).turnHead(180f, 0f);
//		at(87).rightClick(); // reposition for wither essence + pearl
//		at(88).turnHead(16f, 66f).setHotbarSlot(7);
//		at(89).rightClick(); // pearl to mini | lands in 16 ticks
//		at(90).turnHead(-93f, 12.5f).move("N", 4).setHotbarSlot(1);
//		at(91).rightClick(); // etherwarp into wither essence chamber
//		at(92).turnHead(180f, -75f);
//		at(93).rightClick(); // etherwarp up to wither essence
//		at(94).turnHead(-117f, 31f).setHotbarSlot(1);
//		at(95).leftClick();
//		at(96).turnHead(56f, 22f);
//		at(97).leftClick();
//		at(98).turnHead(-11f, 8f).move("N", 2); // pearl lands
//		at(99).rightClick(); // etherwarp to wall
//		at(100).turnHead(-90f, -50f).setHotbarSlot(5);
//		at(101).leftClick();
//		at(102).leftClick();
//		at(103).leftClick(); // stonk to secret
//		at(104).setHotbarSlot(3).leftClick();
//		at(105).turnHead(-45f, 0f); // pearl lands
//		at(106).leftClick();
//		at(107).turnHead(-4, 1f).move("N", 2).setHotbarSlot(1);
//		at(108).rightClick(); // etherwarp to item
//		at(109).turnHead(59f, 7f).setHotbarSlot(7);
//		at(110).rightClick(); // throw pearl to ice fill | lands in 9 ticks
//		at(111).turnHead(0f, 68f).move("N", 0).setHotbarSlot(1);
//		at(112).rightClick(); // etherwarp down
//		at(113).turnHead(180f, 90f).setHotbarSlot(5);
//		at(114).leftClick(); // stonk block
//		at(115).setHotbarSlot(1);
//		at(116).rightClick(); // etherwarp down
//		at(117).move("WP", 0);
//		at(118).move("N", 3);
//		at(119).turnHead(105.5f, 5f); // pearl lands
//		at(120).rightClick(); // etherwarp into ice fill
//		// Well: 53 ticks
//
//		/*
//		 * ██╗ ██████╗███████╗    ███████╗██╗██╗     ██╗
//		 * ██║██╔════╝██╔════╝    ██╔════╝██║██║     ██║
//		 * ██║██║     █████╗      █████╗  ██║██║     ██║
//		 * ██║██║     ██╔══╝      ██╔══╝  ██║██║     ██║
//		 * ██║╚██████╗███████╗    ██║     ██║███████╗███████╗
//		 * ╚═╝ ╚═════╝╚══════╝    ╚═╝     ╚═╝╚══════╝╚══════╝
//		 */
//		iceFill();
//		// Ice Fill: 127 ticks
//
//		at(251).swapItems(1, 28).swapItems(3, 30).swapItems(4, 31).swapItems(6, 33).swapItems(7, 34).setHotbarSlot(5);
//		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
	}

	public void maxor(boolean doContinue) {
		at(3).turnHead(0f, 30f).move("WP", 17);
		at(18).leftClick();
		at(19).leftClick();
		at(21).turnHead(34f, 0f);
		at(22).move("WP", 70); // move to leap spot for healer
		at(287).turnHead(-145f, 0f);
		at(288).move("WP", 0); // move to leap spot for mage1
		at(297).turnHead(-90f, 0f);
		at(337).turnHead(-32f, 0f);
		at(354).turnHead(-32f, 80f).move("WPJ", 0).setHotbarSlot(1);
		at(355).rightClick(); // bonzo to purple pad
		at(356).turnHead(-32f, 0f).move("WP", 21);
		at(377).turnHead(154f, 0f).setHotbarSlot(3);
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
		for(int i = 95; i <= 150; i += 3) {
			at(i - 1).snapHeadToNearestEnemy();
			at(i).loopLeftClick();
		} // clear pad, including shadow assassin
		at(151).turnHead(154f, 0f);
		at(152).move("WP", 31).setHotbarSlot(1); // move off the pad early enough
		at(159).turnHead(154f, 80f);
		at(160).rightClick(); // bonzo back to pillar
		at(161).turnHead(154f, 0f).setHotbarSlot(3);
		for(int i = 187; i <= 532; i += 5) {
			at(i - 1).snapHeadToNearestEnemy();
			at(i).loopLeftClick();
		} // kill outstanding wither skeletons
		at(533).turnHead(-155f, -90f).setHotbarSlot(5).swapItems(5, 32);
		at(534).rightClick();
		at(546).move("WP", 2);
		at(595).setHotbarSlot(6).swapItems(5, 32); // last breath
		int lbFor = 8;
		for(int i = 597; i <= 678 - lbFor - 1; i += lbFor + 1) {
			at(i).rightClick();
			at(i + lbFor).stopRightClick();
		}
		at(679).setHotbarSlot(3).snapHeadAtNearestNamed("Storm");
		at(680).leftClick();
		at(681).setHotbarSlot(4).swapItems(12, 39).swapItems(7, 35); // equip racing helmet (speed auto-set to 650), black cat, and prepare explosive bow
		at(682).leap(fake("akc0303"));
		at(683).move("AWP", 0).setHotbarSlot(1);
		at(685).move("WP", 0);
		at(707).turnHead(12f, 80f);
		at(708).rightClick(); // bonzo staff here because there is less time to build up momentum | lands in 12 ticks
		at(709).turnHead(0f, 0f);
		at(729).move("WPJ", 1).turnHead(-10f, 80f);
		at(730).rightClick();
		// pause to allow time to leap
		at(745).turnHead(0f, 0f);
		at(746).move("SN", 0);
		at(754).move("N", 10);
		at(755).turnHead(-47f, 80f);
		at(784).turnHead(0f, 80f);
		at(785).move("WP", 0);
		at(786).move("WPJ", 0);
		at(787).move("WP", 0);
		at(788).rightClick();
		at(802).move("WPJ", 0);
		at(803).rightClick();
		at(805).turnHead(3f, 0f).setHotbarSlot(5).move("WP", 39);
		at(821).turnHead(0f, 45f);
		at(822).leftClick();
		at(823).leftClick();
		at(824).leftClick();
		at(825).leftClick();
		at(826).leftClick();
		at(827).leftClick();
		at(836).turnHead(111f, 0f);
		at(845).turnHead(-145f, -5).setHotbarSlot(1);
		if(doContinue) {
			at(849).dropItem(true);
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
		turnHead(110f, 80f);
		setHotbarSlot(1);
		at(1).move("WP", 25);
		at(2).rightClick();
		at(3).turnHead(110f, 0f).setHotbarSlot(5);
		at(26).turnHead(170f, 5f);
		// tick 37: tank leaps
		at(38).turnHead(65f, 0f);
		at(39).move("WP", 0);
		at(41).move("WPJ", 10);

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 * ██╔═══╝
		 * ███████╗
		 * ╚══════╝
		 */
		at(51).turnHead(58f, 80f);
		// tick 52: tank leaps
		at(53).turnHead(90f, -30f);
		at(54).move("WP", 8).setHotbarSlot(7);
		at(58).rightClick();
		// holy shit nosethe clutch with the explosive bow idea
		at(71).stopRightClick();
		at(72).turnHead(-150f, 10f).setHotbarSlot(5);
		at(73).move("WPD", 0);
		at(74).move("WP", 26);
		at(96).turnHead(180f, 10f);
		at(97).rightClick(); // s2 5
		// tick 98: terminal completes

		at(99).turnHead(-95f, 10f);
		at(100).move("WAP", 0);
		at(102).move("WP", 6);
		at(117).move("WP", 5);
		at(121).rightClick(); // s2 3
		// tick 122: terminal completes

		at(123).setHotbarSlot(4);
		at(128).leap(fake("akc0303")); // better positioning

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 *  ╚═══██╗
		 * ██████╔╝
		 * ╚═════╝
		 */
		at(129).turnHead(-47.5f, 90f).setHotbarSlot(1);
		at(130).move("WP", 0);
		at(131).move("WPJ", 0);
		at(133).rightClick();
		at(134).turnHead(-47.5f, 28f).setHotbarSlot(4);
		at(135).move("WP", 16);
		at(149).turnHead(-30f, -45f);
		at(169).rightClick();
		// tick 170: terminal activates

		at(171).leap(fake("Beethoven"));

		/*
		 * ██╗  ██╗
		 * ██║  ██║
		 * ███████║
		 * ╚════██║
		 *      ██║
		 *      ╚═╝
		 */
		at(172).move("WP", 15);
		at(187).rightClick(); // s4 3
		// tick 188: terminal completes

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
		at(504).turnHead(-122f, 0f).setHotbarSlot(5);
		at(505).move("WP", 0);
		at(523).move("WPJ", 0);
		at(525).move("WP", 12);
		at(537).turnHead(-90f, 0f);
		at(538).swapItems(3, 30);
		if(doContinue) TASUtils.scheduleTask(this::witherKing, TAS.NECRON_TICKS);
	}

	public void witherKing() {
		rightClick();
		at(1).turnHead(115f, 0f);
		at(2).move("WP", 0);
		at(25).move("A", 1);
		at(26).turnHead(90f, 0f);
		at(27).rightClick();
		at(28).swapItems(5, 32).swapItems(13, 39);
		at(29).setHotbarSlot(4);
		at(100).leap(fake("cookiethebald"));
		at(101).setHotbarSlot(5).turnHead(180f, -90f);
		at(200).rightClick();
		at(261).setHotbarSlot(6);
		int lbFor = 8;
		for(int i = 290; i <= 386 - lbFor - 1; i += lbFor + 1) {
			at(i).rightClick();
			at(i + lbFor).stopRightClick();
		}
		at(387).setHotbarSlot(3);
		at(388).leftClick();
		at(393).leftClick();
		at(394).setHotbarSlot(4);
		at(490).leap(fake("Beethoven"));
		at(491).setHotbarSlot(5);
		at(529).rightClick();
		at(590).setHotbarSlot(6);
		lbFor = 10;
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

	@SuppressWarnings("unused")

	private void iceFill() {
		at(121).turnHead(90f, 60f);
		at(122).rightClick();
		at(123).rightClick();
		at(124).rightClick();
		at(125).turnHead(0f, 60f);
		at(126).rightClick();
		at(127).turnHead(90f, 60f);
		at(128).rightClick();
		at(129).turnHead(180f, 60f);
		at(130).rightClick();
		at(131).turnHead(90f, 60f);
		at(132).rightClick();
		at(133).rightClick();
		at(134).rightClick();
		at(135).turnHead(0f, 60f);
		at(136).rightClick();
		at(137).rightClick();
		at(138).turnHead(90f, 60f);
		at(139).rightClick();
		at(140).rightClick();
		at(141).rightClick();
		at(142).rightClick();
		at(143).turnHead(180f, 60f);
		at(144).rightClick();
		at(145).turnHead(-90f, 60f);
		at(146).rightClick();
		at(147).rightClick();
		at(148).turnHead(180f, 60f);
		at(149).rightClick();
		at(150).rightClick();
		at(151).turnHead(-90f, 60f);
		at(152).rightClick();
		at(153).rightClick();
		at(154).turnHead(180f, 60f);
		at(155).rightClick();
		at(156).turnHead(90f, 60f);
		at(157).rightClick();
		at(158).rightClick();
		at(159).rightClick();
		at(160).rightClick();
		at(161).turnHead(0f, 60f);
		at(162).rightClick();
		at(163).rightClick();
		at(164).turnHead(90f, 60f);
		at(165).rightClick();
		at(166).rightClick();
		at(167).rightClick();
		at(168).turnHead(0f, 60f);
		at(169).rightClick();
		at(170).turnHead(90f, 60f);
		at(171).rightClick();
		at(172).rightClick();
		at(173).turnHead(0f, 60f);
		at(174).rightClick();
		at(175).turnHead(-90f, 60f);
		at(176).rightClick();
		at(177).rightClick();
		at(178).turnHead(0f, 60f);
		at(179).rightClick();
		at(180).turnHead(90f, 60f);
		at(181).rightClick();
		at(182).rightClick();
		at(183).rightClick();
		at(184).rightClick();
		at(185).rightClick();
		at(186).rightClick();
		at(187).turnHead(180f, 60f);
		at(188).rightClick();
		at(189).turnHead(-90f, 60f);
		at(190).rightClick();
		at(191).rightClick();
		at(192).turnHead(180f, 60f);
		at(193).rightClick();
		at(194).turnHead(90f, 60f);
		at(195).rightClick();
		at(196).turnHead(180f, 60f);
		at(197).rightClick();
		at(198).rightClick();
		at(199).turnHead(-90f, 60f);
		at(200).rightClick();
		at(201).turnHead(180f, 60f);
		at(202).rightClick();
		at(203).turnHead(-90f, 60f);
		at(204).rightClick();
		at(205).rightClick();
		at(206).turnHead(0f, 60f);
		at(207).rightClick();
		at(208).turnHead(-90f, 60f);
		at(209).rightClick();
		at(210).rightClick();
		at(211).turnHead(180f, 60f);
		at(212).rightClick();
		at(213).rightClick();
		at(214).turnHead(90f, 60f);
		at(215).rightClick();
		at(216).rightClick();
		at(217).rightClick();
		at(218).rightClick();
		at(219).rightClick();
		at(220).turnHead(0f, 60f);
		at(221).rightClick();
		at(222).turnHead(90f, 60f);
		at(223).rightClick();
		at(224).turnHead(0f, 60f);
		at(225).rightClick();
		at(226).rightClick();
		at(227).turnHead(90f, 60f);
		at(228).rightClick();
		at(229).move("WP", 1);
		at(230).turnHead(68f, -33f);
		at(248).leftClick();
		at(249).turnHead(112f, -33f);
		at(250).leftClick();
	}
}
