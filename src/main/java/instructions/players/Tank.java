package instructions.players;

import commands.TAS;
import instructions.Actions;
import org.bukkit.*;
import org.bukkit.entity.Player;
import plugin.TASUtils;

public class Tank extends Actions {
	public Tank(Player player) {
		super(player);
	}

	// Tank --> Mage2
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
				TASUtils.scheduleTask(() -> storm(false), start);
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
		setHotbarSlot(3);
		move("WPJ", 12);
		at(25).leftClick(); // left click mort to start the run
		// tick 26: click start button
		at(27).move("WP", 3); // run up to door
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
		at(21).leftClick(); // kill miniboss instantly
		at(22).turnHead(0f, 3.98f).move("N", 2).setHotbarSlot(1);
		at(23).rightClick(); // etherwarp to miniboss death location
		at(24).setHotbarSlot(4);
		at(25).leap(fake("Archer"));
		at(26).turnHead(0f, 6f).setHotbarSlot(3).swapItems(4, 31);
		at(46).leftClick(); // kill miniboss instantly
		at(47).setHotbarSlot(1).turnHead(0f, 15f);
		at(48).rightClick();
		at(49).rightClick(); // aotv to pick up blessing | reposition
		at(50).turnHead(94f, -6f).move("N", 0);
		at(51).rightClick(); // etherwarp into museum
		// Blood Rush: 51 ticks

		/*
		 * ███╗   ███╗██╗   ██╗███████╗███████╗██╗   ██╗███╗   ███╗
		 * ████╗ ████║██║   ██║██╔════╝██╔════╝██║   ██║████╗ ████║
		 * ██╔████╔██║██║   ██║███████╗█████╗  ██║   ██║██╔████╔██║
		 * ██║╚██╔╝██║██║   ██║╚════██║██╔══╝  ██║   ██║██║╚██╔╝██║
		 * ██║ ╚═╝ ██║╚██████╔╝███████║███████╗╚██████╔╝██║ ╚═╝ ██║
		 * ╚═╝     ╚═╝ ╚═════╝ ╚══════╝╚══════╝ ╚═════╝ ╚═╝     ╚═╝
		 */
		at(52).turnHead(155f, 10f);
		at(53).rightClick(); // etherwarp into position
		at(54).turnHead(0f, 13f).setHotbarSlot(3);
		at(55).leftClick();
		at(56).turnHead(180f, 1.4f).setHotbarSlot(1);
		at(57).rightClick(); // etherwarp to secret
		at(58).dropItem(true); // blow up wall | cooldown for 255 ticks
		at(59).leftClick();
		at(60).turnHead(45f, -43f);
		at(61).rightClick(); // etherwarp up
		at(62).turnHead(110f, -60f);
		at(63).rightClick(); // etherwarp to wither essence
		at(64).turnHead(-147f, 20f);
		at(65).leftClick();
		at(66).turnHead(-26f, 64f);
		at(67).rightClick(); // etherwarp down
		at(68).turnHead(-10f, 2.5f);
		at(69).rightClick(); // etherwarp across
		at(70).turnHead(1f, 15f).setHotbarSlot(5);
		at(71).leftClick();
		at(72).leftClick(); // stonk wall | you cannot etherwarp through the gap
		at(73).setHotbarSlot(1);
		at(74).rightClick(); // etherwarp in
		at(75).turnHead(-90f, 0f);
		at(76).leftClick();
		at(77).turnHead(-90f, 72.5f).move("WP", 1).setHotbarSlot(5);
		at(78).leftClick();
		at(79).leftClick(); // stonk floor
		at(80).turnHead(-90f, 90f).move("N", 0).setHotbarSlot(1);
		at(81).rightClick(); // etherwarp to floor
		at(82).turnHead(-66f, 0f);
		at(83).leftClick();
		at(84).turnHead(75f, 0f);
		at(85).rightClick(); // etherwarp to wall
		at(86).turnHead(90f, 60f).move("WP", 2).setHotbarSlot(5);
		at(87).leftClick(); // break wall
		at(88).turnHead(30f, 19f).setHotbarSlot(7);
		at(89).rightClick(); // throw pearl towards market | lands in 7 ticks
		at(90).turnHead(66f, 66f).setHotbarSlot(4);
		at(91).rightClick(); // blow up floor
		at(92).turnHead(41f, 51f).move("N", 0).setHotbarSlot(1);
		at(93).rightClick(); // etherwarp down
		at(94).turnHead(165f, 0f);
		at(95).leftClick();
		at(96).turnHead(27f, 7f); // pearl lands
		at(97).rightClick(); // etherwarp into market
		// Museum: 46 ticks

		/*
		 * ███╗   ███╗ █████╗ ██████╗ ██╗  ██╗███████╗████████╗
		 * ████╗ ████║██╔══██╗██╔══██╗██║ ██╔╝██╔════╝╚══██╔══╝
		 * ██╔████╔██║███████║██████╔╝█████╔╝ █████╗     ██║
		 * ██║╚██╔╝██║██╔══██║██╔══██╗██╔═██╗ ██╔══╝     ██║
		 * ██║ ╚═╝ ██║██║  ██║██║  ██║██║  ██╗███████╗   ██║
		 * ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝   ╚═╝
		 */
		at(98).turnHead(-10f, 0f).setHotbarSlot(7);
		at(99).rightClick(); // throw pearl to reposition | lands in 9 ticks
		at(100).turnHead(54.2f, 63.9f).setHotbarSlot(4);
		at(101).rightClick(); // blow up floor
		at(102).setHotbarSlot(1);
		at(103).rightClick(); // etherwarp down
		at(104).turnHead(-72f, 5f).setHotbarSlot(0);
		at(105).rightClick();
		at(106).turnHead(180f, 20f).setHotbarSlot(7);
		at(107).leftClick();
		at(108).turnHead(-12f, -3.5f);
		at(109).rightClick(); // throw pearl | lands in 10 ticks
		at(110).turnHead(53f, -78.5f).setHotbarSlot(1);
		at(111).rightClick(); // etherwarp up
		at(112).turnHead(90f, 35f).move("WP", 5).setHotbarSlot(5);
		at(113).leftClick();
		at(114).leftClick();
		at(115).leftClick();
		at(116).leftClick(); // break through wall
		at(117).turnHead(124f, 16f).setHotbarSlot(7);
		at(118).leftClick();
		at(119).turnHead(15f, 6f); // pearl lands
		at(120).rightClick(); // throw pearl to reposition | lands in 9 ticks
		at(121).turnHead(-90f, 73f).setHotbarSlot(1);
		at(122).rightClick(); // aotv down
		at(123).turnHead(-90f, 90f).setHotbarSlot(5);
		at(124).leftClick(); // break block
		at(125).setHotbarSlot(1);
		at(126).rightClick(); // aotv down
		at(127).turnHead(66f, -8f);
		at(128).leftClick();
		at(129).turnHead(-45f, 0f).setHotbarSlot(3); // pearl lands
		at(130).leftClick(); // kill miniboss
		at(131).turnHead(-10f, 4f).setHotbarSlot(4);
		at(132).rightClick(); // blow up crypt
		at(133).setHotbarSlot(3);
		at(134).leftClick();
		at(135).turnHead(-45f, -70f).setHotbarSlot(1).move("N", 13);
		at(136).rightClick(); // etherwarp up
		at(137).turnHead(20f, -46f).setHotbarSlot(5);
		at(138).leftClick(); // break block
		at(139).setHotbarSlot(1).leftClick();
		at(140).turnHead(-71.5f, 23f);
		at(141).rightClick(); // etherwarp into hallway
		// Market: 44 ticks

		/*
		 * ██╗  ██╗ █████╗ ██╗     ██╗     ██╗    ██╗ █████╗ ██╗   ██╗
		 * ██║  ██║██╔══██╗██║     ██║     ██║    ██║██╔══██╗╚██╗ ██╔╝
		 * ███████║███████║██║     ██║     ██║ █╗ ██║███████║ ╚████╔╝
		 * ██╔══██║██╔══██║██║     ██║     ██║███╗██║██╔══██║  ╚██╔╝
		 * ██║  ██║██║  ██║███████╗███████╗╚███╔███╔╝██║  ██║   ██║
		 * ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚══════╝ ╚══╝╚══╝ ╚═╝  ╚═╝   ╚═╝
		 */
		at(142).turnHead(-97f, 0f);
		at(143).rightClick(); // etherwarp towards miniboss
		at(144).turnHead(-90f, 9f).setHotbarSlot(0);
		at(145).leftClick();
		at(146).turnHead(-99f, 8f).setHotbarSlot(1);
		at(147).rightClick(); // etherwarp towards chest
		at(148).turnHead(-57f, 6f).setHotbarSlot(7);
		at(149).rightClick(); // throw pearl for reposiiton | lands in 6 ticks
		at(150).turnHead(-180f, 40f).setHotbarSlot(5).move("WP", 4);
		at(151).leftClick();
		at(152).leftClick(); // stonk iron bars
		at(153).setHotbarSlot(1); // move for 2 more ticks
		at(154).turnHead(166f, 15f);
		at(155).leftClick();
		at(156).turnHead(-122.5f, 2f).move("N", 2); // pearl lands
		at(157).rightClick(); // etherwarp to next secret
		at(158).turnHead(-90f, 30f).setHotbarSlot(5);
		at(159).leftClick().move("WP", 4);
		at(160).leftClick();
		at(161).leftClick();
		at(162).leftClick();
		at(163).turnHead(-150f, 43f).setHotbarSlot(1);
		at(164).leftClick();
		at(165).turnHead(-75f, -55f).move("N", 10);
		at(166).rightClick(); // etherwarp up
		at(167).turnHead(-104.5f, -5f);
		at(168).rightClick(); // etherwarp to next secret
		at(169).turnHead(-90f, -81f);
		at(170).rightClick(); // etherwarp up
		at(171).turnHead(-22.5f, 17f);
		at(172).rightClick(); // etherwarp to secret
		at(173).leftClick();
		// Hallway: 32 ticks

		at(174).swapItems(1, 28).swapItems(3, 30).swapItems(4, 31).swapItems(6, 33).swapItems(7, 34).setHotbarSlot(3);
		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
	}

	public void maxor(boolean doContinue) {
		at(1).move("WP", 0); // move to aggro spot
		at(50).move("WPJ", 0);
		at(52).move("WP", 0);
		at(63).move("N", 10).turnHead(180f, -5f);
		for(int i = 65; i <= 160; i += 5) {
			at(i).leftClick();
		}
		at(161).rightClick(); // Wither Impact has a 3-tick cooldown
		at(164).rightClick();
		at(167).rightClick();
		at(170).rightClick(); // clear out wither miners
		at(171).setHotbarSlot(6);
		at(172).turnHead(180f, -20f).rightClick();
		at(199).stopRightClick();
		at(200).rightClick();
		at(207).stopRightClick();
		at(208).rightClick();
		at(215).stopRightClick(); // debuff
		at(216).setHotbarSlot(3);
		at(217).leftClick(); // hit 1, avoid insta-enrage
		at(400).leftClick(); // hit 2 to kill
		at(401).setHotbarSlot(4);
		at(402).leap(fake("Mage4"));
		at(403).turnHead(156f, 0f).setHotbarSlot(0);
		at(404).move("WP", 12);
		at(416).turnHead(-90f, 0f);
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
		at(532).leap(fake("Mage4"));
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
		at(1).leap(fake("Mage4"));
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
		at(37).leap(fake("Mage3"));

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

		at(52).leap(fake("Mage3"));
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

		at(108).leap(fake("Archer"));

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

		at(158).leap(fake("Mage1"));

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

		at(201).leap(fake("Mage1"));

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
		at(245).leap(fake("Archer"));
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
		at(502).leap(fake("Archer"));
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
		at(23).leap(fake("Archer"));
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
		at(490).leap(fake("Mage1"));
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
