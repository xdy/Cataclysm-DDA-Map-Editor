package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

public class MonsterGroupMapping extends TileMapping {

	public String monster;
	public double density;
	public int chance;

	public MonsterGroupMapping(final String monster, final double density, final int chance) {
		this.monster = monster;
		this.density = density;
		this.chance = chance;
	}

	@Override
	public String getJson() {
		return "{ \"monster\": \"" + monster + "\", \"density\": " + density + ", \"chance\": " + chance + " }";
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MonsterGroupMapping that = (MonsterGroupMapping) o;

		if (chance != that.chance) return false;
		if (density != that.density) return false;
		return monster.equals(that.monster);

	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = monster.hashCode();
		temp = Double.doubleToLongBits(density);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + chance;
		return result;
	}

	@Override
	public MonsterGroupMapping copy() {
		return new MonsterGroupMapping(monster, density, chance);
	}

	@Override
	public String toString() {
		return "[Monster Group: " + monster + ", Density: " + density + ", Chance: " + chance + "]";
	}

}
