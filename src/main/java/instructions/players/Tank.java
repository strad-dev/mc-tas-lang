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
			case "storm" -> startAt(73.5, 165, 53.5, -90f, 0f);
			case "goldor" -> startAt(-0.240, 120, 76.775, 72f, -25f);
			case "necron" -> startAt(54.524, 64, 100.707, 180f, -6f);
			case "witherking" -> startAt(22.3, 6, 94.452, 67.3f, 29.7f);
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
				TASUtils.scheduleTask(() -> storm(false), start);
				TASUtils.scheduleTask(() -> startAt(46.794, 169, 53.835, -90f, 0f), start + 1);
			}
			case "goldor" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				swapItems(12, 39);
				setHotbarSlot(4);
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
		at(258).turnHead(35f, 45f); // storm tick 8
		at(262).move("");
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
		at(532).leap(fake("NoseThe"));
		at(533).turnHead(73f, 0f).setHotbarSlot(5).swapItems(12, 39); // swap to black cat racing helmet -> speed auto-set to 650
		at(546).move("WP", 0);
		at(554).move("WPJ", 0);
		at(556).move("WP", 0); // move to i3 dig spot
		at(573).turnHead(90f, 45f);
		at(574).leftClick(); // 19/20
		at(575).leftClick(); // 18/20
		at(576).leftClick(); // 17/20
		at(577).leftClick(); // 16/20
		at(578).leftClick(); // 15/20
		at(579).leftClick(); // 14/20
		at(580).leftClick(); // 13/20
		at(581).leftClick(); // 12/20
		at(582).leftClick(); // 11/20
		at(583).leftClick(); // 10/20
		at(584).leftClick(); // 10/20 regen 1
		at(585).leftClick(); // 9/20
		at(586).leftClick(); // 8/20
		at(587).leftClick(); // 7/20
		at(588).leftClick(); // 6/20
		at(589).leftClick(); // 5/20
		at(590).leftClick(); // 4/20
		at(591).turnHead(90f, 90f);
		at(592).leftClick(); // 3/20
		at(593).leftClick(); // 2/20
		at(594).leftClick(); // 2/20 regen 1
		at(595).leftClick(); // 1/20
		at(596).leftClick(); // 0/20
		at(604).leftClick();
		at(614).leftClick();
		at(624).leftClick();
		at(634).leftClick();
		at(644).leftClick();
		at(654).leftClick();
		at(664).leftClick();
		at(674).leftClick();
		at(684).leftClick();
		at(694).leftClick();
		at(704).leftClick();
		at(714).leftClick();
		at(724).leftClick();
		at(734).leftClick(); // move for 22 ticks before landing
		at(744).leftClick(); // move for 22 ticks before landing
		at(745).turnHead(72f, -25f).setHotbarSlot(4);
		at(746).move("WP", 24);
		at(776).move("WP", 4); // move to in front of dev3
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
		rightClick(); // complete i2
		at(1).leap(fake("NoseThe"));
		at(2).setHotbarSlot(5).move("WAP", 0); // get out of the way
		at(3).move("N", 0).turnHead(-112f, -4f); // prevent fall
		at(4).rightClick();
		at(5).rightClick();
		at(6).rightClick(); // finish i1

		at(7).turnHead(-5.5f, 80f).setHotbarSlot(1);
		at(8).rightClick(); // bonzo
		at(9).turnHead(-155f, 0f).setHotbarSlot(5);
		at(10).move("WP", 4);
		at(21).turnHead(-94f, 25f);
		at(22).rightClick(); // s1 2
		// tick 23: terminal completes

		at(24).turnHead(-155f, 45f);
		at(25).move("WP", 6);
		at(31).turnHead(-101f, 69f);
		at(34).rightClick(); // s1 1
		// tick 35: terminal completes

		at(36).setHotbarSlot(4);
		at(37).leap(fake("RedFish2471"));

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 * ██╔═══╝
		 * ███████╗
		 * ╚══════╝
		 */
		at(46).rightClick(); // s2 1
		// tick 47: terminal completes

		at(52).leap(fake("RedFish2471"));
		at(53).move("WP", 0).setHotbarSlot(1);
		at(55).rightClick();
		at(56).turnHead(58f, 0f).setHotbarSlot(5);
		at(69).turnHead(90f, 0f);
		at(70).move("WAP", 0);
		at(71).move("WP", 3);
		at(74).turnHead(-3f, 22.5f);
		at(75).rightClick(); // s2 4
		// tick 76: terminal completes

		at(77).turnHead(90f, 0f).setHotbarSlot(1);
		at(78).move("W", 4);
		at(82).turnHead(-20f, 0);
		at(99).rightClick();
		at(100).turnHead(160f, 25f).setHotbarSlot(4);
		at(101).move("WP", 6);
		at(106).swapItems(9, 39); // equip mask for preleap
		at(107).rightClick(); // s2 bottom

		at(108).leap(fake("akc0303"));

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 *  ╚═══██╗
		 * ██████╔╝
		 * ╚═════╝
		 */
		// wait for better timing, going too early results in worse bonzos
		at(111).move("W", 2).setHotbarSlot(5);
		at(119).move("WP", 9);
		// tick 120: death tick
		at(121).swapItems(9, 39); // racing helmet back on -> speed auto-set to 650
		at(122).turnHead(180f, -20f);
		at(123).dropItem(true);
		at(129).turnHead(108f, -8f);
		at(130).rightClick(); // s3 left

		at(131).turnHead(112.5f, 80f).setHotbarSlot(1);
		at(132).rightClick();
		at(133).turnHead(-90f, 78f).setHotbarSlot(4);
		at(134).move("WP", 8);
		at(142).rightClick(); // s3 right

		at(158).leap(fake("Beethoven"));

		/*
		 * ██╗  ██╗
		 * ██║  ██║
		 * ███████║
		 * ╚════██║
		 *      ██║
		 *      ╚═╝
		 */

		at(159).move("WP", 0).setHotbarSlot(5);
		at(168).turnHead(-5f, 10f).move("WAP", 0);
		at(169).move("WP", 2);
		at(172).rightClick(); // s4 4
		// tick 173: terminal completes

		at(174).move("WP", 8).turnHead(-90f, 90f).setHotbarSlot(7);
		at(190).rightClick();
		at(191).turnHead(-90f, -43f).setHotbarSlot(4);
		at(200).rightClick(); // s4 top FUCK THIS STUPID FUCKING LEVER

		at(201).leap(fake("Beethoven"));

		/*
		 * ███████╗██╗ ██████╗ ██╗  ██╗████████╗
		 * ██╔════╝██║██╔════╝ ██║  ██║╚══██╔══╝
		 * █████╗  ██║██║  ███╗███████║   ██║
		 * ██╔══╝  ██║██║   ██║██╔══██║   ██║
		 * ██║     ██║╚██████╔╝██║  ██║   ██║
		 * ╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═╝   ╚═╝
		 */
		at(202).turnHead(180f, 0f).setHotbarSlot(3);
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
		at(504).turnHead(65f, 0f).setHotbarSlot(5);
		at(505).move("WP", 0);
		at(520).move("WPJ", 0);
		at(522).move("WP", 10);
		at(532).turnHead(90f, 0f);
		at(533).swapItems(3, 30);
		if(doContinue) TASUtils.scheduleTask(this::witherKing, TAS.NECRON_TICKS);
	}

	public void witherKing() {
		rightClick();
		at(1).setHotbarSlot(4);
		at(23).leap(fake("akc0303"));
		at(24).turnHead(-105f, -10f).setHotbarSlot(8);
		at(25).rightClick();
		at(26).turnHead(-6.25f, 0f);
		at(27).move("WP", 0);
		at(60).move("WPJ", 0);
		at(62).move("WP", 0);
		at(71).move("WPJ", 0);
		at(73).move("WP", 0);
		at(83).move("WPJ", 2);
		at(85).swapItems(5, 32).swapItems(13, 39);
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
}
