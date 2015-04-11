package net.krazyweb.cataclysm.mapeditor.map.tilemappings;

import java.util.ArrayList;
import java.util.List;

public class MonsterMapping extends TileMapping {

	public String monster, name;
	public boolean friendly;

	public MonsterMapping(final String monster, final boolean friendly, final String name) {
		this.monster = monster;
		this.friendly = friendly;
		this.name = name;
	}

	@Override
	public List<String> getJsonLines() {
		List<String> lines = new ArrayList<>();
		lines.add("{ \"monster\": \"" + monster + "\", \"friendly\": " + friendly + ", \"name\": " + name + " }");
		return lines;
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MonsterMapping that = (MonsterMapping) o;

		if (friendly != that.friendly) return false;
		if (!monster.equals(that.monster)) return false;
		return name.equals(that.name);

	}

	@Override
	public int hashCode() {
		int result = monster.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + (friendly ? 1 : 0);
		return result;
	}

	@Override
	public MonsterMapping copy() {
		return new MonsterMapping(monster, friendly, name);
	}

}