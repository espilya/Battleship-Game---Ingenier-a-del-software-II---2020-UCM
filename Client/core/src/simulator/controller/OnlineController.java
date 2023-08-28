package simulator.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.model.GameSize;
import simulator.model.Player;
import simulator.model.warships.Cell;
import simulator.model.warships.Ship;
import simulator.observer.IObserver;
import utils.Pair;

/**
 * TODO: <br>
 * - SERVER JSON & GLOBAL SCORE ??
 */

/*
 * This Controller is used for the PvP mode
 */
public abstract class OnlineController extends Controller implements IObserver {
	private static final long serialVersionUID = 138030487073491569L;
	protected static String newLine = System.getProperty("line.separator");
	protected boolean showEnemyBoard;

	/**
	 * All host and client possibles states
	 */
	public enum ConnectionStates {
		CONNECTION, WAIT_CONNECTION;
	};

	/**
	 * Commands that host or client can send and receive
	 */
	protected enum Commands {
		CONNECT(), PLACEMENT_START(), PLACEMENT_END(), START(), EXIT(), ATTACK(), HIT(), KILL(), MISS(), INVALID(),
		YOU_WIN(), WAIT(), SHIELD();

		@Override
		public String toString() {
			return this.name();
		}
	};

	protected Socket socket;
	protected Boolean isHost = false;
	protected BufferedWriter writerChannel;
	protected BufferedReader readerChannel;
	protected GameResponse gameResponse;
	protected ConnectionStates p2pState;
	protected Cell[][] enemyBoard;
	protected int pointsObtained;

	/**
	 * Constructor for the OnlineController. As we are using an observer to
	 * communicate the points. After the player is added we will register the
	 * player's board to the observer
	 * 
	 * @param gameSize
	 */
	public OnlineController(GameSize gameSize, String playerId) {
		super(gameSize);
		init(playerId);
	}

	/**
	 * Will initiate the game with the needed information. It will create the Player
	 * object. Only one as this is the PvP mode and the other one will be added
	 * later We make the player an observer to be able to update points
	 */
	private void init(String playerId) {
		System.out.println("OnlineController: init()");
		pointsObtained = 0;
		showEnemyBoard = false;
		player = new Player(playerId, gameSize);
		player.getBoard().registerObserver(this);
	}

	/**
	 * This method controls the loop of the p2p
	 */

	/**
	 * A method that will return the state of the p2p connection
	 * 
	 * @return p2pState
	 */
	public ConnectionStates getStateConnection() {
		return p2pState;
	}

	/**
	 * Overridden method to comunicate about p2p states
	 */
	@Override
	public GameState getState() {
		return gameState;
	}

	/**
	 * ---------------------------------------------------------------------------------------------------------
	 * --------------------------- Placement interactions ----------------------
	 * ---------------------------------------------------------------------------------------------------------
	 */

	/**
	 * This is a passing method that will call the placeShip method in Player Only
	 * needed for player as the Computer doesn't have a manual placement
	 * 
	 * @param cellLocation
	 * @param ship
	 * @param horizzontal
	 */
	@Override
	public void placeShip(int[] cellLocation, Ship ship, boolean horizontal) {
		player.placeShip(cellLocation, ship, horizontal);
	}

	/**
	 * A passing function that will call askRandomPlacement for the Player
	 */
	@Override
	public void randomPlacement() {
		player.askRandomPlacement();
	}

	/**
	 * ---------------------------------------------------------------------------------------------------------
	 * --------------------------- Fire interactions ---------------------------
	 * ---------------------------------------------------------------------------------------------------------
	 */

	// Se esta viendo como se puede pasar ese ataque por el servidor
	// A lo mejor se puede pasar las coordenadas y que el recieveAttack este en el
	// controller para el modo PvP y que se tengo como paramentros un objeto JSon

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
	@Override
	public GameResponse fire(int x, int y, int fireType, boolean horizontal) throws Exception {
		if (gameState == GameState.PLAY) {
			JSONObject j = new JSONObject();
			j.put("fireType", fireType);
			j.put("x", x);
			j.put("y", y);
			j.put("horizontal", horizontal);
			send(Commands.ATTACK);
			sendData(j.toString(0));
			TimeUnit.MILLISECONDS.sleep(50); // wait for parse the response
			System.out.println("gameResponse: " + gameResponse);
			return gameResponse;
		} else
			return GameResponse.INVALID;
	}

	/**
	 * Manage and response to enemy shot
	 */
	@Override
	public GameResponse autoFire(int x, int y, int fireType, boolean horizontal) throws Exception {
		GameResponse response;
		if (fireType == 0) {
			response = player.receiveNormalAttack(new Pair<Integer, Integer>(x, y));
		} else if (fireType == 1) {
			response = player.receiveLineAttack(new Pair<Integer, Integer>(x, y), horizontal);
		} else if (fireType == 2) {
			response = player.receiveAOEAttack(new Pair<Integer, Integer>(x, y));
		} else {
			throw new Exception("invalid attack type");
		}
		if (player.getNumShips() == 0)
			return GameResponse.WON;
		else
			return response;
	}

	/**
	 * ---------------------------------------------------------------------------------------------------------
	 * --------------------------- Getters && Setters ---------------------------
	 * ---------------------------------------------------------------------------------------------------------
	 */
	public String getEnemyId() {
		return enemyPlayer;
	}
	
	@Override
	public List<Ship> getShipsList() {
		return player.getShipsList();
	}

	@Override
	public Cell[][] getEnemyBoard() {
		if (showEnemyBoard) {
			return enemyBoard;
		} else {
			Cell[][] blank = new Cell[gameSize.getSize()][gameSize.getSize()];
			for (int i = 0; i < gameSize.getSize(); i++) {
				for (int j = 0; j < gameSize.getSize(); j++) {
					blank[i][j] = new Cell(i, j, Cell.MISSED_CELL);
				}
			}
			return blank;
		}
	}

	@Override
	public Cell[][] getPlayerBoard() {
		return player.getBoard().getMat();
	}

	/**
	 * ---------------------------------------------------------------------------------------------------------
	 * --------------------------- Server interactions ---------------------------
	 * ---------------------------------------------------------------------------------------------------------
	 */

	/**
	 * Obtains the data of the host.
	 * 
	 * @param hostSocket
	 * @return jObj.toString
	 */
	protected String getHostData(int hostSocket) {
		JSONObject jObj = new JSONObject();
		jObj.put("id", player.getId());
		jObj.put("gameSize", gameSize);
		jObj.put("socket", hostSocket);
		return jObj.toString(0);
	}

	/**
	 * ---------------------------------------------------------------------------------------------------------
	 * --------------------------- Other interactions ---------------------------
	 * ---------------------------------------------------------------------------------------------------------
	 */

	/**
	 * A method to pass the board in Byte form
	 * 
	 * @return the board in Byte form
	 */
	protected String boardToBytes() {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(player.getBoard().getMat());
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	/**
	 * A method to transform the ByteBoard to board
	 * 
	 * @param bytes
	 * @return board
	 */
	protected Cell[][] bytesToBoard(String bytes) {
		Cell[][] board = null;
		byte[] data = Base64.getDecoder().decode(bytes);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(data));
			board = (Cell[][]) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return board;
	}

	/**
	 * A method to parse the needed command for the PvP mode
	 * 
	 * @throws IOException
	 * @throws JSONException
	 * @throws Exception
	 */
	protected void parseCommand() throws IOException, JSONException, Exception {
		String command, data;
		command = read();
		// while ((command = read()) != null) {
		System.out.println("THREAD: " + command);
		switch (Commands.valueOf(command)) {
		case CONNECT:
			data = read();
			onConnectCommand(data);
			break;

		case PLACEMENT_START:
			data = read();
			onPlacementStartCommand(data);
			break;

		case PLACEMENT_END:
			data = read();
			onPlacementEndCommand(data);
			break;

		case START:
			data = read();
			onStartCommand(data);
			break;

		case EXIT:
			onExitCommand();
			break;

		case ATTACK:
			data = read();
			onAttackCommand(data);
			break;

		case HIT:
			gameResponse = GameResponse.HIT;
			data = read();
			onAttackResponseCommand(data);
			break;

		case KILL:
			gameResponse = GameResponse.SUNK;
			data = read();
			onAttackResponseCommand(data);
			break;

		case MISS:
			gameResponse = GameResponse.MISS;
			data = read();
			onAttackResponseCommand(data);
			gameState = GameState.WAIT_ATTACK;
			break;

		case INVALID:
			gameResponse = GameResponse.INVALID;
			data = read();
			onAttackResponseCommand(data);
			break;

		case SHIELD:
			gameResponse = GameResponse.SHIELD;
			data = read();
			onAttackResponseCommand(data);
			gameState = GameState.WAIT_ATTACK;
			break;

		case YOU_WIN:
			gameResponse = GameResponse.MISS;
			data = read();
			onYouWinCommand(data);
			break;

		case WAIT:
			onWaitCommand();
			break;

		default:
			break;
		}
		// }
	}

	/**
	 * Receives the attack command and passes the response
	 * 
	 * @param data
	 * @throws JSONException
	 * @throws Exception
	 */
	protected void onAttackCommand(String data) throws JSONException, Exception {
		JSONObject j = new JSONObject(new JSONTokener(data));
		GameResponse response = autoFire(j.getInt("x"), j.getInt("y"), j.getInt("fireType"),
				j.getBoolean("horizontal"));
		if (response == GameResponse.MISS) {
			send(Commands.MISS);
			gameState = GameState.PLAY;
		} else if (response == GameResponse.SUNK) {
			send(Commands.KILL);
		} else if (response == GameResponse.HIT) {
			send(Commands.HIT);
		} else if (response == GameResponse.SHIELD) {
			send(Commands.SHIELD);
			gameState = GameState.PLAY;
		} else if (response == GameResponse.INVALID) {
			send(Commands.INVALID);
		} else if (response == GameResponse.WON) {
			send(Commands.YOU_WIN);
			gameState = GameState.LOSE;
		}
		JSONObject o = new JSONObject();
		o.put("board", boardToBytes());
		o.put("points", pointsObtained);
		sendData(o.toString(0));
		pointsObtained = 0;
	}

	/**
	 * Informs on what has to be done after an attack has been received
	 * 
	 * @param data
	 * @throws JSONException
	 * @throws Exception
	 */
	protected void onAttackResponseCommand(String data) throws JSONException, Exception {
		JSONObject o = new JSONObject(new JSONTokener(data));
		String boardInBytes = o.getString("board");
		int points = o.getInt("points");
		enemyBoard = bytesToBoard(boardInBytes);
		player.addPoints(points);
	}

	protected abstract void onExitCommand();

	protected abstract void onConnectCommand(String data) throws IOException;

	protected abstract void onPlacementStartCommand(String data);

	protected abstract void onPlacementEndCommand(String data) throws IOException;

	protected abstract void onStartCommand(String data);

	protected abstract void onYouWinCommand(String data);

	protected abstract void onWaitCommand();
	// --</receive command>---

	protected void readBoard(String data) {
		JSONObject o = new JSONObject(new JSONTokener(data));
		String boardInBytes = o.getString("board");
		enemyBoard = bytesToBoard(boardInBytes);
	}

	protected void sendBoard() throws JSONException, IOException {
		JSONObject o = new JSONObject();
		o.put("board", boardToBytes());
		sendData(o.toString(0));
	}

	/**
	 * Sends the start Command so the needed actions can be carried out
	 * 
	 * @param start
	 * @throws IOException
	 */
	protected void send(Commands start) throws IOException {
		System.out.println("[Socket] Sending: " + start.toString());
		writerChannel.write(start.toString() + newLine);
		writerChannel.flush();
	}

	/**
	 * Sends the data String
	 * 
	 * @param data
	 * @throws IOException
	 */
	protected void sendData(String data) throws IOException {
		System.out.println("[Socket] Sending: " + data);
		writerChannel.write(data + newLine);
		writerChannel.flush();
	}

	/**
	 * Will read the data sent to it
	 * 
	 * @return data
	 * @throws IOException
	 */
	protected String read() throws IOException {
		String data = readerChannel.readLine();
		System.out.println("[Socket] Reading: " + data);
		return data;
	}

	/**
	 * Sends the Reset request and then waits for a response
	 */
	@Override
	public void reset() {
	}

	/**
	 * Sends the Exit request and then send to main menu (or other GUI menu)
	 */
	@Override
	public void exit() {
	}

	/**
	 * Updates the points
	 */
	@Override
	public void update(Pair<Integer, Integer> pos, int points) {
		if (points > 0)
			pointsObtained += points;
	}

	/**
	 * Caught Exceptions in other threads. Used to catch lost connection.
	 */
	Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread th, Throwable ex) {
			System.out.println("[Socket] Lost conenction with player: " + enemyPlayer);
			System.out.println("Uncaught exception: " + ex);
			gameState = GameState.LOST_CONNECTION;
		}
	};

	// CLASS
	protected class ServerSideConnection implements Runnable {

		public ServerSideConnection(Socket s) {
			socket = s;
			try {
				readerChannel = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writerChannel = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			while (!socket.isClosed()) {
				try {
					parseCommand();
				} catch (Exception e) {
					throw new RuntimeException(e + " " + e.getMessage());
				}
				if (socket.isClosed()) {
					System.out.println("socket closed");
				}
			}
		}

//		public void parse() {
//			try {
//				parseCommand();
//			} catch (Exception e) {
//				throw new RuntimeException(e + " " + e.getMessage());
//			}
//		}

	}


}
