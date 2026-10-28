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
			case "storm" -> startAt(73.5, 165, 53.5, 90f, 0f);
			case "goldor" -> startAt(108.5, 120, 94.496, -141f, 1f);
			case "necron" -> startAt(54.524, 64, 100.707, 180f, -6f);
			case "witherking" -> startAt(89.7, 6, 94.406, -75.6f, 18.8f);
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
				setHotbarSlot(1);
				TASUtils.scheduleTask(() -> storm(false), start);
				TASUtils.scheduleTask(() -> startAt(114.640, 170, 94.144, -55f, 0f), start + 1);
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
		at(56).turnHead(180f, -5).move("SN");
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
		at(263).turnHead(-55f, 0f);
		at(269).useDpsLoadout();
		if(doContinue) TASUtils.scheduleTask(() -> storm(true), TAS.MAXOR_TICKS);
	}

	public void storm(boolean doContinue) {

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
		at(3).rightClick();
		at(4).rightClick();
		at(5).rightClick();

		at(6).turnHead(65f, 80f).setHotbarSlot(1);
		at(7).move("WP", 16);
		at(8).rightClick();
		at(16).turnHead(90f, 5f).setHotbarSlot(5);
		at(22).turnHead(87f, 5f);
		at(23).rightClick(); // s1 4
		// tick 24: terminal completes

		at(25).turnHead(-180f, 0f);
		at(26).move("WPD", 0);
		at(28).move("WP", 1);
		at(32).move("WPA", 13);
		at(35).turnHead(90f, 9f);
		at(44).rightClick(); // s1 3
		// tick 45: terminal completes

		at(46).setHotbarSlot(4);
		at(48).leap(fake("RedFish2471"));

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 * ██╔═══╝
		 * ███████╗
		 * ╚══════╝
		 */
		at(49).turnHead(155f, 0f).setHotbarSlot(5);
		at(69).move("WP", 20);
		at(89).turnHead(-85f, 30);
		at(90).rightClick(); // s2 2
		// tick 91: terminal completes

		/*
		 *  ██████╗ ██████╗ ██████╗ ███████╗
		 * ██╔════╝██╔═══██╗██╔══██╗██╔════╝
		 * ██║     ██║   ██║██████╔╝█████╗
		 * ██║     ██║   ██║██╔══██╗██╔══╝
		 * ╚██████╗╚██████╔╝██║  ██║███████╗
		 *  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
		 */
		at(92).turnHead(155f, 25f);
		at(93).leftClick();
		at(94).leftClick();
		at(95).move("WP", 62);
		at(96).turnHead(177.4f, 45f);
		at(97).leftClick();
		at(98).leftClick();
		at(99).leftClick();
		at(100).leftClick();
		at(101).leftClick();
		at(102).leftClick();
		at(147).turnHead(180f, 45f);
		at(148).leftClick();
		at(149).leftClick();
		at(150).turnHead(180f, 0f);

		// turn head to save a tick on other players having to turn head
		at(157).turnHead(-91f, 0f);
		at(159).turnHead(-96f, 80f);
		at(170).turnHead(-128f, 10f);

		/*
		 * ██╗  ██╗
		 * ██║  ██║
		 * ███████║
		 * ╚════██║
		 *      ██║
		 *      ╚═╝
		 */
		at(172).turnHead(0f, 0f);
		at(173).move("WP", 8);

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
		for(int i = 155; i <= 500; i += 5) {
			at(i).leftClick();
		}
		at(501).setHotbarSlot(4);
		at(502).leap(fake("akc0303"));
		at(503).swapItems(13, 39);
		at(504).turnHead(-67f, 0f).setHotbarSlot(5);
		at(505).move("WP", 0);
		at(521).move("WPJ", 0);
		at(522).move("WP", 12);
		at(534).turnHead(-90f, 0f);
		at(535).swapItems(3, 30);
		if(doContinue) TASUtils.scheduleTask(this::witherKing, TAS.NECRON_TICKS);
	}

	public void witherKing() {
		rightClick();
		at(1).setHotbarSlot(4);
		at(22).leap(fake("RedFish2471"));
		at(23).turnHead(90f, 0f).setHotbarSlot(8);
		at(24).rightClick();
		at(25).turnHead(-20f, 0f);
		at(26).move("WP", 0);
		at(59).move("WPJ", 0);
		at(61).move("WP", 8);
		at(69).swapItems(5, 32).swapItems(13, 39);
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
