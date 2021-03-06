package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

public class TrapMapping extends TileMapping {

	public String trap;

	public TrapMapping(final String trap) {
		this.trap = trap;
	}

	@Override
	public String getJson() {
		return "\"" + trap + "\"";
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TrapMapping that = (TrapMapping) o;

		return trap.equals(that.trap);

	}

	@Override
	public int hashCode() {
		return trap.hashCode();
	}

	@Override
	public TrapMapping copy() {
		return new TrapMapping(trap);
	}

	@Override
	public String toString() {
		return "[Trap: " + trap + "]";
	}

}
