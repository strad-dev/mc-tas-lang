package instructions.players;

import commands.TAS;
import instructions.Actions;
import org.bukkit.*;
import org.bukkit.entity.Player;
import plugin.TASUtils;

public class Berserk extends Actions {
	private static final int LEAP_ARCH = 0;
	private static final int LEAP_HEAL = 44;
	private static final int LEAP_MAGE = 8;
	private static final int LEAP_TANK = 36;

	public Berserk(Player player) {
		super(player);
	}

	// Berserk --> RedFish2471
	public void startPosition(String section) {
		switch(section) {
			case "all", "clear" -> startAt(-120.5, 71, -183.5, 0f, 0f);
			case "maxor", "boss" -> startAt(73.5, 221, 14.5, 0f, 0f);
			case "storm" -> startAt(100.980, 169, 53.831, -90f, 0f);
			case "goldor" -> startAt(61.499, 132, 138.940, 0f, 0f);
			case "necron" -> startAt(54.6, 105, 27.3, 180f, -6f);
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
				setHotbarSlot(5);
				TASUtils.scheduleTask(() -> goldor(false), start);
			}
			case "necron" -> {
				swapItems(1, 28);
				swapItems(3, 30);
				swapItems(6, 33);
				swapItems(7, 34);
				useDpsLoadout();
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
		swapItems(4, 31);
		setHotbarSlot(1);
		move("N");
		at(1).rightClick(0f, 23f);
		at(2).rightClick();
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
		at(15).rightClick(-2f, -1.5f); // etherwarp into fairy
		at(16).rightClick(-90f, 13f); // etherwarp into wizard
		at(17).setHotbarSlot(0).rightClick(); // kill 1st group of mobs
		at(18).setHotbarSlot(7).rightClick(11f, 9f); // throw reposition pearl, lands in 4 ticks
		at(19).setHotbarSlot(1).rightClick(90f, -73f); // etherwarp up
		at(20).rightClick(0f, -39f); // etherwarp to chest
		at(21).rightClick(0f, 0f); // wizard 1/4
		at(22).setHotbarSlot(5).turnHead(45f, 75f).leftClick(); // break floor
		at(23).setHotbarSlot(1).rightClick(); // etherwarp down
		at(24).rightClick(45f, 15f); // pick up crystal
		at(25).rightClick(-160f, -24f); // etherwarp up
		at(26).setHotbarSlot(0).rightClick(-80f, 0f); // kill 2nd group of mobs
		at(27).rightClick(145f, 0f); // kill 3rd group of mobs
		at(28).rightClick(173f, 0f); // continue killing mobs down the hallway
		at(29).rightClick(-175f, 0f);
		at(30).rightClick(-170f, 0f);
		at(31).rightClick();
		at(32).setHotbarSlot(1).rightClick(180f, 9f); // etherwarp to dig spot for bat
		at(33).setHotbarSlot(7).rightClick(158f, 10f); // throw reposition pearl, lands in 4 ticks
		at(34).setHotbarSlot(5).turnHead(180f, 90f).leftClick();
		at(35).leftClick(); // dig down to bat
		at(36).setHotbarSlot(0).rightClick(); // wizard 2/4
		at(37).rightClick(160f, 25f); // kill last group of mobs
		at(38).setHotbarSlot(7).rightClick(-90f, -5f); // throw pearl into well, lands 14 ticks
		at(39).setHotbarSlot(1).rightClick(120f, 8f); // etherwarp to wall
		at(40).setHotbarSlot(4).rightClick(180f, -65f); // blow up wall
		at(41).setHotbarSlot(5).leftClick(); // break block
		at(42).setHotbarSlot(1).rightClick(); // etherwarp up
		at(43).rightClick(-170f, 15f); // wizard 3/4
		at(44).rightClick(-90f, -70f); // etherwarp up
		at(45).rightClick(-84f, -36.5f); // etherwarp towards wizard
		at(46).rightClick(-102f, 30f); // etherwarp to wizard
		at(47).turnHead(-90f, 0f).leftClick(); // give crystal to wizard
		at(48).rightClick(90f, 1.1f).rightClick(); // etherwarp to secret
		at(49).setHotbarSlot(4).rightClick(41f, 1.1f); // blow up next wall
		at(50).setHotbarSlot(1).rightClick(); // etherwarp towards secret
		at(51).rightClick(-45f, 15f); // wizard 4/4
		// Wizard: 35 ticks (-32 | -16)

		/*
		 * ██╗    ██╗███████╗██╗     ██╗
		 * ██║    ██║██╔════╝██║     ██║
		 * ██║ █╗ ██║█████╗  ██║     ██║
		 * ██║███╗██║██╔══╝  ██║     ██║
		 * ╚███╔███╔╝███████╗███████╗███████╗
		 *  ╚══╝╚══╝ ╚══════╝╚══════╝╚══════╝
		 */
		at(52).rightClick(-124f, -79.3f); // etherwarp up
		at(53).rightClick(60f, -30f); // well 1/7
		at(54).rightClick(-65f, -17f); // reposition for next secret
		at(55).rightClick(85f, 17.5f); // well 2/7
		at(56).rightClick(-96.3f, 3.1f); // etherwarp out
		at(57).rightClick(-158f, 24f); // etherwarp down
		at(58).rightClick(-93f, 1.8f); // etherwarp to secret
		at(59).setHotbarSlot(5).turnHead(-107f, 31f).leftClick(); // break wall
		at(60).rightClick(); // well 3/7
		at(61).setHotbarSlot(7).rightClick(-7f, 52f); // throw 2nd reposition pearl, lands in 12 ticks
		at(62).rightClick(90f, 5f); // throw 1st reposition pearl, lands in 6 ticks
		at(63).setHotbarSlot(1).rightClick(160f, -2f); // reposition etherwarp
		at(64).rightClick(-97.5f, 10.5f); // etherwarp towards secrets
		at(65).rightClick(180f, -75f); // etherwarp up
		at(66).rightClick(-115f, 25f); // well 4/7
		at(67).rightClick(56f, 15f); // well 5/7
		at(68).rightClick(39f, 14f); // reposition etherwarp
		at(69).rightClick(-19f, 4f); // etherwarp to secret
		at(70).setHotbarSlot(5).turnHead(-90f, -50f).leftClick();
		at(71).leftClick(); // break wall
		at(72).rightClick(); // well 6/7
		at(73).setHotbarSlot(1).rightClick(90f, 70f); // etherwarp to floor
		at(74).setHotbarSlot(3).turnHead(-171f, 0f).leftClick(); // kill miniboss
		at(75).setHotbarSlot(1).rightClick(-3.7f, 0.65f); // etherwarp to next secret
		at(76).setHotbarSlot(7).rightClick(135f, 10f); // throw reposition pearl, lands in 4 ticks
		at(77).setHotbarSlot(1).rightClick(180f, 67f); // etherwarp down
		at(78).setHotbarSlot(5).turnHead(180f, 90f).leftClick(); // break block
		at(79).setHotbarSlot(1).rightClick(); // well 7/7
		at(80).rightClick(87f, 3.5f); // begin ice fill
		// Well: 29 ticks (-24 | -40)

		/*
		 * ██╗ ██████╗███████╗    ███████╗██╗██╗     ██╗
		 * ██║██╔════╝██╔════╝    ██╔════╝██║██║     ██║
		 * ██║██║     █████╗      █████╗  ██║██║     ██║
		 * ██║██║     ██╔══╝      ██╔══╝  ██║██║     ██║
		 * ██║╚██████╗███████╗    ██║     ██║███████╗███████╗
		 * ╚═╝ ╚═════╝╚══════╝    ╚═╝     ╚═╝╚══════╝╚══════╝
		 */
		at(81).rightClick(90f, 60f);
		at(82).rightClick(0f, 60f);
		at(83).rightClick(90f, 60f);
		at(84).rightClick(180f, 60f);
		at(85).rightClick(90f, 60f); // layer 1 complete

		at(86).rightClick(90f, 10f);
		at(87).rightClick(0f, 60f);
		at(88).rightClick();
		at(89).rightClick(90f, 60f);
		at(90).rightClick();
		at(91).rightClick();
		at(92).rightClick();
		at(93).rightClick(180f, 60f);
		at(94).rightClick(-90f, 60f);
		at(95).rightClick();
		at(96).rightClick(180f, 60f);
		at(97).rightClick();
		at(98).rightClick(-90f, 60f);
		at(99).rightClick();
		at(100).rightClick(180f, 60f);
		at(101).rightClick(90f, 60f);
		at(102).rightClick();
		at(103).rightClick();
		at(104).rightClick();
		at(105).rightClick(0f, 60f);
		at(106).rightClick();
		at(107).rightClick(90f, 60f); // layer 2 complete

		at(108).rightClick(90f, 10f);
		at(109).rightClick(0f, 60f);
		at(110).rightClick();
		at(111).rightClick();
		at(112).rightClick(90f, 60f);
		at(113).rightClick(180f, 60f);
		at(114).rightClick();
		at(115).rightClick(90f, 60f);
		at(116).rightClick(0f, 60f);
		at(117).rightClick();
		at(118).rightClick(90f, 60f);
		at(119).rightClick();
		at(120).rightClick();
		at(121).rightClick();
		at(122).rightClick(180f, 60f);
		at(123).rightClick(-90f, 60f);
		at(124).rightClick();
		at(125).rightClick(180f, 60f);
		at(126).rightClick(90f, 60f);
		at(127).rightClick(180f, 60f);
		at(128).rightClick();
		at(129).rightClick(-90f, 60f);
		at(130).rightClick(180f, 60f);
		at(131).rightClick(-90f, 60f);
		at(132).rightClick();
		at(133).rightClick(0f, 60f);
		at(134).rightClick(-90f, 60f);
		at(135).rightClick();
		at(136).rightClick(180f, 60f);
		at(137).rightClick();
		at(138).rightClick(90f, 60f);
		at(139).rightClick();
		at(140).rightClick();
		at(141).rightClick();
		at(142).rightClick();
		at(143).rightClick(0f, 60f);
		at(144).rightClick(90f, 60f);
		at(145).rightClick(0f, 60f);
		at(146).rightClick();
		at(147).rightClick(90f, 60f); // finish layer 3

		at(168).rightClick(70f, -38f);
		at(169).rightClick(110f, -38f);
		// Ice Fill: 88 ticks (-39 | -79)
		at(170).swapItems(1, 28).swapItems(3, 30).swapItems(4, 31).swapItems(6, 33).swapItems(7, 34).swapItems(11, 36).useSpeedLoadout().setHotbarSlot(5); // swap out boss items
		if(doContinue) TASUtils.scheduleTask(() -> maxor(true), TAS.CLEAR_TICKS);
	}

	public void maxor(boolean doContinue) {
		turnHead(13f, 0f);
		move("WP");
		at(10).move("WN");
		at(23).move("WP");
		at(45).rightClick();
		at(46).setHotbarSlot(4).rightClick();
		at(47).clickGui(LEAP_HEAL);
		at(80).move("WN");
		at(91).move("WP");
		at(114).swapItems(11, 36);
		at(120).rightClick();
		at(160).clickGui(LEAP_HEAL);
		at(161).turnHead(-91.5f, 36f).move("WP").setHotbarSlot(5);
		at(170).leftClick();
		at(171).setHotbarSlot(0).rightClick(-91.5f, 42f);
		at(172).rightClick();
		at(173).rightClick();
		at(174).setHotbarSlot(5).leftClick();
		at(175).leftClick();
		at(177).move("").setHotbarSlot(0); // stop moving to fall into hole
		at(181).turnHead(-60f, 0f).move("WP"); // continue moving here, after players leap
		at(230).turnHead(-90f, 0f);
		at(240).useDpsLoadout();
		if(doContinue) TASUtils.scheduleTask(() -> storm(true), TAS.MAXOR_TICKS);
	}

	public void storm(boolean doContinue) {
		rightClick();
		at(1).rightClick();
		at(2).setHotbarSlot(3);
		for(int i = 3; i < 445; i += 5) {
			at(i).snapHeadToNearestEnemy().leftClick();
		}
		at(444).setHotbarSlot(4).rightClick();
		at(445).clickGui(LEAP_MAGE);
		at(446).setHotbarSlot(3);
		for(int i = 447; i <= 520; i += 5) {
			at(i).snapHeadToNearestEnemy().leftClick();
		}
		at(521).setHotbarSlot(5).useSpeedLoadout();
		at(526).turnHead(9.6f, 75f).setHotbarSlot(1).move("WP");
		at(531).rightClick();
		at(532).turnHead(9.6f, 0f).setHotbarSlot(5);
		at(552).move("WPJ");
		at(565).turnHead(0f, 90f).move("WP");
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
		at(583).leftClick();
		at(584).leftClick();
		at(585).leftClick();
		at(586).leftClick();
		at(587).leftClick();
		at(588).leftClick();
		at(589).leftClick();
		at(590).leftClick();
		at(591).leftClick();
		at(592).leftClick();
		at(593).move("S");
		at(605).move("AS");
		at(606).move("");
		at(620).move("ASN");
		at(635).move("N");
		at(646).move("");
		at(702).turnHead(5f, 35f).move("WP");
		at(710).turnHead(0f, 35f).leftClick();
		at(711).leftClick();
		at(712).leftClick();
		at(713).leftClick();
		at(714).leftClick().move("W");
		at(715).leftClick();
		at(718).turnHead(92.5f, 0f).move("WP");
		at(730).setHotbarSlot(1).turnHead(78f, 75f);
		at(733).rightClick();
		at(734).setHotbarSlot(5).turnHead(78f, -90f);
		at(761).turnHead(0f, 0f);
		at(771).turnHead(85f, 0f);
		at(790).turnHead(60f, 0f).move("S");
		at(796).move("").turnHead(0f, 0f);
		if(doContinue) TASUtils.scheduleTask(() -> goldor(true), TAS.STORM_TICKS);
	}

	public void goldor(boolean doContinue) {
		/*
		 * ███████╗███████╗██████╗
		 * ██╔════╝██╔════╝╚════██╗
		 * █████╗  █████╗   █████╔╝
		 * ██╔══╝  ██╔══╝  ██╔═══╝
		 * ███████╗███████╗███████╗
		 * ╚══════╝╚══════╝╚══════╝
		 */
		rightClick(); // complete i2
		at(1).turnHead(-148f, 0f).move("WP");
		at(25).turnHead(180f, 15f);
		at(27).turnHead(65f, 0f).move("WP");
		at(30).move("WPJ");
		at(40).move("");

		/*
		 * ██████╗
		 * ╚════██╗
		 *  █████╔╝
		 * ██╔═══╝
		 * ███████╗
		 * ╚══════╝
		 */
		at(42).turnHead(-155f, 0f).move("WP");
		at(45).move("S");
		at(47).turnHead(-155f, -90f);
		at(51).move("");
		at(56).rightClick(90f, -72f);
		at(57).clickGui(TERM_SOLUTION_2); // s2 2

		at(58).swapItems(9, 39).setHotbarSlot(4).rightClick(0f, 0f);
		at(59).clickGui(LEAP_ARCH);

		/*
		 *  ██████╗ ██████╗ ██████╗ ███████╗
		 * ██╔════╝██╔═══██╗██╔══██╗██╔════╝
		 * ██║     ██║   ██║██████╔╝█████╗
		 * ██║     ██║   ██║██╔══██╗██╔══╝
		 * ╚██████╗╚██████╔╝██║  ██║███████╗
		 *  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
		 */
		at(60).turnHead(180f, 0f).move("WP");
		at(61).swapItems(9, 39).move("WP");
		at(65).turnHead(-90f, 75f);
		at(68).setHotbarSlot(1).rightClick();
		at(69).turnHead(-90f, 40f).setHotbarSlot(5);
		at(71).move("WDP");
		at(73).move("WP");
		at(85).leftClick();
		at(86).turnHead(-90f, 75f).setHotbarSlot(1).rightClick();
		at(87).setHotbarSlot(5);
		at(100).move("");
		at(101).swapItems(10, 39);
		at(121).swapItems(10, 39);

		/*
		 * ██╗  ██╗
		 * ██║  ██║
		 * ███████║
		 * ╚════██║
		 *      ██║
		 *      ╚═╝
		 */
		at(141).turnHead(-12.5f, 0f).move("WP");
		at(152).turnHead(0f, 35f).leftClick();
		at(153).leftClick();
		at(154).leftClick();
		at(155).leftClick();
		at(156).move("").turnHead(169f, 0f);
		at(157).useDpsLoadout();

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
}
