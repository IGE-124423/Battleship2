package battleship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.*;

/**
 * Shot
 *
 * @author Your Name
 * Date: 20/02/2026
 * Time: 19:39
 */
public class Move implements IMove {

	//-------------------------------------------------------------------
	private final int number;
	private final List<IPosition> shots;
	private final List<IGame.ShotResult> shotResults;

	//-------------------------------------------------------------------
	public Move(int moveNumber, List<IPosition> moveShots, List<IGame.ShotResult> moveResults) {
		this.number = moveNumber;
		this.shots = moveShots;
		this.shotResults = moveResults;
	}

	@Override
	public String toString() {
		return "Move{" +
				"number=" + number +
				", shots=" + shots.size() +
				", results=" + shotResults.size() +
				'}';
	}

	@Override
	public int getNumber() {
		return this.number;
	}

	@Override
	public List<IPosition> getShots() {
		return this.shots;
	}

	@Override
	public List<IGame.ShotResult> getShotResults() {
		return this.shotResults;
	}

	/**
	 * Processes the results of enemy fire on the game board, analyzing the outcomes of shots,
	 * such as valid shots, repeated shots, missed shots, hits on ships, and sunk ships. It can
	 * also display a detailed summary of the shot results if verbose mode is activated.
	 *
	 * @param verbose a boolean indicating whether a detailed summary should be printed to the console
	 *                for the processed enemy fire data.
	 * @return a JSON-formatted string that encapsulates the results, including counts of valid shots,
	 *         repeated shots, missed shots, shots outside the game board, and details of hits and
	 *         sunk ships.
	 */

	@Override
	public String processEnemyFire(boolean verbose) {

		FireStats stats = processShots();

		if (verbose) {
			printVerbose(stats);
		}

		return buildJson(stats);
	}

	private static class FireStats {
		int validShots = 0;
		int repeatedShots = 0;
		int missedShots = 0;
		int outsideShots = 0;

		Map<String, Integer> sunkBoats = new HashMap<>();
		Map<String, Integer> hitsPerBoat = new HashMap<>();
	}

	private FireStats processShots() {
		FireStats stats = new FireStats();

		for (IGame.ShotResult result : this.shotResults) {
			if (isInvalid(result)) continue;

			if (isRepeated(result)) {
				stats.repeatedShots++;
				continue;
			}

			stats.validShots++;

			if (result.ship() == null) {
				stats.missedShots++;
			} else {
				String boat = result.ship().getCategory();

				stats.hitsPerBoat.put(boat,
						stats.hitsPerBoat.getOrDefault(boat, 0) + 1);

				if (result.sunk()) {
					stats.sunkBoats.put(boat,
							stats.sunkBoats.getOrDefault(boat, 0) + 1);
				}
			}
		}

		stats.outsideShots = Game.NUMBER_SHOTS - stats.validShots - stats.repeatedShots;

		return stats;
	}

	private static boolean isInvalid(IGame.ShotResult result) {
		return !result.valid();
	}
	private static boolean isRepeated(IGame.ShotResult result) {
		return result.repeated();
	}

	private void printVerbose(FireStats stats) {
		StringBuilder output = new StringBuilder();

		if (stats.validShots == 0 && stats.repeatedShots > 0) {
			output.append(formatShots(stats.repeatedShots, "repetido"));
		} else {
			if (stats.validShots > 0) {
				output.append(formatShots(stats.validShots, "válido")).append(": ");
			}

			appendSunkBoats(output, stats);
			appendHits(output, stats);
			appendMisses(output, stats);
			appendRepeated(output, stats);
		}

		appendOutside(output, stats);

		System.out.println("Jogada nº" + this.number + " -> " + output);
	}

	private String formatShots(int count, String label) {
		return count + " tiro" + (count > 1 ? "s " : " ") + label + (count > 1 ? "s" : "");
	}

	private void appendSunkBoats(StringBuilder output, FireStats stats) {
		for (var entry : stats.sunkBoats.entrySet()) {
			output.append(entry.getValue())
					.append(" ").append(entry.getKey())
					.append(entry.getValue() > 1 ? "s" : "")
					.append(" ao fundo + ");
		}
	}

	private void appendHits(StringBuilder output, FireStats stats) {
		for (var entry : stats.hitsPerBoat.entrySet()) {
			if (!stats.sunkBoats.containsKey(entry.getKey())) {
				output.append(entry.getValue())
						.append(" tiro")
						.append(entry.getValue() > 1 ? "s" : "")
						.append(" num(a) ")
						.append(entry.getKey())
						.append(" + ");
			}
		}
	}

	private void appendMisses(StringBuilder output, FireStats stats) {
		if (stats.missedShots > 0) {
			output.append(formatShots(stats.missedShots, "na água"));
		} else if (output.length() > 2) {
			output.setLength(output.length() - 2);
		}
	}

	private void appendRepeated(StringBuilder output, FireStats stats) {
		if (stats.repeatedShots > 0) {
			if (stats.validShots > 0) output.append(", ");
			output.append(formatShots(stats.repeatedShots, "repetido"));
		}
	}

	private void appendOutside(StringBuilder output, FireStats stats) {
		if (stats.outsideShots > 0) {
			if (!output.isEmpty()) output.append(", ");
			output.append(formatShots(stats.outsideShots, "exterior"));
		}
	}

	private String buildJson(FireStats stats) {
		Map<String, Object> response = new HashMap<>();

		response.put("validShots", stats.validShots);
		response.put("outsideShots", stats.outsideShots);
		response.put("repeatedShots", stats.repeatedShots);
		response.put("missedShots", stats.missedShots);

		response.put("sunkBoats", buildSunkBoats(stats));
		response.put("hitsOnBoats", buildHits(stats));

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			return mapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Erro ao serializar JSON", e);
		}
	}

	private List<Map<String, Object>> buildSunkBoats(FireStats stats) {
		List<Map<String, Object>> list = new ArrayList<>();

		for (var entry : stats.sunkBoats.entrySet()) {
			Map<String, Object> boat = new HashMap<>();
			boat.put("type", entry.getKey());
			boat.put("count", entry.getValue());
			list.add(boat);
		}

		return list;
	}

	private List<Map<String, Object>> buildHits(FireStats stats) {
		List<Map<String, Object>> list = new ArrayList<>();

		for (var entry : stats.hitsPerBoat.entrySet()) {
			if (!stats.sunkBoats.containsKey(entry.getKey())) {
				Map<String, Object> boat = new HashMap<>();
				boat.put("type", entry.getKey());
				boat.put("hits", entry.getValue());
				list.add(boat);
			}
		}

		return list;
	}
}