package utils;

import org.json.*;

import gui.screens.LobbyScreen;

/**
 * Used to store the host. Used in {@link LobbyScreen} to store all available
 * hosts in a list.
 *
 */
public class Host {

	private String id;
	private String size;
	private int socket;

	public Host(String data) {
		JSONTokener tokener = new JSONTokener(data);
		JSONObject o = new JSONObject(tokener);
		this.id = o.getString("id");
		this.size = o.getString("gameSize");
		this.socket = o.getInt("socket");
	}

	public Host(JSONObject o) {
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
