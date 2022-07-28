package fields;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import particle.GameOfLifeCell;

public class GameOfLifeField extends Field {
	private final int DEF_SEED_CHANCE = 94;
	private short cycles;

	public GameOfLifeField() {
		buildField();

		seedField();
		setName("Field Thread");

	}

	public GameOfLifeField(int width, int heigth) {
		buildField(width, heigth);

		seedField();
		setName("Field Thread");

	}

	public void seedField() {
		System.out.println("seedField()");
		Random rand = new Random();
		for (short i = 0; i < field.length; i++) {
			for (short j = 0; j < field[i].length; j++) {
				field[i][j] = new GameOfLifeCell(rand.nextInt(100) > DEF_SEED_CHANCE, i, j, (GameOfLifeCell[][]) field);
			}
		}
	}

	void buildField() {

		field = new GameOfLifeCell[DEF_WIDTH][DEF_HEIGTH];

	}

	void buildField(int width, int heigth) {
		field = new GameOfLifeCell[width][heigth];

	}

	synchronized public void check() {

		try {
			wait(SPEED);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				field[i][j].run();
			}
		}
		requestMeshRebuild();
	}

	private Color[] colors = { Color.BLACK, Color.DARK_GRAY, Color.BLACK, Color.GREEN, Color.BLACK, Color.BLUE,
			Color.BLACK, Color.CYAN, Color.BLACK, Color.BLACK, Color.magenta, Color.BLACK, Color.LIGHT_GRAY,
			Color.BLACK, Color.DARK_GRAY, Color.BLACK, Color.LIGHT_GRAY, Color.RED, Color.BLACK, Color.GRAY,
			Color.BLACK, Color.BLACK, Color.GREEN, Color.BLUE, Color.DARK_GRAY, Color.orange, Color.BLACK,
			Color.DARK_GRAY, Color.BLACK, Color.RED, Color.BLACK, Color.BLACK, Color.BLACK, Color.DARK_GRAY,
			Color.LIGHT_GRAY, Color.GREEN, Color.PINK, Color.BLACK, Color.BLACK, Color.RED, Color.BLACK,
			Color.DARK_GRAY, Color.BLACK, Color.RED, Color.magenta, Color.GREEN, Color.BLACK, Color.BLUE, Color.BLACK,
			Color.GREEN, Color.DARK_GRAY, Color.BLACK, Color.LIGHT_GRAY, Color.RED, Color.BLACK, Color.DARK_GRAY,
			Color.BLACK, Color.pink, Color.BLACK, Color.BLACK, Color.LIGHT_GRAY, Color.black };
	private Random rand = new Random();

	void drawCells() {
		Graphics2D gDb = (Graphics2D) image.getGraphics();

		gDb.setColor(Color.black);
		// gDb.setColor(colors[rand.nextInt(colors.length)]);

		for (int i = zoom.getLeft(); i < zoom.getRight(); i++) {

			for (int j = zoom.getTop(); j < zoom.getBottom(); j++) {

				if (((GameOfLifeCell) field[i][j]).isExist()) {
				//	gDb.setColor(colors[rand.nextInt(colors.length)]);
					gDb.fillRect((int) (cellXSize * (i - zoom.getLeft())), (int) (cellYSize * (j - zoom.getTop())),
							(int) (cellXSize), (int) (cellYSize));

				}

			}

		}

		g2.drawImage(fitImage(), 0, 0, null);

	}

	@Override
	public void run() {
		while (isAlive()) {
			try {
				synchronized (this) {
					wait(10);
				}

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			while (isRunning) {
				check();
				cycles++;
				if (cycles == 100) {

					System.gc();
				}

			}
			if (!isRunning) {
				synchronized (this) {
					notify();
				}
			}

		}

	}

}
