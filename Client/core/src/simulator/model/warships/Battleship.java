package simulator.model.warships;

import java.util.LinkedHashMap;

public class Battleship extends Ship {
	private static final long serialVersionUID = -3542671846451773976L;

	/**
	 * Constructor for the Battleship
	 * 
	 * @param id
	 */
	public Battleship(String id) {
		super(id);
		size = 5;
		aliveCells = size;
		type = "Battleship";
		cellsLocation = new LinkedHashMap<>();
	}

}
