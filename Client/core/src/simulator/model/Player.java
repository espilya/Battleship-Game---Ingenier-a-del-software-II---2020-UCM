package simulator.model;

import java.io.Serializable;
import java.util.List;

import org.json.JSONObject;

import simulator.controller.GameResponse;
import simulator.model.warships.Cell;
import simulator.model.warships.Ship;
import simulator.observer.IObserver;
import utils.GenerationControl;
import utils.Pair;

/**
 * The player class is the model for the player. Each player has their own
 * board, points, and their id
 */
public class Player implements IObserver, IAttacks, Serializable {
	private static final long serialVersionUID = 2512846372348463011L;

	protected String id;
	protected int points;
	protected Board board;
	GenerationControl genControl;
	protected Cell[][] historial; // por ahora lo dejo asi, mas tarde creare una clase generica matrix que
	// usaremos en todos los sitios donde hay matriz

	/**
	 * Constructor for the player class
	 * 
	 * @param id
	 * @param tam
	 */
	public Player(String id, GameSize tam) {
//		System.out.println("Player: constr()");
		this.id = id;
		points = 0;
		board = new Board(tam);
		genControl = new GenerationControl(tam);
		int size = tam.getSize();
		historial = new Cell[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				historial[i][j] = new Cell(i, j, Cell.WATER_CELL);
			}
		}
	}

// -------------------------------------------------------------------------- IObserver implementation --------------------------------------------------------------------------

	/**
	 * Updates the necessary elements contained by the player.
	 * 
	 * If int p is greater that 0 it means that a ship was destroyed. That is why we
	 * set the given cell to KILLED_CELL
	 * 
	 * @param pos
	 * @param p
	 */
	@Override
	public void update(Pair<Integer, Integer> pos, int p) {
		if (p > 0) {
			points += p;
			historial[pos.getFirst()][pos.getSecond()].set(Cell.KILLED_CELL);
		} else if (p == 0) {
			// Que pasara si es solo hit

			// ya que para el jugador no importa saber si exactamente es hit o kill, podemos
			// pintar el historial de todos modos para despues "posiblemente" usar lo en la
			// GUI

			historial[pos.getFirst()][pos.getSecond()].set(Cell.KILLED_CELL);
		} else {
			// Que pasara si es un miss
		}
	}

// -------------------------------------------------------------------------- Placement interactions --------------------------------------------------------------------------
	/**
	 * Manages the placement of the ship and places it in the given cells
	 * 
	 * @param cellLocation
	 * @param ship
	 * @param horizontal
	 */
	public void placeShip(int[] cellLocation, Ship ship, boolean horizontal) {
		board.placementShip(cellLocation[0], cellLocation[1], ship, (horizontal ? 1 : 0));
		board.toString();
	}

	/**
	 * Manages the placement of the ships in a random order.
	 */
	public void askRandomPlacement() {
		// System.out.println("Player: askRandomPlacement()");
		board.randomShipPlacement();
		// board.toString(); //show the board in console
	}

// -------------------------------------------------------------------------- Attack interactions --------------------------------------------------------------------------

	/**
	 * Given a position it recieves the attack on said position
	 * 
	 * @param pos
	 */
	@Override
	public GameResponse receiveNormalAttack(Pair<Integer, Integer> pos) {
		return receiveAttack(pos);
	}

	/**
	 * Given a position it will attack the whole line that be horizontal or vertical
	 * (decided by the boolean horizontal) This is achieved by attacking every cell
	 * of that line individually with a for loop
	 * 
	 * @param pos
	 * @param horizontal
	 */
	@Override
	public GameResponse receiveLineAttack(Pair<Integer, Integer> pos, boolean horizontal) {
		GameResponse temp = GameResponse.MISS;
		GameResponse response = GameResponse.MISS;
		for (int i = 0; i < board.getBoardLength(); i++) {
			if (!horizontal) {
				pos.setSecond(i);
				temp = receiveAttack(pos);

			} else {
				pos.setFirst(i);
				temp = receiveAttack(pos);
			}
			if (temp.equals(GameResponse.HIT) && !response.equals(GameResponse.SUNK)) {
				response = temp;
			} else if (temp.equals(GameResponse.SUNK)) {
				response = temp;
			}

		}
		buySkills(10);
		return response;
	}

	/**
	 * Given a position it will attack all cells adjacent to the initial cell This
	 * is achieved by attacking every cell of that adjacency individually with a for
	 * loop
	 * 
	 * @param pos
	 */
	@Override
	public GameResponse receiveAOEAttack(Pair<Integer, Integer> pos) {
		GameResponse response = GameResponse.MISS;
		GameResponse temp = GameResponse.MISS;
		Pair<Integer, Integer> par = new Pair<Integer, Integer>(0, 0);

		for (int a = 0; a <= 2; a++) {
			for (int b = 0; b <= 2; b++) {
				par.setFirst(pos.getFirst() - 1 + a);
				par.setSecond(pos.getSecond() - 1 + b);
				if (!board.isOut(par)) {
					temp = receiveAttack(par);
				}
				if (temp.equals(GameResponse.HIT) && !response.equals(GameResponse.SUNK)) {
					response = temp;
				} else if (temp.equals(GameResponse.SUNK)) {
					response = temp;
				}
			}
		}
		buySkills(8);
		return response;
	}

	/**
	 * Depending on what the hit is classified as will return one game response or
	 * another.
	 * 
	 * @param pos
	 * @return GameResponse
	 */
	private GameResponse receiveAttack(Pair<Integer, Integer> pos) {
//		System.out.println("Player: reciveAttack()");
		int hit = -1;
		hit = board.posHasHit(pos);

		if (hit == 0)
			return GameResponse.HIT;
		else if (hit > 0)
			return GameResponse.SUNK;
		else if (hit == -1)
			return GameResponse.MISS;
		else if (hit == -2)
			return GameResponse.SHIELD;
		else
			return GameResponse.INVALID;
	}

//--------------------------------------------------------------------------------
	/**
	 * Places a shield on the given cell
	 * 
	 * @param pos
	 * @return
	 */
	public GameResponse receiveDefenseCell(Pair<Integer, Integer> pos) {
//		System.out.println("Player: receiveDefense()");
		boolean res = false;
		res = board.isShip(pos);
		if (res) {
			board.setCellShield(pos.getFirst(), pos.getSecond());
		}
		return GameResponse.MISS;
	}

	/**
	 * Places a shield on the whole boat associates with that cell
	 * 
	 * @param pos
	 * @return
	 */
	public GameResponse reciveDefenseShip(Pair<Integer, Integer> pos) {
		if (board.isShip(pos)) {
			Ship s = board.getShip(pos.getFirst(), pos.getSecond());
			s.setShield(true);
		}
		return GameResponse.MISS;
	}

// -------------------------------------------------------------------------- Setters, getters & increments --------------------------------------------------------------------------

	public void addPoints(int toAdd) {
		points += toAdd;
	}

	public List<Ship> getShipsList() {
		return board.getShipsList();
	}

	public int getPoints() {
		return points;
	}

	public String getId() {
		return id;
	}

	public int getNumShips() {
		return board.getShipsNumber();
	}

	public Board getBoard() {
		return board;
	}

	public String toString() {
		board.toString();
		return null;
	}

	public GenerationControl getGenControl() {
		return genControl;
	}

	public void setGenControl(GenerationControl genControl) {
		this.genControl = genControl;
	}

	public int getBoardSize() {
		return genControl.getBoardSize();
	}

	public JSONObject report() {
		JSONObject ob = new JSONObject();
		ob.put("id", id);
		ob.put("points", points);
		ob.put("board", board.report());
		return ob;
	}

	public void load(JSONObject jo) {
		id = jo.getString("id");
		points = jo.getInt("points");
		board.load(jo.getJSONObject("board"));
	}

	public void setBoard(Cell[][] mat) {
		board.setMat(mat);
	}

	/**
	 * Resta los puntos al jugador al comprar una habilidad
	 * 
	 * @param p Puntos
	 */
	public void buySkills(int p) {
		this.points -= p;
	}

}