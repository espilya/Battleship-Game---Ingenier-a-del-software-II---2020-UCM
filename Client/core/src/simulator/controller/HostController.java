package simulator.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import simulator.model.GameSize;

/**
 * Controller that is used for player who hosts the game in PvP mode.
 *
 */
public class HostController extends OnlineController {
	private static final long serialVersionUID = -5943166518867952361L;

	private ServerSocket serverSocket;
	private Socket socketForSendDataToMainServer;
	private BufferedWriter writerMainServer;
	private int mySocket = 12346;
	private boolean isGuestShipPlacementFinished;

	public HostController(GameSize gameSize, String playerId) {
		super(gameSize, playerId);
		System.out.println("[I am HostController]");
		p2pState = ConnectionStates.WAIT_CONNECTION;
		isGuestShipPlacementFinished = false;

		try {
			createServer();
			sendThisHostToMainServer();
			acceptConnections();
			Thread t = new Thread(new ServerSideConnection(socket));
			t.setUncaughtExceptionHandler(h);
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("error");
		}
	}

	/*
	 * Create server socket
	 */
	private void createServer() throws IOException {
		serverSocket = new ServerSocket(12346);
	}

	/**
	 * Send this host information to main server.
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void sendThisHostToMainServer() throws UnknownHostException, IOException {
		// create JSON
		JSONObject o = new JSONObject();
		o.put("id", player.getId());
		o.put("gameSize", gameSize);
		o.put("socket", mySocket);
		// send JSON
		socketForSendDataToMainServer = new Socket("127.0.0.1", 12345);
		writerMainServer = new BufferedWriter(new OutputStreamWriter(socketForSendDataToMainServer.getOutputStream()));
		writerMainServer.write("0" + newLine);
		writerMainServer.flush();
		writerMainServer.write(o.toString(0) + newLine);
		writerMainServer.flush();
	}

	/*
	 * Send request to main server to remove this host from the hosts list.
	 */
	private void sendDeleteRequest() throws IOException {
		writerMainServer.write("1" + newLine);
		writerMainServer.flush();
		socketForSendDataToMainServer.close();
		writerMainServer = null;
	}

	/**
	 * Accept incoming guest connection. Update readerChannel and writerChannel.
	 * Send request to main server to remove this host from the hosts list.
	 * 
	 * @throws JSONException
	 * @throws Exception
	 */
	public void acceptConnections() throws JSONException, Exception {
		System.out.println("Waiting for connections...");
		socket = serverSocket.accept();
		readerChannel = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writerChannel = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		parseCommand();
		System.out.println("Player '" + enemyPlayer + "' has connected.");
		sendDeleteRequest();

	}

	/**
	 * Performs the action when the Exit command is called
	 */
	protected void onExitCommand() {

	}

	/**
	 * Performs the action needed when the onWin command is called
	 */
	protected void onWinCommand() {

	}

	@Override
	public void setPlacementAsFinished() {
		gameState = GameState.WAIT_PLACE;
		if (isGuestShipPlacementFinished) {
			gameState = GameState.PLAY;
			showEnemyBoard = true;
			try {
				send(Commands.START);
				sendBoard();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Performs the action when the Connect command is called
	 * 
	 * @param data
	 * @throws IOException
	 */
	@Override
	protected void onConnectCommand(String data) throws IOException {
		gameState = GameState.PLACE;
		enemyPlayer = data;
		send(Commands.PLACEMENT_START);
		sendData(player.getId());
	}

	/**
	 * Performs the action when the PlacementStart command is called
	 */
	@Override
	protected void onPlacementStartCommand(String data) {
	}

	/**
	 * Performs the action when the PlacementEnd command is called
	 * 
	 * @throws IOException
	 */
	@Override
	protected void onPlacementEndCommand(String data) throws IOException {
		isGuestShipPlacementFinished = true;
		readBoard(data);
		if (gameState == GameState.WAIT_PLACE) {
			gameState = GameState.PLAY;
			showEnemyBoard = true;
			try {
				send(Commands.START);
				sendBoard();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Performs the action when the Start command is called
	 */
	@Override
	protected void onStartCommand(String data) {
	}

	/**
	 * Performs the action when the YouWin command is called
	 */
	@Override
	protected void onYouWinCommand(String data) {
		readBoard(data);
		gameState = GameState.WON;
	}

	/**
	 * Performs the action when the Wait command is called
	 */
	@Override
	protected void onWaitCommand() {
	}

}
