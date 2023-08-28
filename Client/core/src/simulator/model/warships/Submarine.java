package simulator.model.warships;

import java.util.LinkedHashMap;

public class Submarine extends Ship {
	private static final long serialVersionUID = 8069152091013455393L;

	/**
	 * Constructor for the Submarine
	 * 
	 * @param id
	 */
	Submarine(String id) {
		super(id);
		size = 3;
		aliveCells = size;
		type = "Submarine";
		cellsLocation = new LinkedHashMap<>();
	}
}
