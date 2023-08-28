package simulator.model.warships;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Pair;

/**
 * Clase del objeto de los diferentes ships TODO: ALIVE_CELL pasar a P00
 * 
 * @author puppy
 *
 */
public abstract class Ship implements Serializable {
	private static final long serialVersionUID = -8892819495958979421L;

	protected final String ALIVE_CELL = "O";
	protected final String KILLED_CELL = "X";

	/**
	 * LinkedHashMap that contains the position of the ship's cell. Also is used to
	 * know that the cell is killed or not. <br>
	 * Key = Pair(Integer, Integer) <br>
	 * Value = Cell <br>
	 * The value is not a cell from Board matrix
	 */
	protected Map<Pair<Integer, Integer>, Cell> cellsLocation;
	// ----- Posiblemente se hara un refactor de esta clase, sobre todo del mapa

	/**
	 * True - horizontal, False - vertical
	 */
	protected boolean orientationHorizontal;
	protected int size;
	protected boolean alive;
	protected String id;
	protected String type;
	protected int aliveCells;
	protected boolean shield;

	Ship(String id) {
		this.id = id;
		alive = true;
		shield = false;
	}

	/**
	 * Manages the hit on the ship. <br>
	 * Kill the cell if the ship have not shield.
	 * 
	 * @return True if the ship is sunk
	 * @return False if is only a hit in the ship OR hit on shield
	 */
	public boolean hit(int x, int y) {
		if (!shield) {
			aliveCells--;
			setCellState(x, y, KILLED_CELL);
			if (aliveCells == 0)
				alive = false;
		} else {
			shield = false;
		}
		return !alive;
	}

	// Sets the cell where the ship is locates to kill
	public void setKilled() {
		for (Map.Entry<Pair<Integer, Integer>, Cell> entry : cellsLocation.entrySet()) {
			entry.getValue().set(Cell.KILLED_CELL);
		}
	}

	/**
	 * Adds the ship's cells to the LinkedHashMap - cellsLocation
	 * 
	 * @param cell
	 */
	public void placeShipCells(Cell cell) {
		cellsLocation.put(new Pair<>(cell.getX(), cell.getY()), cell);
	}

	// -------------------------------
	// Getters & Setters
	// -------------------------------
	public List<Pair<Integer, Integer>> getCells() {
		List<Pair<Integer, Integer>> cells = new ArrayList<Pair<Integer, Integer>>();
		for (Map.Entry<Pair<Integer, Integer>, Cell> entry : cellsLocation.entrySet()) {
			cells.add(entry.getKey());
		}
		return cells;
	}

	public boolean getShield() {
		return shield;
	}

	public void setShield(boolean shield) {
		this.shield = shield;
	}

	public void destroyShield() {
		this.shield = false;
	}

	public String getId() {
		return id;
	}

	public int getSize() {
		return size;
	}

	public int getX() {
		return cellsLocation.entrySet().iterator().next().getValue().getX();
	}

	public int getY() {
		return cellsLocation.entrySet().iterator().next().getValue().getY();
	}

	public String getCellState(int x, int y) {
		return cellsLocation.get(new Pair<>(x, y)).get();
	}

	private void setCellState(int x, int y, String n) {
		cellsLocation.get(new Pair<>(x, y)).set(n);
	}

	public boolean getState() {
		return alive;
	}

	/**
	 * @return true == horizontal
	 */
	public boolean getOrientation() {
		return orientationHorizontal;
	}

	/**
	 * set 'orientationHorizontal'
	 */
	public void setOrientation(boolean b) {
		orientationHorizontal = b;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		String out = id + ": ";
		for (Map.Entry<Pair<Integer, Integer>, Cell> entry : cellsLocation.entrySet()) {
			Pair<Integer, Integer> key = entry.getKey();
			out += "(";
			out += key;
			out += ")";

		}
		return out;
	}

	public int getAliveCells() {
		return this.aliveCells;
	}

	public void setAliveCells(int ac) {
		this.aliveCells = ac;
	}

	public JSONObject report() {
		JSONObject ship = new JSONObject();
		JSONArray celdas = new JSONArray();
		ship.put("id", id);
		for (Map.Entry<Pair<Integer, Integer>, Cell> entry : cellsLocation.entrySet()) {
			celdas.put(entry.getKey());
		}
		ship.put("Cells", celdas);
		return ship;
	}

	public void loadShip(List<Pair<Integer, Integer>> shipCells) {
		for (int i = 0; i < shipCells.size(); i++) {
			Cell cell = new Cell(shipCells.get(i).getFirst(), shipCells.get(i).getSecond(), (id + i));
			placeShipCells(cell);
		}

	}

}