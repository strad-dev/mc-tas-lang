package instructions.players;

import commands.TAS;
import instructions.Actions;
import org.bukkit.entity.Player;
import plugin.TASUtils;

public class Mage extends Actions {
	public Mage(Player player) {
		super(player);
	}

	public void startPosition(String section) {
		switch(section) {
			case "all", "clear" -> startAt(-120.5, 71, -183.5, 0f, 0f);
			case "maxor", "boss" -> startAt(73.5, 221, 14.5, 0f, 0f);
			case "storm" -> startAt(100.504, 169, 53.534, 90f, 0f);
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
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				setHotbarSlot(0);
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

	private void preClear() {
		setHotbarSlot(1);
		move("N");
		rightClick(0f, 23f);
		at(1).rightClick();
		at(2).setHotbarSlot(3).turnHead(0f, 0f);
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
		at(18).clickGui(0); // wait one tick so that archer can actually open the door
		at(19).move("N"); // must re-sneak after using leap
		// door opens tick 32
		at(32).setHotbarSlot(1).rightClick(0f, 2.3f);
		at(33).leftClick(); // open blood

//		/*
//		 * ██████╗ ███████╗ █████╗ ████████╗██╗  ██╗███╗   ███╗██╗████████╗███████╗
//		 * ██╔══██╗██╔════╝██╔══██╗╚══██╔══╝██║  ██║████╗ ████║██║╚══██╔══╝██╔════╝
//		 * ██║  ██║█████╗  ███████║   ██║   ███████║██╔████╔██║██║   ██║   █████╗
//		 * ██║  ██║██╔══╝  ██╔══██║   ██║   ██╔══██║██║╚██╔╝██║██║   ██║   ██╔══╝
//		 * ██████╔╝███████╗██║  ██║   ██║   ██║  ██║██║ ╚═╝ ██║██║   ██║   ███████╗
//		 * ╚═════╝ ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝╚═╝     ╚═╝╚═╝   ╚═╝   ╚══════╝
//		 */
//		// tick 1: tac TP back
//		at(2).rightClick();
//		at(4).setHotbarSlot(4);
//		at(50).leap(fake("cookiethebald"));
//		at(51).turnHead(-65f, 3f).setHotbarSlot(7);
//		at(52).rightClick(); // pearl to next secret | lands in 8 ticks
//		at(53).turnHead(72f, -3f).setHotbarSlot(1);
//		at(54).rightClick(); // aotv to secret
//		at(55).turnHead(0f, 0f);
//		at(56).move("WP", 3).dropItem(true); // blow up wall with Guided Sheep | cooldown for 300 ticks
//		at(59).swapItems(4, 31);
//		at(60).turnHead(162f, -66.2f).setHotbarSlot(7); // pearl lands
//		at(61).rightClick(); // pearl to top | lands in 11 ticks
//		at(62).turnHead(0f, 90f).setHotbarSlot(5);
//		at(63).leftClick(); // stonk down
//		at(64).leftClick();
//		at(65).leftClick();
//		at(66).setHotbarSlot(1);
//		at(67).rightClick(); // aotv down
//		at(68).leftClick();
//		// tick 72: pearl lands
//		at(73).rightClick(); // reposition
//		at(74).turnHead(-90f, 0f);
//		at(75).leftClick();
//		at(76).turnHead(110.2f, 0.97f).move("N", 2);
//		at(77).rightClick(); // etherwarp to next secret
//		at(78).setHotbarSlot(7).turnHead(-105.5f, 4.5f);
//		at(79).rightClick(); // pearl towards crypts | lands in 7 ticks
//		at(80).turnHead(-138f, -60f).setHotbarSlot(1).move("N", 2);
//		at(81).rightClick(); // etherwarp to top compartment
//		at(82).turnHead(-180f, 90f).setHotbarSlot(0);
//		at(83).rightClick(); // kill bat
//		at(84).turnHead(-180f, 30f);
//		at(85).leftClick();
//		at(86).turnHead(-90f, -6.5f).setHotbarSlot(1).move("N", 2); // pearl lands
//		at(87).rightClick(); // etherwarp to crypts
//		at(88).turnHead(-90f, 30f);
//		at(89).rightClick(); // aotv to correct spot
//		at(90).turnHead(-125f, 10f).setHotbarSlot(4);
//		at(91).rightClick(); // blow up crypt #1
//		at(92).setHotbarSlot(3);
//		at(93).leftClick();
//		at(94).turnHead(120f, 10f).setHotbarSlot(4);
//		// infinityboom technically has a 20-tick cooldown with dupe player
//		// but let's assume normal superboom is being used (which has no cooldown)
//		at(95).rightClick(); // blow up crypt #2
//		at(96).setHotbarSlot(3);
//		at(98).leftClick(); // player beam cooldown is 5 ticks with max attack speed
//		at(98).turnHead(-5f, 16f).setHotbarSlot(1);
//		at(99).rightClick(); // aotv towards secret
//		at(100).turnHead(-110f, 67f).move("N", 2);
//		at(101).rightClick(); // etherwarp to gate
//		at(102).turnHead(60f, -20f).setHotbarSlot(7);
//		at(103).rightClick(); // throw pearl back towards blood | lands in 13 ticks
//		at(104).turnHead(-90f, 36f).setHotbarSlot(5);
//		at(105).leftClick();
//		at(106).leftClick(); // stonk gate
//		at(107).turnHead(-90f, -12f).setHotbarSlot(1).move("N", 0);
//		at(108).rightClick(); // etherwarp towards chest
//		at(109).turnHead(-90f, 18f).setHotbarSlot(3).move("WP", 1); // walk forward to reposition
//		at(110).leftClick();
//		at(111).leftClick(); // kill mimic
//		at(116).turnHead(68f, 11.5f).setHotbarSlot(1).move("N", 6); // pearl lands
//		at(117).rightClick(); // etherwarp back towards blood
//		at(118).turnHead(124f, 3f);
//		at(119).rightClick(); // etherwarp opposite of blood (positioning)
//		at(120).turnHead(-2f, 3.5f);
//		at(121).rightClick(); // etherwarp to blood
//		at(122).turnHead(0f, 0f).setHotbarSlot(3);
//		// Deathmite: 72 ticks
//
//		/*
//		 * ██████╗ ██╗      ██████╗  ██████╗ ██████╗      ██████╗ █████╗ ███╗   ███╗██████╗
//		 * ██╔══██╗██║     ██╔═══██╗██╔═══██╗██╔══██╗    ██╔════╝██╔══██╗████╗ ████║██╔══██╗
//		 * ██████╔╝██║     ██║   ██║██║   ██║██║  ██║    ██║     ███████║██╔████╔██║██████╔╝
//		 * ██╔══██╗██║     ██║   ██║██║   ██║██║  ██║    ██║     ██╔══██║██║╚██╔╝██║██╔═══╝
//		 * ██████╔╝███████╗╚██████╔╝╚██████╔╝██████╔╝    ╚██████╗██║  ██║██║ ╚═╝ ██║██║
//		 * ╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝ ╚═════╝      ╚═════╝╚═╝  ╚═╝╚═╝     ╚═╝╚═╝
//		 */
//		// Left-click the blood door to open it.  This requires the Blood Key, and is handled by
//		// Actions.leftClick's door detection; the old hard openBloodDoor() call is gone.
//		at(350).leftClick();
//		at(371).snapHeadAtNearestNamed("Diamante Giant");
//		at(372).leftClick();
//		at(373).snapHeadAtNearestNamed("Bonzo");
//		at(377).leftClick();
//		at(378).snapHeadAtNearestNamed("Nucleararmadillo");
//		at(382).leftClick();
//		at(383).snapHeadAtNearestNamed("Jamie_2013");
//		at(387).leftClick();
//		at(388).move("WP", 10).turnHead(0f, -35f);
//		at(430).leftClick();
//		at(447).leftClick();
//		at(472).leftClick();
//		at(486).leftClick();
//		at(503).leftClick();
//		at(516).leftClick();
//		at(529).leftClick();
//		at(542).leftClick();
//		at(559).leftClick();
//		at(575).leftClick();
//		at(600).leftClick();
//		at(614).leftClick();
//		at(631).leftClick();
//		at(644).leftClick();
//		at(658).leftClick();
//		at(659).turnHead(0f, 3.5f).setHotbarSlot(1).move("N", 2);
//		at(660).rightClick(); // etherwarp to portal
//		at(661).swapItems(1, 28).swapItems(3, 30).swapItems(4, 31).swapItems(6, 33).swapItems(7, 34).swapItems(11, 36).setHotbarSlot(5);
//		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
	}

	public void maxor(boolean doContinue) {
		at(1).turnHead(-13f, 0f);
		at(2).move("WP", 0);
		at(25).move("WN", 0); // spring boots to left crystal
		at(38).move("WP", 0);
		at(60).rightClick();
		at(61).turnHead(-90f, 0f);
		at(63).turnHead(-135f, 0f);
		at(67).turnHead(-132f, 45f); // minor repositioning to avoid bonking
		at(68).leftClick(); // stonk block to avoid bonking
		at(69).turnHead(-132f, 0f);
		at(86).swapItems(11, 36); // temporarily remove spring boots so that pos readjustment doesnt trigger ability
		at(87).move("WN", 1);
		at(88).turnHead(52f, 0f);
		at(89).swapItems(11, 36);
		at(161).move("WP", 0);
		at(162).move("WN", 0); // spring boots to left crystal
		at(173).move("WP", 22);
		at(200).swapItems(11, 36);
		at(240).rightClick();
		at(241).turnHead(-128f, 0f);
		at(242).move("WP", 24);
		at(243).setHotbarSlot(4);
		at(339).leap(fake("RedFish2471"));
		at(340).turnHead(-121f, 0f).setHotbarSlot(0);
		at(341).move("WP", 9); // move to storm clear spot
		at(350).turnHead(90f, 0f);
		if(doContinue) TASUtils.scheduleTask(() -> storm(true), TAS.MAXOR_TICKS);
	}

	public void storm(boolean doContinue) {
		for(int i = 0; i <= 10; i += 3) {
			at(i).rightClick();
		} // clear platform
		at(11).setHotbarSlot(3);
		for(int i = 15; i <= 530; i += 5) {
			at(i - 1).snapHeadToNearestEnemy();
			at(i).loopLeftClick();
		} // kill outstanding wither skeletons
		at(531).setHotbarSlot(4);
		at(532).leap(fake("RedFish2471"));
		at(533).turnHead(-26f, 0f).setHotbarSlot(1).swapItems(5, 32);
		at(546).move("WP", 0);
		at(554).move("WPJ", 0);
		at(556).turnHead(-26f, 80f);
		at(557).rightClick(); // bonzo back to pad
		at(558).turnHead(-26f, 0f).move("WP", 15).setHotbarSlot(3);
		at(574).snapHeadAtNearestNamed("Storm");
		for(int i = 575; i <= 630; i += 5) {
			at(i).leftClick();
		}
		at(585).setHotbarSlot(5);
		at(586).rightClick(); // rag buff
		at(631).move("S", 0);
		at(637).turnHead(180f, 0f);
		at(638).move("A", 22);
		at(647).setHotbarSlot(6).swapItems(5, 32);
		at(648).rightClick();
		at(649).turnHead(-171.5f, -29f);
		at(667).stopRightClick();
		at(679).setHotbarSlot(3).snapHeadAtNearestNamed("Storm");
		at(680).leftClick();
		at(681).setHotbarSlot(4);
		at(682).leap(fake("NoseThe"));
		at(683).move("S", 29).setHotbarSlot(3);
		for(int i = 685; i <= 760; i += 5) {
			at(i - 1).snapHeadAtNearestNamed("Storm");
			at(i).leftClick();
		}
		at(734).swapItems(11, 36);
		at(735).move("N", 2);
		at(738).swapItems(11, 36);
		at(800).setHotbarSlot(4).swapItems(12, 39); // put on racing helmet -> speed auto-set to 650
		at(811).leap(fake("NoseThe"));
		at(812).setHotbarSlot(5);
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
