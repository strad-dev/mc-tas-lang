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
			case "goldor" -> startAt(63.487, 127, 36.068, -75f, -50f);
			case "necron" -> startAt(54.6, 105, 27.3, 180f, -6f);
			case "witherking" -> startAt(22.626, 6, 59.644, 90f, 0f);
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
				dropItem(false);
				setHotbarSlot(5);
				useSpeedLoadout();
				at(2).swapItems(18, 39).swapItems(19, 38).swapItems(20, 37).swapItems(21, 36);
				at(24).rightClick();
				at(31).stopRightClick();
				at(32).rightClick();
				at(39).stopRightClick();
				at(40).rightClick();
				at(47).stopRightClick();
				at(48).turnHead(-12f, -17f).setHotbarSlot(4);
				at(49).dropItem(true);
				at(51).turnHead(-4f, -4f);
				// tick 52: rapid fire arrow shoots
				at(55).rightClick(-16f, -2f).turnHead(-4f, 4f);
				// tick 56: rapid fire arrow 2 shoots
				at(59).rightClick(-16f, 6f).setHotbarSlot(5);
				TASUtils.scheduleTask(() -> goldor(false), start);
			}
			case "necron" -> {
				swapItems(1, 28);
				swapItems(6, 33);
				swapItems(7, 34);
				useDpsLoadout();
				TASUtils.scheduleTask(() -> necron(false), start);
			}
			case "witherking" -> {
				swapItems(1, 28);
				swapItems(6, 33);
				swapItems(7, 34);
				setHotbarSlot(5);
				useSpeedLoadout();
				at(2).swapItems(13, 39);
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
		at(114).swapItems(11, 36).rightClick();
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
		at(670).turnHead(120f, 0f);
		at(672).swapItems(5, 32).swapItems(18, 39).swapItems(19, 38).swapItems(20, 37).swapItems(21, 36).turnHead(-74.5f, -50f); // equip thermodynamic
		if(doContinue) {
			at(740).dropItem(false);
			at(764).rightClick();
			at(771).stopRightClick();
			at(772).rightClick();
			at(779).stopRightClick();
			at(780).rightClick();
			at(787).stopRightClick();
			at(788).turnHead(-12f, -17f).setHotbarSlot(4);
			at(789).dropItem(true);
			at(791).turnHead(-4f, -4f);
			// tick 792: rapid fire arrow shoots
			at(795).rightClick(-16f, -2f).turnHead(-4f, 4f);
			// tick 796: rapid fire arrow 2 shoots
			at(799).rightClick(-16f, 6f).setHotbarSlot(5);
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
		at(1).swapItems(5, 32).swapItems(18, 39).swapItems(19, 38).swapItems(20, 37).swapItems(21, 36);

		/*
		 * ███████╗███████╗██████╗
		 * ██╔════╝██╔════╝╚════██╗
		 * █████╗  █████╗   █████╔╝
		 * ██╔══╝  ██╔══╝   ╚═══██╗
		 * ███████╗███████╗██████╔╝
		 * ╚══════╝╚══════╝╚═════╝
		 */
		at(4).turnHead(141f, 0f).move("WAP").setHotbarSlot(1);
		at(28).move("").rightClick(-120f, 0f);
		at(29).move("WAP").turnHead(105f, 0f).setHotbarSlot(5);
		at(53).turnHead(0f, 30f).move("WP");
		at(56).leftClick();
		at(57).leftClick();
		at(58).leftClick();
		at(59).swapItems(9, 39).move("WP").leftClick();
		// tick 60: take death tick, body blocked by bers
		at(61).swapItems(9, 39).move("W").leftClick();
		at(62).leftClick();
		at(64).move("").setHotbarSlot(1).rightClick(-125f, 0f);
		at(65).turnHead(10f, 0f).move("WDP").setHotbarSlot(5);
		at(81).move("");
		at(87).turnHead(15f, 0f).move("WP");

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 *  ╚═══██╗
		 * ██████╔╝
		 * ╚═════╝
		 */
		at(93).turnHead(-25f, 0f);
		at(96).turnHead(0f, 0f).leftClick();
		at(97).leftClick().turnHead(4f, -5f).move("WPD");
		at(98).leftClick().move("WP");
		at(99).turnHead(-5f, 75f).setHotbarSlot(1).rightClick();
		at(100).turnHead(4f, 0f).setHotbarSlot(5);
		at(118).rightClick(75f, 4f);
		at(119).clickGui(TERM_SOLUTION_1); // s3 1

		at(120).setHotbarSlot(2).rightClick(0f, 0f);
		at(121).clickGui(LEAP_BERS);

		/*
		 * ██╗  ██╗
		 * ██║  ██║
		 * ███████║
		 * ╚════██║
		 *      ██║
		 *      ╚═╝
		 */
		at(122).turnHead(130f, 0f).move("WP").setHotbarSlot(5);
		at(137).rightClick(107f, 29f);
		at(138).clickGui(TERM_SOLUTION_1); // s4 1

		at(150).rightClick(-90f, -46.5f);
		at(151).clickGui(TERM_SOLUTION_2); // s4 2

		/*
		 * ███████╗██╗ ██████╗ ██╗  ██╗████████╗
		 * ██╔════╝██║██╔════╝ ██║  ██║╚══██╔══╝
		 * █████╗  ██║██║  ███╗███████║   ██║
		 * ██╔══╝  ██║██║   ██║██╔══██║   ██║
		 * ██║     ██║╚██████╔╝██║  ██║   ██║
		 * ╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═╝   ╚═╝
		 */

		at(156).setHotbarSlot(2).rightClick(0f, 0f);
		at(157).clickGui(LEAP_BERS);
		at(158).useDpsLoadout();
		at(162).turnHead(180f, 90f).setHotbarSlot(5).leftClick();
		at(163).leftClick();
		at(164).leftClick();
		at(165).leftClick();
		at(167).leftClick();
		at(170).leftClick();
		at(172).leftClick();
		at(174).leftClick();
		at(175).leftClick();
		at(176).leftClick();
		at(177).leftClick();
		at(178).leftClick();
		at(179).move("");

		if(doContinue) TASUtils.scheduleTask(() -> necron(true), TAS.GOLDOR_TICKS);
	}

	public void necron(boolean doContinue) {
		setHotbarSlot(2);
		rightClick();
		at(1).clickGui(LEAP_HEAL);
		at(2).swapItems(5, 32);
		at(3).setHotbarSlot(5).rightClick();
		at(99).aimTerminatorAtNearestEnemy().stopRightClick();
		at(100).rightClick();
		at(102).stopRightClick();
		at(103).rightClick();
		at(105).stopRightClick();
		at(106).swapItems(5, 32).turnHead(180f, 90f).move("J").leftClick();
		at(107).move("SN").leftClick();
		at(108).move("");
		at(109).useSpeedLoadout();
		at(323).turnHead(74f, 0f).move("WDP");
		at(337).turnHead(119f, 0f).move("WPJ");
		at(338).turnHead(74f, 0f).move("WDP");
		at(345).turnHead(90f, 0f).swapItems(13, 39);
		if(doContinue) TASUtils.scheduleTask(this::witherKing, TAS.NECRON_TICKS);
	}

	public void witherKing() {
		rightClick();
		at(1).turnHead(-166.65f, 0f).move("WDP");
		at(22).move("").rightClick(-115f, -15f);
		at(23).swapItems(13, 39).turnHead(-36f, 0f).move("WP");
		at(51).move("WPJ");
		at(60).move("").turnHead(-176f, -90f).swapItems(5, 32).useDpsLoadout();
		at(187).setHotbarSlot(5).rightClick();
		at(241).stopRightClick();
		at(245).move("WP");
		at(260).move("WPJ");
		at(261).move("WP");
		at(280).move("").rightClick();
		at(341).stopRightClick();
		at(345).turnHead(91.5f, -90f).setHotbarSlot(5).rightClick().move("WP");
		at(390).move("WPJ");
		at(392).move("");
		at(405).rightClick();
		at(441).stopRightClick();
		at(445).turnHead(-1f, -90f).move("WP");
		at(454).move("WPJ");
		at(455).move("WP");
		at(478).move("").rightClick();
		at(541).stopRightClick();
	}
}
