package net.krazyweb.cataclysm.mapeditor.map;

import javafx.scene.paint.Color;

public class PlaceGroupZone {

	//TODO More colors
	private static final Color[][] ZONE_COLORS = new Color[][] {
			new Color[] { Color.color(0.2, 0.8, 0.4, 0.3), Color.color(0.2, 0.8, 0.4, 0.85) },
			new Color[] { Color.color(0.7, 0.8, 0.2, 0.3), Color.color(0.7, 0.8, 0.2, 0.85) },
			new Color[] { Color.color(0.7, 0.2, 0.3, 0.3), Color.color(0.7, 0.2, 0.3, 0.85) },
			new Color[] { Color.color(0.1, 0.2, 0.8, 0.3), Color.color(0.1, 0.2, 0.8, 0.85) },
	};

	private static int currentZoneColor = 0;

	private class Point2D {
		private int x, y;
		public Point2D(final int x, final int y) {
			this.x = x;
			this.y = y;
		}
	}

	public int x, y, w, h;
	public Color fillColor;
	public Color strokeColor;
	public PlaceGroup group;

	public PlaceGroupZone(final PlaceGroup group) {
		this.group = group;
		fillColor = ZONE_COLORS[currentZoneColor][0];
		strokeColor = ZONE_COLORS[currentZoneColor][1];
		if (++currentZoneColor >= ZONE_COLORS.length) {
			currentZoneColor = 0;
		}
	}

	public PlaceGroupZone(final int x, final int y, final int w, final int h, final PlaceGroup group) {
		this(group);
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void rotate() {

		Point2D[] points = new Point2D[] {
				new Point2D(x, y),
				new Point2D(x + w, y),
				new Point2D(x, y + h),
				new Point2D(x + w, y + h)
		};

		for (Point2D point : points) {

			point.x -= 12;
			point.y -= 12;

			int temp = point.x;
			point.x = -point.y;
			point.y = temp;

			point.x += 12;
			point.y += 12;

		}

		Point2D leastXY = null;
		Point2D greatestXY = null;

		for (Point2D point : points) {
			if (leastXY == null || (point.x <= leastXY.x && point.y <= leastXY.y)) {
				leastXY = point;
			}
			if (greatestXY == null || (point.x >= greatestXY.x && point.y >= greatestXY.y)) {
				greatestXY = point;
			}
		}

		if (leastXY != null) {
			x = leastXY.x;
			y = leastXY.y;
			w = Math.abs(greatestXY.x - leastXY.x);
			h = Math.abs(greatestXY.y - leastXY.y);
		} else {
			throw new IllegalStateException("Could not rotate a PlaceGroupZone; somehow leastXY ended up null." +
					" Coordinates pre-rotation: [" + x + ", " + y + ", " + w + ", " + h + "]");
		}

	}

	public boolean contains(final int x, final int y) {
		return x >= this.x && x < this.x + this.w && y >= this.y && y < this.y + this.h;
	}

}