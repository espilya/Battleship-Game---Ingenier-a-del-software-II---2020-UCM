package simulator.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import simulator.model.GameSize;

/**
 * This controller is used for the Guest in a PvP game
 *
 */
public class GuestController extends OnlineController {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for the guest controller
	 * 
	 * @param gameSize
	 * @param hostSocket
	 */
	public GuestController(GameSize gameSize, int hostSocket, String playerId) {
		super(gameSize, playerId);
		System.out.println("[I am GuestController]");
		p2pState = ConnectionStates.CONNECTION;
		connectToHost(hostSocket);
		Thread t = new Thread(new ServerSideConnection(socket));
		t.setUncaughtExceptionHandler(h);
		t.start();
	}

	/**
	 * As this is the Guest Controller it will need to connect to the hosts game.
	 * That is why this method is needed. The hostSocket is received and the needed
	 * actions are taken to make a connection between the Guest and the Host
	 * 
	 * @param hostSocket
	 * @return connected
	 */
	private boolean connectToHost(int hostSocket) {
		boolean connected = false;
		int connectionErrorNum = 0;
		final int timeoutAt = 3;
		while (!connected && connectionErrorNum < timeoutAt) {
			try {
				socket = new Socket("127.0.0.1", hostSocket);
				writerChannel = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				readerChannel = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				if (socket.isConnected()) {
					send(Commands.CONNECT);
					sendData(player.getId());
					connected = true;
				} else
					TimeUnit.SECONDS.sleep(5);
			} catch (IOException | InterruptedException e) {
				connectionErrorNum++;
				System.out.println("Cannot connect to the server. Try number " + connectionErrorNum + '/' + timeoutAt);
				System.err.println("Wow. Caught error [" + e.getMessage() + "] Much error. So programmer.");
			}
		}
		if (!connected)
			System.err.println("Exit from PvP game mode.");
		else
			System.out.println("Connected.");
		return connected;
	}

	/**
	 * Performs the action when the Exit command is called
	 */
	@Override
	protected void onExitCommand() {
	}

	/**
	 * Performs the action when the Connect command is called
	 * 
	 * @param data
	 * @throws IOException
	 */
	@Override
	protected void onConnectCommand(String data) {
	}

	/**
	 * Changes the state of the p2pState to place
	 */
	@Override
	protected void onPlacementStartCommand(String data) {
		enemyPlayer = data;
		gameState = GameState.PLACE;
	}

	/**
	 * Performs the action when the PlacementEnd command is called
	 * 
	 * @throws IOException
	 */
	@Override
	protected void onPlacementEndCommand(String data) {
	}

	/**
	 * Performs the action when the Start command is called
	 */
	@Override
	protected void onStartCommand(String data) {
		readBoard(data);
		showEnemyBoard = true;
		gameState = GameState.WAIT_ATTACK;
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
		if (gameState == GameState.WAIT_PLACE)
			gameState = GameState.WAIT_PLACE;
		// else

	}

	@Override
	public void setPlacementAsFinished() {
		gameState = GameState.WAIT_PLACE;
		if (gameState == GameState.WAIT_PLACE) {
			try {
				send(Commands.PLACEMENT_END);
				sendBoard();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
