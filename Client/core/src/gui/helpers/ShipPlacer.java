package gui.helpers;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import gui.screens.BattleScreen;
import simulator.model.GameSize;
import simulator.model.warships.Cell;
import simulator.model.warships.Ship;

/**
 * This class helps with the placement of the ships when it is selected to be
 * done manually
 */
public class ShipPlacer {
	ViewPort yourBoard;
	int hoveredOverX = 0;
	int hoveredOverY = 0;
	Ship nextShip;
	int boardSize;
	BattleScreen game;
	Texture place;
	/**
	 * Sprites for transparent ships
	 */
	private Texture shipStartTransparentH, shipStartTransparentV, shipCenterTransparentH, shipCenterTransparentV,
			shipEndTransparentH, shipEndTransparentV, shipSingleTransparentH, shipSingleTransparentV;
	boolean horizontal = true;
	GameSize gameSize;

	List<Ship> ships; // arraylist

//	List<Integer> shipsSizeList;
	int listIndex;

	Cell[][] board;

	/**
	 * Constructor for the ShipPlacer class, it takes all textures and initialises
	 * them.
	 * 
	 * @param yourBoard player board 'ViewPort'
	 * @param game      'BattleScreen'
	 * @param boardSize 'int'
	 * @param gameSize  'GameSize'
	 */
	public ShipPlacer(ViewPort yourBoard, BattleScreen game, int boardSize, GameSize gameSize) {
		this.yourBoard = yourBoard;
		this.game = game;
		this.boardSize = boardSize;
		this.gameSize = gameSize;
		ships = game.getController().getShipsList();
		listIndex = ships.size() - 1;
		nextShip = ships.get(listIndex);

		// Create Cell[][]
		int size = gameSize.getSize();
		board = new Cell[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = new Cell(i, j, Cell.WATER_CELL);
			}
		}

	}

	/**
	 * Draw a transparent ship
	 */
	public void hoverPlacingShip() {
		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();
		if (yourBoard.mouseInGrid(mouseX, mouseY)) {
			if (hoveredOverX != mouseX || hoveredOverY != mouseY) {
				hoveredOverX = mouseX;
				hoveredOverY = mouseY;
				game.readBoard(yourBoard, true);
				int cellLocation[] = yourBoard.returnMouseLocation(mouseX, mouseY);
				if (horizontal) {
					if (cellLocation[0] >= 0 && cellLocation[0] + nextShip.getSize() - 1 < yourBoard.getxCells()
							&& cellLocation[1] >= 0 && cellLocation[1] < yourBoard.getyCells()) {
						for (int i = 0; i < nextShip.getSize(); i++) {
							if (nextShip.getSize() == 1)
								yourBoard.setCell(cellLocation[0] + i, cellLocation[1], shipSingleTransparentH);
							else {
								if (i == 0)
									yourBoard.setCell(cellLocation[0] + i, cellLocation[1], shipStartTransparentH);
								else if (i > 0 && i < nextShip.getSize() - 1)
									yourBoard.setCell(cellLocation[0] + i, cellLocation[1], shipCenterTransparentH);
								else
									yourBoard.setCell(cellLocation[0] + i, cellLocation[1], shipEndTransparentH);
							}
//							yourBoard.setCell(cellLocation[0] + i, cellLocation[1], place);
						}
					}
				} else {
					if (cellLocation[0] >= 0 && cellLocation[0] < yourBoard.getxCells() && cellLocation[1] >= 0
							&& cellLocation[1] + nextShip.getSize() - 1 < yourBoard.getyCells()) {
						for (int i = 0; i < nextShip.getSize(); i++) {
							if (nextShip.getSize() == 1)
								yourBoard.setCell(cellLocation[0] + i, cellLocation[1], shipSingleTransparentV);
							else {
								if (i == 0)
									yourBoard.setCell(cellLocation[0], cellLocation[1] + i, shipEndTransparentV);
								else if (i > 0 && i < nextShip.getSize() - 1)
									yourBoard.setCell(cellLocation[0], cellLocation[1] + i, shipCenterTransparentV);
								else
									yourBoard.setCell(cellLocation[0], cellLocation[1] + i, shipStartTransparentV);
							}
//							yourBoard.setCell(cellLocation[0], cellLocation[1] + i, place);
						}
					}
				}
			}
		}

	}

	/**
	 * Place the ship
	 */
	public void placeShip() {
		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();

		int cellLocation[] = yourBoard.returnMouseLocation(mouseX, mouseY);
		if (checkPos(cellLocation)) {
			if (horizontal) {
				if (cellLocation[0] >= 0 && cellLocation[0] + nextShip.getSize() - 1 < yourBoard.getxCells()
						&& cellLocation[1] >= 0 && cellLocation[1] < yourBoard.getyCells()) {
					for (int i = 0; i < nextShip.getSize(); i++) {
						board[cellLocation[0] + i][cellLocation[1]].set(nextShip.getId());
					}
				}
			} else {
				if (cellLocation[0] >= 0 && cellLocation[0] < yourBoard.getxCells() && cellLocation[1] >= 0
						&& cellLocation[1] + nextShip.getSize() - 1 < yourBoard.getyCells()) {
					for (int i = 0; i < nextShip.getSize(); i++) {
						board[cellLocation[0]][cellLocation[1] + i].set(nextShip.getId());
					}
				}
			}
			nextShip.setOrientation(horizontal);
			game.getController().placeShip(cellLocation, nextShip, horizontal);
			game.readBoard(yourBoard, true);
			getNextShipSize();
		} else
			System.err.println("You can not place a ship here: [" + cellLocation[0] + ", " + cellLocation[1] + "]-["
					+ (horizontal ? ((cellLocation[0] + nextShip.getSize()) + ", " + cellLocation[1])
							: (cellLocation[0] + ", " + (cellLocation[1] + nextShip.getSize())))
					+ "]");
		if (nextShip == null) {
			game.placementFinished();
		}
	}

	/**
	 * Changes the value of nextShip to the next Ship size
	 */
	private void getNextShipSize() {
		if (listIndex > 0) {
			listIndex--;
			nextShip = ships.get(listIndex);
		} else {
			nextShip = null;
		}
	}

	/**
	 * Checks if the position where the ship wants to be placed is valid will throw
	 * an exception.
	 * 
	 * @param cell
	 * @return ok
	 */
	private boolean checkPos(int cell[]) {
		int i = 0;
		boolean ok = true;
		try {
			while (i < nextShip.getSize() && ok) {
				if (horizontal && !game.getController().getPlayerBoard()[cell[0] + i][cell[1]].equals(Cell.WATER_CELL))
					ok = false;
				else if (!horizontal
						&& !game.getController().getPlayerBoard()[cell[0]][cell[1] + i].equals(Cell.WATER_CELL))
					ok = false;
				i++;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println(
					"Wow. Caught 'ArrayIndexOutOfBoundsException' Ship out of table. Much error. So programmer.");
			return false;
		}
		return ok;
	}

	/**
	 * Will change the orientation of the boat that wants to be placed.
	 */
	public void switchOrientation() {
		if (horizontal == true) {
			horizontal = false;
		} else {
			horizontal = true;
		}
		hoveredOverX = 0;
		hoveredOverY = 0;
	}

	/**
	 * Load textures
	 */
	public void load() {
		try {
			shipStartTransparentH = new Texture("textures/ships/shipStartTransparentH.png");
			shipStartTransparentV = new Texture("textures/ships/shipStartTransparentV.png");
			shipCenterTransparentV = new Texture("textures/ships/shipCenterTransparentV.png");
			shipCenterTransparentH = new Texture("textures/ships/shipCenterTransparentH.png");
			shipEndTransparentH = new Texture("textures/ships/shipEndTransparentH.png");
			shipEndTransparentV = new Texture("textures/ships/shipEndTransparentV.png");
			shipSingleTransparentH = new Texture("textures/ships/shipSingleTransparentH.png");
			shipSingleTransparentV = new Texture("textures/ships/shipSingleTransparentV.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dispose textures
	 */
	public void dispose() {
		shipStartTransparentH.dispose();
		shipStartTransparentV.dispose();
		shipCenterTransparentV.dispose();
		shipCenterTransparentH.dispose();
		shipEndTransparentH.dispose();
		shipEndTransparentV.dispose();
		shipSingleTransparentH.dispose();
		shipSingleTransparentV.dispose();
	}
}
