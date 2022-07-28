package particle;

public class GameOfLifeCell extends Cell {
	private boolean isExist;
//	private int location_x;
//	private int location_y;
	int counter;
	GameOfLifeCell[][] field;

	public GameOfLifeCell(boolean isLiveCell, short place_x, short place_y, GameOfLifeCell[][] field) {

		this.isExist = isLiveCell;
		this.location_x = place_x;
		this.location_y = place_y;
		this.field = field;
	}

	synchronized public void checkSuroundings(/* Dot[][] field */) {
		counter = 0;
		if (location_x - 1 != -1 && field[location_x - 1][location_y].isExist()) {
			counter++;
		}
		if (location_x - 1 != -1 && location_y - 1 != -1 && field[location_x - 1][location_y - 1].isExist()) {
			counter++;
		}
		if (location_y - 1 != -1 && field[location_x][location_y - 1].isExist()) {
			counter++;
		}
		if (location_y - 1 != -1 && location_x + 1 < field.length && field[location_x + 1][location_y - 1].isExist()) {
			counter++;
		}
		if (location_x + 1 < field.length && field[location_x + 1][location_y].isExist()) {
			counter++;
		}
		if (location_y + 1 < field[0].length && location_x + 1 < field.length
				&& field[location_x + 1][location_y + 1].isExist()) {
			counter++;
		}
		if (location_y + 1 < field[0].length && field[location_x][location_y + 1].isExist()) {
			counter++;
		}
		if (location_y + 1 < field[0].length && location_x - 1 != -1
				&& field[location_x - 1][location_y + 1].isExist()) {
			counter++;
		}

	}

	public boolean isExist() {
		return isExist;
	}

	public void setField(GameOfLifeCell[][] field) {
		this.field = field;
	}

	public void setExist(boolean isExist) {
		this.isExist = isExist;

	}

	private void setExisance() {

		switch (counter) {
			case 2: {
				if (isExist()) {

					setExist(true);
				}
				break;

			}
			case 3: {

				setExist(true);

				break;
			}

			default:

				setExist(false);
		}
	}

	public void run() {

		// if (isChecking) {
		synchronized (field) {
			checkSuroundings();
			// }

			setExisance();
		}
	}

	public void check() {
		checkSuroundings();
		setExisance();
	}

}
