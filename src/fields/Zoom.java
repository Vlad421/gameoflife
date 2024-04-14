package fields;

import interfaces.Single;

public class Zoom {
	private final int xStart;
	private final int yStart;
	private final int xEnd;
	private final int yEnd;
	private int xZoomSize;
	private int yZoomSize;
	private int xZoomStart;
	private int yZoomStart;
	private int xZoomEnd;
	private int yZoomEnd;
	private int xZoomCenter;
	private int yZoomCenter;

	public Zoom(int xSize, int ySize) {

		this.xStart = 0;
		this.yStart = 0;
		this.xEnd = xSize;
		this.yEnd = ySize;
		this.xZoomStart = xStart;
		this.yZoomStart = yStart;
		this.xZoomEnd = xEnd;
		this.yZoomEnd = yEnd;
		initZoomSize();
		setCenter();
		resetZoom();
	}

	public void resetZoom() {
		this.xZoomStart = xStart;
		this.yZoomStart = yStart;
		this.xZoomEnd = xEnd;
		this.yZoomEnd = yEnd;
		initZoomSize();
		setCenter();
	}

	private void initZoomSize() {
		this.xZoomSize = xEnd - xStart;
		this.yZoomSize = yEnd - yStart;

	}

	private void setCenter() {
		this.xZoomCenter = xZoomSize / 2 + xZoomStart;
		this.yZoomCenter = yZoomSize / 2 + yZoomStart;
	}

	private void shift(int xPoint, int yPoint) {
		int xDiff = xPoint - xZoomCenter;
		int yDiff = yPoint - yZoomCenter;

		synchronized (this) {
			xShift(xDiff);
			yShift(yDiff);

		}

	}

	public void mouseShift(int xPoint, int yPoint) {
		int xDiff = -xPoint;
		int yDiff = -yPoint;

		synchronized (this) {
			xShift(xDiff);
			yShift(yDiff);
			setCenter();

		}

	}

	private void xShift(int diff) {

		if (xZoomStart + diff > xStart && xZoomEnd + diff < xEnd) {
			xZoomStart += diff;
			xZoomEnd = xZoomStart + xZoomSize;

		} else if (!(xZoomStart + diff > xStart)) {
			xZoomStart = xStart;
			xZoomEnd = xZoomStart + xZoomSize;

		} else if (!(xZoomEnd + diff < xEnd)) {
			xZoomEnd = xEnd;
			xZoomStart = xZoomEnd - xZoomSize;

		}

	}

	private void yShift(int diff) {

		if (yZoomStart + diff > yStart && yZoomEnd + diff < yEnd) {
			yZoomStart += diff;
			yZoomEnd = yZoomStart + yZoomSize;

		} else if (!(yZoomStart + diff > yStart)) {
			yZoomStart = yStart;
			yZoomEnd = yZoomStart + yZoomSize;

		} else if (!(yZoomEnd + diff < yEnd)) {
			yZoomEnd = yEnd;
			yZoomStart = yZoomEnd - yZoomSize;

		}

	}

	public boolean zoomIn(int zoomAmount, int xShift, int yShift) {

		if (xZoomSize - zoomAmount > 0 && yZoomSize - zoomAmount > 0) {
			xZoomSize -= zoomAmount;
			yZoomSize -= zoomAmount;
			shift(xShift, yShift);
			printZoom();
			return true;
		}
		return false;
	}

	public boolean zoomOut(int zoomAmount, int xShift, int yShift) {

		if (xZoomSize + zoomAmount <= xEnd && yZoomSize + zoomAmount <= yEnd) {
			xZoomSize += zoomAmount;
			yZoomSize += zoomAmount;
			shift(xShift, yShift);
			printZoom();
			return true;
		}
		return false;
	}

	private void printZoom() {
		if (Single.isDebug) {
			System.out.printf(
					"# Zooming visibles #\nxVisible: %d ---- %d\nyVisible: %d ---- %d\nxSize - %d, ySize - %d\n",
					xZoomStart, xZoomEnd, yZoomStart, yZoomEnd, xZoomSize, yZoomSize);
		}

	}

	public int getLeft() {
		return xZoomStart;
	}

	public int getTop() {
		return yZoomStart;
	}

	public int getRight() {
		return xZoomEnd;
	}

	public int getBottom() {
		return yZoomEnd;
	}

	public int getxStart() {
		return xStart;
	}

	public int getyStart() {
		return yStart;
	}

	public int getxEnd() {
		return xEnd;
	}

	public int getyEnd() {
		return yEnd;
	}

	public int getxZoomSize() {
		return xZoomSize;
	}

	public int getyZoomSize() {
		return yZoomSize;
	}

}
