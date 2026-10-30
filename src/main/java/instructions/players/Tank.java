package instructions.players;

import commands.TAS;
import instructions.Actions;
import org.bukkit.*;
import org.bukkit.entity.Player;
import plugin.TASUtils;

public class Tank extends Actions {
	private static final int LEAP_ARCH = 0;
	private static final int LEAP_BERS = 44;
	private static final int LEAP_HEAL = 36;
	private static final int LEAP_MAGE = 8;

	public Tank(Player player) {
		super(player);
	}

	// Tank --> cookiethebald
	public void startPosition(String section) {
		switch(section) {
			case "all", "clear" -> startAt(-120.5, 71, -183.5, 0f, 0f);
			case "maxor", "boss" -> startAt(73.5, 221, 14.5, 0f, 0f);
			case "storm" -> {
				startAt(73.5, 165, 53.5, -90f, 0f);
				// The run's tick 1; start() comes a tick after this.
				TASUtils.scheduleTask(() -> startAt(32.556, 170, 94.378, 35f, 0f), TAS.startDelay(section) + 2);
			}
			case "goldor" -> startAt(-0.557, 120, 77.748, 62f, 37f);
			case "necron" -> startAt(54.6, 105, 27.3, 180f, -6f);
			case "witherking" -> startAt(22.3, 6, 94.782, 90f, 0f);
		}
	}

	public void tankInstructions(String section) {
		int start = TAS.startDelay(section);
		switch(section) {
			case "all", "clear" -> {
				preclear();
				TASUtils.scheduleTask(() -> clear(section.equals("all")), start);
			}
			case "maxor", "boss" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
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
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				useDpsLoadout();
				setHotbarSlot(0);
				TASUtils.scheduleTask(() -> storm(false), start);
			}
			case "goldor" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				useSpeedLoadout();
				setHotbarSlot(4);
				TASUtils.scheduleTask(() -> goldor(false), start);
			}
			case "necron" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				swapItems(7, 35);
				useDpsLoadout();
				TASUtils.scheduleTask(() -> necron(false), start);
			}
			case "witherking" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				swapItems(7, 35);
				useSpeedLoadout();
				setHotbarSlot(5);
				TASUtils.scheduleTask(this::witherKing, start);
			}
		}
	}

	private void preclear() {
		setHotbarSlot(1);
		move("N");
		at(1).rightClick(0f, 23f);
		at(2).rightClick();
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
		at(15).rightClick(0f, 4f); // etherwarp to key spot
		at(16).setHotbarSlot(4).rightClick();
		at(18).clickGui(LEAP_ARCH); // wait one tick so that archer can actually open the door
		at(19).turnHead(0f, 6f).swapItems(4, 31).move("N"); // must re-sneak after using leap
		at(32).setHotbarSlot(3).leftClick();
		// Blood Rush: 32 ticks (-19)

		/*
		 *  ██████╗ ██████╗  █████╗ ██╗   ██╗███████╗██╗
		 * ██╔════╝ ██╔══██╗██╔══██╗██║   ██║██╔════╝██║
		 * ██║  ███╗██████╔╝███████║██║   ██║█████╗  ██║
		 * ██║   ██║██╔══██╗██╔══██║╚██╗ ██╔╝██╔══╝  ██║
		 * ╚██████╔╝██║  ██║██║  ██║ ╚████╔╝ ███████╗███████╗
		 *  ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝  ╚═══╝  ╚══════╝╚══════╝
		 */
		at(33).setHotbarSlot(1).rightClick(11f, 6.7f); //
		at(34).rightClick(-57.15f, 1.75f); // etherwarp to gravel
		at(35).rightClick(45.2f, 4.2f); // etherwarp to opening
		at(36).rightClick(-59f, 4f); // etherwarp to middle
		at(37).setHotbarSlot(7).rightClick(-90f, 4f); // throw reposition pearl, lands in 6 ticks
		at(38).setHotbarSlot(1).rightClick(-129f, 5f); // etherwarp to secret
		at(39).setHotbarSlot(5).turnHead(-90f, 6f).leftClick(); // break iron bar
		at(40).setHotbarSlot(1).rightClick(); // gravel 1/6
		at(41).rightClick(28.7f, -56f); // etherwarp up
		at(42).setHotbarSlot(0).rightClick(-99.7f, -53f); // gravel 2/6
		at(43).setHotbarSlot(1).rightClick(-85f, 2.5f); // etherwarp to miniboss
		at(44).setHotbarSlot(3).turnHead(90f, 0f).leftClick(); // kill miniboss
		at(45).setHotbarSlot(1).rightClick(-90f, 3.3f); // etherwarp towards quiz
		at(46).rightClick(0f, 6.5f); // open quiz
		at(47).rightClick(179f, -2.2f); // etherwarp into dino
		// Gravel (Part 1): 15 ticks

		/*
		 * ██████╗ ██╗███╗   ██╗ ██████╗     ██████╗ ██╗ ██████╗     ███████╗██╗████████╗███████╗
		 * ██╔══██╗██║████╗  ██║██╔═══██╗    ██╔══██╗██║██╔════╝     ██╔════╝██║╚══██╔══╝██╔════╝
		 * ██║  ██║██║██╔██╗ ██║██║   ██║    ██║  ██║██║██║  ███╗    ███████╗██║   ██║   █████╗
		 * ██║  ██║██║██║╚██╗██║██║   ██║    ██║  ██║██║██║   ██║    ╚════██║██║   ██║   ██╔══╝
		 * ██████╔╝██║██║ ╚████║╚██████╔╝    ██████╔╝██║╚██████╔╝    ███████║██║   ██║   ███████╗
		 * ╚═════╝ ╚═╝╚═╝  ╚═══╝ ╚═════╝     ╚═════╝ ╚═╝ ╚═════╝     ╚══════╝╚═╝   ╚═╝   ╚══════╝
		 */
		at(48).rightClick(161.8f, 9.5f); // etherwarp to miniboss
		at(49).setHotbarSlot(3).turnHead(0f, 0f).leftClick(); // kill miniboss
		at(50).setHotbarSlot(4).rightClick(55f, 0f); // blow up crypt
		at(51).setHotbarSlot(0).rightClick(0f, 90f); // crypt 1/5, use wither impact due to beam on cooldown
		at(52).setHotbarSlot(7).rightClick(90f, 6f); // throw reposition pearl, lands in 5 ticks
		at(53).setHotbarSlot(1).rightClick(-9.5f, -60f); // etherwarp up
		at(54).setHotbarSlot(4).rightClick(0f, 2f); // blow up wall
		at(55).setHotbarSlot(1).rightClick(); // etherwarp into secret
		at(56).rightClick(0f, 40f); // dino 1/4
		at(57).rightClick(83f, 56f); // etherwarp down
		at(58).setHotbarSlot(7).rightClick(-100f, 14f); // throw reposition pearl, lands in 5 ticks
		at(59).setHotbarSlot(1).rightClick(20f, 7.5f); // dino 2/4
		at(60).rightClick(135.1f, 30.8f); // etherwarp down
		at(61).setHotbarSlot(5).turnHead(-155f, -26f).leftClick(); // stonk block
		at(62).setHotbarSlot(1).rightClick(); // dino 3/4
		at(63).rightClick(-86.5f, 10.5f); // etherwarp across
		at(64).setHotbarSlot(0).rightClick(-10f, -3f); // dino 3/4
		at(65).setHotbarSlot(1).rightClick(90f, -75f); // etherwarp up
		at(66).rightClick(28f, -9f); // etherwarp back towards gravel
		at(67).rightClick(-25f, -6f); // etherwarp back into gravel
		// Dino Dig Site: 20 ticks (-15)

		/*
		 *  ██████╗ ██████╗  █████╗ ██╗   ██╗███████╗██╗
		 * ██╔════╝ ██╔══██╗██╔══██╗██║   ██║██╔════╝██║
		 * ██║  ███╗██████╔╝███████║██║   ██║█████╗  ██║
		 * ██║   ██║██╔══██╗██╔══██║╚██╗ ██╔╝██╔══╝  ██║
		 * ╚██████╔╝██║  ██║██║  ██║ ╚████╔╝ ███████╗███████╗
		 *  ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝  ╚═══╝  ╚══════╝╚══════╝
		 */
		at(68).setHotbarSlot(7).rightClick(90f, 65f).move(""); // throw reposition pearl, lands in 4 ticks
		at(69).setHotbarSlot(1).rightClick(-80f, 20f).move("N"); // aotv in
		at(70).rightClick(160f, 45f); // gravel 3/6
		at(71).rightClick(65f, 30f); // gravel 4/6
		at(72).setHotbarSlot(7).rightClick(0f, 5f); // throw reposition pearl, lands in 6 ticks
		at(73).setHotbarSlot(1).rightClick(46f, -40f); // etherwarp up
		at(74).rightClick(44f, 45f); // reposition
		at(75).setHotbarSlot(5).turnHead(90f, -70f).leftClick(); // break block
		at(76).setHotbarSlot(1).rightClick(); // etherwarp up, gravel 5/6
		at(77).rightClick(90f, 16.5f); // gravel 6/6
		at(78).rightClick(2f, 1f); // etherwarp into quiz
		// Gravel (Part 2): 11 ticks
		// Gravel: 26 ticks (-26 | -58)

		/*
		 *  ██████╗ ██╗   ██╗██╗███████╗
		 * ██╔═══██╗██║   ██║██║╚══███╔╝
		 * ██║   ██║██║   ██║██║  ███╔╝
		 * ██║▄▄ ██║██║   ██║██║ ███╔╝
		 * ╚██████╔╝╚██████╔╝██║███████╗
		 *  ╚══▀▀═╝  ╚═════╝ ╚═╝╚══════╝
		 */
		at(79).setHotbarSlot(5).turnHead(0f, 60f);
		at(267).rightClick();
		at(407).rightClick();
		at(547).rightClick();
		// Quiz: 469 ticks | 501 ticks from open

		at(548).swapItems(1, 28).swapItems(3, 30).swapItems(4, 31).swapItems(6, 33).swapItems(7, 34).useSpeedLoadout().setHotbarSlot(5);
		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
	}

	public void maxor(boolean doContinue) {
		turnHead(-20f, 0f);
		move("WP");
		at(14).turnHead(-85f, 10f);
		at(17).move("WPJ");
		at(19).leftClick();
		at(20).turnHead(-85f, 0f).leftClick();
		at(21).leftClick();
		at(40).turnHead(-130f, 0f).setHotbarSlot(4);
		at(44).move("WN");
		at(46).turnHead(50f, 0f).move("");
		at(179).rightClick();
		at(180).clickGui(LEAP_BERS);
		at(181).turnHead(40f, 0f).move("WP").setHotbarSlot(1);
		at(245).turnHead(20f, 75f).rightClick();
		at(246).setHotbarSlot(0);
		at(258).turnHead(35f, 0f); // storm tick 8
		at(262).useDpsLoadout();
		if(doContinue) TASUtils.scheduleTask(() -> storm(true), TAS.MAXOR_TICKS);
	}

	public void storm(boolean doContinue) {
		for(int i = 21; i < 80; i += 5) {
			at(i).snapHeadToNearestEnemy().leftClick();
		}
		at(80).setHotbarSlot(0).rightClick();
		at(81).setHotbarSlot(0).rightClick();
		for(int i = 85; i < 180; i += 5) {
			at(i).snapHeadToNearestEnemy().leftClick();
		}
		at(180).turnHead(-155f, 75f).setHotbarSlot(1).move("WP");
		at(187).rightClick();
		at(208).move("").setHotbarSlot(3);
		for(int i = 210; i <= 520; i += 5) {
			at(i).snapHeadToNearestEnemy().leftClick();
		}
		at(521).setHotbarSlot(5).useSpeedLoadout();
		at(526).turnHead(77.5f, 0f).move("WP");
		at(534).move("WPJ");
		at(535).move("WP");
		at(553).turnHead(90f, 90f);
		at(559).leftClick();
		at(560).leftClick();
		at(561).leftClick();
		at(562).leftClick();
		at(563).leftClick();
		at(564).leftClick();
		at(565).leftClick();
		at(566).leftClick();
		at(567).leftClick();
		at(568).leftClick();
		at(569).leftClick();
		at(570).leftClick();
		at(571).leftClick();
		at(572).leftClick();
		at(573).leftClick();
		at(574).leftClick();
		at(575).leftClick();
		at(576).leftClick();
		at(577).leftClick();
		at(578).leftClick();
		at(579).leftClick();
		at(580).leftClick();
		at(581).leftClick();
		at(582).leftClick();
		at(583).move("WAP");
		at(594).move("WP");
		at(597).move("WAP");
		at(602).move("WP");
		at(610).move("");
		at(625).move("WAP");
		at(626).move("").turnHead(62f, 37f).setHotbarSlot(4);
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
		rightClick(); // complete i3
		at(1).rightClick(-90f, 0f);
		at(2).clickGui(LEAP_MAGE);
		at(3).turnHead(-155f, 0f).move("WP").setHotbarSlot(5);
		at(5).rightClick(-102f, 4f).move("WAP");
		at(6).rightClick(-70f, 2f).move("A");
		at(7).rightClick(-33f, -4f).move("AS"); // help with i1
		at(8).turnHead(-135f, 0f).move("WAP");
		at(18).rightClick(0f, 22f);
		at(19).clickGui(TERM_SOLUTION_2); // s1 2

		at(22).move("N");
		at(23).rightClick(180f, 74f);
		at(24).clickGui(TERM_SOLUTION_1); // s1 1

		at(25).setHotbarSlot(4).rightClick(0f, 0f);
		at(28).clickGui(LEAP_BERS); // wait 1 tick for more optimal movement
		at(29).turnHead(180f, 5f).move("WP").setHotbarSlot(5);
		at(31).move("");

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 * ██╔═══╝
		 * ███████╗
		 * ╚══════╝
		 */
		at(39).rightClick();
		at(40).clickGui(TERM_SOLUTION_1); // s2 1

		at(41).setHotbarSlot(4);
		at(45).rightClick(0f, 0f);
		at(46).clickGui(LEAP_MAGE);
		at(47).turnHead(138f, 3f).move("WP").setHotbarSlot(5);
		at(49).move("WPJ");
		at(50).move("WP");
		at(59).rightClick(133f, 16f);
		at(60).clickGui(TERM_SOLUTION_3); // s2 3

		at(61).turnHead(75f, 0f).move("WDP");
		at(62).move("WP");
		at(71).turnHead(90f, -90f);
		at(72).move("");
		at(85).rightClick(80f, -38f); // s2 bl

		at(86).setHotbarSlot(4).rightClick(0f, 0f);
		at(87).clickGui(LEAP_ARCH);

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 *  ╚═══██╗
		 * ██████╔╝
		 * ╚═════╝
		 */
		at(88).turnHead(-177.5f, -5f).move("WP");
		at(93).swapItems(7, 35).setHotbarSlot(7).rightClick();
		at(109).stopRightClick();
		at(110).rightClick(114f, -26f); // s3 ll

		at(111).setHotbarSlot(1).rightClick(100f, 72f);
		at(112).setHotbarSlot(5);
		at(112).turnHead(90f, 0f).move("WP");
		at(119).rightClick(-90f, 45f); // s3 rl

		at(120).setHotbarSlot(4).rightClick(0f, 0f);
		at(121).clickGui(LEAP_BERS);

		/*
		 * ██╗  ██╗
		 * ██║  ██║
		 * ███████║
		 * ╚════██║
		 *      ██║
		 *      ╚═╝
		 */
		at(122).turnHead(-127f, 0f).move("WP").setHotbarSlot(5);
		at(137).turnHead(-127f, 34f).rightClick();
		at(138).clickGui(TERM_SOLUTION_3); // s4 3

		at(140).setHotbarSlot(4).rightClick(0f, 0f);
		at(141).clickGui(LEAP_BERS);
		at(142).turnHead(-90f, 0f).move("WP").setHotbarSlot(5);
		at(152).turnHead(0f, 0f);
		at(155).rightClick(-1f, 3f);
		at(156).clickGui(TERM_SOLUTION_4); // s4 4

		at(157).setHotbarSlot(4).rightClick(180f, 0f);
		at(158).clickGui(LEAP_BERS);

		/*
		 * ███████╗██╗ ██████╗ ██╗  ██╗████████╗
		 * ██╔════╝██║██╔════╝ ██║  ██║╚══██╔══╝
		 * █████╗  ██║██║  ███╗███████║   ██║
		 * ██╔══╝  ██║██║   ██║██╔══██║   ██║
		 * ██║     ██║╚██████╔╝██║  ██║   ██║
		 * ╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═╝   ╚═╝
		 */
		at(160).move("WP").setHotbarSlot(3);
		at(165).move("").snapHeadAtNearestNamed("Goldor").leftClick();
		at(166).useDpsLoadout();

		if(doContinue) TASUtils.scheduleTask(() -> necron(true), TAS.GOLDOR_TICKS);
	}

	public void necron(boolean doContinue) {
		setHotbarSlot(4);
		rightClick();
		at(1).clickGui(LEAP_HEAL);
		at(2).setHotbarSlot(3);
		for(int i = 5; i <= 320; i += 5) {
			at(i).snapHeadAtNearestNamed("Necron").leftClick();
		}
		at(321).setHotbarSlot(4).rightClick();
		at(322).clickGui(LEAP_ARCH);
		at(323).useSpeedLoadout();
		at(325).turnHead(17f, 0f).setHotbarSlot(5).move("WDP");
		at(337).turnHead(62f, 0f).move("WPJ");
		at(338).turnHead(17f, 0f).move("WDP");
		at(348).turnHead(90f, 0f).move("");
		if(doContinue) TASUtils.scheduleTask(this::witherKing, TAS.NECRON_TICKS);
	}

	public void witherKing() {
		rightClick();
		at(1).setHotbarSlot(4).rightClick();
		at(20).clickGui(LEAP_ARCH);
		at(21).setHotbarSlot(8).rightClick(-100f, -8f);
		at(22).swapItems(3, 30).swapItems(5, 32).useDpsLoadout();
		at(83).setHotbarSlot(4).rightClick();
		at(84).clickGui(LEAP_MAGE);
		at(85).setHotbarSlot(5).rightClick();
		at(145).setHotbarSlot(6).rightClick();
		at(182).stopRightClick().setHotbarSlot(3);
		at(184).leftClick();
		at(185).setHotbarSlot(4).rightClick();
		at(186).clickGui(LEAP_ARCH);
		at(187).setHotbarSlot(6).rightClick();
		at(241).stopRightClick().setHotbarSlot(3);
		at(244).leftClick();
		at(245).move("WP");
		at(260).move("WPJ");
		at(261).move("WP");
		at(280).move("").setHotbarSlot(6).rightClick();
		at(341).stopRightClick().setHotbarSlot(3);
		at(344).leftClick();
		at(345).turnHead(91.5f, -90f).setHotbarSlot(5).rightClick().move("WP");
		at(390).move("WPJ");
		at(392).move("");
		at(405).setHotbarSlot(6).rightClick();
		at(441).stopRightClick().setHotbarSlot(3);
		at(444).leftClick();
		at(445).turnHead(-1f, -90f).move("WP");
		at(454).move("WPJ");
		at(455).move("WP");
		at(478).move("").setHotbarSlot(6).rightClick();
		at(541).stopRightClick().setHotbarSlot(3);
		at(544).leftClick();
	}
}
