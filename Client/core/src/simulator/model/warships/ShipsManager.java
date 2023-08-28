package simulator.model.warships;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.GameSize;
import utils.Pair;

public class ShipsManager implements Serializable {
	private static final long serialVersionUID = 8344310474834990295L;

	/**
	 * LinkedHashMap that contains a list of all alive ships. Key - Id. Value - Ship
	 */
	private HashMap<String, Ship> map;

	private int shipsNumber;

	public ShipsManager() {
		map = new LinkedHashMap<String, Ship>();
		shipsNumber = 0;
	}

	/**
	 * Manages the creation of the ships
	 * 
	 * @param modo
	 */
	public void createShips(GameSize modo) {
		String key = "P";
		for (int i = 0; i < modo.getNumPatrolBoat(); i++) {
			map.put((key + i), new PatrolBoat((key + i)));
			shipsNumber++;
		}

		key = "D";
		for (int i = 0; i < modo.getNumDestroyer(); i++) {
			map.put((key + i), new Destroyer((key + i)));
			shipsNumber++;
		}

		key = "S";
		for (int i = 0; i < modo.getNumSubmarine(); i++) {
			map.put((key + i), new Submarine((key + i)));
			shipsNumber++;
		}

		key = "C";
		for (int i = 0; i < modo.getNumCruiser(); i++) {
			map.put((key + i), new Cruiser((key + i)));
			shipsNumber++;
		}

		key = "B";
		for (int i = 0; i < modo.getNumBattleship(); i++) {
			map.put((key + i), new Battleship((key + i)));
			shipsNumber++;
		}

		key = "R";
		for (int i = 0; i < modo.getNumCarrier(); i++) {
			map.put((key + i), new Carrier((key + i)));
			shipsNumber++;
		}
	}

	// used for undo destroyed ship
	public Ship createShip(String id) {
		Ship ship = null;
		switch (id.charAt(0)) {
		case 'P':
			ship = new PatrolBoat((id));
			map.put((id), ship);
			shipsNumber++;
			break;
		case 'D':
			ship = new Destroyer((id));
			map.put((id), ship);
			shipsNumber++;
			break;
		case 'S':
			ship = new Submarine((id));
			map.put((id), ship);
			shipsNumber++;
			break;
		case 'C':
			ship = new Cruiser((id));
			map.put((id), ship);
			shipsNumber++;
			break;
		case 'B':
			ship = new Battleship((id));
			map.put((id), ship);
			shipsNumber++;
			break;
		case 'R':
			ship = new Carrier((id));
			map.put((id), ship);
			shipsNumber++;
			break;
		}
		return ship;
	}

	/**
	 * Manages the placement of the ships
	 * 
	 * @param ship
	 * @param cell
	 */
	public void shipPlace(Ship ship, Cell cell) {
		try {
			ship.placeShipCells(cell);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Manages the hit on the ships
	 * 
	 * @return True if the ship is sunk (is a kill)
	 * @return False if is only a hit in the ship or if is hit on shield
	 */
	public boolean manageHit(String id, Pair<Integer, Integer> pos) {
		Ship ship = map.get(id);
		boolean isKill = ship.hit(pos.getFirst(), pos.getSecond());
		return isKill;
	}

	public void setShipKilled(String id) {
		shipsNumber--;
		removeShip(id);
	}

	// -------------------------------------------------------------------------
	// ---------------------------------Privates--------------------------------
	// -------------------------------------------------------------------------

	private void removeShip(String id) {
		map.remove(id);
	}

	// -------------------------------------------------------------------------
	// ---------------------------------GETTERS---------------------------------
	// -------------------------------------------------------------------------

	public int getShipsNumber() {
		return shipsNumber;
	}

	public int getSize(Ship ship) {
		return map.get(ship.getId()).getSize();
	}

	public int getSize(String id) {
		return map.get(id).getSize();
	}

	public List<Ship> getListOfShips() {
		ArrayList<Ship> list = new ArrayList<Ship>();
		Set<String> keys = map.keySet();
		for (String k : keys) {
			list.add(map.get(k));
		}
		return Collections.unmodifiableList(list);
	}

	public int getX(String id) {
		return map.get(id).getX();
	}

	public int getX(Ship ship) {
		return map.get(ship.getId()).getX();
	}

	public int getY(String id) {
		return map.get(id).getY();
	}

	public int getY(Ship ship) {
		return map.get(ship.getId()).getY();
	}

	public boolean getOrientation(Ship ship) {
		return map.get(ship.getId()).getOrientation();
	}

	public String getType(Ship ship) {
		return map.get(ship.getId()).getType();
	}

	public Ship getShip(String id) {
		return map.get(id.substring(0, 2));
	}

	public boolean getState(Ship ship) {
		return map.get(ship.getId()).getOrientation();

	}

	public String toString() {
		System.out.println("Actual shipList:");
		for (Ship i : getListOfShips()) {
			System.out.print(i.toString() + " - ");
		}
		System.out.println("");
		return null;
	}

	public JSONArray report() {
		JSONArray ships = new JSONArray();
		for (Map.Entry<String, Ship> entry : map.entrySet()) {
			Ship value = entry.getValue();
			ships.put(value.report());
		}
		return ships;
	}

	public void load(JSONArray ja) {
		JSONObject jShip = null;
		JSONArray jCells = null;
		List<Pair<Integer, Integer>> shipCells; // <<x, y>, cell>
		String id;
		for (int i = 0; i < ja.length(); i++) {
			String cell;
			jShip = ja.getJSONObject(i);
			id = jShip.getString("id");
			jCells = jShip.getJSONArray("Cells");
			shipCells = new ArrayList<Pair<Integer, Integer>>();
			for (int j = 0; j < jCells.length(); j++) {
				cell = jCells.getString(j);
				shipCells.add(new Pair<Integer, Integer>(Character.getNumericValue(cell.charAt(0)),
						Character.getNumericValue(cell.charAt(2))));
			}
			Ship ship = getShip(id);
			if (ship == null) {
				// crear un nuevo ship
				ship = createShip(id);
			}
			ship.loadShip(shipCells);
		}

	}

	/**
	 * Manages the ships and prepares them for the GUI <br>
	 * Cell.get() = [position][type][orientation][shield] <br>
	 * <br>
	 * 
	 * position = [S]tart-[C]enter-[E]nd <br>
	 * type = [S]ingle-[N]ormal <br>
	 * orientation = [H]orizontal-[V]ertical <br>
	 * shield = 1-On, 0-Off<br>
	 * 
	 * @param export
	 * @return
	 */
	public Cell[][] prepareForGUI(Cell[][] export) {
		for (Map.Entry<String, Ship> entry : map.entrySet()) {
			Ship ship = entry.getValue();
			List<Pair<Integer, Integer>> lista = ship.getCells();
			for (int i = 0; i < lista.size(); i++) {
				Pair<Integer, Integer> pos = lista.get(i);
				if (!ship.getCellState(pos.getFirst(), pos.getSecond()).equals("O")) {
					if (ship.getId().charAt(0) == 'P') {
						if (ship.getOrientation()) {
							if (ship.getShield())
								export[pos.getFirst()][pos.getSecond()].set("SSH1");
							else
								export[pos.getFirst()][pos.getSecond()].set("SSH0");
						} else {
							if (ship.getShield())
								export[pos.getFirst()][pos.getSecond()].set("SSV1");
							else
								export[pos.getFirst()][pos.getSecond()].set("SSV0");
						}
					} else {
						if (ship.getOrientation()) {
							if (ship.getShield()) {
								if (i == 0)
									export[pos.getFirst()][pos.getSecond()].set("SNH1");
								else if (i > 0 && i < lista.size() - 1)
									export[pos.getFirst()][pos.getSecond()].set("CNH1");
								else
									export[pos.getFirst()][pos.getSecond()].set("ENH1");

							} else {
								if (i == 0)
									export[pos.getFirst()][pos.getSecond()].set("SNH0");
								else if (i > 0 && i < lista.size() - 1)
									export[pos.getFirst()][pos.getSecond()].set("CNH0");
								else
									export[pos.getFirst()][pos.getSecond()].set("ENH0");
							}
						} else {
							if (ship.getShield()) {
								if (i == 0)
									export[pos.getFirst()][pos.getSecond()].set("ENV1");
								else if (i > 0 && i < lista.size() - 1)
									export[pos.getFirst()][pos.getSecond()].set("CNV1");
								else
									export[pos.getFirst()][pos.getSecond()].set("SNV1");
							} else {
								if (i == 0)
									export[pos.getFirst()][pos.getSecond()].set("ENV0");
								else if (i > 0 && i < lista.size() - 1)
									export[pos.getFirst()][pos.getSecond()].set("CNV0");
								else
									export[pos.getFirst()][pos.getSecond()].set("SNV0");
							}
						}
					}
				}
			}
		}
		return export;
	}

}
