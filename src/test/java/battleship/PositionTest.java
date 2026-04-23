package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Position.
 * Author: britoeabreu
 * Date: 2024-03-19 15:30
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getRow: 1
 * - getColumn: 1
 * - isValid: 4
 * - isAdjacentTo: 4
 * - isOccupied: 1
 * - isHit: 1
 * - occupy: 1
 * - shoot: 1
 * - equals: 3
 * - hashCode: 1
 * - toString: 1
 */
public class PositionTest {
	private Position position;

	@BeforeEach
	void setUp() {
		position = new Position(2, 3);
	//	position = new Position('C', 4);
	}

	@AfterEach
	void tearDown() {
		position = null;
	}

	@Test
	void constructor() {
		Position pos = new Position(1, 1);
		assertNotNull(pos, "Failed to create Position: object is null");
		assertEquals(1, pos.getRow(), "Failed to set row: expected 1 but got " + pos.getRow());
		assertEquals(1, pos.getColumn(), "Failed to set column: expected 1 but got " + pos.getColumn());
		assertFalse(pos.isOccupied(), "New position should not be occupied");
		assertFalse(pos.isHit(), "New position should not be hit");
	}

	@Test
	void getRow() {
		assertEquals(2, position.getRow(), "Failed to get row: expected 2 but got " + position.getRow());
	}

	@Test
	void getColumn() {
		assertEquals(3, position.getColumn(), "Failed to get column: expected 3 but got " + position.getColumn());
	}

	@Test
	void getClassicRow() {
		assertEquals('C', position.getClassicRow(), "Failed to get row: expected 2 but got " + position.getRow());
	}

	@Test
	void getClassicColumn() {
		assertEquals(4, position.getClassicColumn(),
				"Failed to get classic column: expected 4 but got " + position.getClassicColumn());
	}

	@Test
	void isValid1() {
		position = new Position(0, 0);
		assertTrue(position.isInside(), "Position (0,0) should be valid");
	}

	@Test
	void isValid2() {
		position = new Position(-1, 5);
		assertFalse(position.isInside(), "Position with negative row should be invalid");
	}

	@Test
	void isValid3() {
		position = new Position(5, -1);
		assertFalse(position.isInside(), "Position with negative column should be invalid");
	}

	@Test
	void isValid4() {
		position = new Position(Game.BOARD_SIZE, 5);
		assertFalse(position.isInside(), "Position with row >= BOARD_SIZE should be invalid");
	}

	@Test
	void isValid5() {
		position = new Position(5, Game.BOARD_SIZE);
		assertFalse(position.isInside(), "Position with column >= BOARD_SIZE should be invalid");
	}

	@Test
	void isAdjacentTo1() {
		Position other = new Position(2, 4);
		assertTrue(position.isAdjacentTo(other), "Failed to detect horizontally adjacent position");
	}

	@Test
	void isAdjacentTo2() {
		Position other = new Position(3, 3);
		assertTrue(position.isAdjacentTo(other), "Failed to detect vertically adjacent position");
	}

	@Test
	void isAdjacentTo3() {
		Position other = new Position(3, 4);
		assertTrue(position.isAdjacentTo(other), "Failed to detect diagonally adjacent position");
	}

	@Test
	void isAdjacentTo4() {
		Position other = new Position(4, 5);
		assertFalse(position.isAdjacentTo(other), "Non-adjacent position incorrectly identified as adjacent");
	}

	@Test
	void isAdjacentToWithNull() {
		assertThrows(NullPointerException.class, () -> position.isAdjacentTo(null),
				"isAdjacentTo should throw NullPointerException for null input");
	}

	@Test
	void isOccupied() {
		assertFalse(position.isOccupied(), "New position should not be occupied");
		position.occupy();
		assertTrue(position.isOccupied(), "Position should be occupied after occupy()");
	}

	@Test
	void isHit() {
		assertFalse(position.isHit(), "New position should not be hit");
		position.shoot();
		assertTrue(position.isHit(), "Position should be hit after shoot()");
	}

	@Test
	void equals1() {
		Position same = new Position(2, 3);
		assertTrue(position.equals(same), "Equal positions not identified as equal");
	}

	@Test
	void equals2() {
		assertFalse(position.equals(null), "Position should not equal null");
	}

	@Test
	void equals3() {
		Object other = new Object();
		assertFalse(position.equals(other), "Position should not equal non-Position object");
	}

	@Test
	void equals4() {
		Position other = new Position(2, 4);
		assertFalse(position.equals(other), "Positions with the same row but different column should not be equal");
	}

	@Test
	void equals5() {
		assertTrue(position.equals(position), "A position should be equal to itself");
	}

	@Test
	void hashCodeConsistency() {
		Position same = new Position(2, 3);
		assertEquals(position.hashCode(), same.hashCode(),
				"Hash codes not consistent for equal positions");
	}

	@Test
	void toStringFormat() {
//		String expected = "Row = C, Column = 4";
		String expected = "C4";
		assertEquals(expected, position.toString(),
				"Incorrect string representation: expected '" + expected +
						"' but got '" + position.toString() + "'");
	}

	@Test
	void constructorWithClassicCoordinates() {
		Position pos = new Position('c', 4);

		assertAll(
				() -> assertEquals(2, pos.getRow(), "Error: classic row 'c' devia corresponder a row 2."),
				() -> assertEquals(3, pos.getColumn(), "Error: classic column 4 devia corresponder a column 3."),
				() -> assertEquals('C', pos.getClassicRow(), "Error: classic row devia ser 'C'."),
				() -> assertEquals(4, pos.getClassicColumn(), "Error: classic column devia ser 4."),
				() -> assertFalse(pos.isOccupied(), "Error: nova posição não devia estar ocupada."),
				() -> assertFalse(pos.isHit(), "Error: nova posição não devia estar atingida.")
		);
	}

	@Test
	void randomPositionShouldBeInsideBoard() {
		Position random = Position.randomPosition();

		assertTrue(random.isInside(), "Error: randomPosition() devia gerar sempre posições dentro do tabuleiro.");
	}

	@Test
	void adjacentPositionsFromCenterShouldReturnEightPositions() {
		Position pos = new Position(5, 5);

		var adjacents = pos.adjacentPositions();

		assertEquals(8, adjacents.size(), "Error: uma posição central devia ter 8 adjacentes.");
		assertTrue(adjacents.contains(new Position(4, 5)), "Error: devia conter a posição a norte.");
		assertTrue(adjacents.contains(new Position(5, 6)), "Error: devia conter a posição a este.");
		assertTrue(adjacents.contains(new Position(6, 5)), "Error: devia conter a posição a sul.");
		assertTrue(adjacents.contains(new Position(5, 4)), "Error: devia conter a posição a oeste.");
		assertTrue(adjacents.contains(new Position(4, 4)), "Error: devia conter a diagonal noroeste.");
		assertTrue(adjacents.contains(new Position(4, 6)), "Error: devia conter a diagonal nordeste.");
		assertTrue(adjacents.contains(new Position(6, 4)), "Error: devia conter a diagonal sudoeste.");
		assertTrue(adjacents.contains(new Position(6, 6)), "Error: devia conter a diagonal sudeste.");
	}

	@Test
	void adjacentPositionsFromCornerShouldReturnThreePositions() {
		Position pos = new Position(0, 0);

		var adjacents = pos.adjacentPositions();

		assertEquals(3, adjacents.size(), "Error: um canto devia ter 3 adjacentes válidas.");
		assertTrue(adjacents.contains(new Position(0, 1)), "Error: devia conter a posição à direita.");
		assertTrue(adjacents.contains(new Position(1, 0)), "Error: devia conter a posição abaixo.");
		assertTrue(adjacents.contains(new Position(1, 1)), "Error: devia conter a diagonal.");
	}



}