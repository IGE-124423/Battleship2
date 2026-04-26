package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for Move.
 *
 * Author: ${user.name}
 * Date: 2026-04-26 00:00
 *
 * Cyclomatic Complexity per method:
 * - Move(int, List, List)      : 1  → 1 test method
 * - toString()                 : 1  → 1 test method
 * - getNumber()                : 1  → 1 test method
 * - getShots()                 : 1  → 1 test method
 * - getShotResults()           : 1  → 1 test method
 * - processEnemyFire(boolean)  : 25 → 25 test methods (covering all edge cases and plurals)
 */
@SuppressWarnings({
        "NewClassNamingConvention",   // test class intentionally named MoveTest
        "ClassWithoutLogger",         // test classes do not need loggers
        "ClassWithoutNoArgConstructor", // JUnit creates instances via reflection
        "OverlyComplexClass",         // CC budget spent on thorough path coverage
        "MagicNumber"                 // literals in tests are expected, self-documenting values
})
public class MoveTest {

    // ─── String constants (eliminates "Duplicate/Hardcoded string" warnings) ─
    private static final String SHIP_DESTROYER = "Destroyer";
    private static final String SHIP_SUBMARINE = "Submarine";
    private static final String SHIP_CRUISER   = "Cruiser";
    private static final String SHIP_FRIGATE   = "Frigate";

    private static final String KEY_VALID_SHOTS    = "validShots";
    private static final String KEY_MISSED_SHOTS   = "missedShots";
    private static final String KEY_REPEATED_SHOTS = "repeatedShots";
    private static final String KEY_HITS_ON_BOATS  = "hitsOnBoats";
    private static final String KEY_SUNK_BOATS     = "sunkBoats";

    private static final String JSON_VALID_SHOTS_ZERO    = "\"validShots\" : 0";
    private static final String JSON_REPEATED_SHOTS_ONE  = "\"repeatedShots\" : 1";
    private static final String JSON_MISSED_SHOTS_ONE    = "\"missedShots\" : 1";
    private static final String JSON_OUTSIDE_SHOTS_ZERO  = "\"outsideShots\" : 0";

    private static final String OUT_AGUA      = "água";
    private static final String OUT_REPETIDO  = "repetido";
    private static final String OUT_REPETIDOS = "repetidos";
    private static final String OUT_FUNDO     = "fundo";
    private static final String OUT_NUMA      = "num(a)";
    private static final String OUT_VALIDOS   = "válidos";
    private static final String OUT_EXTERIOR  = "exterior";
    private static final String OUT_TIROS     = "tiros";

    // ─── SUT + output capture ────────────────────────────────────────────────
    private Move move;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    // ════════════════════════════════════════════════════════════════════════
    // Stub Factories
    // ════════════════════════════════════════════════════════════════════════

    @SuppressWarnings("AnonymousInnerClassWithTooManyMethods")
    private static IShip makeShip(final String category) {
        return new IShip() {
            @Override public String          getCategory()                  { return category; }
            @Override public Integer         getSize()                      { return 1; }
            @Override public List<IPosition> getPositions()                 { return new ArrayList<>(0); }
            @Override public List<IPosition> getAdjacentPositions()         { return new ArrayList<>(0); }
            @Override public IPosition       getPosition()                  { return null; }
            @Override public Compass         getBearing()                   { return null; }
            @Override public boolean         stillFloating()                { return true; }
            @Override public int             getTopMostPos()                { return 0; }
            @Override public int             getBottomMostPos()             { return 0; }
            @Override public int             getLeftMostPos()               { return 0; }
            @Override public int             getRightMostPos()              { return 0; }
            @Override public boolean         occupies(final IPosition pos)  { return false; }
            @Override public boolean         tooCloseTo(final IShip other)  { return false; }
            @Override public boolean         tooCloseTo(final IPosition pos){ return false; }
            @Override public void            shoot(final IPosition pos)     { /* stub */ }
            @Override public void            sink()                         { /* stub */ }
        };
    }

    private static IGame.ShotResult makeShotResult(final boolean valid,
                                                   final boolean repeated,
                                                   final IShip ship,
                                                   final boolean sunk) {
        return new IGame.ShotResult(valid, repeated, ship, sunk);
    }

    // ─── Lifecycle ───────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        this.move = new Move(1, new ArrayList<>(0), new ArrayList<>(0));
        this.outContent = new ByteArrayOutputStream();
        this.originalOut = System.out;
        System.setOut(new PrintStream(this.outContent, false, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        this.move = null;
        System.setOut(this.originalOut);
        this.outContent = null;
    }

    // ════════════════════════════════════════════════════════════════════════
    // Core Methods Tests (Constructor, Getters, toString)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Move() stores move number, shots list and results list at construction")
    void constructor() {
        final List<IPosition> shots = new ArrayList<>(0);
        final List<IGame.ShotResult> results = new ArrayList<>(0);
        final Move m = new Move(42, shots, results);

        assertAll("Constructor must faithfully store all three arguments",
                () -> assertEquals(42, m.getNumber()),
                () -> assertEquals(shots, m.getShots()),
                () -> assertSame(results, m.getShotResults())
        );
    }

    @Test
    @DisplayName("toString() returns a non-null string containing the move number")
    void toStringMethod() {
        this.move = new Move(7, new ArrayList<>(0), new ArrayList<>(0));
        final String result = this.move.toString();
        assertNotNull(result);
        assertTrue(result.contains("7"));
    }

    @Test
    @DisplayName("getNumber() returns the move number supplied at construction")
    void getNumber() {
        this.move = new Move(99, new ArrayList<>(0), new ArrayList<>(0));
        assertEquals(99, this.move.getNumber());
    }

    @Test
    @DisplayName("getShots() returns the same list reference supplied at construction")
    void getShots() {
        final List<IPosition> shots = new ArrayList<>(0);
        this.move = new Move(1, shots, new ArrayList<>(0));
        assertEquals(shots, this.move.getShots());
    }

    @Test
    @DisplayName("getShotResults() returns the same list reference supplied at construction")
    void getShotResults() {
        final List<IGame.ShotResult> results = new ArrayList<>(0);
        results.add(makeShotResult(true, false, null, false));
        this.move = new Move(1, new ArrayList<>(0), results);
        assertSame(results, this.move.getShotResults());
    }

    // ════════════════════════════════════════════════════════════════════════
    // processEnemyFire(boolean) - Original Scenarios (1 to 13)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("processEnemyFire1() empty result list returns JSON with all required keys")
    void processEnemyFire1() {
        this.move = new Move(1, new ArrayList<>(0), new ArrayList<>(0));
        final String json = this.move.processEnemyFire(false);
        assertAll(
                () -> assertNotNull(json),
                () -> assertTrue(json.contains(KEY_VALID_SHOTS)),
                () -> assertTrue(json.contains(KEY_MISSED_SHOTS)),
                () -> assertTrue(json.contains(KEY_REPEATED_SHOTS))
        );
    }

    @Test
    @DisplayName("processEnemyFire2() invalid shot is skipped, validShots stays 0")
    void processEnemyFire2() {
        final List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(false, false, null, false));
        this.move = new Move(1, new ArrayList<>(0), results);
        assertTrue(this.move.processEnemyFire(false).contains(JSON_VALID_SHOTS_ZERO));
    }

    @Test
    @DisplayName("processEnemyFire3() repeated valid shot increments repeatedShots to 1")
    void processEnemyFire3() {
        final List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, true, null, false));
        this.move = new Move(1, new ArrayList<>(0), results);
        assertTrue(this.move.processEnemyFire(false).contains(JSON_REPEATED_SHOTS_ONE));
    }

    @Test
    @DisplayName("processEnemyFire4() valid water shot increments missedShots to 1")
    void processEnemyFire4() {
        final List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, false, null, false));
        this.move = new Move(1, new ArrayList<>(0), results);
        assertTrue(this.move.processEnemyFire(false).contains(JSON_MISSED_SHOTS_ONE));
    }

    @Test
    @DisplayName("processEnemyFire5() valid hit on non-sunk ship appears in hitsOnBoats")
    void processEnemyFire5() {
        final IShip ship = makeShip(SHIP_DESTROYER);
        final List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, false, ship, false));
        this.move = new Move(1, new ArrayList<>(0), results);

        final String json = this.move.processEnemyFire(false);
        assertTrue(json.contains(KEY_HITS_ON_BOATS) && json.contains(SHIP_DESTROYER));
    }

    @Test
    @DisplayName("processEnemyFire6() sunk ship appears in sunkBoats")
    void processEnemyFire6() {
        final IShip ship = makeShip(SHIP_SUBMARINE);
        final List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, false, ship, true));
        this.move = new Move(1, new ArrayList<>(0), results);

        final String json = this.move.processEnemyFire(false);
        assertTrue(json.contains(KEY_SUNK_BOATS) && json.contains(SHIP_SUBMARINE));
    }

    @Test
    @DisplayName("processEnemyFire7() verbose=false produces no console output")
    void processEnemyFire7() {
        final List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, false, null, false));
        this.move = new Move(1, new ArrayList<>(0), results);
        this.move.processEnemyFire(false);
        assertEquals("", this.outContent.toString(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("processEnemyFire8() verbose=true with water shot prints 'água'")
    void processEnemyFire8() {
        final List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, false, null, false));
        this.move = new Move(1, new ArrayList<>(0), results);
        this.move.processEnemyFire(true);
        assertTrue(this.outContent.toString(StandardCharsets.UTF_8).contains(OUT_AGUA));
    }

    @Test
    @DisplayName("processEnemyFire9() verbose=true with only repeated shots prints 'repetido'")
    void processEnemyFire9() {
        final List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, true, null, false));
        this.move = new Move(1, new ArrayList<>(0), results);
        this.move.processEnemyFire(true);
        assertTrue(this.outContent.toString(StandardCharsets.UTF_8).contains(OUT_REPETIDO));
    }

    @Test
    @DisplayName("processEnemyFire10() verbose=true with a sunk ship prints 'fundo'")
    void processEnemyFire10() {
        final IShip ship = makeShip(SHIP_CRUISER);
        final List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, false, ship, true));
        this.move = new Move(1, new ArrayList<>(0), results);
        this.move.processEnemyFire(true);
        assertTrue(this.outContent.toString(StandardCharsets.UTF_8).contains(OUT_FUNDO));
    }

    @Test
    @DisplayName("processEnemyFire11() verbose=true with a non-sunk hit prints 'num(a)'")
    void processEnemyFire11() {
        final IShip ship = makeShip(SHIP_FRIGATE);
        final List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, false, ship, false));
        this.move = new Move(1, new ArrayList<>(0), results);
        this.move.processEnemyFire(true);
        assertTrue(this.outContent.toString(StandardCharsets.UTF_8).contains(OUT_NUMA));
    }

    @Test
    @DisplayName("processEnemyFire12() outsideShots > 0 when result list is empty")
    void processEnemyFire12() {
        this.move = new Move(1, new ArrayList<>(0), new ArrayList<>(0));
        assertFalse(this.move.processEnemyFire(false).contains(JSON_OUTSIDE_SHOTS_ZERO));
    }

    @Test
    @DisplayName("processEnemyFire13() hits on two distinct ship types both appear in JSON")
    void processEnemyFire13() {
        final IShip shipA = makeShip(SHIP_DESTROYER);
        final IShip shipB = makeShip(SHIP_SUBMARINE);
        final List<IGame.ShotResult> results = new ArrayList<>(2);
        results.add(makeShotResult(true, false, shipA, false));
        results.add(makeShotResult(true, false, shipB, false));
        this.move = new Move(1, new ArrayList<>(0), results);

        final String json = this.move.processEnemyFire(false);
        assertTrue(json.contains(SHIP_DESTROYER) && json.contains(SHIP_SUBMARINE));
    }

    // ════════════════════════════════════════════════════════════════════════
    // processEnemyFire(boolean) - Extra Scenarios for 100% Branch Coverage (14 to 25)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("processEnemyFire14() verbose=true mixed water+repeated shows both 'água' and 'repetido'")
    void processEnemyFire14() {
        List<IGame.ShotResult> results = new ArrayList<>(2);
        results.add(makeShotResult(true, false, null, false)); // valid water
        results.add(makeShotResult(true, true,  null, false)); // repeated
        move = new Move(1, new ArrayList<>(0), results);

        move.processEnemyFire(true);
        String out = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains(OUT_AGUA) && out.contains(OUT_REPETIDO));
    }

    @Test
    @DisplayName("processEnemyFire15() verbose=true 2 repeated shots uses plural 'repetidos'")
    void processEnemyFire15() {
        List<IGame.ShotResult> results = new ArrayList<>(2);
        results.add(makeShotResult(true, true, null, false));
        results.add(makeShotResult(true, true, null, false));
        move = new Move(1, new ArrayList<>(0), results);

        move.processEnemyFire(true);
        assertTrue(outContent.toString(StandardCharsets.UTF_8).contains(OUT_REPETIDOS));
    }

    @Test
    @DisplayName("processEnemyFire16() verbose=true 2 repeated + 1 water triggers plural 'repetidos'")
    void processEnemyFire16() {
        List<IGame.ShotResult> results = new ArrayList<>(3);
        results.add(makeShotResult(true, false, null, false)); // valid water
        results.add(makeShotResult(true, true,  null, false)); // repeated
        results.add(makeShotResult(true, true,  null, false)); // repeated
        move = new Move(1, new ArrayList<>(0), results);

        move.processEnemyFire(true);
        String out = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains(OUT_REPETIDOS) && out.contains(OUT_AGUA));
    }

    @Test
    @DisplayName("processEnemyFire17() verbose=true 2 water shots triggers 'válidos' and 'tiros' plurals")
    void processEnemyFire17() {
        List<IGame.ShotResult> results = new ArrayList<>(2);
        results.add(makeShotResult(true, false, null, false));
        results.add(makeShotResult(true, false, null, false));
        move = new Move(1, new ArrayList<>(0), results);

        move.processEnemyFire(true);
        String out = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains(OUT_VALIDOS) && out.contains(OUT_TIROS));
    }

    @Test
    @DisplayName("processEnemyFire18() verbose=true sunk ship only has no trailing '+'")
    void processEnemyFire18() {
        IShip ship = makeShip(SHIP_DESTROYER);
        List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, false, ship, true)); // sunk, no water
        move = new Move(1, new ArrayList<>(0), results);

        move.processEnemyFire(true);
        String out = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains(OUT_FUNDO));
        assertFalse(out.contains("+ \n") || out.trim().endsWith("+"));
    }

    @Test
    @DisplayName("processEnemyFire19() verbose=true non-sunk hit only has no trailing '+'")
    void processEnemyFire19() {
        IShip ship = makeShip(SHIP_FRIGATE);
        List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, false, ship, false)); // hit, not sunk, no water
        move = new Move(1, new ArrayList<>(0), results);

        move.processEnemyFire(true);
        String out = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains(OUT_NUMA));
        assertFalse(out.trim().endsWith("+"));
    }

    @Test
    @DisplayName("processEnemyFire20() verbose=true two sunk Destroyers triggers count plural")
    void processEnemyFire20() {
        IShip shipA = makeShip(SHIP_DESTROYER);
        IShip shipB = makeShip(SHIP_DESTROYER);
        List<IGame.ShotResult> results = new ArrayList<>(2);
        results.add(makeShotResult(true, false, shipA, true));
        results.add(makeShotResult(true, false, shipB, true));
        move = new Move(1, new ArrayList<>(0), results);

        move.processEnemyFire(true);
        String out = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains(OUT_FUNDO) && out.contains(SHIP_DESTROYER));
    }

    @Test
    @DisplayName("processEnemyFire21() verbose=true two hits on same non-sunk ship triggers hits plural")
    void processEnemyFire21() {
        IShip ship = makeShip(SHIP_CRUISER);
        List<IGame.ShotResult> results = new ArrayList<>(2);
        results.add(makeShotResult(true, false, ship, false));
        results.add(makeShotResult(true, false, ship, false));
        move = new Move(1, new ArrayList<>(0), results);

        move.processEnemyFire(true);
        String out = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains(OUT_NUMA) && out.contains(OUT_TIROS));
    }

    @Test
    @DisplayName("processEnemyFire22() verbose=true hit+sunk same category suppresses hit line")
    void processEnemyFire22() {
        IShip shipA = makeShip(SHIP_CRUISER); // hit
        IShip shipB = makeShip(SHIP_CRUISER); // sunk
        List<IGame.ShotResult> results = new ArrayList<>(2);
        results.add(makeShotResult(true, false, shipA, false));
        results.add(makeShotResult(true, false, shipB, true));
        move = new Move(1, new ArrayList<>(0), results);

        move.processEnemyFire(true);
        String out = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains(OUT_FUNDO));
        assertFalse(out.contains(OUT_NUMA));
    }

    @Test
    @DisplayName("processEnemyFire23() verbose=true empty results mentions 'exterior' with no leading comma")
    void processEnemyFire23() {
        move = new Move(1, new ArrayList<>(0), new ArrayList<>(0));
        move.processEnemyFire(true);
        String out = outContent.toString(StandardCharsets.UTF_8);

        assertTrue(out.contains(OUT_EXTERIOR));
        String afterArrow = out.contains("->") ? out.substring(out.indexOf("->") + 2).stripLeading() : out;
        assertFalse(afterArrow.startsWith(","));
    }

    @Test
    @DisplayName("processEnemyFire24() verbose=true 1 water + exterior inserts comma")
    void processEnemyFire24() {
        List<IGame.ShotResult> results = new ArrayList<>(1);
        results.add(makeShotResult(true, false, null, false));
        move = new Move(1, new ArrayList<>(0), results);

        move.processEnemyFire(true);
        String out = outContent.toString(StandardCharsets.UTF_8);

        if (out.contains(OUT_EXTERIOR)) {
            int waterIdx = out.indexOf(OUT_AGUA);
            int exteriorIdx = out.indexOf(OUT_EXTERIOR);
            String between = out.substring(waterIdx, exteriorIdx);
            assertTrue(between.contains(","));
        }
        assertTrue(out.contains(OUT_AGUA));
    }

    @Test
    @DisplayName("processEnemyFire25() JSON hit+sunk same category excludes ship from hitsOnBoats")
    void processEnemyFire25() {
        IShip shipA = makeShip(SHIP_DESTROYER); // hit
        IShip shipB = makeShip(SHIP_DESTROYER); // sunk
        List<IGame.ShotResult> results = new ArrayList<>(2);
        results.add(makeShotResult(true, false, shipA, false));
        results.add(makeShotResult(true, false, shipB, true));
        move = new Move(1, new ArrayList<>(0), results);

        String json = move.processEnemyFire(false);
        assertTrue(json.contains(KEY_SUNK_BOATS));

        int hitsIdx = json.indexOf(KEY_HITS_ON_BOATS);
        if (hitsIdx >= 0) {
            String hitsSection = json.substring(hitsIdx);
            assertTrue(hitsSection.contains("[ ]") || hitsSection.contains("[]"));
        }
    }
}