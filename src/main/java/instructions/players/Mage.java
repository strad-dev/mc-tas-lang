package instructions.players;

import commands.TAS;
import instructions.Actions;
import org.bukkit.entity.Player;
import plugin.TASUtils;

public class Mage extends Actions {
	private static final int LEAP_ARCH = 0;
	private static final int LEAP_BERS = 44;
	private static final int LEAP_HEAL = 36;
	private static final int LEAP_TANK = 8;

	public Mage(Player player) {
		super(player);
	}

	public void startPosition(String section) {
		switch(section) {
			case "all", "clear" -> startAt(-120.5, 71, -183.5, 0f, 0f);
			case "maxor", "boss" -> startAt(73.5, 221, 14.5, 0f, 0f);
			case "storm" -> {
				startAt(73.5, 165, 53.5, 90f, 0f);
				// The run's tick 1; start() comes a tick after this.
				TASUtils.scheduleTask(() -> startAt(114.640, 170, 94.144, -55f, 0f), TAS.startDelay(section) + 2);
			}
			case "goldor" -> startAt(108.424, 120, 92.967, -120f, 3f);
			case "necron" -> startAt(54.6, 105, 27.3, 180f, -6f);
			case "witherking" -> startAt(89.7, 6, 94.506, -90f, 0f);
		}
	}

	public void mageInstructions(String section) {
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
				setHotbarSlot(3);
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
				setHotbarSlot(3);
				TASUtils.scheduleTask(() -> storm(false), start);
			}
			case "goldor" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				useSpeedLoadout();
				setHotbarSlot(5);
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

	private void preClear() {
		setHotbarSlot(1);
		move("N");
		at(1).rightClick(0f, 23f);
		at(2).rightClick();
		at(3).setHotbarSlot(3).turnHead(0f, 0f);
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
		at(15).leftClick(); // immediately kill miniboss
		at(16).setHotbarSlot(4).rightClick();
		at(18).clickGui(LEAP_ARCH); // wait one tick so that archer can actually open the door
		at(19).swapItems(4, 31).move("N"); // swap tnt with infinileap, must re-sneak after using leap
		// door opens tick 32
		at(32).setHotbarSlot(1).rightClick(0f, 2.3f);
		at(33).leftClick(); // open blood
		// Blood Rush: 33 ticks

		/*
		 * ██████╗ ███████╗ █████╗ ████████╗██╗  ██╗███╗   ███╗██╗████████╗███████╗
		 * ██╔══██╗██╔════╝██╔══██╗╚══██╔══╝██║  ██║████╗ ████║██║╚══██╔══╝██╔════╝
		 * ██║  ██║█████╗  ███████║   ██║   ███████║██╔████╔██║██║   ██║   █████╗
		 * ██║  ██║██╔══╝  ██╔══██║   ██║   ██╔══██║██║╚██╔╝██║██║   ██║   ██╔══╝
		 * ██████╔╝███████╗██║  ██║   ██║   ██║  ██║██║ ╚═╝ ██║██║   ██║   ███████╗
		 * ╚═════╝ ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝╚═╝     ╚═╝╚═╝   ╚═╝   ╚══════╝
		 */
		at(34).rightClick(180f, 5f); // reposition
		at(35).setHotbarSlot(7).rightClick(0f, 40f); // throw reposition pearl, lands in 4 ticks
		at(36).setHotbarSlot(1).rightClick(42f, 5f); // etherwarp to secret
		at(37).setHotbarSlot(4).rightClick(0f, 16f); // blow up wall
		at(38).setHotbarSlot(1).rightClick(); // deathmite 1/6
		at(39).rightClick(-52f, 4f); // etherwarp to next secret
		at(40).setHotbarSlot(5).turnHead(0f, 90f).leftClick();
		at(41).leftClick();
		at(42).leftClick(); // break to secret
		at(43).setHotbarSlot(1).rightClick(); // etherwarp down
		at(44).rightClick(); // deathmite 2/6
		at(45).rightClick(180f, -83.5f); // etherwarp up
		at(46).rightClick(157.5f, -69.5f); // etherwarp to next secret
		at(47).rightClick(-90f, 0f); // deathmite 3/6
		at(48).rightClick(110.2f, 0.97f); // etherwarp to next secret
		at(49).setHotbarSlot(7).rightClick(-108f, 10f); // throw reposition pearl, lands in 4 ticks
		at(50).setHotbarSlot(1).rightClick(-138f, -60f); // etherwarp to secrets
		at(51).setHotbarSlot(0).rightClick(180f, 90f); // deathmite 4/6
		at(52).setHotbarSlot(1).rightClick(180f, 20f); // deathmite 5/6
		at(53).rightClick(-90f, -4f); // etherwarp towards crypts
		at(54).rightClick(-90f, 5.5f); // etherwarp to crypts
		at(55).setHotbarSlot(4).rightClick(180f, 45f); // blow up crypts
		at(56).setHotbarSlot(0).rightClick(180f, 90f).move(""); // crypt 2/5, crypt 3/5 (wither impact)
		at(57).setHotbarSlot(1).rightClick(-5f, 16f).move("N"); // aotv over cliff
		at(58).rightClick(-109f, 65f); // etherwarp down
		at(59).setHotbarSlot(7).rightClick(90f, 8f); // throw reposition pearl, lands in 5 ticks
		at(60).setHotbarSlot(5).turnHead(-90f, -7f).leftClick(); // break block
		at(61).setHotbarSlot(1).rightClick(); // etherwarp in
		at(62).rightClick(-90f, 9f); // reposition etherwarp
		at(63).rightClick(); // deathmite 6/6
		at(64).rightClick(-11.5f, -43f); // reposition etherwarp
		at(65).rightClick(80f, 14.5f); // etherwarp towards blood
		at(66).rightClick(140f, 10f);
		at(67).turnHead(63f, 4f); // prepare blood etherwarp, do not execute to avoid "aggro" (even though it doesn't exist here)
		// Deathmite: 34 ticks (-38 | -55)

		/*
		 * ██████╗ ██╗      ██████╗  ██████╗ ██████╗      ██████╗ █████╗ ███╗   ███╗██████╗
		 * ██╔══██╗██║     ██╔═══██╗██╔═══██╗██╔══██╗    ██╔════╝██╔══██╗████╗ ████║██╔══██╗
		 * ██████╔╝██║     ██║   ██║██║   ██║██║  ██║    ██║     ███████║██╔████╔██║██████╔╝
		 * ██╔══██╗██║     ██║   ██║██║   ██║██║  ██║    ██║     ██╔══██║██║╚██╔╝██║██╔═══╝
		 * ██████╔╝███████╗╚██████╔╝╚██████╔╝██████╔╝    ╚██████╗██║  ██║██║ ╚═╝ ██║██║
		 * ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝ ╚═════╝      ╚═════╝╚═╝  ╚═╝╚═╝     ╚═╝╚═╝
		 */
		at(453).rightClick(); // execute the prepared etherwarp to blood
		at(454).rightClick(0f, 4f); // etherwarp into blood
		at(455).setHotbarSlot(3).snapHeadAtNearestNamed("Diamante Giant").leftClick();
		at(460).snapHeadAtNearestNamed("Bonzo").leftClick();
		at(465).snapHeadAtNearestNamed("BeansBeans13").leftClick();
		at(470).snapHeadAtNearestNamed("valej").leftClick();
		at(471).rightClick(0f, -35f).move("");
		at(510).leftClick();
		at(525).leftClick();
		at(537).leftClick();
		at(551).leftClick();
		at(574).leftClick();
		at(585).leftClick();
		at(599).leftClick();
		at(610).leftClick();
		at(622).leftClick();
		at(633).leftClick();
		at(647).leftClick().move("N");
		at(648).turnHead(0f, 3.5f).setHotbarSlot(1);
		at(649).rightClick(); // etherwarp to portal
		at(650).swapItems(1, 28).swapItems(3, 30).swapItems(4, 31).swapItems(6, 33).swapItems(7, 34).setHotbarSlot(3);
		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
	}

	public void maxor(boolean doContinue) {
		move("WP"); // move to aggro spot
		at(44).move("WPJ"); // make the jump
		at(52).move("WP"); // sneak at end of conveyor belt
		at(56).turnHead(180f, -5f).move("SN");
		at(60).leftClick().move("N");
		at(65).leftClick();
		at(70).leftClick().move("");
		at(75).leftClick();
		at(80).leftClick(); // aggro
		at(81).setHotbarSlot(0).rightClick();
		at(82).setHotbarSlot(0).rightClick(); // clear wither skellies
		for(int i = 85; i <= 190; i += 5) {
			at(i).leftClick(); // keep aggro/dps
		}
		at(191).setHotbarSlot(4).useSpeedLoadout();
		at(193).rightClick();
		at(220).clickGui(LEAP_BERS); // wait to leap, use berserk's momentum for faster movement
		at(221).turnHead(-55f, 0f).setHotbarSlot(1).move("WP");
		at(238).turnHead(-30f, 75f);
		at(252).rightClick();
		at(263).turnHead(-55f, 0f).setHotbarSlot(3);
		at(269).useDpsLoadout();
		if(doContinue) TASUtils.scheduleTask(() -> storm(true), TAS.MAXOR_TICKS);
	}

	public void storm(boolean doContinue) {
		for(int i = 21; i < 80; i += 5) {
			at(i).snapHeadToNearestEnemy().leftClick();
		}
		at(80).setHotbarSlot(0).rightClick();
		at(81).setHotbarSlot(0).rightClick();
		for(int i = 85; i < 160; i += 5) {
			at(i).snapHeadToNearestEnemy().leftClick();
		}
		at(160).turnHead(154f, 75f).setHotbarSlot(1).move("WP");
		at(167).rightClick();
		at(188).move("").setHotbarSlot(3);
		for(int i = 190; i <= 445; i += 5) {
			at(i).snapHeadToNearestEnemy().leftClick();
		}
		at(446).turnHead(9.6f, 75f).setHotbarSlot(1).move("WP");
		at(453).rightClick();
		at(454).turnHead(9.6f, 45f).setHotbarSlot(5);
		at(483).move("WPJ");
		at(486).turnHead(0f, 45f).move("WP");
		at(493).leftClick();
		at(494).leftClick();
		at(495).leftClick();
		at(496).leftClick();
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
		at(514).clickGui(LEAP_BERS);
		at(515).swapItems(5, 32);
		at(526).turnHead(-20f, 75f).setHotbarSlot(1).move("WP");
		at(534).rightClick();
		at(540).setHotbarSlot(5).rightClick(); // charge rag
		at(549).move("").snapHeadAtNearestNamed("Storm");
		for(int i = 550; i <= 595; i += 5) {
			at(i).leftClick();
		}
		// storm: begins moving on tick 645, crushed at 660, therefore pillar must start moving at 600
		at(598).turnHead(94.2f, 75f).move("S");
		at(600).move("WP");
		at(606).setHotbarSlot(1).rightClick();
		at(625).swapItems(5, 32).setHotbarSlot(6).rightClick(-155f, -28f);
		at(647).stopRightClick();
		at(660).setHotbarSlot(3).snapHeadAtNearestNamed("Storm").leftClick();
		at(661).setHotbarSlot(4).rightClick();
		at(662).clickGui(LEAP_HEAL);
		at(663).turnHead(-90f, 0f).move("S").setHotbarSlot(3);
		at(688).move("J");
		at(737).move("");
		at(740).snapHeadAtNearestNamed("Storm").leftClick();
		at(741).useSpeedLoadout();
		at(756).setHotbarSlot(4).rightClick();
		at(757).clickGui(LEAP_HEAL);
		at(758).setHotbarSlot(5).turnHead(-120f, 3f);
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
		at(2).rightClick();
		at(3).setHotbarSlot(1).move("WP").rightClick(58f, 75f);
		at(14).setHotbarSlot(5).turnHead(83f, 30f).leftClick();
		at(15).turnHead(90f, 25f).leftClick();
		at(16).leftClick();
		at(19).turnHead(180f, 0f).move("WP");
		at(20).move("").rightClick(62f, 0f);
		at(21).clickGui(TERM_SOLUTION_4); // s1 4

		at(22).turnHead(180f, 0f).move("WP");
		at(38).rightClick(126f, 8f);
		at(39).clickGui(TERM_SOLUTION_3); // s1 3

		at(40).setHotbarSlot(4).swapItems(7, 35).rightClick(0f, 0f);
		at(42).clickGui(LEAP_BERS);

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 * ██╔═══╝
		 * ███████╗
		 * ╚══════╝
		 */
		at(43).turnHead(95f, 0f).setHotbarSlot(7).move("WP");
		at(51).move("S").rightClick();
		at(71).turnHead(91f, -5f).stopRightClick();
		at(72).turnHead(178f, 5f).move("WP").setHotbarSlot(5);
		at(85).rightClick();
		at(86).clickGui(MELODY_1).clickGui(MELODY_2).clickGui(MELODY_3); // s2 5

		at(87).setHotbarSlot(4).rightClick(0f, 0f);
		at(88).clickGui(LEAP_ARCH);

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 *  ╚═══██╗
		 * ██████╔╝
		 * ╚═════╝
		 */
		at(89).turnHead(0f, 0f).setHotbarSlot(1).move("WP");
		at(90).rightClick(-35f, 75f);
		at(91).turnHead(-35f, -90f).setHotbarSlot(5);
		at(122).rightClick(-89.5f, -45f);
		at(123).clickGui(TERM_SOLUTION_3); // s3 3

		at(124).setHotbarSlot(4).rightClick(0f, 0f);
		at(125).clickGui(LEAP_BERS);

		/*
		 * ██╗  ██╗
		 * ██║  ██║
		 * ███████║
		 * ╚════██║
		 *      ██║
		 *      ╚═╝
		 */
		at(126).turnHead(-96f, 0f).move("WP").setHotbarSlot(1);
		at(134).move("WPJ");
		at(137).rightClick(-96f, 75f);
		at(138).setHotbarSlot(5);
		at(147).rightClick(-140f, -25f); // s4 bl

		at(148).setHotbarSlot(4).rightClick(0f, 0f);
		at(157).clickGui(LEAP_BERS);
		at(158).useDpsLoadout();

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
		at(325).turnHead(-109f, 0f).setHotbarSlot(5).move("WDP");
		at(339).turnHead(-64f, 0f).move("WPJ");
		at(340).turnHead(-109f, 0f).move("WDP");
		at(349).turnHead(-90f, 0f).move("");
		if(doContinue) TASUtils.scheduleTask(this::witherKing, TAS.NECRON_TICKS);
	}

	public void witherKing() {
		rightClick();
		at(1).setHotbarSlot(4).rightClick();
		at(20).clickGui(LEAP_BERS);
		at(21).setHotbarSlot(8).rightClick();
		at(22).swapItems(3, 30).swapItems(5, 32).turnHead(4f, 0f).move("WP");
		at(51).move("WPJ");
		at(52).move("WP");
		at(62).move("WPJ");
		at(63).move("WP");
		at(71).move("WPJ");
		at(73).move("").turnHead(180f, -90f).useDpsLoadout();
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
