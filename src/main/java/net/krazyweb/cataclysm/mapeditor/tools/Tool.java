package net.krazyweb.cataclysm.mapeditor.tools;

import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

public abstract class Tool {

	public void click(final int x, final int y, final Tile tile, final CataclysmMap map) {}
	public void drag(final int x, final int y, final Tile tile, final CataclysmMap map) {}
	public void dragStart(final int x, final int y, final Tile tile, final CataclysmMap map) {}
	public void dragEnd(final int x, final int y, final Tile tile, final CataclysmMap map) {}

}
