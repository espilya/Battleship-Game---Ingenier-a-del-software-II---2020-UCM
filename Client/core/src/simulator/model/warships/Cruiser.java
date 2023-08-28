package simulator.model.warships;

import java.util.LinkedHashMap;

public class Cruiser extends Ship {
	private static final long serialVersionUID = -1698857922790504924L;

	/**
	 * Constructor for the Cruiser
	 * 
	 * @param id
	 */
	Cruiser(String id) {
		super(id);
		size = 4;
		aliveCells = size;
		type = "Cruiser";
		cellsLocation = new LinkedHashMap<>();
	}

}
