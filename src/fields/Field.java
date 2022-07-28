package fields;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import interfaces.Controls;
import interfaces.DrawOnCall;
import particle.Cell;

public abstract class Field extends Thread implements Controls {
	protected final int DEF_WIDTH = 100;
	protected final int DEF_HEIGTH = 100;

	protected final int SPEED = 50;
	protected Cell[][] field;
	private Dimension dim;

	protected float cellXSize;
	protected float cellYSize;
	private float visibleCellXSize;
	private float visibleCellYSize;

	protected boolean isRunning;

	protected DrawOnCall redrawCaller;
	protected Image image;
	protected Graphics2D g2;
	protected int zoomAmount;

	protected Zoom zoom;
	private int dragXStartPos;
	private int dragYStartPos;
	private int xDrag;
	private int yDrag;
	private int xPos;
	private int yPos;

	public void drawMesh(Graphics graphic, int x, int y, Image img, DrawOnCall callBack) {
		this.image = img;
		setVisibleSize();
		Graphics2D gDb = (Graphics2D) image.getGraphics();

		redrawCaller = callBack;

		g2 = (Graphics2D) graphic;

		float width = image.getWidth(null);
		float heigth = image.getHeight(null);
		cellXSize = width / (zoom.getRight() - zoom.getLeft());
		cellYSize = heigth / (zoom.getBottom() - zoom.getTop());
		gDb.setColor(Color.GRAY);
		gDb.setStroke(new BasicStroke(5f));

		for (int i = 0; i <= zoom.getRight() - zoom.getLeft(); i++) {
			gDb.drawLine(i != zoom.getRight() ? (int) (cellXSize * i) : (int) width, 0,
					i != zoom.getRight() ? (int) (cellXSize * i) : (int) width, (int) heigth);

		}

		for (int i = 0; i <= zoom.getBottom() - zoom.getTop(); i++) {
			gDb.drawLine(0, i != zoom.getBottom() ? (int) (cellYSize * i) : (int) heigth, (int) width,
					i != zoom.getBottom() ? (int) (cellYSize * i) : (int) heigth);

		}
		try {
			drawCells();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("top-" + zoom.getTop() + ", bot-" + zoom.getBottom() + ", left-" + zoom.getLeft()
					+ ", right-" + zoom.getRight());
		}

	}

	public void setDimension(Dimension dim) {
		this.dim = dim;

	}

	private void setVisibleSize() {
		visibleCellXSize = dim.width / (zoom.getRight() - zoom.getLeft());
		visibleCellYSize = dim.height / (zoom.getBottom() - zoom.getTop());
	}

	abstract void drawCells();

	abstract void buildField();

	abstract void buildField(int x, int y);

	abstract void seedField();

	public Field() {
		zoom = new Zoom(DEF_WIDTH, DEF_HEIGTH);
		zoomAmount = DEF_HEIGTH / 50;
		resetZoom();
	}

	Field(int width, int heigth) {
		zoom = new Zoom(width, heigth);
		zoomAmount = heigth / 40;
		resetZoom();

	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void zoomIn(int coursorPosX, int coursorPosY) {
		setXYpos(coursorPosX, coursorPosY);

		if (zoom.zoomIn(zoomAmount, xPos, yPos)) {
			System.out.println("zoomin");
			requestMeshRebuild();
		}

	}

	public void zoomOut(int coursorPosX, int coursorPosY) {
		setXYpos(coursorPosX, coursorPosY);

		if (zoom.zoomOut(zoomAmount, xPos, yPos)) {

			requestMeshRebuild();
		}

	}

	public void shift(int coursorPosX, int coursorPosY, int dragXstart, int dragYstart) {
		dragXStartPos = (int) (dragXstart / visibleCellXSize);
		dragYStartPos = (int) (dragYstart / visibleCellYSize);
		setXYpos(coursorPosX, coursorPosY);

		if (xDrag != xPos - dragXStartPos || yDrag != yPos - dragYStartPos) {
			xDrag = xPos - dragXStartPos;
			yDrag = yPos - dragYStartPos;
			System.out.println(xPos + "-" + yPos + "-" + dragXStartPos + "-" + dragYStartPos);
			zoom.mouseShift(xDrag, yDrag);
			requestMeshRebuild();

		}

	}

	private void setXYpos(int coursorPosX, int coursorPosY) {
		xPos = (int) (coursorPosX / visibleCellXSize); // new xPoint
		yPos = (int) (coursorPosY / visibleCellYSize); // new yPoint
	}

	public void shift(int coursorPosX, int coursorPosY) {
		setXYpos(coursorPosX, coursorPosY);

		if (xDrag != xPos - dragXStartPos || yDrag != yPos - dragYStartPos) {
			xDrag = xPos - dragXStartPos;
			yDrag = yPos - dragYStartPos;
			System.out.println(xPos + "-" + yPos + "-" + dragXStartPos + "-" + dragYStartPos);
			zoom.mouseShift(xDrag, yDrag);
			requestMeshRebuild();

		}

	}

	private void resetZoom() {
		zoom.resetZoom();

	}

	synchronized public void requestMeshRebuild() {
		redrawCaller.draw();
	}

	@Override
	public void startSim() {
		System.gc();
		setRunning(true);

	}

	@Override
	public void stopSim() {
		setRunning(false);
		System.gc();

	}

	@Override
	public void resetSim() {
		stopSim();
		resetZoom();
		seedField();
		Runtime.getRuntime().gc();
		requestMeshRebuild();

	}

	@Override
	public void resizeField(int width, int heigth) {
		stopSim();
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

		if (width > 100 || heigth > 100 && width / heigth == 1) {
			buildField(width, heigth);
			zoom = new Zoom(width, heigth);
			resetZoom();

		} else {
			buildField();
			resetZoom();

		}
		resetSim();

	}

	protected Image fitImage() {
		return image.getScaledInstance(600, 600, Image.SCALE_REPLICATE);
	}

}
