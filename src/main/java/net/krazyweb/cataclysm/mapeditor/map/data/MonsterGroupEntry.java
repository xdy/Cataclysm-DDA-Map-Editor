package net.krazyweb.cataclysm.mapeditor.map.data;

import java.util.ArrayList;
import java.util.List;

public class MonsterGroupEntry implements Jsonable {

	/*

        "type":"monstergroup",
        "name" : "GROUP_PLAIN",
        "default" : "mon_zombie",
        "monsters" : [{ "monster" : "mon_zombie", "freq" : 40, "cost_multiplier" : 1 }]
	 */

	public String name;
	public String defaultGroup;
	public List<MonsterGroupMonster> monsters = new ArrayList<>();

	private MonsterGroupEntry lastSavedState;

	public MonsterGroupEntry() {

	}

	public MonsterGroupEntry(final String name, final String defaultGroup, final List<MonsterGroupMonster> monsters) {
		this.name = name;
		this.defaultGroup = defaultGroup;
		this.monsters = monsters;
	}

	public MonsterGroupEntry(final MonsterGroupEntry entry) {
		this(entry.name, entry.defaultGroup, entry.monsters);
	}

	public boolean isSaved() {
		return this.equals(lastSavedState);
	}

	public void markSaved() {
		lastSavedState = new MonsterGroupEntry(this);
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MonsterGroupEntry that = (MonsterGroupEntry) o;

		return defaultGroup.equals(that.defaultGroup) && monsters.equals(that.monsters) && name.equals(that.name);

	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + defaultGroup.hashCode();
		result = 31 * result + monsters.hashCode();
		return result;
	}

	@Override
	public List<String> getJsonLines() {

		List<String> lines = new ArrayList<>();

		lines.add("{");
		lines.add(INDENT + "\"type\": \"monstergroup\",");
		lines.add(INDENT + "\"name\": \"" + name + "\",");
		lines.add(INDENT + "\"default\": \"" + defaultGroup + "\",");
		lines.add(INDENT + "\"monsters\": [");

		List<String> tempLines = new ArrayList<>();
		monsters.forEach(monster -> tempLines.add(INDENT + "{ \"monster\": \"" + monster.monster + "\", \"freq\": " + monster.frequency + ", \"cost_multiplier\": " + monster.multiplier + " }"));

		for (int i = 0; i < tempLines.size(); i++) {
			lines.add(INDENT + tempLines.get(i) + ((i < tempLines.size() - 1) ? "," : ""));
		}

		lines.add(INDENT + "]");
		lines.add("}");

		return lines;

	}

}
