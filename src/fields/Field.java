package fields;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import interfaces.Controls;
import interfaces.DrawOnCall;
import particle.Cell;

public abstract class Field extends Thread implements Controls {
	protected final int DEF_WIDTH = 100;
	protected final int DEF_HEIGTH = 100;
	protected int fieldXSize;
	protected int fieldYSize;

	protected final int SPEED = 50;
	protected Cell[][] field;
	private Dimension dim;

	protected float cellXSize;
	protected float cellYSize;

	private final short IMG_X_SCALE = 5;
	private final short IMG_Y_SCALE = 5;

	protected boolean isRunning;

	protected DrawOnCall redrawCaller;
	protected BufferedImage image;
	protected Graphics2D g2;
	protected int zoomAmount;

	protected Zoom zoom;
	private int dragXStartPos;
	private int dragYStartPos;
	private int xDrag;
	private int yDrag;
	private int xPos;
	private int yPos;

	Field() {

		zoomAmount = 10;

	}

	Field(int width, int heigth) {

		zoomAmount = 10;

	}

	public void drawMesh(Graphics graphic, int x, int y/* ,BufferedImage img */, DrawOnCall callBack) {

		Graphics2D gDb = (Graphics2D) image.getGraphics();
		gDb.setColor(Color.white);
		gDb.fillRect(0, 0, image.getWidth(), image.getHeight());
		redrawCaller = callBack;

		g2 = (Graphics2D) graphic;

		float width = image.getWidth(null);
		float heigth = image.getHeight(null);
		cellXSize = width / fieldXSize;
		cellYSize = heigth / fieldYSize;
		gDb.setColor(Color.GRAY);
		float scaleAmount = zoom.getxEnd() / zoom.getxZoomSize();
		if (scaleAmount > 0.7f && scaleAmount <= 1f) {
			gDb.setStroke(new BasicStroke(9f));
		} else if (scaleAmount > 0.5f && scaleAmount <= 0.7f) {
			gDb.setStroke(new BasicStroke(4f));
		} else if (scaleAmount > 0.3f && scaleAmount <= 0.5f) {
			gDb.setStroke(new BasicStroke(2f));
		} else if (scaleAmount > 0f && scaleAmount <= 0.3f) {
			gDb.setStroke(new BasicStroke(1f));
		}

		System.out.println(
				cellXSize + "-" + cellYSize + "-" + width + "-" + heigth + "-" + fieldXSize + "-" + fieldYSize);

		for (int i = 0; i <= fieldXSize; i++) {
			gDb.drawLine((int) (i * cellXSize), 0, (int) (i * cellXSize), (int) heigth);

		}

		for (int i = 0; i <= fieldYSize; i++) {
			gDb.drawLine(0, (int) (i * cellYSize), (int) width, (int) (i * cellYSize));

		}
		try {
			drawCells();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Field.drawMesh()");
			System.out.println("top-" + zoom.getTop() + ", bot-" + zoom.getBottom() + ", left-" + zoom.getLeft()
					+ ", right-" + zoom.getRight());
		}

	}

	public void setImage(Dimension dim) {
		this.dim = dim;
		this.image = new BufferedImage(this.dim.width * IMG_X_SCALE, this.dim.height * IMG_Y_SCALE,
				BufferedImage.TYPE_INT_RGB);
		zoom = new Zoom(image.getWidth(), image.getHeight());

	}

	abstract void drawCells();

	abstract protected void buildField();

	abstract void buildField(int width, int heigth);

	abstract void seedField();

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void zoomIn(int coursorPosX, int coursorPosY) {
		setXYpos(coursorPosX * IMG_X_SCALE, coursorPosY * IMG_Y_SCALE);

		if (zoom.zoomIn(zoomAmount, xPos, yPos)) {

			requestMeshRebuild();
		}

	}

	public void zoomOut(int coursorPosX, int coursorPosY) {
		setXYpos(coursorPosX * IMG_X_SCALE, coursorPosY * IMG_Y_SCALE);

		if (zoom.zoomOut(zoomAmount, xPos, yPos)) {

			requestMeshRebuild();
		}

	}

	public void shift(int coursorPosX, int coursorPosY, int dragXstart, int dragYstart) {
		dragXStartPos = (int) (dragXstart);// visibleCellXSize);
		dragYStartPos = (int) (dragYstart);// visibleCellYSize);
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
		xPos = (int) (coursorPosX); // new xPoint
		yPos = (int) (coursorPosY); // new yPoint
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
			zoom = null;

			zoom = new Zoom(width, heigth);
			resetZoom();

		} else {
			buildField();
			resetZoom();

		}
		resetSim();

	}

	protected Image zoomImage() {
		return image.getSubimage(zoom.getLeft(), zoom.getTop(), zoom.getxZoomSize(), zoom.getyZoomSize());
	}

	protected Image fitImage() {
		zoomImage();
		return zoomImage().getScaledInstance(600, 600, BufferedImage.SCALE_FAST);
	}

}
