package launcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Command;
import utils.Host;

/**
 * Server that contains, manage and can represent the list of actual hosts that
 * are waiting players to create a game. <Br>
 * It can read and manage 3 commands: 0 - Add host to the list. <br>
 * 1 - Remove host from the list. <br>
 * 2 - Get list of actual hosts.<br>
 * To use one of the comments is neccesary send [0-2] integer to '12345' server
 * socket <br>
 * For each connection it creates a new thread that constantly read the input
 * stream. <br>
 * When the connection is lost or it receive remove request, ther server deletes
 * the host that is related to this connection (only if it has created hosts).
 * <br>
 * <br>
 * More about server logic. <br>
 * La logica del server: <br>
 * - Tener un listado de los host activos<br>
 * - Para que un host aparezca en el listado hace falta mandar un mensaje al
 * server<br>
 * - Si un host se completo, el server recive un mensaje y elimina a ese
 * host<br>
 * 
 * Inputs del servidor:<br>
 * - Comando para pedir la lista de hosts<br>
 * - Comando de anadir un host a la lista:<br>
 * --- id del host, tipo/modo de partida, socket del host<br>
 * - Comando de eliminar un host de la lista:<br>
 * --- id del host<br>
 * 
 * Outputs del servidor:<br>
 * - Un listado con toda la informacion acerca los disponibles hosts:<br>
 * --- (id del creador del host, tamano de la partida, puerto/ip/socket..(?))
 * <br>
 * 
 * 
 */
public class Server {

	private ServerSocket serverSocket;
	private int numPlayers;
	protected List<Host> list;
	private static String newLine = System.getProperty("line.separator");
	private static boolean _debug_addElementsToList = false;

	/**
	 * Cliente P2P:<br>
	 * - Player 1 out: <br>
	 * --- connection<br>
	 * --- game type, mode, size, player ID<br>
	 * --- board OR ships one-by-one<br>
	 * --- fire position<br>
	 * --- buy ability (line attack, AoE, shield)<br>
	 * 
	 * - Player 2 out:<br>
	 * --- connection response<br>
	 * --- found game<br>
	 * --- game start<br>
	 * --- fire response<br>
	 * --- buy ability response<br>
	 * 
	 * La logica de comprar cosas la podemos dejar en el controller de cada cliente
	 * 
	 */

	/**
	 * Main function for the server. It create and launches the server.
	 * 
	 * @param args Launch arguments
	 */
	public static void main(String[] args) {
		Server gs = new Server();
		gs.acceptConnections();
	}

	/**
	 * It launch the server.
	 */
	public Server() {
		log("Starting server...");
		numPlayers = 0;
		list = new ArrayList<Host>();
		if (_debug_addElementsToList)
			addTestElementsToList();
		try {
			serverSocket = new ServerSocket(12345);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Accept all incoming connections and creating new threads to read and manage
	 * connections one-by-one. <br>
	 * The threads are closed when client is disconnected or when exception occurs.
	 */
	public void acceptConnections() {
		log("Waiting for connections...");
		while (true) {
			try {
				Socket s = serverSocket.accept();
				Thread t = new Thread(new ServerSideConnection(s, numPlayers));
				t.start();
				numPlayers++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create JSONArray from actual state of the hosts list.
	 * 
	 * @return JSONArray that represent the List<Host> list
	 */
	private String getListJSON() {
		JSONArray jA = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject j = new JSONObject();
			j.put("socket", list.get(i).getSocket());
			j.put("id", list.get(i).getId());
			j.put("gameSize", list.get(i).getSize());
			jA.put(j);
		}
		return jA.toString(0);
	}

	/**
	 * Used for testing the connection with the server
	 */
	private void addTestElementsToList() {
		// <test>
		Host a;
		for (int i = 0; i < 4; i++) {
			JSONObject jObj = new JSONObject();
			jObj.put("id", "" + i);
			jObj.put("gameSize", "test" + 1);
			jObj.put("socket", i);
			a = new Host(jObj.toString(0));
			list.add(a);
		}
		// </test>
	}

	private void log(String data) {
		System.out.println("[Server]: " + data);
	}

	// CLASS
	private class ServerSideConnection implements Runnable {
		private Socket socket;
		private BufferedReader readerChannel;
		private BufferedWriter writerChannel;
		private int playerID;
		private Host thisHost;

		public ServerSideConnection(Socket s, int id) {
			socket = s;
			playerID = id;
			try {
				readerChannel = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writerChannel = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			log("Player '" + playerID + "' has connected.");
//			while (socket.isClosed()) {
			try {
				String command, data;
				while ((command = readerChannel.readLine()) != null) {
//					log("Received " + command );
					switch (parse(command)) {
					case ADD:
						log("Add request from '" + playerID + "'.");
						data = readerChannel.readLine();
						thisHost = new Host(data);
						list.add(thisHost);
						break;

					case REMOVE:
						log("Remove request from '" + playerID + "'.");
						data = readerChannel.readLine();
						thisHost = new Host(data);
						removeFromListAction();
						break;

					case LIST:
						log("List request from '" + playerID + "'.");
						send(getListJSON());
						break;

					case ERROR:
						// TODO: send error msg or..?
						break;
					default:
						break;
					}
				}
			} catch (Exception e) {
				onClosedConnection();
				return;
			}
//			}
//			onClosedConnection();
//			return;
		}

		private void onClosedConnection() {
			log("Player '" + playerID + "' has disconnected.");
			log("Deleting '" + playerID + "' host from list.");
			removeFromListAction();
			numPlayers--;
		}

		private void removeFromListAction() {
			list.remove(thisHost);
		}

		private void send(String str) throws IOException {
			writerChannel.write(str + newLine);
			writerChannel.flush();
		}

		private Command parse(String command) {
			if (command.equals("0"))
				return Command.ADD;
			else if (command.equals("1"))
				return Command.REMOVE;
			else if (command.equals("2"))
				return Command.LIST;
			else
				return Command.ERROR;
		}
	}

}
