	package simulator.model;

/* 
 * This enum is used to determine the game mode. That is PvP or PvE this is important to know because we use a different controller for each mode
 * Also depending on the game mode one of the player is fixed to be the Computer which is a subclass of Player
 */
public enum GameMode {
	PVE(), PVP();

	public String paint() {
		return "GameDifficulty: " + this.toString();
	}

	public static String[] getNames() {
		String[] names = new String[GameMode.values().length];
		int i = 0;
		for (GameMode env : GameMode.values()) {
			names[i] = env.name();
			i++;
		}
		return names;
	}
}