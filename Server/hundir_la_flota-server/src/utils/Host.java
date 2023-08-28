package utils;

import org.json.*;

/**
 * Contains all necessary information about a specific host. <br>
 * String id, String size, int socket
 *
 */
public class Host {

	private String id;
	private String size;
	private int socket;

	/**
	 * Constructor that receive JSONObject as String and read it to replicate the
	 * host on an object.
	 * 
	 * @param data JSONObject in String format
	 */
	public Host(String data) {
		JSONTokener tokener = new JSONTokener(data);
		JSONObject o = new JSONObject(tokener);
		this.id = o.getString("id");
		this.size = o.getString("gameSize");
		this.socket = o.getInt("socket");
	}

	public String getId() {
		return id;
	}

	public String getSize() {
		return size;
	}

	public int getSocket() {
		return socket;
	}
}
