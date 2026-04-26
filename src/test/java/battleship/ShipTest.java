package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for Ship.
 * Author: ${user.name}
 * Date: ${current_date}
 * Time: ${current_time}
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getCategory: 1
 * - getSize: 1
 * - getBearing: 1
 * - getPositions: 1
 * - stillFloating: 2
 * - shoot: 2
 * - occupies: 2
 * - tooCloseTo (IShip): 2
 * - tooCloseTo (IPosition): 2
 * - getTopMostPos: 2
 * - getBottomMostPos: 2
 * - getLeftMostPos: 2
 * - getRightMostPos: 2
 */
public class ShipTest {

    private Ship ship;

    @BeforeEach
    void setUp() {
        // Since Ship is abstract, instantiate it with a concrete subclass (e.g., Barge)
        ship = new Barge(Compass.NORTH, new Position(5, 5));
    }

    @AfterEach
    void tearDown() {
        ship = null;
    }

    /**
     * Test for the constructor.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testConstructor() {
        assertNotNull(ship, "Error: Ship instance should not be null.");
        assertEquals("Barca", ship.getCategory(), "Error: Ship category is incorrect.");
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing is incorrect.");
        assertEquals(1, ship.getSize(), "Error: Ship size is incorrect.");
        assertFalse(ship.getPositions().isEmpty(), "Error: Ship positions should not be empty.");
    }

    /**
     * Test for the getCategory method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetCategory() {
        assertEquals("Barca", ship.getCategory(), "Error: Ship category should be 'Barca'.");
    }

    /**
     * Test for the getSize method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetSize() {
        assertEquals(1, ship.getSize(), "Error: Ship size should be 1.");
    }

    /**
     * Test for the getBearing method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetBearing() {
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing should be NORTH.");
    }

    /**
     * Test for the getPositions method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetPositions() {
        List<IPosition> positions = ship.getPositions();
        assertNotNull(positions, "Error: Ship positions should not be null.");
        assertEquals(1, positions.size(), "Error: Ship should have exactly one position.");
        assertEquals(5, positions.get(0).getRow(), "Error: Position's row should be 5.");
        assertEquals(5, positions.get(0).getColumn(), "Error: Position's column should be 5.");
    }

    /**
     * Test for the stillFloating method (all positions intact).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testStillFloating1() {
        assertTrue(ship.stillFloating(), "Error: Ship should still be floating.");
    }

    /**
     * Test for the stillFloating method (all positions hit).
     */
    @Test
    void testStillFloating2() {
        ship.getPositions().get(0).shoot();
        assertFalse(ship.stillFloating(), "Error: Ship should no longer be floating after being hit.");
    }

    /**
     * Test for the shoot method (valid position).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testShoot1() {
        Position target = new Position(5, 5);
        ship.shoot(target);
        assertTrue(ship.getPositions().get(0).isHit(), "Error: Position should be marked as hit.");
    }

    /**
     * Test for the shoot method (invalid position).
     */
    @Test
    void testShoot2() {
        Position target = new Position(0, 0);
        ship.shoot(target); // No exception expected
        assertFalse(ship.getPositions().get(0).isHit(), "Error: Position should not be marked as hit for an invalid target.");
    }

    /**
     * Test for the occupies method (position occupied).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testOccupies1() {
        Position pos = new Position(5, 5);
        assertTrue(ship.occupies(pos), "Error: Ship should occupy position (5, 5).");
    }

    /**
     * Test for the occupies method (position not occupied).
     */
    @Test
    void testOccupies2() {
        Position pos = new Position(1, 1);
        assertFalse(ship.occupies(pos), "Error: Ship should not occupy position (1, 1).");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships too close).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testTooCloseToShip1() {
        Ship nearbyShip = new Barge(Compass.NORTH, new Position(5, 6));
        assertTrue(ship.tooCloseTo(nearbyShip), "Error: Ships should be too close.");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships not close).
     */
    @Test
    void testTooCloseToShip2() {
        Ship farShip = new Barge(Compass.NORTH, new Position(10, 10));
        assertFalse(ship.tooCloseTo(farShip), "Error: Ships should not be too close.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions adjacent).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testTooCloseToPosition1() {
        Position pos = new Position(5, 6); // Adjacent position
        assertTrue(ship.tooCloseTo(pos), "Error: Ship should be too close to the given position.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions not adjacent).
     */
    @Test
    void testTooCloseToPosition2() {
        Position pos = new Position(7, 7); // Non-adjacent position
        assertFalse(ship.tooCloseTo(pos), "Error: Ship should not be too close to the given position.");
    }

    /**
     * Test for the getTopMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetTopMostPos() {
        assertEquals(5, ship.getTopMostPos(), "Error: The topmost position should be 5.");
    }

    /**
     * Test for the getBottomMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetBottomMostPos() {
        assertEquals(5, ship.getBottomMostPos(), "Error: The bottommost position should be 5.");
    }

    /**
     * Test for the getLeftMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetLeftMostPos() {
        assertEquals(5, ship.getLeftMostPos(), "Error: The leftmost position should be 5.");
    }

    /**
     * Test for the getRightMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetRightMostPos() {
        assertEquals(5, ship.getRightMostPos(), "Error: The rightmost position should be 5.");
    }

    /**
     * Test for the shoot method (null position).
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("Tests Assert Error when position given is null for the method shoot")
    void testShoot3() {
        assertThrows(AssertionError.class, () -> ship.shoot(null), "Error: method shoot should not accept a position outside the board for argument");
    }

    /**
     * Test for the shoot method (!pos.isInside()).
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("Tests Assert Error when position given is outside the board for the method shoot")
    void testShoot4() {
        IPosition invalid = new Position(-1,-1);
        assertThrows(AssertionError.class, () -> ship.shoot(invalid), "Error: method shoot should not accept an  argument");
    }

    /**
     * Test for the stillFloating method (partial positions hit).
     */
    @Test
    @DisplayName("Tests the condition for when a ship is partially hit")
    void testStillFloating3() {
        Ship caravela = Ship.buildShip("caravela", Compass.NORTH, new Position(5, 5));
        caravela.getPositions().get(0).shoot();
        assertTrue(caravela.stillFloating(), "Error: Ship should be floating when only one part is hit.");
    }

    /**
     * Test for the getTopMostPos and getBottomMostPos method (Galeao).
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("Forces the for cicle in getTopMostPos and getBottomMostPos")
    void testTopBottomGalleon() {
        Ship galeao = Ship.buildShip("galeao", Compass.NORTH, new Position(5, 5));

        assertEquals(5, galeao.getTopMostPos(), "Error: Galeao's top should be in row 5.");
        assertEquals(7, galeao.getBottomMostPos(), "Error: Galeao's bottom should be in row 7.");
    }

    /**
     * Test for the getLeftMostPos and getRightMostPos method (Galeao).
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("Forces the for cicle in getLeftMostPos and getRightMostPos")
    void testLeftRightGalleon() {
        Ship galeao = Ship.buildShip("galeao", Compass.EAST, new Position(5, 5));

        assertEquals(3, galeao.getLeftMostPos(), "Error: Galeao's left should be in column 5.");
        assertEquals(5, galeao.getRightMostPos(), "Error: Galeao's right should be in column 8.");
    }

    /**
     * Test for the getAdjacentPositions method.
     * Cyclomatic Complexity: 5
     */
    @Test
    @DisplayName("Compares the values given by the method with the expected values")
    void testGetAdjacentPositions1() {
        List<IPosition> expectedAdjacents = new ArrayList<IPosition>();
        expectedAdjacents.add(new Position(4, 4)); expectedAdjacents.add(new Position(4, 5));
        expectedAdjacents.add(new Position(4, 6)); expectedAdjacents.add(new Position(5, 4));
        expectedAdjacents.add(new Position(5, 6)); expectedAdjacents.add(new Position(6, 4));
        expectedAdjacents.add(new Position(6, 5)); expectedAdjacents.add(new Position(6, 6));

        List<IPosition> actualAdjacents = ship.getAdjacentPositions();

        assertNotNull(actualAdjacents, "Error: AdjacentPositions List should not be null");
        assertFalse(actualAdjacents.isEmpty(), "Error: AdjacentPositions List should not be empty");
        assertEquals(expectedAdjacents.size(), actualAdjacents.size(), "Error: The method didn't gave all possible adjacents.");
        assertTrue(actualAdjacents.containsAll(expectedAdjacents), "Error: The method doesn't give the expected adjacent positions.");
    }

    @Test
    @DisplayName("Compares the values given by the method with the expected values for a bigger boat")
    void testGetAdjacentPositions2() {
        Ship galeao = Ship.buildShip("galeao", Compass.NORTH, new Position(5, 5));

        List<IPosition> expectedAdjacents = new ArrayList<IPosition>();
        expectedAdjacents.add(new Position(4, 4)); expectedAdjacents.add(new Position(4, 5));
        expectedAdjacents.add(new Position(4, 6)); expectedAdjacents.add(new Position(4, 7));
        expectedAdjacents.add(new Position(4, 8)); expectedAdjacents.add(new Position(5, 4));
        expectedAdjacents.add(new Position(5, 8)); expectedAdjacents.add(new Position(6, 4));
        expectedAdjacents.add(new Position(6, 5)); expectedAdjacents.add(new Position(6, 7));
        expectedAdjacents.add(new Position(6, 8)); expectedAdjacents.add(new Position(7, 5));
        expectedAdjacents.add(new Position(7, 7)); expectedAdjacents.add(new Position(8, 5));
        expectedAdjacents.add(new Position(8, 6)); expectedAdjacents.add(new Position(8, 7));

        List<IPosition> actualAdjacents = galeao.getAdjacentPositions();

        assertNotNull(actualAdjacents, "Error: AdjacentPositions List should not be null");
        assertFalse(actualAdjacents.isEmpty(), "Error: AdjacentPositions List should not be empty");
        assertEquals(expectedAdjacents.size(), actualAdjacents.size(), "Error: The method didn't gave all possible adjacents.");
        assertTrue(actualAdjacents.containsAll(expectedAdjacents), "Error: The method doesn't give the expected adjacent positions.");
    }

    /**
     * Test for the getPosition method.
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("Verifies if ship's initial position equals (5,5)")
    void testGetPosition() {
        Position expectedPosition = new Position(5,5);
        assertNotNull(ship.getPosition(), "Error: Position of the ship should not be null.");
        assertEquals(expectedPosition, ship.getPosition(), "Error: getPosition() should give position(5, 5).");
    }

    /**
     * Test for the sink method.
     * Cyclomatic Complexity: 2
     */
    @Test
    @DisplayName("Checks if all positions have state isHit == True when ship is sunked")
    void testSink() {
        ship.sink();

        for (IPosition pos : ship.getPositions()) {
            assertTrue(pos.isHit(), "Error: When sunk, ship position" + pos + " state should be isHit=True.");
        }
    }

    /**
     * Test for the toString method.
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("Checks ")
    void testToString() {
        assertEquals("[Barca n F6]", ship.toString(),"Error: String representation for ship should be [Barca n (5,5)].");
    }

    /**
     * Test for the buildShip method.
     * Cyclomatic Complexity: 6
     */
    @Test
    @DisplayName("Tests buildShip for all cases including invalid case")
    void testBuildShip() {
        assertThrows(AssertionError.class, () -> Ship.buildShip(null, Compass.NORTH, new Position(1,1)), "Error: buildShip should not accepts null value for shipkind");
        assertThrows(AssertionError.class, () -> Ship.buildShip("barca", Compass.NORTH, null),"Error: buildShip should not accepts null value for position");
        assertThrows(AssertionError.class, () -> Ship.buildShip("barca", null, new Position(1,1)),"Error: buildShip should not accepts null value for bearing");
        assertNotNull(Ship.buildShip("barca", Compass.NORTH, new Position(1,1)),"Error: Ship instance should not be null.");
        assertNotNull(Ship.buildShip("caravela", Compass.NORTH, new Position(1,1)),"Error: Ship instance should not be null.");
        assertNotNull(Ship.buildShip("nau", Compass.NORTH, new Position(1,1)),"Error: Ship instance should not be null.");
        assertNotNull(Ship.buildShip("fragata", Compass.NORTH, new Position(1,1)),"Error: Ship instance should not be null.");
        assertNotNull(Ship.buildShip("galeao", Compass.NORTH, new Position(1,1)),"Error: Ship instance should not be null.");
        assertNull(Ship.buildShip("submarino", Compass.NORTH, new Position(1,1)), "Error: Ship instance should be null for invalid case");
        assertThrows(NullPointerException.class, () -> new Barge(null, new Position(1,1)), "Error: Should throw NPE for null bearing");
        assertThrows(NullPointerException.class, () -> new Barge(Compass.NORTH, null), "Error: Should throw NPE for null position");
    }

}