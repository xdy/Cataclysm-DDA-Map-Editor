package net.krazyweb.cataclysm.mapeditor.map.data;

import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.MapTile;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.*;
import net.krazyweb.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapgenEntry implements Jsonable {

	private static final char[] SYMBOLS = new char[] {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '!',
			'@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', '{', ']', '}', ':', ';', '"', '\'',
			'<', ',', '>', '.', '?', '/', '`', '~', '\\', '|', ' '
	};

	private static Logger log = LogManager.getLogger(MapgenEntry.class);

	public MapTile[][] tiles = new MapTile[MapEditor.SIZE][MapEditor.SIZE];
	public MapTile fillTerrain;
	public List<PlaceGroupZone> placeGroupZones = new ArrayList<>();
	public MapSettings settings = new MapSettings();

	private MapgenEntry lastSavedState;

	public MapgenEntry() {

	}

	public MapgenEntry(final MapgenEntry mapgenEntry) {
		for (int x = 0; x < MapEditor.SIZE; x++) {
			System.arraycopy(mapgenEntry.tiles[x], 0, tiles[x], 0, MapEditor.SIZE);
		}
		mapgenEntry.placeGroupZones.forEach(zone -> placeGroupZones.add(new PlaceGroupZone(zone)));
		settings = new MapSettings(mapgenEntry.settings);
		fillTerrain = mapgenEntry.fillTerrain;
	}

	public boolean isSaved() {
		return this.equals(lastSavedState);
	}

	public void markSaved() {
		lastSavedState = new MapgenEntry(this);
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapgenEntry that = (MapgenEntry) o;

		if (!Arrays.deepEquals(tiles, that.tiles)) return false;
		if (fillTerrain != null ? !fillTerrain.equals(that.fillTerrain) : that.fillTerrain != null) return false;
		if (!placeGroupZones.equals(that.placeGroupZones)) return false;
		return settings.equals(that.settings);

	}

	@Override
	public int hashCode() {
		int result = Arrays.deepHashCode(tiles);
		result = 31 * result + (fillTerrain != null ? fillTerrain.hashCode() : 0);
		result = 31 * result + placeGroupZones.hashCode();
		result = 31 * result + settings.hashCode();
		return result;
	}

	@Override
	public List<String> getJsonLines() {

		List<String> lines = new ArrayList<>();
		Map<MapTile, Character> mappings = mapSymbols();

		lines.add("{");

		settings.getJsonLines().forEach(line -> lines.add(INDENT + line + ","));

		lines.add(INDENT + "\"object\": {");

		if (fillTerrain != null) {
			lines.add(INDENT + INDENT + "\"fill_ter\": \"" + fillTerrain.displayTerrain + "\",");
		}

		lines.addAll(getRows(mappings));
		lines.addAll(getTerrainLines(mappings));
		lines.addAll(getFurnitureLines(mappings));

		/*lines.add(INDENT + INDENT + "\"place_specials\": [");

		tempLines.clear();

		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				if (furniture[x][y].equals("f_toilet")) {
					tempLines.add(INDENT + INDENT + INDENT + "{ \"type\": \"toilet\", \"x\": " + x + ", \"y\": " + y + " },");
				}
			}
		}

		if (tempLines.size() > 0) {
			String line = tempLines.remove(tempLines.size() - 1);
			tempLines.add(line.substring(0, line.length() - 1));
		}

		lines.addAll(tempLines);

		lines.add(INDENT + INDENT + "],");
		lines.add(INDENT + INDENT + "\"set\": [");

		tempLines.clear();

		createRandomGrass().forEach(line -> tempLines.add(INDENT + INDENT + INDENT + line));

		if (tempLines.size() > 0) {
			String line = tempLines.remove(tempLines.size() - 1);
			tempLines.add(line.substring(0, line.length() - 1));
		}

		lines.addAll(tempLines);

		lines.add(INDENT + INDENT + "],");
		lines.add(INDENT + INDENT + "\"place_groups\": [");

		tempLines.clear();

		placeGroupZones.forEach(zone -> tempLines.add(INDENT + INDENT + INDENT + zone.getJsonLines().get(0) + ","));

		if (tempLines.size() > 0) {
			String line = tempLines.remove(tempLines.size() - 1);
			tempLines.add(line.substring(0, line.length() - 1));
		}

		lines.addAll(tempLines);

		lines.add(INDENT + INDENT + "]");*/
		lines.add(INDENT + "}");
		lines.add("}");

		return lines;

	}

	private List<String> getRows(final Map<MapTile, Character> mappings) {

		List<String> lines = new ArrayList<>();

		lines.add(INDENT + INDENT + "\"rows\": [");

		for (int y = 0; y < MapEditor.SIZE; y++) {
			String row = "";
			for (int x = 0; x < MapEditor.SIZE; x++) {
				if (tiles[x][y] == null) {
					row += mappings.get(fillTerrain);
				} else {
					row += mappings.get(tiles[x][y]);
				}
			}
			lines.add(INDENT + INDENT + INDENT + "\"" + row + "\"" + ((y == MapEditor.SIZE - 1) ? "" : ","));
		}

		lines.add(INDENT + INDENT + "],");

		return lines;

	}

	private List<String> getTerrainLines(final Map<MapTile, Character> mappings) {

		List<String> lines = new ArrayList<>();

		lines.add(INDENT + INDENT + "\"terrain\": {");

		List<String> tempLines = new ArrayList<>();

		mappings.entrySet().forEach(entry -> {

			if (entry.getKey().equals(fillTerrain)) {
				return;
			}

			List<String> terrain = entry.getKey().tileMappings
					.stream()
					.filter(tileMapping -> tileMapping instanceof TerrainMapping)
					.map(TileMapping::getJson)
					.collect(Collectors.toList());

			if (terrain.size() > 0) {

				String line = INDENT + INDENT + INDENT + "\"" + entry.getValue() + "\": ";

				if (terrain.size() > 1) {
					line += "[ ";
				}

				line += StringUtils.join(", ", terrain);

				if (terrain.size() > 1) {
					line += " ]";
				}

				tempLines.add(line + ",");

			}

		});

		if (tempLines.size() > 0) {
			String line = tempLines.remove(tempLines.size() - 1);
			tempLines.add(line.substring(0, line.length() - 1));
		}

		lines.addAll(tempLines);

		lines.add(INDENT + INDENT + "},");

		return lines;

	}

	private List<String> getFurnitureLines(final Map<MapTile, Character> mappings) {

		List<String> lines = new ArrayList<>();

		lines.add(INDENT + INDENT + "\"furniture\": {");

		List<String> tempLines = new ArrayList<>();

		mappings.entrySet().forEach(entry -> {

			List<String> furniture = entry.getKey().tileMappings
					.stream()
					.filter(tileMapping -> tileMapping instanceof FurnitureMapping)
					.map(TileMapping::getJson)
					.collect(Collectors.toList());

			if (furniture.size() > 0) {

				String line = INDENT + INDENT + INDENT + "\"" + entry.getValue() + "\": ";

				if (furniture.size() > 1) {
					line += "[ ";
				}

				line += StringUtils.join(", ", furniture);

				if (furniture.size() > 1) {
					line += " ]";
				}

				tempLines.add(line + ",");

			}

		});

		if (tempLines.size() > 0) {
			String line = tempLines.remove(tempLines.size() - 1);
			tempLines.add(line.substring(0, line.length() - 1));
		}

		lines.addAll(tempLines);

		lines.add(INDENT + INDENT + "}");

		return lines;

	}

	private class CharacterMapping {

		private Character character;
		private int priority;

		private CharacterMapping(final Character character, final int priority) {
			this.character = character;
			this.priority = priority;
		}

		@Override
		public boolean equals(Object o) {

			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			CharacterMapping that = (CharacterMapping) o;

			return !(character != null ? !character.equals(that.character) : that.character != null);

		}

		@Override
		public int hashCode() {
			return character != null ? character.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "[" + priority + ", '" + character + "']";
		}

	}

	//TODO Clean this all up and optimize where possible if needed
	private Map<MapTile, Character> mapSymbols() {

		TreeMap<MapTile, List<CharacterMapping>> commonMappings = new TreeMap<>((o1, o2) -> {
			if (o1.hashCode() > o2.hashCode()) {
				return -1;
			} else if (o1.hashCode() < o2.hashCode()) {
				return 1;
			}
			return 0;
		});

		Map<MapTile, Character> mappings = new HashMap<>();
		Set<MapTile> uniqueTiles = new HashSet<>();

		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				if (tiles[x][y] != null) {
					uniqueTiles.add(tiles[x][y]);
				}
			}
		}

		log.debug(uniqueTiles.size());

		if (fillTerrain != null) {
			//Fill Terrain should monopolize the " " mapping for ease of map reading.
			List<CharacterMapping> fillTerrainMapping = new ArrayList<>();
			fillTerrainMapping.add(new CharacterMapping(' ', Integer.MAX_VALUE));
			commonMappings.put(fillTerrain, fillTerrainMapping);
		}

		for (MapTile mapTile : uniqueTiles) {

			commonMappings.put(mapTile, new ArrayList<>());
			log.debug(commonMappings.size());
			List<String> tileTerrain = new ArrayList<>();
			List<String> tileFurniture = new ArrayList<>();
			String tileExtra = "";

			for (TileMapping mapping : mapTile.tileMappings) {
				if (mapping instanceof TerrainMapping) {
					tileTerrain.add(((TerrainMapping) mapping).terrain);
				}
				if (mapping instanceof FurnitureMapping) {
					tileFurniture.add(((FurnitureMapping) mapping).furniture);
				}
				if (mapping instanceof ToiletMapping) {
					tileExtra = "toilet";
				}
				if (mapping instanceof GasPumpMapping) {
					tileExtra = "gaspump";
				}
				if (mapping instanceof SignMapping) {
					tileExtra = "sign";
				}
				if (mapping instanceof VendingMachineMapping) {
					tileExtra = "vendingmachine";
				}
			}

			List<String> closestMatch = new ArrayList<>();

			try {

				BufferedReader reader = new BufferedReader(new FileReader(Paths.get("data/tileMappings.txt").toFile()));
				String line;

				List<String> mappingTerrain = new ArrayList<>();
				List<String> mappingFurniture = new ArrayList<>();
				String mappingExtra = "";
				boolean inDefinition = true;
				boolean inMatch = false;

				int score = 0;

				while ((line = reader.readLine()) != null) {

					if (line.startsWith("t:")) {
						Collections.addAll(mappingTerrain, line.substring(2).trim().split(","));
						inDefinition = true;
						inMatch = false;
					}

					if (line.startsWith("f:")) {
						Collections.addAll(mappingFurniture, line.substring(2).trim().split(","));
						inDefinition = true;
						inMatch = false;
					}

					if (line.startsWith("s:")) {
						mappingExtra = line.substring(2).trim();
						inDefinition = true;
						inMatch = false;
					}

					if (line.startsWith("	")) {

						Character character = line.charAt(1);
						int rank = Integer.parseInt(line.substring(3));

						if (inDefinition) {

							inDefinition = false;
							boolean reject = false;

							Collection<String> terrainDisjunction = CollectionUtils.disjunction(tileTerrain, mappingTerrain);
							Collection<String> furnitureDisjunction = CollectionUtils.disjunction(tileFurniture, mappingFurniture);
							score = terrainDisjunction.size() + furnitureDisjunction.size();

							Set<String> mTerrain = new HashSet<>(mappingTerrain);
							Set<String> tTerrain = new HashSet<>(tileTerrain);
							mTerrain.removeAll(tTerrain);

							Set<String> mFurniture = new HashSet<>(mappingFurniture);
							Set<String> tFurniture = new HashSet<>(tileFurniture);
							mFurniture.removeAll(tFurniture);

							score += mTerrain.size() + mFurniture.size();

							if (!(mappingExtra.isEmpty() && tileExtra.isEmpty()) && !mappingExtra.equals(tileExtra)) {
								score += 10;
							}

							if (mTerrain.size() == mappingTerrain.size() && mFurniture.size() == mappingFurniture.size() && !(!mappingExtra.isEmpty() && mappingExtra.equals(tileExtra))) {
								reject = true;
							}

							log.trace(mTerrain + ", " + mFurniture);
							log.trace(score + "\t" + mappingTerrain + " " + mappingFurniture + " " + mappingExtra);
							log.trace("===");

							if (!reject) {
								inMatch = true;
								closestMatch.clear();
								//commonMappings.get(mapTile).clear();
								closestMatch.addAll(mappingTerrain);
								closestMatch.addAll(mappingFurniture);
								closestMatch.add(mappingExtra);
							}

						}

						if (inMatch) {
							//Subtract score here so that the closest matched tiles win
							//(ex: [t_grass] vs. [t_grass, t_grass, t_dirt])
							//[t_grass] should win since it's the most specific
							commonMappings.get(mapTile).add(new CharacterMapping(character, rank - score));
						}

						mappingTerrain = new ArrayList<>();
						mappingFurniture = new ArrayList<>();
						mappingExtra = "";

					}

				}

				log.debug("===========");
				log.debug(mapTile);
				log.debug(closestMatch);
				log.debug(commonMappings.get(mapTile));
				log.debug("===========");

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		List<CharacterMapping> usedCharacters = new ArrayList<>();

		while (!commonMappings.isEmpty()) {

			commonMappings.values().forEach(characterMapping -> {
				characterMapping.removeAll(usedCharacters);
				Collections.sort(characterMapping, (o1, o2) -> {
					if (o1.priority > o2.priority) {
						return -1;
					} else if (o1.priority < o2.priority) {
						return 1;
					}
					return 0;
				});
			});

			Stream<Map.Entry<MapTile, List<CharacterMapping>>> sorted = commonMappings.entrySet().stream()
					.sorted(Map.Entry.comparingByValue((charMap1, charMap2) -> {
						if (!charMap1.isEmpty() && charMap2.isEmpty()) {
							return -1;
						} else if (charMap1.isEmpty() && !charMap2.isEmpty()) {
							return 1;
						} else if (charMap1.isEmpty()) {
							return 0;
						}
						if (charMap1.get(0).priority > charMap2.get(0).priority) {
							return -1;
						} else if (charMap1.get(0).priority < charMap2.get(0).priority) {
							return 1;
						}
						return 0;
					}));

			sorted.limit(1).forEach(mapTileListEntry -> {

				log.debug(mapTileListEntry);

				if (mapTileListEntry.getValue().isEmpty()) {

					//TODO Match closer to characters present in terrain/furniture/extras instead of iterating all symbols
					symbolLoop:
					for (char symbol : SYMBOLS) {
						for (CharacterMapping characterMapping : usedCharacters) {
							if (characterMapping.character == symbol) {
								continue symbolLoop;
							}
						}
						mappings.put(mapTileListEntry.getKey(), symbol);
						usedCharacters.add(new CharacterMapping(symbol, 0));
						break;
					}

				} else {
					mappings.put(mapTileListEntry.getKey(), mapTileListEntry.getValue().get(0).character);
					usedCharacters.add(mapTileListEntry.getValue().get(0));
				}

				commonMappings.remove(mapTileListEntry.getKey());

			});

		}

		return mappings;

	}

}
