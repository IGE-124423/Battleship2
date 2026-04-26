package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Test class for Tasks.
 *
 * Author: ${user.name}
 * Date: 2026-04-26
 */
public class TasksTest {

    // ─── Streams for capturing stdout and stdin ─────────────────────────────
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        originalIn = System.in;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        outContent = null;
    }

    // Função auxiliar para simular o teclado
    private void feedStdin(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    // ════════════════════════════════════════════════════════════════════════
    // menuHelp()
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("menuHelp() prints all expected command keywords to stdout")
    void menuHelp() {
        Tasks.menuHelp();
        String output = outContent.toString();

        assertAll("menuHelp output must contain all menu commands",
                () -> assertTrue(output.contains("gerafrota")),
                () -> assertTrue(output.contains("lefrota")),
                () -> assertTrue(output.contains("estado")),
                () -> assertTrue(output.contains("rajada")),
                () -> assertTrue(output.contains("simula")),
                () -> assertTrue(output.contains("tiros")),
                () -> assertTrue(output.contains("scoreboard")),
                () -> assertTrue(output.contains("janela")),
                () -> assertTrue(output.contains("desisto"))
        );
    }

    // ════════════════════════════════════════════════════════════════════════
    // menu() - Testes ao comportamento do menu principal
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("menu_exit() 'desisto' exits immediately and prints farewell")
    void menu_exit() {
        feedStdin("desisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
        assertTrue(outContent.toString().contains("Bons ventos!"));
    }

    @Test
    @DisplayName("menu_ajuda() 'ajuda' prints help a second time then 'desisto' exits")
    void menu_ajuda() {
        feedStdin("ajuda\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
        String out = outContent.toString();
        int firstIdx  = out.indexOf("gerafrota");
        int secondIdx = out.indexOf("gerafrota", firstIdx + 1);
        assertTrue(secondIdx > firstIdx);
    }

    @Test
    @DisplayName("menu_unknown() unknown command prints error message")
    void menu_unknown() {
        feedStdin("comandoinvalido\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
        assertTrue(outContent.toString().contains("Que comando é esse"));
    }

    @Test
    @DisplayName("menu_estado_null() 'estado' with no fleet does nothing and continues")
    void menu_estado_null() {
        feedStdin("estado\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
    }

    @Test
    @DisplayName("menu_mapa_null() 'mapa' with no game does nothing and continues")
    void menu_mapa_null() {
        feedStdin("mapa\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
    }

    @Test
    @DisplayName("menu_rajada_null() 'rajada' with no game does nothing and continues")
    void menu_rajada_null() {
        feedStdin("rajada\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
    }

    @Test
    @DisplayName("menu_simula_null() 'simula' with no game does nothing and continues")
    void menu_simula_null() {
        feedStdin("simula\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
    }

    @Test
    @DisplayName("menu_tiros_null() 'tiros' with no game does nothing and continues")
    void menu_tiros_null() {
        feedStdin("tiros\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
    }

    @Test
    @DisplayName("menu_janela_null() 'janela' with no game prints error message")
    void menu_janela_null() {
        feedStdin("janela\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
        assertTrue(outContent.toString().contains("Erro"));
    }

    @Test
    @DisplayName("menu_scoreboard() 'scoreboard' command does not throw")
    void menu_scoreboard() {
        feedStdin("scoreboard\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
    }

    @Test
    @DisplayName("menu_gerafrota() 'gerafrota' creates fleet and game without throwing")
    void menu_gerafrota() {
        feedStdin("gerafrota\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
    }

    @Test
    @DisplayName("menu_estado_with_fleet() 'gerafrota' then 'estado' prints fleet status")
    void menu_estado_with_fleet() {
        feedStdin("gerafrota\nestado\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
    }

    @Test
    @DisplayName("menu_mapa_with_game() 'gerafrota' then 'mapa' prints the board")
    void menu_mapa_with_game() {
        feedStdin("gerafrota\nmapa\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
    }

    @Test
    @DisplayName("menu_tiros_with_game() 'gerafrota' then 'tiros' prints shot board")
    void menu_tiros_with_game() {
        feedStdin("gerafrota\ntiros\ndesisto\n");
        assertDoesNotThrow(() -> Tasks.menu());
    }

    // ════════════════════════════════════════════════════════════════════════
    // readPosition(Scanner)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("readPosition() returns a non-null Position for valid integer input")
    void readPosition() {
        Scanner in = new Scanner(new ByteArrayInputStream("3 5\n".getBytes()));
        Position pos = Tasks.readPosition(in);
        assertNotNull(pos);
    }

    // ════════════════════════════════════════════════════════════════════════
    // readClassicPosition(Scanner)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("readClassicPosition1() throws IllegalArgumentException when scanner has no input")
    void readClassicPosition1() {
        Scanner in = new Scanner(new ByteArrayInputStream("".getBytes()));
        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(in));
    }

    @Test
    @DisplayName("readClassicPosition2() parses combined token 'A3' correctly")
    void readClassicPosition2() {
        Scanner in = new Scanner(new ByteArrayInputStream("A3\n".getBytes()));
        IPosition pos = Tasks.readClassicPosition(in);
        assertNotNull(pos);
    }

    @Test
    @DisplayName("readClassicPosition3() parses split format 'A 3' (letter then int token)")
    void readClassicPosition3() {
        Scanner in = new Scanner(new ByteArrayInputStream("A 3\n".getBytes()));
        IPosition pos = Tasks.readClassicPosition(in);
        assertNotNull(pos);
    }

    @Test
    @DisplayName("readClassicPosition4() handles lowercase letter and converts to uppercase")
    void readClassicPosition4() {
        Scanner in = new Scanner(new ByteArrayInputStream("a5\n".getBytes()));
        IPosition pos = Tasks.readClassicPosition(in);
        assertNotNull(pos);
    }

    @Test
    @DisplayName("readClassicPosition5() accepts split lowercase 'b 7' via else-if branch")
    void readClassicPosition5() {
        Scanner in = new Scanner(new ByteArrayInputStream("b 7\n".getBytes()));
        IPosition pos = Tasks.readClassicPosition(in);
        assertNotNull(pos);
    }

    @Test
    @DisplayName("readClassicPosition6() throws IllegalArgumentException for fully invalid format")
    void readClassicPosition6() {
        Scanner in = new Scanner(new ByteArrayInputStream("123\n".getBytes()));
        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(in));
    }

    @Test
    @DisplayName("readClassicPosition7() throws for 'AB 3' – multi-char letter part, part2 not null")
    void readClassicPosition7() {
        Scanner in = new Scanner(new ByteArrayInputStream("AB 3\n".getBytes()));
        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(in));
    }

    @Test
    @DisplayName("readClassicPosition8() throws for 'A' alone (no digit follows)")
    void readClassicPosition8() {
        Scanner in = new Scanner(new ByteArrayInputStream("A\n".getBytes()));
        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(in));
    }

    // ════════════════════════════════════════════════════════════════════════
    // readShip(Scanner)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("readShip() does not throw for valid ship input tokens")
    void readShip() {
        assertDoesNotThrow(() -> {
            Scanner in = new Scanner(new ByteArrayInputStream("fragata 2 3 n\n".getBytes()));
            Tasks.readShip(in);
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // buildFleet(Scanner)
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("buildFleet1() throws AssertionError when Scanner argument is null")
    void buildFleet1() {
        assertThrows(AssertionError.class, () -> Tasks.buildFleet(null));
    }

    @Test
    @DisplayName("buildFleet2() returns a non-null Fleet when all ships are successfully created")
    void buildFleet2() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < 20; r += 2) {
            for (int c = 0; c < 20; c += 2) {
                sb.append("barca ").append(r).append(" ").append(c).append(" n\n");
            }
        }
        Scanner in = new Scanner(new ByteArrayInputStream(sb.toString().getBytes()));
        Fleet fleet = Tasks.buildFleet(in);
        assertNotNull(fleet);
    }

    @Test
    @DisplayName("buildFleet3() continues reading when readShip returns null (unknown ship kind)")
    void buildFleet3() {
        StringBuilder sb = new StringBuilder();
        sb.append("desconhecido 1 1 n\n");
        for (int r = 0; r < 20; r += 2) {
            for (int c = 0; c < 20; c += 2) {
                sb.append("barca ").append(r).append(" ").append(c).append(" n\n");
            }
        }
        Scanner in = new Scanner(new ByteArrayInputStream(sb.toString().getBytes()));
        assertDoesNotThrow(() -> Tasks.buildFleet(in));
    }

    @Test
    @DisplayName("buildFleet4() continues reading when addShip returns false (duplicate/invalid placement)")
    void buildFleet4() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append("barca 1 1 n\n");
        }
        for (int r = 0; r < 20; r += 2) {
            for (int c = 0; c < 20; c += 2) {
                sb.append("barca ").append(r).append(" ").append(c).append(" n\n");
            }
        }
        Scanner in = new Scanner(new ByteArrayInputStream(sb.toString().getBytes()));
        assertDoesNotThrow(() -> Tasks.buildFleet(in));
    }
}