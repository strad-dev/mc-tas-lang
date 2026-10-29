package instructions.players;

import commands.TAS;
import instructions.Actions;
import org.bukkit.entity.Player;
import plugin.TASUtils;

public class Archer extends Actions {
	private static final int LEAP_BERS = 44;
	private static final int LEAP_HEAL = 8;
	private static final int LEAP_MAGE = 36;
	private static final int LEAP_TANK = 0;

	public Archer(Player player) {
		super(player);
	}

	public void startPosition(String section) {
		switch(section) {
			case "all", "clear" -> startAt(-120.5, 71, -183.5, 0f, 0f);
			case "maxor", "boss" -> startAt(73.5, 221, 14.5, 0f, 0f);
			case "storm" -> startAt(46.020, 169, 53.502, 90f, 0f);
			case "goldor" -> startAt(63.444, 127, 35.569, -74.5f, -46f);
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
				useSpeedLoadout();
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
				useDpsLoadout();
				setHotbarSlot(4);
				TASUtils.scheduleTask(() -> storm(false), start);
			}
			case "goldor" -> {
				swapItems(1, 28);
				swapItems(5, 32);
				swapItems(6, 33);
				swapItems(7, 34);
				setHotbarSlot(5);
				at(1).dropItem(false);
				at(2).useSpeedLoadout(); // not tick 1: the pets menu would still be open for the drop
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
				at(59).turnHead(-14.5f, 5f).setHotbarSlot(5);
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
		at(1).rightClick(0f, 23f);
		at(2).rightClick();
		at(3).swapItems(6, 33).setHotbarSlot(6).rightClick().move("N"); // charge rag to do more damage, idk if this will actually help
		at(64).setHotbarSlot(1);
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
		at(61).setHotbarSlot(7).rightClick(-36f, 6f); // pearl for exit, lands in 5 ticks
		at(62).setHotbarSlot(3).rightClick(54f, 63f); // blow up floor
		at(63).setHotbarSlot(1).rightClick(); // etherwarp down
		at(64).setHotbarSlot(0).rightClick(-90f, 2f); // market 1/5
		at(65).setHotbarSlot(5).rightClick(135f, 50f); // market 2/5
		at(66).setHotbarSlot(7).rightClick(-50f, 0f); // throw reposition pearl, lands in 6 ticks
		at(67).setHotbarSlot(1).rightClick(32f, -60.5f); // etherwarp up
		at(68).setHotbarSlot(5).turnHead(116f, 23f).leftClick().move("WN");
		at(69).leftClick();
		at(70).turnHead(110f, 6f).leftClick();
		at(71).leftClick().move("N"); // break through wall, reposition self
		at(72).rightClick(); // market 3/5
		at(73).setHotbarSlot(1).rightClick(20f, 17f); // etherwarp to next secret
		at(74).setHotbarSlot(5).turnHead(90f, 90f).leftClick(); // break floor
		at(75).setHotbarSlot(1).rightClick(); // etherwarp down
		at(76).rightClick(64f, -14f).move(""); // market 4/5, pre-unsneak
		at(77).rightClick(90f, -90f).move("N"); // aotv up, pre-sneak
		at(78).rightClick(18.5f, 17f); // etherwarp towards miniboss
		at(79).turnHead(0f, 9f).dropItem(true); // use ability to kill miniboss + get a crypt 4/5
		at(80).setHotbarSlot(4).rightClick(); // still need to fire an arrow to kill off miniboss
		at(81).setHotbarSlot(1).rightClick(0f, -55f); // etherwarp up
		at(82).setHotbarSlot(5).turnHead(18.5f, -47f).leftClick(); // stonk block
		at(83).rightClick(); // market 5/5
		at(84).setHotbarSlot(1).rightClick(-71.5f, 23f); // etherwarp into hallway
		// Market: 24 ticks (-20 | -57)

		/*
		 * ██╗  ██╗ █████╗ ██╗     ██╗     ██╗    ██╗ █████╗ ██╗   ██╗
		 * ██║  ██║██╔══██╗██║     ██║     ██║    ██║██╔══██╗╚██╗ ██╔╝
		 * ███████║███████║██║     ██║     ██║ █╗ ██║███████║ ╚████╔╝
		 * ██╔══██║██╔══██║██║     ██║     ██║███╗██║██╔══██║  ╚██╔╝
		 * ██║  ██║██║  ██║███████╗███████╗╚███╔███╔╝██║  ██║   ██║
		 * ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚══════╝ ╚══╝╚══╝ ╚═╝  ╚═╝   ╚═╝
		 */
		at(85).rightClick(-97f, 0f); // etherwarp for miniboss
		at(86).setHotbarSlot(4).rightClick(-90f, 11f); // kill miniboss
		at(87).setHotbarSlot(1).rightClick(-99f, 8f).move(""); // etherwarp towards chest
		at(88).setHotbarSlot(5).turnHead(180f, 12f).leftClick(); // break iron bar
		at(89).setHotbarSlot(1).rightClick(); // aotv in
		at(90).rightClick(134f, 45f); // hallway 1/3
		at(91).rightClick(0f, 0f).move("N"); // aotv out (faster than pearl, and better position)
		at(92).rightClick(-102f, 2f); // etherwarp to next secret
		at(93).setHotbarSlot(5).turnHead(-170f, 13f).leftClick(); // break wall
		at(94).setHotbarSlot(1).rightClick(); // etherwarp to chest
		at(95).rightClick(-90f, 90f); // hallway 2/3
		at(96).rightClick(0f, -45f); // etherwarp out
		at(97).rightClick(-106f, -4f); // etherwarp to next secret
		at(98).rightClick(-90f, -81f); // etherwarp up
		at(99).rightClick(-23f, 24f); // reposition
		at(100).rightClick(); // hallway 3/3
		// Hallway: 16 ticks (-16 | -73)

		at(101).swapItems(1, 28).swapItems(7, 34).swapItems(11, 36).useSpeedLoadout().setHotbarSlot(5); // swap out boss items
		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
	}

	public void maxor(boolean doContinue) {
		turnHead(-13f, 0f);
		move("WP");
		at(10).move("WN");
		at(23).move("WP");
		at(45).rightClick();
		at(46).setHotbarSlot(2).rightClick();
		at(47).clickGui(LEAP_TANK);
		at(80).move("WN");
		at(91).move("WP");
		at(114).move("").swapItems(11, 36);
		at(120).rightClick();
		at(160).clickGui(LEAP_TANK);
		at(179).rightClick();
		at(180).clickGui(LEAP_BERS);
		at(181).turnHead(44f, 0f).move("WP").setHotbarSlot(4);
		at(229).turnHead(90f, 0f);
		at(240).setHotbarSlot(4).useDpsLoadout();
		if(doContinue) TASUtils.scheduleTask(() -> storm(true), TAS.MAXOR_TICKS);
	}

	public void storm(boolean doContinue) {
		for(int i = 0; i <= 120; i += 5) {
			at(i).aimTerminatorAtNearestEnemy().rightClick();
		}
		at(121).turnHead(180f, 0f).move("WP");
		at(138).move("");

		for(int i = 140; i <= 505; i += 5) {
			at(i).aimTerminatorAtNearestEnemy().rightClick();
		}

		at(506).setHotbarSlot(5).useSpeedLoadout();
		at(508).turnHead(-90f, 35f).move("D");
		at(514).move("DN");
		at(515).move("");
		at(526).move("WP");
		at(555).leftClick();
		at(556).leftClick();
		at(557).leftClick();
		at(558).leftClick();
		at(559).leftClick();
		at(560).leftClick();
		at(561).leftClick();
		at(562).leftClick();
		at(563).leftClick();
		at(564).leftClick();
		at(566).turnHead(40f, 0f);
		at(588).turnHead(90f, 35f);
		at(606).leftClick();
		at(607).leftClick();
		at(608).leftClick();
		at(609).leftClick();
		at(610).leftClick();
		at(611).leftClick();
		at(632).turnHead(180f, -90f);
		at(639).move("");
		at(643).move("WP");
		at(670).turnHead(150f, 0f);
		at(674).swapItems(5, 32).swapItems(18, 39).swapItems(19, 38).swapItems(20, 37).swapItems(21, 36).turnHead(-74.5f, -46f); // equip thermodynamic
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
}
