package simulator.model;

/**
 * This enum is used to determine the game difficulty. This is important as the behaviour is determined by this enum
 */
public enum BotDifficulty {
	EASY(), NORMAL(), HARD();

	public String paint() {
		return "GameDifficulty: " + this.toString();
	}

	public static String[] getNames() {
		String[] names = new String[BotDifficulty.values().length];
		int i = 0;
		for (BotDifficulty env : BotDifficulty.values()) {
			names[i] = env.name();
			i++;
		}
		return names;
	}
}
