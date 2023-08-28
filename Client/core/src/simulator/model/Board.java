package simulator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.warships.Cell;
import simulator.model.warships.Ship;
import simulator.model.warships.ShipsManager;
import simulator.observer.IObserver;
import simulator.observer.ISubject;
import utils.Pair;

//import org.json.JSONObject;

/**
 * The board class contains all game elements present in the GUI It contains all
 * information on the CELLS as well as their state It also has all the ship
 * objects and their state and position
 *
 */
public class Board implements ISubject, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int size;
	private ShipsManager lista;
	private Cell mat[][];
	/**
	 * This list is needed to be able to handle the list of Observers the board has
	 */
	private List<IObserver> observers = new ArrayList<IObserver>();
	private int[] lastHit;

	/**
	 * Constructor for the board object.
	 * 
	 * @param difficulty
	 */
	public Board(GameSize difficulty) {
//		System.out.println("Board: constr()");
		this.size = difficulty.getSize();
		create();
		lista = new ShipsManager();
		lista.createShips(difficulty);
//		lista.toString();
	}

	/**
	 * Used to initialise all cells in the board and sets them to WATER as a default
	 * value
	 */
	public void create() {
		mat = new Cell[this.size][this.size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				mat[i][j] = new Cell(i, j, Cell.WATER_CELL);
			}
		}
	}

// -------------------------------------------------------------------------- ISubject methods implementation --------------------------------------------------------------------------
	public void registerObserver(IObserver observer) {
		observers.add(observer);
	}

	public void unregisterObserver(IObserver observer) {
		observers.remove(observer);
	}

	public void notifyObserver(Pair<Integer, Integer> pos, int points) {
		for (IObserver o : observers)
			o.update(pos, points);
	}

	public int[] getLastShot() {
		return lastHit;
	}

// -------------------------------------------------------------------------- Placement interactions --------------------------------------------------------------------------
	/**
	 * The algorithm for the random placement of ships on the board. This is used
	 * when the Player chooses random placement or for when the machine is playing
	 */
	public void randomShipPlacement() {
//		System.out.println("Board: randomShipPlacement()");
		int x, y, or;
		for (int i = 0; i < lista.getListOfShips().size(); i++) {
			int tam = (lista.getSize(lista.getListOfShips().get(i)));
			or = (int) (Math.random() * 2);
			Ship ship = lista.getListOfShips().get(i);
			if (or == 1)
				ship.setOrientation(true);
			else
				ship.setOrientation(false);
			do {
				x = (int) (Math.random() * size);
				y = (int) (Math.random() * size);

			} while (!checkPosition(tam, or, x, y));
			for (int j = 0; j < tam; j++) {
				mat[x][y].set(ship.getId() + String.valueOf(j));
				lista.shipPlace(ship, mat[x][y]);
				if (or == 1)
					x++;
				else
					y++;
			}
		}
//		System.out.println("Board: randomShipPlacement() :: exit");
	}

	/**
	 * This method is used for the manual placement. It performs all the necessary
	 * checking to validate the placement
	 */
	public void manualPlacement() {
//		System.out.println("Player: manualPlacement()");
		Scanner scan = new Scanner(System.in);
		for (Ship ship : lista.getListOfShips()) {
			boolean ok = true;
			do {
				System.out.println("Introduzca coordenadas(x, y, orientacion) para el barco '" + ship.getType()
						+ "' y de tamanyo " + ship.getSize());
				System.out.print("x:\n>");
				int x = scan.nextInt();
				System.out.print("y:\n>");
				int y = scan.nextInt();
				System.out.println("Orientacion(1=True, 0=False):\n>");
				int or = scan.nextInt();
				ok = placementShip(x, y, ship, or);
			} while (!ok);
		}
		scan.close();
//		System.out.println("Player: manualPlacement() : exit");
	}

	/**
	 * This method checks for the current positions and all other positions that the
	 * ship might take (tam) and its orientation (or) Will return true if its
	 * possible to place in the desired spot and false in any other scenario.
	 * 
	 * @param tam
	 * @param or
	 * @param x
	 * @param y
	 * @return TRUE or FALSE
	 */
	public boolean checkPosition(int tam, int or, int x, int y) {// check que se puede colocar en las coordenadas x & y
		if (or == 1) {

			if (x + tam > size) {
				return false;
			} else {
				for (int i = 0; i < tam; i++) {
					if (!mat[x][y].equals(Cell.WATER_CELL)) {

						return false;
					}
					x++;
				}
			}
		} else if (or == 0) {
			if (y + tam > size) {
				return false;
			} else {
				for (int i = 0; i < tam; i++) {
					if (!mat[x][y].equals(Cell.WATER_CELL)) {

						return false;
					}
					y++;
				}
			}
		} else {
			if (!mat[x][y].equals(Cell.WATER_CELL))
				return false;
		}

		return true;
	}

	/**
	 * A passing method that places the cell of the ship and then goes to the next
	 * cell
	 * 
	 * @param x
	 * @param y
	 * @param ship
	 * @param or
	 * @return TRUE or FALSE
	 */
	public boolean placementShip(int x, int y, Ship ship, int or) {
		if (checkPosition(ship.getSize(), or, x, y)) {
			for (int i = 0; i < ship.getSize(); i++) {
				mat[x][y].set(ship.getId() + String.valueOf(i));
				lista.shipPlace(ship, mat[x][y]);
				if (or == 1)
					x++;
				else
					y++;
			}
			return true;
		} else {
			if (x >= size - 1 || x < 0 || y >= size - 1 || y < 0) {
				System.out.println("Coordenadas incorrectas. Prueba otra vez");
				return false;
			}
			System.out.println("Coordenadas ocupadas. Prueba otra vez");
			return false;
		}
	}

// -------------------------------------------------------------------------- Attack interactions --------------------------------------------------------------------------

	/**
	 * Determines if its a hit on an alive ship <br>
	 * Also call notifyObserver() to publish updates
	 * 
	 * @param pos - position of hit
	 * @return '0' If its just a hit. If its a kill, it returns 'ship size' (ship
	 *         size == points for certain ship)
	 */
	public int posHasHit(Pair<Integer, Integer> pos) {
		int points = -1;
		if (isShip(pos))
			points = isKill(pos);
		else if (mat[pos.getFirst()][pos.getSecond()].get().equals(Cell.KILLED_CELL)
				|| mat[pos.getFirst()][pos.getSecond()].get().equals(Cell.MISSED_CELL)
				|| mat[pos.getFirst()][pos.getSecond()].get().equals(Cell.HITTED_CELL))
			points = -3;
		notifyObserver(new Pair<Integer, Integer>(pos.getFirst(), pos.getSecond()), points);
		updateBoard(pos.getFirst(), pos.getSecond(), points);
		return points;
	}

	public boolean isHit(Pair<Integer, Integer> pos) {
		if (isShip(pos))
			return true;
		else
			return false;
	}

	/**
	 * Is this hit a kill? If yes, how many points do I get?
	 * 
	 * @param position of hit
	 * @return '0' If its just a hit. If its a kill, it returns 'ship size' (ship
	 *         size == points for certain ship)
	 */
	private int isKill(Pair<Integer, Integer> pos) {
		String id = mat[pos.getFirst()][pos.getSecond()].get().substring(0, 2);
		Ship ship = lista.getShip(id);
		if (id != Cell.HITTED_CELL && id != Cell.KILLED_CELL) {
			int pointsForShip = ship.getSize();
			if (ship.getShield()) {
				lista.manageHit(id, pos);
				return -2;
			} else if (lista.manageHit(id, pos)) {
				setShipKilled(id);
				return pointsForShip;
			}
		}
		return 0;
	}

	/**
	 * Will set the ship id to KILLED
	 * 
	 * @param id
	 */
	private void setShipKilled(String id) {
		Ship ship = lista.getShip(id);
		for (Pair<Integer, Integer> pos : ship.getCells()) {
			mat[pos.getFirst()][pos.getSecond()].set(Cell.KILLED_CELL);
		}
		ship.setKilled();
		lista.setShipKilled(id);
	}

	/**
	 * Checks if the given position is in the board. If in the board out == false
	 * else out == true
	 * 
	 * @param pos
	 * @return out
	 */
	public boolean isOut(Pair<Integer, Integer> pos) {
		boolean out = true;
		if ((pos.getFirst() >= 0) && (pos.getFirst() < size) && (pos.getSecond() >= 0) && (pos.getSecond() < size))
			out = false;

		return out;
	}

	/**
	 * Will return true if the cell has already been shot and false in any other
	 * case
	 * 
	 * @param pos
	 * @return alreadyShot
	 */
	public boolean alreadyShot(Pair<Integer, Integer> pos) {
		boolean alreadyShot = false;
		if ((mat[pos.getFirst()][pos.getSecond()].get().equals(Cell.KILLED_CELL))
				|| (mat[pos.getFirst()][pos.getSecond()].get().equals(Cell.MISSED_CELL)))
			alreadyShot = true;
		return alreadyShot;
	}

	/**
	 * Lee el primer simbolo del cell y comprueba si es letra, si lo es entonces es
	 * un barco
	 * 
	 * @param pos[] in the board
	 * @return True if the cell contains a ship, else False
	 */
	public boolean isShip(Pair<Integer, Integer> pos) {
		String cell = null;
		try {
			cell = mat[pos.getFirst()][pos.getSecond()].get();
		} catch (ArrayIndexOutOfBoundsException e) {
			e.getStackTrace();
		}
		return !cell.equals(Cell.HITTED_CELL) && !cell.equals(Cell.KILLED_CELL) && !cell.equals(Cell.WATER_CELL)
				&& !cell.equals(Cell.MISSED_CELL);
	}

// -------------------------------------------------------------------------- Setters & getters --------------------------------------------------------------------------

	public List<Ship> getShipsList() {
		return lista.getListOfShips();
	}

	public int getShipsNumber() {
		return lista.getShipsNumber();
	}

	public void setCellShield(int x, int y) {
		mat[x][y].setShield(true);
	}

	/**
	 * @return the mat
	 */
	public Cell[][] getMat() {
		Cell[][] export = new Cell[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				export[i][j] = new Cell(i, j, mat[i][j].get());
			}
		}
		export = lista.prepareForGUI(export);
//				else {
//					if(lista.getShip(mat[i][j].get()).getSize() ==1 )
//						export[i][j].set("1");
//					else if(mat[i][j].get().charAt(2) == '0')
//						export[i][j].set("S");
//					else if(lista.getShip(mat[i][j].get().charAt(2) == '0') {
//						export[i][j].set("E");
//					}
//					else 
//						export[i][j].set("C");
//					
//				}
		return export;
	}

	/**
	 * @param mat the mat to set
	 */
	public void setMat(Cell[][] mat) {
		this.mat = mat;
	}

	public int getBoardLength() {
		return this.size;
	}

	/**
	 * Actualiza la celda (x, y) del tablero segun el resultado del disparo
	 * 
	 * @param x     coor
	 * @param y     coor
	 * @param isHit boleano de si el disparo fue hit o no
	 */
	public void updateBoard(int x, int y, int points) {
		int[] pos = new int[2];
		pos[0] = x;
		pos[1] = y;
		if ((points == 0))
			mat[x][y].set(Cell.HITTED_CELL);
		else if ((points >= 0))
			mat[x][y].set(Cell.KILLED_CELL);
		else if (mat[x][y].equals(Cell.WATER_CELL))
			mat[x][y].set(Cell.MISSED_CELL);
		else {
			System.err.println("This cell has already been attacked: " + x + "," + y);
		}
	}

	void cargar() {

	};

	public Ship getShip(int x, int y) {

		String id = mat[x][y].get();
		return lista.getShip(id);

	}

	public String toString() {
//		System.out.println("Board: report()");
//		System.out.println("");
//		for (int i = size - 1; i >= 0; i--) {
//			System.out.print("[" + i + "]  ");
//			for (int j = 0; j < size; j++) {
//				String str;
//				str = mat[j][i].get();
//				if (str.equals(Cell.MISSED_CELL) || str.equals(Cell.KILLED_CELL) || str.equals(Cell.WATER_CELL))
//					str = " " + str + " ";
//				System.out.print(str + " ");
//			}
//			System.out.println("");
//		}
//		System.out.print("[ ]  ");
//		for (int j = 0; j < size; j++) {
//			System.out.print("[" + j + "] ");
//		}
//		System.out.println("");
		return null;

	}

	public JSONObject report() {
		JSONObject ob = new JSONObject();
		JSONArray x = new JSONArray();
		JSONArray y = new JSONArray();
		ob.put("Ships", lista.report());
		for (Cell[] xCells : mat) {
			for (Cell yCells : xCells) {
				y.put(yCells.get());
			}
			x.put(y);
			y = new JSONArray();
		}
		ob.put("Matrix", x);
		return ob;
	}

	public void load(JSONObject jo) {
		lista.load(jo.getJSONArray("Ships"));
		JSONArray x = jo.getJSONArray("Matrix");
		JSONArray y = new JSONArray();
		for (int i = 0; i < x.length(); i++) {
			y = x.getJSONArray(i);
			for (int j = 0; j < y.length(); j++) {
				mat[i][j].set(y.getString(j));
			}
		}
	}

}