package simulator.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.json.JSONObject;

import simulator.model.BotDifficulty;
import simulator.model.Computer;
import simulator.model.GameSize;
import simulator.model.Player;
import simulator.model.warships.Cell;
import simulator.model.warships.Ship;
import utils.Pair;

/**
 * This Controller is used for the PvE Game Mode
 */
public class LocalController extends Controller {
	private static final long serialVersionUID = 3323912481939358336L;

	private Computer computer;
	private BotDifficulty botDifficulty;
	private static String _outFile = null;
	private boolean playerWait;
	/**
	 * Numero del turno
	 */
	private int turn;
	/**
	 * El turno donde hace el save
	 */
	@SuppressWarnings("unused")
	private int saveTurn;

	/**
	 * Constructor for LocalController. It will create the object with the given
	 * gameSize and will initiate the game
	 * 
	 * @param gameSize
	 * @param botDifficulty
	 */
	public LocalController(GameSize gameSize, BotDifficulty botDifficulty) {
		super(gameSize);
		// this.botDifficulty = botDifficulty;
		turn = 0;
		// otherPlayer = "Computer: " + botDifficulty.toString();
		init(gameSize, botDifficulty);
	}
	
	
	public LocalController() {
		super(null);
	}

	/**
	 * Will initiate the game with the needed information. It will create the 2
	 * players that is the Player and the Computer as the game mode is PvE The Ships
	 * are also placed on each players board. The computer is always a random ships
	 * placement while the player can be random or placed manually. All this is
	 * decided in the GUI
	 * 
	 * @param size
	 * @param botDif
	 */
	private void init(GameSize size, BotDifficulty botDif) {

		String idA = "testPlayer_1";

		this.gameSize = size;
		this.botDifficulty = botDif;

		player = new Player(idA, gameSize);
		computer = new Computer("AI", gameSize, botDifficulty, this);
		playerWait = false;

		// playerA.askShipPlacement();
		computer.askRandomPlacement();
		player.getBoard().registerObserver(computer);
		computer.getBoard().registerObserver(player);
//		System.out.println("//Objects created. Waiting for ships place.//");

	}

	/**
	 * Will intitate the game again making it a fresh game
	 */
	public void reset() {
		init(gameSize, botDifficulty);
	}

	/**
	 * This is a passing method that will call the placeShip method in Player Only
	 * needed for player as the Computer doesn't have a manual placement
	 * 
	 * @param cellLocation
	 * @param ship
	 * @param horizzontal
	 */
	public void placeShip(int[] cellLocation, Ship ship, boolean horizontal) {
		player.placeShip(cellLocation, ship, horizontal);
		try {
			saveForUndo();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used for player fire action.
	 * 
	 * @param x          pos
	 * @param y          pos
	 * @param fireType   0 = normal shot , 1 = attack line, 2 = AoE shot
	 * @param horizontal
	 * @return GameResponse
	 * @throws Exception
	 */
	public GameResponse fire(int x, int y, int fireType, boolean horizontal) throws Exception {
		GameResponse response = GameResponse.INVALID;
		if (!playerWait) {
			if (fireType == 0) {
				response = computer.receiveNormalAttack(new Pair<Integer, Integer>(x, y));
				Pair<Integer, Integer> pos = new Pair<Integer, Integer>(x, y);
				player.getGenControl().removeFromPossibles(pos);
			} else if (fireType == 1) {
				response = computer.receiveLineAttack(new Pair<Integer, Integer>(x, y), horizontal);
			} else if (fireType == 2) {
				response = computer.receiveAOEAttack(new Pair<Integer, Integer>(x, y));
			} else {
				throw new Exception("invalid attack type");
				// las excepciones se haran despues, por ahora la dejo asi
			}
			if (response == GameResponse.INVALID || response == GameResponse.MISS)
				playerWait = true;
		} else {
			// Si toca esperar no se hace nada
		}
		if (computer.getNumShips() == 0)
			return GameResponse.WON;
		else
			return response;
	}

	/**
	 * Used for IA fire
	 * 
	 * @return GameResponse :: The response of the fire
	 * @throws Exception
	 */
	public GameResponse autoFire(int x, int y, int fireType, boolean horizontal) throws Exception {
		GameResponse response = GameResponse.INVALID;
		if (playerWait) {
			if (fireType == 0) {
				response = player.receiveNormalAttack(computer.askAttackAction(this));
				Pair<Integer, Integer> pos = new Pair<Integer, Integer>(x, y);
				computer.getGenControl().removeFromPossibles(pos);
			} else if (fireType == 1) {
				response = player.receiveLineAttack(computer.askAttackAction(this), horizontal);
			} else if (fireType == 2) {
				response = player.receiveAOEAttack(computer.askAttackAction(this));
			} else {
				throw new Exception("invalid attack type");
			}
			++turn;
			saveForUndo();
			if (response == GameResponse.INVALID || response == GameResponse.MISS)
				playerWait = false;

		}
		if (player.getNumShips() == 0)
			return GameResponse.WON;
		else
			return response;
	}

	/**
	 * Used for setting the shield type.<br>
	 * Por cuestiones de tiempo y poca urgencia de esta implementacion en el modo
	 * PvE se dejara su implementacion para un futuro.
	 * 
	 * @param x          pos
	 * @param y          pos
	 * @param typeShield 0 = single cell, 1 = whole boat
	 * @return GameResponse response
	 * @throws Exception
	 */
	@Override
	public void setShield(int x, int y, int typeShield) throws Exception {
	}

	/**
	 * A passing function that will call askRandomPlacement for the Player
	 */
	public void randomPlacement() {
		player.askRandomPlacement();
	}

	/**
	 * --- Memento interactions & Others interactions ---
	 */

	public void undo() {
		if (turn > 0)
			try {
				loadForUndo();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public JSONObject report() {
		JSONObject jo = new JSONObject();
		jo.put("GameSize", gameSize);
		jo.put("Player", player.report());
		jo.put("Computer", computer.report());
		return jo;
	}

	public void saveForUndo() throws IOException {
		try {
			String _outFile = "turn" + turn + ".dat";
			OutputStream out = _outFile == null ? System.out
					: new FileOutputStream(new File("savesForUndo/" + _outFile));
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(this.computer);
			oos.writeObject(this.player);
			oos.writeObject(this.turn);
		} catch (FileNotFoundException e) {
			System.err.println("A " + e.getMessage() + "occurred during creating file: " + _outFile);
			e.printStackTrace();
		}
	}

	public void loadForUndo() throws IOException, ClassNotFoundException {
		if (turn > 0)
			--turn;
		String _inFile = "turn" + turn + ".dat";
		try {
			InputStream in = new FileInputStream(new File("savesForUndo/" + _inFile));
			ObjectInputStream ois = new ObjectInputStream(in);
			this.computer = (Computer) ois.readObject();
			this.player = (Player) ois.readObject();
			this.turn = (int) ois.readObject();
		} catch (FileNotFoundException e) {
			System.err.println("A " + e.getMessage() + "occurred during read file: " + _inFile);
			e.printStackTrace();
		}
	}

	public boolean save() throws IOException {
		try {
			String _outFile = "savedGame.dat";
			OutputStream out = _outFile == null ? System.out : new FileOutputStream(new File("savedGames/" + _outFile));
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(this.computer);
			oos.writeObject(this.botDifficulty);
			oos.writeObject(this.gameSize);
			oos.writeObject(this.gameState);
			oos.writeObject(this.player);
			oos.writeObject(this.playerWait);
			oos.writeObject(this.saveTurn);
			oos.writeObject(this.turn);
			oos.writeObject(this.enemyPlayer);
			this.saveTurn = this.turn;
		} catch (FileNotFoundException e) {
			System.err.println("A " + e.getMessage() + "occurred during creating file: " + _outFile);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean load(String file)  {
		String _inFile = file;
		try {
			InputStream in = new FileInputStream(new File("savedGames/" + _inFile));
			ObjectInputStream ois = new ObjectInputStream(in);
			this.computer = (Computer) ois.readObject();
			this.botDifficulty = (BotDifficulty) ois.readObject();
			this.gameSize = (GameSize) ois.readObject();
			this.gameState = (GameState) ois.readObject();
			this.player = (Player) ois.readObject();
			this.playerWait = (boolean) ois.readObject();
			this.saveTurn = (int) ois.readObject();
			this.turn = (int) ois.readObject();
			this.enemyPlayer = (String) ois.readObject();
		} catch (Exception e) {
			System.err.println("A " + e.getMessage() + "occurred during read file: " + _inFile);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * ---------------------------------------------------------------------------------------------------------
	 * --------------------------- Getters && Setters ---------------------------
	 * ---------------------------------------------------------------------------------------------------------
	 */

	/**
	 * @return the size
	 */
	public GameSize getGameType() {
		return gameSize;
	}

	/**
	 * @return the shipList
	 */
	public List<Ship> getShipsList() {
		return player.getShipsList();
	}

	public Cell[][] getPlayerBoard() {
		return player.getBoard().getMat();
	}

	public Cell[][] getEnemyBoard() {
		return computer.getBoard().getMat();
	}

	/**
	 * Will set all classes to null
	 */
	@Override
	public void exit() {
		System.out.println("Game: exit()");
		turn = 0;
		saveTurn = 0;
		// mementos = null;
		gameSize = null;
		botDifficulty = null;
		player = null;
		computer = null;
	}

	@Override
	public void setPlacementAsFinished() {
		// Nothing
	}

}
