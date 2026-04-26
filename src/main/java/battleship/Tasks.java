package battleship;

import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.lang3.time.StopWatch;

/**
 * The type Tasks.
 */
public class Tasks {
	/**
	 * The constant LOGGER.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The constant GOODBYE_MESSAGE.
	 */
	private static final String GOODBYE_MESSAGE = "Bons ventos!";

	/**
	 * Strings to be used by the user
	 */
	private static final String AJUDA = "ajuda";
	private static final String GERAFROTA = "gerafrota";
	private static final String LEFROTA = "lefrota";
	private static final String DESISTIR = "desisto";
	private static final String RAJADA = "rajada";
	private static final String TIROS = "tiros";
	private static final String JANELA = "janela";
	private static final String MAPA = "mapa";
	private static final String STATUS = "estado";
	private static final String SIMULA = "simula";
	private static final String SCOREBOARD = "scoreboard";

	private static final Pattern CLASSIC_POSITION_PATTERN = Pattern.compile("[A-Z]\\d+");
	private static final Pattern COLUMN_PATTERN = Pattern.compile("[A-Z]");
	private static final Pattern ROW_PATTERN = Pattern.compile("\\d+");

	/**
	 * This task also tests the fighting element of a round of three shots
	 */
	public static void menu() {

		MenuState state = new MenuState();
		menuHelp();

		System.out.print("> ");
		Scanner in = new Scanner(System.in);
		String command = in.next();

		while (!command.equals(DESISTIR)) {

			MenuAction action = MenuAction.from(command);

			if (action != null) {
				action.execute(state, in);
			} else {
				System.out.println("Que comando é esse??? Repete ...");
			}

			System.out.print("> ");
			command = in.next();
		}

		System.out.println(GOODBYE_MESSAGE);
	}

	private static class MenuState {
		private IFleet myFleet;
		private IGame game;
	}

	private enum MenuAction {

		GERAR_FROTA(GERAFROTA) {
			@Override
			void execute(MenuState state, Scanner in) {
				state.myFleet = Fleet.createRandom();
				state.game = new Game(state.myFleet);
				state.game.printMyBoard(false, true);
			}
		},

		LER_FROTA(LEFROTA) {
			@Override
			void execute(MenuState state, Scanner in) {
				state.myFleet = buildFleet(in);
				state.game = new Game(state.myFleet);
				state.game.printMyBoard(false, true);
			}
		},

		MOSTRAR_STATUS(STATUS) {
			@Override
			void execute(MenuState state, Scanner in) {
				if (state.myFleet != null) {
					state.myFleet.printStatus();
				}
			}
		},

		MOSTRAR_MAPA(MAPA) {
			@Override
			void execute(MenuState state, Scanner in) {
				if (state.game != null) {
					state.game.printMyBoard(false, true);
				}
			}
		},

		EXECUTAR_RAJADA(RAJADA) {
			@Override
			void execute(MenuState state, Scanner in) {
				handleRajadaCommand(state, in);
			}
		},

		SIMULAR_JOGO(SIMULA) {
			@Override
			void execute(MenuState state, Scanner in) {
				handleSimulaCommand(state);
			}
		},

		MOSTRAR_TIROS(TIROS) {
			@Override
			void execute(MenuState state, Scanner in) {
				if (state.game != null) {
					state.game.printMyBoard(true, true);
				}
			}
		},

		MOSTRAR_SCOREBOARD(SCOREBOARD) {
			@Override
			void execute(MenuState state, Scanner in) {
				ScoreboardDatabase.printScoreboard();
			}
		},

		MOSTRAR_AJUDA(AJUDA) {
			@Override
			void execute(MenuState state, Scanner in) {
				menuHelp();
			}
		},

		ABRIR_JANELA(JANELA) {
			@Override
			void execute(MenuState state, Scanner in) {
				if (state.game != null) {
					GameGUI gui = new GameGUI((Game) state.game);
					gui.setVisible(true);
				} else {
					System.out.println("Erro: Gera primeiro uma frota usando 'gerafrota' ou 'lefrota'!");
				}
			}
		};

		private final String command;

		MenuAction(String command) {
			this.command = command;
		}

		abstract void execute(MenuState state, Scanner in);

		private static MenuAction from(String command) {
			for (MenuAction action : values()) {
				if (action.command.equals(command)) {
					return action;
				}
			}
			return null;
		}
	}

	private static void handleRajadaCommand(MenuState state, Scanner in) {
		if (state.game != null) {
			StopWatch sw = StopWatch.createStarted();

			state.game.readEnemyFire(in);

			sw.stop();
			state.myFleet.printStatus();
			state.game.printMyBoard(true, false);

			System.out.println(">>> Tempo gasto na jogada: " + sw.toString());

			if (state.game.getRemainingShips() == 0) {
				state.game.over();
				System.exit(0);
			}
		}
	}

	private static void handleSimulaCommand(MenuState state) {
		if (state.game != null) {
			while (state.game.getRemainingShips() > 0) {
				state.game.randomEnemyFire();
				state.myFleet.printStatus();
				state.game.printMyBoard(true, false);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			if (state.game.getRemainingShips() == 0) {
				state.game.over();
				System.exit(0);
			}
		}
	}

	/**
	 * This function provides help information about the menu commands.
	 */
	public static void menuHelp() {
		System.out.println("======================= AJUDA DO MENU =========================");
		System.out.println("Digite um dos comandos abaixo para interagir com o jogo:");
		System.out.println("- " + GERAFROTA + ": Gera uma frota aleatória de navios.");
		System.out.println("- " + LEFROTA + ": Permite criar e carregar uma frota personalizada.");
		System.out.println("- " + STATUS + ": Mostra o estado atual da frota.");
		System.out.println("- " + MAPA + ": Exibe o mapa da frota.");
		System.out.println("- " + RAJADA + ": Realiza uma rajada de disparos.");
		System.out.println("- " + SIMULA + ": Simula um jogo completo.");
		System.out.println("- " + TIROS + ": Lista os tiros válidos realizados (* = tiro em navio, o = tiro na água).");
		System.out.println("- " + SCOREBOARD + ": Mostra o histórico dos jogos terminados.");
		System.out.println("- " + JANELA + ": Abre uma interface gráfica com o tabuleiro atual.");
		System.out.println("- " + DESISTIR + ": Encerra o jogo.");
		System.out.println("===============================================================");
	}

	/**
	 * This operation allows the build up of a fleet, given user data
	 *
	 * @param in The scanner to read from
	 * @return The fleet that has been built
	 */
	public static Fleet buildFleet(Scanner in) {

		assert in != null;

		Fleet fleet = new Fleet();
		int i = 0; // i represents the total of successfully created ships
		while (i < Fleet.FLEET_SIZE) {
			IShip s = readShip(in);
			if (s != null) {
				boolean success = fleet.addShip(s);
				if (success) {
					i++;
				} else {
					LOGGER.info("Falha na criacao de {} {} {}", s.getCategory(), s.getBearing(), s.getPosition());
				}
			} else {
				LOGGER.info("Navio desconhecido!");
			}
		}
		LOGGER.info("{} navios adicionados com sucesso!", i);
		return fleet;
	}

	/**
	 * This operation reads data about a ship, build it and returns it
	 *
	 * @param in The scanner to read from
	 * @return The created ship based on the data that has been read
	 */
	public static Ship readShip(Scanner in) {

		assert in != null;

		String shipKind = in.next();
		Position pos = readPosition(in);
		char c = in.next().charAt(0);
		Compass bearing = Compass.charToCompass(c);
		return Ship.buildShip(shipKind, bearing, pos);
	}

	/**
	 * This operation allows reading a position in the map
	 *
	 * @param in The scanner to read from
	 * @return The position that has been read
	 */
	public static Position readPosition(Scanner in) {

		assert in != null;

		int row = in.nextInt();
		int column = in.nextInt();
		return new Position(row, column);
	}

	/**
	 * This operation allows reading a position in the map
	 *
	 * @param in The scanner to read from
	 * @return The classic position that has been read
	 */
	public static IPosition readClassicPosition(@NotNull Scanner in) {
		if (!in.hasNext()) {
			throw new IllegalArgumentException("Nenhuma posição válida encontrada!");
		}

		String part1 = in.next();
		String part2 = null;

		if (in.hasNextInt()) {
			part2 = in.next();
		}

		String input = (part2 != null) ? part1 + part2 : part1;

		input = input.toUpperCase();

		if (CLASSIC_POSITION_PATTERN.matcher(input).matches()) {
			char column = input.charAt(0);
			int row = Integer.parseInt(input.substring(1));
			return new Position(column, row);
		} else if (part2 != null && COLUMN_PATTERN.matcher(part1).matches() && ROW_PATTERN.matcher(part2).matches()) {
			char column = part1.charAt(0);
			int row = Integer.parseInt(part2);
			return new Position(column, row);
		} else {
			throw new IllegalArgumentException("Formato inválido. Use 'A3', 'A 3' ou similar.");
		}
	}
}