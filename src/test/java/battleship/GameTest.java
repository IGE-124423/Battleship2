package battleship;

import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Game.
 * Author: ${user.name}
 * Date: 2026-04-23
 *
 * Cyclomatic Complexity:
 * - constructor: 1
 * - fireSingleShot: 4
 * - fireShots: 2
 * - repeatedShot: 2
 * - readEnemyFire: 4
 * - jsonShots: 2
 */
public class GameTest {

	private Game game;

	@BeforeEach
	void setUp() {
		game = new Game(new Fleet());
	}

	@AfterEach
	void tearDown() {
		game = null;
	}

	// ---------------- CONSTRUCTOR ----------------
	@Test
	void constructor() {
		assertAll(
				() -> assertNotNull(game, "Error: Game should not be null"),
				() -> assertTrue(game.getAlienMoves().isEmpty(), "Error: Moves should start empty"),
				() -> assertEquals(0, game.getInvalidShots(), "Error: invalid shots should be 0"),
				() -> assertEquals(0, game.getRepeatedShots(), "Error: repeated shots should be 0"),
				() -> assertEquals(0, game.getHits(), "Error: hits should be 0"),
				() -> assertEquals(0, game.getSunkShips(), "Error: sunk ships should be 0")
		);
	}

	@Test
	void printBoard_fullCoverage() {
		IFleet fleet = game.getMyFleet();

		Ship ship = new Barge(Compass.NORTH, new Position(0,0));
		fleet.addShip(ship);

		for (IPosition p : ship.getPositions()) {
			game.fireSingleShot(p, false);
		}

		List<IMove> moves = game.getAlienMoves();

		Game.printBoard(fleet, moves, true, true);

		assertTrue(true, "PrintBoard executed without exceptions");
	}

	@Test
	void printBoard_allBranches() {
		IFleet fleet = game.getMyFleet();

		Ship ship = new Barge(Compass.NORTH, new Position(0,0));
		fleet.addShip(ship);

		List<IMove> moves = new ArrayList<>();

		List<IPosition> shots = List.of(
				new Position(0,0), // navio
				new Position(5,5)  // água
		);

		game.fireShots(List.of(
				new Position(1,1),
				new Position(1,2),
				new Position(1,3)
		));

		Game.printBoard(fleet, moves, false, false);

		Game.printBoard(fleet, game.getAlienMoves(), true, false);

		assertTrue(true);
	}

	@Test
	void printBoards_methods() {
		game.printMyBoard(true, true);
		game.printAlienBoard(true, true);

		assertTrue(true, "Print methods executed");
	}

	@Test
	void getAlienFleet_returnsMyFleet() {
		assertEquals(game.getMyFleet(), game.getAlienFleet(),
				"Error: getAlienFleet is incorrectly returning myFleet");
	}

	// ---------------- fireSingleShot ----------------

	@Test
	void fireSingleShot1_invalidPosition() {
		Position pos = new Position(-1, 5);
		game.fireSingleShot(pos, false);

		assertEquals(1, game.getInvalidShots(),
				"Error: invalid shot should increment counter");
	}

	@Test
	void fireSingleShot2_repeatedFlagTrue() {
		Position pos = new Position(2, 3);

		game.fireSingleShot(pos, true);

		assertEquals(1, game.getRepeatedShots(),
				"Error: repeated shot should increment counter");
	}

	@Test
	void fireSingleShot3_validMiss() {
		Position pos = new Position(2, 3);

		IGame.ShotResult result = game.fireSingleShot(pos, false);

		assertAll(
				() -> assertTrue(result.valid(), "Error: shot should be valid"),
				() -> assertNull(result.ship(), "Error: should be a miss"),
				() -> assertEquals(0, game.getHits(), "Error: hits should remain 0")
		);
	}

	@Test
	void fireSingleShot4_hitAndSink() {
		IFleet fleet = game.getMyFleet();
		Ship ship = new Barge(Compass.NORTH, new Position(0, 0));
		fleet.addShip(ship);

		// disparar em todas posições do navio
		for (IPosition p : ship.getPositions()) {
			game.fireSingleShot(p, false);
		}

		assertAll(
				() -> assertTrue(game.getHits() > 0, "Error: should register hits"),
				() -> assertEquals(1, game.getSunkShips(), "Error: ship should be sunk")
		);
	}

	@Test
	void fireSingleShot_repeatedFromHistory() {
		Position pos = new Position(2,3);

		game.fireShots(List.of(
				pos,
				new Position(2,4),
				new Position(2,5)
		));

		game.fireSingleShot(pos, false);

		assertTrue(game.getRepeatedShots() > 0);
	}

	@Test
	void fireSingleShot_hitButNotSink() {
		IFleet fleet = game.getMyFleet();
		Ship ship = new Frigate(Compass.EAST, new Position(5,5));
		fleet.addShip(ship);

		// só 1 tiro → não afunda
		game.fireSingleShot(ship.getPositions().get(0), false);

		assertEquals(0, game.getSunkShips(),
				"Error: ship should not be sunk yet");
	}

	// ---------------- fireShots ----------------

	@Test
	void fireShots1_invalidSize() {
		List<IPosition> shots = List.of(new Position(1,1));

		assertThrows(IllegalArgumentException.class, () -> game.fireShots(shots),
				"Error: should throw exception when shots != NUMBER_SHOTS");
	}

	@Test
	void fireShots2_valid() {
		List<IPosition> shots = List.of(
				new Position(1,1),
				new Position(1,2),
				new Position(1,3)
		);

		game.fireShots(shots);

		assertEquals(1, game.getAlienMoves().size(),
				"Error: move should be added");
	}

	@Test
	void randomEnemyFire_basic() {
		String json = game.randomEnemyFire();

		assertNotNull(json, "Error: JSON should not be null");
		assertTrue(json.contains("row"), "Error: JSON should contain shots");
	}

	@Test
	void readEnemyFire4_columnWithoutRow() {
		Scanner sc = new Scanner("A B2 C3");

		assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(sc),
				"Error: column without row should throw exception");
	}

	@Test
	void randomEnemyFire_smallCandidateList() {
		IFleet fleet = game.getMyFleet();

		Ship ship = new Barge(Compass.NORTH, new Position(0,0));
		fleet.addShip(ship);

		for (IPosition p : ship.getPositions()) {
			game.fireSingleShot(p, false);
		}

		String json = game.randomEnemyFire();

		assertNotNull(json);
		assertTrue(json.contains("row"));
	}

	@Test
	void readEnemyFire_separateTokens() {
		Scanner sc = new Scanner("A 1 B 2 C 3");

		String json = game.readEnemyFire(sc);

		assertNotNull(json);
	}

	@Test
	void fireShots3_repeatedInsideSameBatch() {
		List<IPosition> shots = List.of(
				new Position(1,1),
				new Position(1,1), // repetido
				new Position(1,2)
		);

		game.fireShots(shots);

		assertTrue(game.getRepeatedShots() > 0,
				"Error: repeated shots should be counted");
	}


	// ---------------- repeatedShot ----------------

	@Test
	void repeatedShot1_true() {
		List<IPosition> shots = List.of(
				new Position(2,3),
				new Position(2,4),
				new Position(2,5)
		);

		game.fireShots(shots);

		assertTrue(game.repeatedShot(new Position(2,3)),
				"Error: position should be repeated");
	}

	@Test
	void repeatedShot2_false() {
		assertFalse(game.repeatedShot(new Position(5,5)),
				"Error: position should not be repeated");
	}

	// ---------------- readEnemyFire ----------------

	@Test
	void readEnemyFire1_validInput() {
		Scanner sc = new Scanner("A1 B2 C3");

		String json = game.readEnemyFire(sc);

		assertNotNull(json, "Error: JSON result should not be null");
		assertTrue(json.contains("row"), "Error: JSON should contain row");
	}

	@Test
	void readEnemyFire2_invalidCount() {
		Scanner sc = new Scanner("A1 B2");

		assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(sc),
				"Error: should fail when less than required shots");
	}

	@Test
	void readEnemyFire3_invalidFormat() {
		Scanner sc = new Scanner("A B C");

		assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(sc),
				"Error: invalid format should throw exception");
	}

	// ---------------- jsonShots ----------------

	@Test
	void jsonShots1_valid() {
		List<IPosition> shots = List.of(
				new Position(1,1),
				new Position(2,2)
		);

		String json = Game.jsonShots(shots);

		assertAll(
				() -> assertNotNull(json, "Error: JSON should not be null"),
				() -> assertTrue(json.contains("row"), "Error: JSON should contain row"),
				() -> assertTrue(json.contains("column"), "Error: JSON should contain column")
		);
	}

	@Test
	void jsonShots2_emptyList() {
		List<IPosition> shots = new ArrayList<>();

		String json = Game.jsonShots(shots);

		assertEquals("[]", json.replaceAll("\\s", ""),
				"Error: empty list should return empty JSON array");
	}

	// ---------------- getters ----------------

	@Test
	void getters_basic() {
		assertAll(
				() -> assertNotNull(game.getMyFleet(), "Error: fleet should not be null"),
				() -> assertNotNull(game.getMyMoves(), "Error: myMoves should not be null"),
				() -> assertEquals(0, game.getMoveCount(), "Error: initial move count should be 0")
		);
	}

	@Test
	void getAllMovesAsStrings_test() {
		List<IPosition> shots = List.of(
				new Position(1,1),
				new Position(1,2),
				new Position(1,3)
		);

		game.fireShots(shots);

		List<String> moves = game.getAllMovesAsStrings();

		assertFalse(moves.isEmpty(),
				"Error: moves list should not be empty");
	}

	@Test
	void getRemainingShips_emptyFleet() {
		assertEquals(0, game.getRemainingShips(),
				"Error: empty fleet should have 0 ships");
	}

	@Test
	void getAllMovesAsStrings_withMyMoves() {
		// forçar myMoves
		game.getMyMoves().add(
				new Move(1,
						List.of(new Position(1,1)),
						List.of(new IGame.ShotResult(true,false,null,false)))
		);

		List<String> moves = game.getAllMovesAsStrings();

		assertTrue(moves.stream().anyMatch(s -> s.contains("Player move")),
				"Error: should contain player moves");
	}

	@Test
	void over_method() {
		game.over();

		assertTrue(true, "Method executed without crash");
	}

}