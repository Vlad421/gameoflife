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
		setName("GOL Field Thread");

	}

	public void seedField() {

		Random rand = new Random();
		for (short i = 0; i < field.length; i++) {
			for (short j = 0; j < field[i].length; j++) {
				field[i][j] = new GameOfLifeCell(rand.nextInt(100) > DEF_SEED_CHANCE, i, j, (GameOfLifeCell[][]) field);
			}
		}
	}

	@Override
	protected void buildField() {

		field = new GameOfLifeCell[DEF_WIDTH][DEF_HEIGTH];
		fieldXSize = DEF_WIDTH;
		fieldYSize = DEF_HEIGTH;

	}

	@Override
	protected void buildField(int width, int heigth) {
		field = new GameOfLifeCell[width][heigth];
		fieldXSize = width;
		fieldYSize = heigth;

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

	void drawCells() {
		Graphics2D gDb = (Graphics2D) image.getGraphics();

		gDb.setColor(Color.black);

		for (int i = 0; i < fieldXSize; i++) {

			for (int j = 0; j < fieldYSize; j++) {

				if (((GameOfLifeCell) field[i][j]).isExist()) {
					// gDb.setColor(colors[rand.nextInt(colors.length)]);
					gDb.fillRect((int) (i * cellXSize), (int) (j * cellYSize), (int) (cellXSize), (int) (cellYSize));

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
