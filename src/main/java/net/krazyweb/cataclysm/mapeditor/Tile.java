package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.krazyweb.util.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Tile {

	private static final Logger log = LogManager.getLogger(Tile.class);

	private static List<Tile> tiles = new ArrayList<>();

	public String id;
	public boolean connectsToWalls = false;

	private Tile(final String id, final boolean connectsToWalls) {
		this.id = id;
		this.connectsToWalls = connectsToWalls;
	}

	public static Tile get(final String tileId) {
		for (Tile tile : tiles) {
			if (tile.id.equals(tileId)) {
				return tile;
			}
		}
		return null;
	}

	public static List<Tile> getAll() {
		return tiles;
	}

	public static void loadTiles() {

		Path gameFolder = ApplicationSettings.getInstance().getPath(ApplicationSettings.Preference.GAME_FOLDER);

		try {
			load(gameFolder.resolve(Paths.get("data", "json")));
		} catch (IOException e) {
			log.error("Error while loading terrain and furniture definitions:", e);
		}

	}

	private static void load(final Path path) throws IOException {

		FileUtils.listFiles(path).stream().filter(file -> file.getFileName().toString().endsWith(".json")).forEach(file -> {

			log.info("Loading tiles from: '" + file + "'");

			try {

				JsonNode root = new ObjectMapper().readTree(file.toFile());

				root.forEach(node -> {

					if (node.get("type").asText().equals("terrain") || node.get("type").asText().equals("furniture")) {

						boolean connectsToWalls = false;

						if (node.has("flags")) {
							for (JsonNode flag : node.get("flags")) {

								String parsedFlag = flag.asText().replaceAll("\"", "");

								if (parsedFlag.equals("CONNECT_TO_WALL") || parsedFlag.equals("WALL")) {
									log.trace("Connects to Walls: " + node.get("id").asText());
									connectsToWalls = true;
									break;
								}

							}
						}

						tiles.add(new Tile(node.get("id").asText(), connectsToWalls));
						log.trace("Loaded tile: '" + node.get("id").asText() + "'");

					}

				});

			} catch (IOException e) {
				e.printStackTrace();
			}

		});

		tiles.sort((tile1, tile2) -> {

			if (tile1.id.startsWith("t_") && tile2.id.startsWith("f_")) {
				return -1;
			} else if (tile1.id.startsWith("f_") && tile2.id.startsWith("t_")) {
				return 1;
			}

			return 0;

		});

	}

}
